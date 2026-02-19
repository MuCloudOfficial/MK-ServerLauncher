import axios from "axios";

interface RuntimeLocation {
    protocol: string;
    hostname: string;
    port: string;
    host: string;
    origin: string;
}

type PendingReceiver = {
    resolve: (value: unknown) => void;
    reject: (reason?: unknown) => void;
    minSequence: number;
    matcher?: (payload: unknown) => boolean;
    timer?: ReturnType<typeof setTimeout>;
    settled?: boolean;
};

type BufferedMessage = {
    sequence: number;
    payload: unknown;
};

const DEFAULT_TIMEOUT_MS = 10000;
const MAX_BUFFERED_MESSAGES = 200;

const getSetTimeout = (): typeof setTimeout => {
    const runtimeSetTimeout = globalThis.setTimeout;
    return typeof runtimeSetTimeout === "function" ? runtimeSetTimeout : setTimeout;
};

const getClearTimeout = (): typeof clearTimeout => {
    const runtimeClearTimeout = globalThis.clearTimeout;
    return typeof runtimeClearTimeout === "function" ? runtimeClearTimeout : clearTimeout;
};

const trimTrailingSlash = (value: string): string => value.replace(/\/+$/, "");
const normalizePath = (path: string): string => path.trim().replace(/^\/+/, "");
const hasProtocol = (value: string): boolean => /^[a-zA-Z][a-zA-Z\d+.-]*:/.test(value);

const toHttpProtocol = (value: string | undefined): "http:" | "https:" => {
    if (value === "https:" || value === "wss:") {
        return "https:";
    }

    if (value === "http:" || value === "ws:") {
        return "http:";
    }

    return "https:";
};

const toWebSocketProtocol = (value: string | undefined): "ws:" | "wss:" => {
    if (value === "https:" || value === "wss:") {
        return "wss:";
    }

    if (value === "http:" || value === "ws:") {
        return "ws:";
    }

    return "wss:";
};

const getRuntimeLocation = (): RuntimeLocation | undefined => {
    const globalLocation = typeof globalThis !== "undefined" ? globalThis.location : undefined;
    const windowLocation = typeof window !== "undefined" ? window.location : undefined;
    const location = globalLocation ?? windowLocation;
    if (!location) {
        return undefined;
    }

    const { protocol, hostname, port, host, origin } = location;
    return { protocol, hostname, port, host, origin };
};

const getEnvValue = (key: string): string | undefined => {
    if (typeof import.meta !== "undefined" && import.meta.env) {
        const value = import.meta.env[key];
        if (typeof value === "string" && value.trim().length > 0) {
            return value.trim();
        }
    }

    return undefined;
};

const getFirstEnvValue = (...keys: string[]): string | undefined => {
    for (const key of keys) {
        const value = getEnvValue(key);
        if (value) {
            return value;
        }
    }

    return undefined;
};

const parseAsUrl = (value: string): URL | undefined => {
    try {
        return new URL(value);
    } catch {
        return undefined;
    }
};

const parseOriginInput = (
    value: string,
    fallbackProtocol: string,
    runtimeLocation?: RuntimeLocation,
): URL | undefined => {
    const trimmed = value.trim();
    if (trimmed.length === 0) {
        return undefined;
    }

    const direct = parseAsUrl(trimmed);
    if (direct) {
        return direct;
    }

    if (runtimeLocation && (trimmed.startsWith("/") || trimmed.startsWith("./") || trimmed.startsWith("../"))) {
        return parseAsUrl(new URL(trimmed, `${trimTrailingSlash(runtimeLocation.origin)}/`).toString());
    }

    if (trimmed.startsWith("//")) {
        return parseAsUrl(`${fallbackProtocol}${trimmed}`);
    }

    if (!hasProtocol(trimmed)) {
        return parseAsUrl(`${fallbackProtocol}//${trimmed}`);
    }

    return undefined;
};

const normalizeHttpOrigin = (
    value: string,
    fallbackProtocol: string = "http:",
    runtimeLocation?: RuntimeLocation,
): string | undefined => {
    const parsed = parseOriginInput(value, fallbackProtocol, runtimeLocation);
    if (!parsed) {
        return undefined;
    }

    if (parsed.protocol === "ws:") {
        parsed.protocol = "http:";
    } else if (parsed.protocol === "wss:") {
        parsed.protocol = "https:";
    }

    if (parsed.protocol !== "http:" && parsed.protocol !== "https:") {
        return undefined;
    }

    return trimTrailingSlash(parsed.toString());
};

