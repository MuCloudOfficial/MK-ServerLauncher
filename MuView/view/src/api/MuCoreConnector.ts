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
    timer?: ReturnType<typeof setTimeout>;
};

const DEFAULT_TIMEOUT_MS = 10000;

const trimTrailingSlash = (value: string): string => value.replace(/\/+$/, "");
const normalizePath = (path: string): string => path.trim().replace(/^\/+/, "");
const hasProtocol = (value: string): boolean => /^[a-zA-Z][a-zA-Z\d+.-]*:/.test(value);

const getRuntimeLocation = (): RuntimeLocation | undefined => {
    const globalLocation = typeof globalThis !== "undefined" ? globalThis.location : undefined;
    const location = globalLocation;
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

const parseAsUrl = (value: string): URL | undefined => {
    try {
        return new URL(value);
    } catch {
        return undefined;
    }
};

const parseOriginInput = (value: string, fallbackProtocol: string): URL | undefined => {
    const trimmed = value.trim();
    if (trimmed.length === 0) {
        return undefined;
    }

    const direct = parseAsUrl(trimmed);
    if (direct) {
        return direct;
    }

    if (trimmed.startsWith("//")) {
        return parseAsUrl(`${fallbackProtocol}${trimmed}`);
    }

    if (!hasProtocol(trimmed)) {
        return parseAsUrl(`${fallbackProtocol}//${trimmed}`);
    }

    return undefined;
};

const normalizeHttpOrigin = (value: string, fallbackProtocol: string = "http:"): string | undefined => {
    const parsed = parseOriginInput(value, fallbackProtocol);
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

const normalizeWebSocketOrigin = (value: string, fallbackProtocol: string = "ws:"): string | undefined => {
    const parsed = parseOriginInput(value, fallbackProtocol);
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

    if (!explicitHost && !explicitPort) {
        return trimTrailingSlash(location.origin);
    }

    const host = explicitHost ?? location.hostname;
    const normalizedHost = host.replace(/^\[|\]$/g, "");

    if (explicitPort) {
        return `${location.protocol}//${normalizedHost}:${explicitPort}`;
    }

    return `${location.protocol}//${normalizedHost}`;
};

const buildCoreHttpBaseUrl = (): string | undefined => {
    const location = getRuntimeLocation();
    const runtimeProtocol = location?.protocol ?? "http:";

    const envOrigin = getEnvValue("VITE_MUCORE_HTTP_ORIGIN") ?? getEnvValue("VITE_MUCORE_ORIGIN");
    if (envOrigin) {
        const normalizedEnvOrigin = normalizeHttpOrigin(envOrigin, runtimeProtocol);
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
    const runtimeProtocol = location?.protocol === "https:" ? "wss:" : "ws:";

    const envOrigin = getEnvValue("VITE_MUCORE_WS_ORIGIN");
    if (envOrigin) {
        const explicitWsOrigin = normalizeWebSocketOrigin(envOrigin, runtimeProtocol);
        if (explicitWsOrigin) {
            return explicitWsOrigin;
        }
    }

    if (httpBaseUrl) {
        const convertedHttpBase = normalizeWebSocketOrigin(httpBaseUrl, runtimeProtocol);
        if (convertedHttpBase) {
            return convertedHttpBase;
        }
    }

    if (location) {
        const runtimeOriginAsWs = normalizeWebSocketOrigin(location.origin, runtimeProtocol);
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

export const apiClient = axios.create({
    baseURL: resolveCoreHttpBaseUrl(),
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

const isWebSocketAvailable = (): boolean => typeof WebSocket !== "undefined";

const toError = (reason: unknown, fallback: string): Error => {
    if (reason instanceof Error) {
        return reason;
    }

    if (typeof reason === "string" && reason.trim().length > 0) {
        return new Error(reason);
    }

    return new Error(fallback);
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
    private readonly bufferedMessages: unknown[] = [];
    private sendChain: Promise<void> = Promise.resolve();

    constructor(path: string) {
        if (!isWebSocketAvailable()) {
            throw new Error("WebSocket is not available in this runtime");
        }

        const wsBase = resolveCoreWebSocketBaseUrl();
        if (!wsBase) {
            throw new Error("Unable to resolve MuCore WebSocket base URL. Set VITE_MUCORE_WS_ORIGIN or VITE_MUCORE_HTTP_ORIGIN.");
        }

        this.instance = new WebSocket(joinUrl(wsBase, path));

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
            this.connectionError = new Error("MuCore WebSocket connection error");
            if (!this.isConnected) {
                this.rejectOpen?.(this.connectionError);
                this.rejectOpen = undefined;
                this.resolveOpen = undefined;
            }

            if (this.isClosed) {
                this.flushPendingReceivers(this.connectionError);
            }

            console.error("WebSocket error:", event);
        };

        this.instance.onmessage = async (event) => {
            const message = await decodeWebSocketPayload(event.data);
            this.lastMessage = message;

            const receiver = this.pendingReceivers.shift();
            if (receiver) {
                if (receiver.timer) {
                    clearTimeout(receiver.timer);
                }
                receiver.resolve(this.lastMessage);
                return;
            }

            this.bufferedMessages.push(this.lastMessage);
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

    private flushPendingReceivers(error: Error): void {
        while (this.pendingReceivers.length > 0) {
            const receiver = this.pendingReceivers.shift();
            if (!receiver) {
                continue;
            }

            if (receiver.timer) {
                clearTimeout(receiver.timer);
            }
            receiver.reject(error);
        }
    }

    private ensureOpenState(): void {
        if (this.instance.readyState === WebSocket.OPEN && !this.isClosed) {
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
        if (this.instance.readyState === WebSocket.OPEN) {
            this.isConnected = true;
            return;
        }

        if (this.instance.readyState === WebSocket.CLOSED || this.instance.readyState === WebSocket.CLOSING || this.isClosed) {
            throw this.connectionError ?? new Error("MuCore WebSocket is closed");
        }

        let timer: ReturnType<typeof setTimeout> | undefined;

        try {
            await Promise.race([
                this.openPromise,
                new Promise<void>((_, reject) => {
                    timer = setTimeout(() => reject(new Error("MuCore WebSocket connect timeout")), timeoutMs);
                }),
            ]);
        } finally {
            if (timer) {
                clearTimeout(timer);
            }
        }
    }

    public receive(timeoutMs: number = DEFAULT_TIMEOUT_MS): Promise<unknown> {
        if (this.bufferedMessages.length > 0) {
            return Promise.resolve(this.bufferedMessages.shift());
        }

        if (this.instance.readyState === WebSocket.CLOSED || this.instance.readyState === WebSocket.CLOSING || this.isClosed) {
            return Promise.reject(this.connectionError ?? new Error("MuCore WebSocket is closed"));
        }

        return new Promise((resolve, reject) => {
            const receiver: PendingReceiver = {
                resolve,
                reject,
            };

            receiver.timer = setTimeout(() => {
                const index = this.pendingReceivers.indexOf(receiver);
                if (index >= 0) {
                    this.pendingReceivers.splice(index, 1);
                }
                reject(new Error("MuCore WebSocket response timeout"));
            }, timeoutMs);

            this.pendingReceivers.push(receiver);
        });
    }

    public async send(jsonMsg: unknown, timeoutMs: number = DEFAULT_TIMEOUT_MS): Promise<unknown> {
        const dispatch = async (): Promise<unknown> => {
            await this.waitForConnect(timeoutMs);
            this.ensureOpenState();

            let payload: string;
            try {
                payload = JSON.stringify(jsonMsg);
            } catch (error) {
                throw toError(error, "Failed to serialize MuCore WebSocket payload");
            }

            try {
                this.instance.send(payload);
            } catch (error) {
                throw toError(error, "Failed to send MuCore WebSocket payload");
            }

            return this.receive(timeoutMs);
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

        if (this.instance.readyState === WebSocket.OPEN || this.instance.readyState === WebSocket.CONNECTING) {
            this.instance.close();
        }

        this.flushPendingReceivers(new Error("MuCore WebSocket is closed"));
    }
}