const normalizeWebSocketOrigin = (
    value: string,
    fallbackProtocol: string = "ws:",
    runtimeLocation?: RuntimeLocation,
): string | undefined => {
    const parsed = parseOriginInput(value, fallbackProtocol, runtimeLocation);
    if (!parsed) {
        return undefined;
    }

    if (parsed.protocol === "https:") {
        parsed.protocol = "wss:";
    } else if (parsed.protocol === "http:") {
        parsed.protocol = "ws:";
    }

    if (parsed.protocol !== "ws:" && parsed.protocol !== "wss:") {
        return undefined;
    }

    return trimTrailingSlash(parsed.toString());
};

const buildRuntimeHttpOrigin = (location: RuntimeLocation): string => {
    const explicitHost = getEnvValue("VITE_MUCORE_HOST");
    const explicitPort = getEnvValue("VITE_MUCORE_PORT");
    const runtimeProtocol = toHttpProtocol(location.protocol);

    if (!explicitHost && !explicitPort) {
        const runtimeOrigin = normalizeHttpOrigin(location.origin, runtimeProtocol, location);
        if (runtimeOrigin) {
            return runtimeOrigin;
        }

        return `${runtimeProtocol}//${location.host}`;
    }

    const baseHost = explicitHost ?? location.host;
    const normalizedFromHost = normalizeHttpOrigin(baseHost, runtimeProtocol, location);
    if (normalizedFromHost) {
        if (!explicitPort) {
            return normalizedFromHost;
        }

        try {
            const url = new URL(`${trimTrailingSlash(normalizedFromHost)}/`);
            url.port = explicitPort;
            return trimTrailingSlash(url.toString());
        } catch {
            // fallback below
        }
    }

    const hostForFallback = explicitHost ?? location.hostname;
    if (explicitPort) {
        return `${runtimeProtocol}//${hostForFallback}:${explicitPort}`;
    }

    return `${runtimeProtocol}//${hostForFallback}`;
};

const buildCoreHttpBaseUrl = (): string | undefined => {
    const location = getRuntimeLocation();
    const runtimeProtocol = toHttpProtocol(location?.protocol);

    const envOrigin = getFirstEnvValue(
        "VITE_MUCORE_HTTP_ORIGIN",
        "VITE_MUCORE_HTTP_URL",
        "VITE_MUCORE_ORIGIN",
        "VITE_MUCORE_BASE_URL",
        "VITE_MUCORE_API_ORIGIN",
        "VITE_MUCORE_API_BASE_URL",
    );
    if (envOrigin) {
        const normalizedEnvOrigin = normalizeHttpOrigin(envOrigin, runtimeProtocol, location);
        if (normalizedEnvOrigin) {
            return normalizedEnvOrigin;
        }
    }

    if (location) {
        return buildRuntimeHttpOrigin(location);
    }

    return undefined;
};

const buildCoreWebSocketBaseUrl = (httpBaseUrl?: string): string | undefined => {
    const location = getRuntimeLocation();
    const runtimeProtocol = toWebSocketProtocol(location?.protocol);

    const envOrigin = getFirstEnvValue(
        "VITE_MUCORE_WS_ORIGIN",
        "VITE_MUCORE_WS_URL",
        "VITE_MUCORE_WS_BASE_URL",
        "VITE_MUCORE_ORIGIN",
        "VITE_MUCORE_BASE_URL",
    );
    if (envOrigin) {
        const explicitWsOrigin = normalizeWebSocketOrigin(envOrigin, runtimeProtocol, location);
        if (explicitWsOrigin) {
            return explicitWsOrigin;
        }
    }

    if (httpBaseUrl) {
        const convertedHttpBase = normalizeWebSocketOrigin(httpBaseUrl, runtimeProtocol, location);
        if (convertedHttpBase) {
            return convertedHttpBase;
        }
    }

    if (location) {
        const runtimeOriginAsWs = normalizeWebSocketOrigin(location.origin, runtimeProtocol, location);
        if (runtimeOriginAsWs) {
            return runtimeOriginAsWs;
        }
    }

    return undefined;
};

const resolveCoreHttpBaseUrl = (): string | undefined => buildCoreHttpBaseUrl();
const resolveCoreWebSocketBaseUrl = (): string | undefined => buildCoreWebSocketBaseUrl(resolveCoreHttpBaseUrl());

const joinUrl = (base: string, path: string): string => {
    const normalizedPath = normalizePath(path);
    if (normalizedPath.length === 0) {
        return trimTrailingSlash(base);
    }

    try {
        return new URL(normalizedPath, `${trimTrailingSlash(base)}/`).toString();
    } catch {
        return `${trimTrailingSlash(base)}/${normalizedPath}`;
    }
};

const getWebSocketConstructor = (): typeof WebSocket | undefined => {
    if (typeof globalThis === "undefined") {
        return undefined;
    }

    const ctor = globalThis.WebSocket;
    return typeof ctor === "function" ? ctor : undefined;
};

export const apiClient = axios.create({
    allowAbsoluteUrls: true,
    timeout: DEFAULT_TIMEOUT_MS,
    headers: {
        "Content-Type": "application/json",
        "Accept": "application/json",
    },
});

apiClient.interceptors.request.use((config) => {
    const runtimeBase = resolveCoreHttpBaseUrl();
    if (runtimeBase) {
        config.baseURL = runtimeBase;
    }
    return config;
});

const getWebSocketReadyState = (instance: WebSocket): number => {
    const ctor = instance.constructor as typeof WebSocket;
    return ctor.OPEN;
};

const isWebSocketClosedState = (instance: WebSocket): boolean => {
    const ctor = instance.constructor as typeof WebSocket;
    return instance.readyState === ctor.CLOSED || instance.readyState === ctor.CLOSING;
};

const toError = (reason: unknown, fallback: string): Error => {
    if (reason instanceof Error) {
        return reason;
    }

    if (typeof reason === "string" && reason.trim().length > 0) {
        return new Error(reason);
    }

    return new Error(fallback);
};

const resolveTimeoutMs = (timeoutMs: number): number => {
    if (!Number.isFinite(timeoutMs) || timeoutMs <= 0) {
        return DEFAULT_TIMEOUT_MS;
    }

    return timeoutMs;
};

const decodeWebSocketPayload = async (raw: unknown): Promise<unknown> => {
    if (typeof raw === "string") {
        try {
            return JSON.parse(raw);
        } catch {
            return raw;
        }
    }

    if (raw instanceof ArrayBuffer) {
        const text = new TextDecoder().decode(raw);
        try {
            return JSON.parse(text);
        } catch {
            return text;
        }
    }

    if (typeof Blob !== "undefined" && raw instanceof Blob) {
        const text = await raw.text();
        try {
            return JSON.parse(text);
        } catch {
            return text;
        }
    }

    return raw;
};

const getPacketId = (payload: unknown): string | undefined => {
    if (!payload || typeof payload !== "object") {
        return undefined;
    }

    const candidate = (payload as Record<string, unknown>).MP_ID;
    return typeof candidate === "string" && candidate.trim().length > 0 ? candidate : undefined;
};

const createResponseMatcher = (requestPayload: unknown): ((payload: unknown) => boolean) | undefined => {
    const requestPacketId = getPacketId(requestPayload);
    if (!requestPacketId) {
        return undefined;
    }

    return (payload: unknown): boolean => getPacketId(payload) === requestPacketId;
};

export class MuWebSocket {
    private readonly instance: WebSocket;
    private readonly openPromise: Promise<void>;
    private resolveOpen?: () => void;
    private rejectOpen?: (reason?: unknown) => void;

    private lastMessage: unknown;
    private isConnected = false;
    private isClosed = false;
    private connectionError?: Error;

    private readonly pendingReceivers: PendingReceiver[] = [];
    private readonly bufferedMessages: BufferedMessage[] = [];
    private messageSequence = 0;
    private sendChain: Promise<void> = Promise.resolve();
    private messageHandleChain: Promise<void> = Promise.resolve();

    constructor(path: string) {
        const WebSocketCtor = getWebSocketConstructor();
        if (!WebSocketCtor) {
            throw new Error("WebSocket is not available in this runtime");
        }

        const wsBase = resolveCoreWebSocketBaseUrl();
        if (!wsBase) {
            throw new Error("Unable to resolve MuCore WebSocket base URL. Set VITE_MUCORE_WS_ORIGIN or VITE_MUCORE_HTTP_ORIGIN.");
        }

        this.instance = new WebSocketCtor(joinUrl(wsBase, path));

        this.openPromise = new Promise((resolve, reject) => {
            this.resolveOpen = resolve;
            this.rejectOpen = reject;
        });

        this.instance.onopen = () => {
            this.isConnected = true;
            this.isClosed = false;
            this.connectionError = undefined;
            this.resolveOpen?.();
            this.resolveOpen = undefined;
            this.rejectOpen = undefined;
        };

        this.instance.onerror = (event) => {
            const nextError = new Error("MuCore WebSocket connection error");
            this.connectionError = nextError;
            if (!this.isConnected) {
                this.rejectOpen?.(nextError);
                this.rejectOpen = undefined;
                this.resolveOpen = undefined;
            }

            if (this.pendingReceivers.length > 0) {
                this.flushPendingReceivers(nextError);
            }

            console.error("WebSocket error:", event);
        };

        this.instance.onmessage = (event) => {
            this.messageHandleChain = this.messageHandleChain
                .catch(() => undefined)
                .then(() => this.handleIncomingMessage(event.data));
        };

        this.instance.onclose = (event) => {
            this.isConnected = false;
            this.isClosed = true;
            this.connectionError = new Error(event.reason || "MuCore WebSocket is closed");
            this.rejectOpen?.(this.connectionError);
            this.rejectOpen = undefined;
            this.resolveOpen = undefined;
            this.flushPendingReceivers(this.connectionError);
        };
    }

    private async handleIncomingMessage(rawData: unknown): Promise<void> {
        let message: unknown;
        try {
            message = await decodeWebSocketPayload(rawData);
        } catch (error) {
            message = rawData;
            console.error("Failed to decode MuCore WebSocket payload:", error);
        }

        this.lastMessage = message;
        this.messageSequence += 1;
        const nextBuffered: BufferedMessage = {
            sequence: this.messageSequence,
            payload: this.lastMessage,
        };

        const receiverIndex = this.pendingReceivers.findIndex((pending) => {
            if (nextBuffered.sequence < pending.minSequence) {
                return false;
            }

            if (pending.matcher && !pending.matcher(nextBuffered.payload)) {
                return false;
            }

            return true;
        });

        if (receiverIndex >= 0) {
            const [receiver] = this.pendingReceivers.splice(receiverIndex, 1);
            if (receiver?.timer) {
                getClearTimeout()(receiver.timer);
            }
            receiver?.resolve(nextBuffered.payload);
            return;
        }

        this.bufferedMessages.push(nextBuffered);
        if (this.bufferedMessages.length > MAX_BUFFERED_MESSAGES) {
            this.bufferedMessages.splice(0, this.bufferedMessages.length - MAX_BUFFERED_MESSAGES);
        }
    }

    private flushPendingReceivers(error: Error): void {
        while (this.pendingReceivers.length > 0) {
            const receiver = this.pendingReceivers.shift();
            if (!receiver) {
                continue;
            }

            if (receiver.timer) {
                getClearTimeout()(receiver.timer);
            }
            receiver.reject(error);
        }
    }

    private ensureOpenState(): void {
        if (this.instance.readyState === getWebSocketReadyState(this.instance) && !this.isClosed) {
            return;
        }

        if (this.connectionError) {
            throw this.connectionError;
        }

        throw new Error("MuCore WebSocket is not open");
    }

    public getMsg(): unknown {
        return this.lastMessage;
    }

    public isConnect(): boolean {
        return this.isConnected;
    }

    public async waitForConnect(timeoutMs: number = DEFAULT_TIMEOUT_MS): Promise<void> {
        const effectiveTimeoutMs = resolveTimeoutMs(timeoutMs);

        if (this.instance.readyState === getWebSocketReadyState(this.instance)) {
            this.isConnected = true;
            return;
        }

        if (isWebSocketClosedState(this.instance) || this.isClosed) {
            throw this.connectionError ?? new Error("MuCore WebSocket is closed");
        }

        let timer: ReturnType<typeof setTimeout> | undefined;
        const scheduleTimeout = getSetTimeout();
        const cancelTimeout = getClearTimeout();

        try {
            await Promise.race([
                this.openPromise,
                new Promise<void>((_, reject) => {
                    timer = scheduleTimeout(() => reject(new Error("MuCore WebSocket connect timeout")), effectiveTimeoutMs);
                }),
            ]);
        } finally {
            if (timer) {
                cancelTimeout(timer);
            }
        }
    }

    private registerPendingReceiver(
        timeoutMs: number,
        minSequence: number,
        matcher?: (payload: unknown) => boolean,
    ): { promise: Promise<unknown>; cancel: (reason: Error) => void } {
        const effectiveTimeoutMs = resolveTimeoutMs(timeoutMs);
        const scheduleTimeout = getSetTimeout();

        let receiver: PendingReceiver | undefined;
        const promise = new Promise<unknown>((resolve, reject) => {
            const nextReceiver: PendingReceiver = {
                resolve: (value: unknown) => {
                    if (nextReceiver.settled) {
                        return;
                    }
                    nextReceiver.settled = true;
                    resolve(value);
                },
                reject: (reason?: unknown) => {
                    if (nextReceiver.settled) {
                        return;
                    }
                    nextReceiver.settled = true;
                    reject(reason);
                },
                minSequence,
                matcher,
            };

            nextReceiver.timer = scheduleTimeout(() => {
                const index = this.pendingReceivers.indexOf(nextReceiver);
                if (index >= 0) {
                    this.pendingReceivers.splice(index, 1);
                }
                nextReceiver.reject(new Error("MuCore WebSocket response timeout"));
            }, effectiveTimeoutMs);

            receiver = nextReceiver;
            this.pendingReceivers.push(nextReceiver);
        });

        const cancel = (reason: Error): void => {
            if (!receiver) {
                return;
            }

            const index = this.pendingReceivers.indexOf(receiver);
            if (index >= 0) {
                this.pendingReceivers.splice(index, 1);
            }

            if (receiver.timer) {
                getClearTimeout()(receiver.timer);
            }

            receiver.reject(reason);
        };

        return { promise, cancel };
    }

    public receive(
        timeoutMs: number = DEFAULT_TIMEOUT_MS,
        minSequence: number = 0,
        matcher?: (payload: unknown) => boolean,
    ): Promise<unknown> {
        const bufferedIndex = this.bufferedMessages.findIndex((message) => {
            if (message.sequence < minSequence) {
                return false;
            }

            if (matcher && !matcher(message.payload)) {
                return false;
            }

            return true;
        });

        if (bufferedIndex >= 0) {
            const [message] = this.bufferedMessages.splice(bufferedIndex, 1);
            return Promise.resolve(message.payload);
        }

        if (isWebSocketClosedState(this.instance) || this.isClosed) {
            return Promise.reject(this.connectionError ?? new Error("MuCore WebSocket is closed"));
        }

        return this.registerPendingReceiver(timeoutMs, minSequence, matcher).promise;
    }

    public async send(jsonMsg: unknown, timeoutMs: number = DEFAULT_TIMEOUT_MS): Promise<unknown> {
        const effectiveTimeoutMs = resolveTimeoutMs(timeoutMs);
        const dispatch = async (): Promise<unknown> => {
            await this.waitForConnect(effectiveTimeoutMs);
            this.ensureOpenState();

            const minResponseSequence = this.messageSequence + 1;
            const responseMatcher = createResponseMatcher(jsonMsg);

            let payload: string;
            try {
                payload = JSON.stringify(jsonMsg);
            } catch (error) {
                throw toError(error, "Failed to serialize MuCore WebSocket payload");
            }

            const responseReceiver = this.registerPendingReceiver(
                effectiveTimeoutMs,
                minResponseSequence,
                responseMatcher,
            );

            try {
                this.instance.send(payload);
            } catch (error) {
                responseReceiver.cancel(toError(error, "Failed to send MuCore WebSocket payload"));
                throw toError(error, "Failed to send MuCore WebSocket payload");
            }

            return responseReceiver.promise;
        };

        const run = this.sendChain
            .catch(() => undefined)
            .then(() => dispatch());

        this.sendChain = run
            .then(() => undefined)
            .catch(() => undefined);

        return run;
    }

    public close(): void {
        this.isConnected = false;
        this.isClosed = true;

        const ctor = this.instance.constructor as typeof WebSocket;
        if (this.instance.readyState === ctor.OPEN || this.instance.readyState === ctor.CONNECTING) {
            this.instance.close();
        }

        this.flushPendingReceivers(new Error("MuCore WebSocket is closed"));
    }
}
