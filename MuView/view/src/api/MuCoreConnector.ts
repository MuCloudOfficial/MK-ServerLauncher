import axios from "axios";

interface RuntimeLocation {
    protocol: string;
    hostname: string;
    port: string;
    origin: string;
}

type PendingReceiver = {
    resolve: (value: unknown) => void;
    reject: (reason?: unknown) => void;
    timer?: ReturnType<typeof setTimeout>;
};

const trimTrailingSlash = (value: string): string => value.replace(/\/+$/, "");
const normalizePath = (path: string): string => path.trim().replace(/^\/+/, "");

const getRuntimeLocation = (): RuntimeLocation | undefined => {
    const maybeWindow = typeof window !== "undefined" ? window : undefined;
    const location = maybeWindow?.location;
    if (!location) {
        return undefined;
    }

    const { protocol, hostname, port, origin } = location;
    return { protocol, hostname, port, origin };
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

const tryNormalizeOrigin = (value: string): string | undefined => {
    const parsed = parseAsUrl(value);
    if (!parsed) {
        return undefined;
    }

    return trimTrailingSlash(parsed.toString());
};

const toWebSocketOrigin = (value: string): string | undefined => {
    const parsed = parseAsUrl(value);
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
    const explicitPort = getEnvValue("VITE_MUCORE_PORT");
    if (explicitPort) {
        return `${location.protocol}//${location.hostname}:${explicitPort}`;
    }

    return trimTrailingSlash(location.origin);
};

const buildCoreHttpBaseUrl = (): string | undefined => {
    const envOrigin = getEnvValue("VITE_MUCORE_HTTP_ORIGIN");
    if (envOrigin) {
        const normalizedEnvOrigin = tryNormalizeOrigin(envOrigin);
        if (normalizedEnvOrigin) {
            return normalizedEnvOrigin;
        }
    }

    const location = getRuntimeLocation();
    if (location) {
        return buildRuntimeHttpOrigin(location);
    }

    return undefined;
};

const buildCoreWebSocketBaseUrl = (httpBaseUrl?: string): string | undefined => {
    const envOrigin = getEnvValue("VITE_MUCORE_WS_ORIGIN");
    if (envOrigin) {
        const explicitWsOrigin = toWebSocketOrigin(envOrigin);
        if (explicitWsOrigin) {
            return explicitWsOrigin;
        }

        const normalizedEnvOrigin = tryNormalizeOrigin(envOrigin);
        if (normalizedEnvOrigin) {
            const converted = toWebSocketOrigin(normalizedEnvOrigin);
            if (converted) {
                return converted;
            }
        }
    }

    if (httpBaseUrl) {
        const convertedHttpBase = toWebSocketOrigin(httpBaseUrl);
        if (convertedHttpBase) {
            return convertedHttpBase;
        }
    }

    const location = getRuntimeLocation();
    if (location) {
        const runtimeOriginAsWs = toWebSocketOrigin(location.origin);
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
    timeout: 10000,
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

export class MuWebSocket {
    private readonly instance: WebSocket;
    private readonly openPromise: Promise<void>;
    private resolveOpen?: () => void;
    private rejectOpen?: (reason?: unknown) => void;

    private lastMessage: unknown;
    private isConnected = false;
    private isClosed = false;

    private readonly pendingReceivers: PendingReceiver[] = [];
    private readonly bufferedMessages: unknown[] = [];
    private sendChain: Promise<unknown> = Promise.resolve(undefined);

    constructor(path: string) {
        if (!isWebSocketAvailable()) {
            throw new Error("WebSocket is not available in this runtime");
        }

        const wsBase = resolveCoreWebSocketBaseUrl();
        if (!wsBase) {
            throw new Error("Unable to resolve MuCore WebSocket base URL. Set VITE_MUCORE_WS_ORIGIN or run in browser runtime.");
        }

        this.instance = new WebSocket(joinUrl(wsBase, path));

        this.openPromise = new Promise((resolve, reject) => {
            this.resolveOpen = resolve;
            this.rejectOpen = reject;
        });

        this.instance.onopen = () => {
            this.isConnected = true;
            this.isClosed = false;
            this.resolveOpen?.();
            this.resolveOpen = undefined;
            this.rejectOpen = undefined;
            console.log("WebSocket connected");
        };

        this.instance.onerror = (e) => {
            if (!this.isConnected) {
                this.rejectOpen?.(new Error("MuCore WebSocket connection failed"));
                this.rejectOpen = undefined;
                this.resolveOpen = undefined;
            }

            if (this.isClosed) {
                this.flushPendingReceivers(new Error("MuCore WebSocket is closed"));
            }

            console.error("WebSocket error:", e);
        };

        this.instance.onmessage = (e) => {
            const raw = e.data;
            try {
                this.lastMessage = typeof raw === "string" ? JSON.parse(raw) : raw;
            } catch {
                this.lastMessage = raw;
            }

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

        this.instance.onclose = (e) => {
            this.isConnected = false;
            this.isClosed = true;
            this.rejectOpen?.(new Error("MuCore WebSocket is closed before opening"));
            this.rejectOpen = undefined;
            this.resolveOpen = undefined;
            console.warn("WebSocket closed:", e.reason || "no reason");
            this.flushPendingReceivers(new Error("MuCore WebSocket is closed"));
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

    public getMsg(): unknown {
        return this.lastMessage;
    }

    public isConnect(): boolean {
        return this.isConnected;
    }

    public async waitForConnect(timeoutMs: number = 10000): Promise<void> {
        if (this.instance.readyState === WebSocket.OPEN) {
            this.isConnected = true;
            return;
        }

        if (this.instance.readyState === WebSocket.CLOSED || this.instance.readyState === WebSocket.CLOSING || this.isClosed) {
            throw new Error("MuCore WebSocket is closed");
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

    public receive(timeoutMs: number = 10000): Promise<unknown> {
        if (this.bufferedMessages.length > 0) {
            return Promise.resolve(this.bufferedMessages.shift());
        }

        if (this.instance.readyState === WebSocket.CLOSED || this.instance.readyState === WebSocket.CLOSING || this.isClosed) {
            return Promise.reject(new Error("MuCore WebSocket is closed"));
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

    public async send(jsonMsg: unknown, timeoutMs: number = 10000): Promise<unknown> {
        const dispatch = async (): Promise<unknown> => {
            await this.waitForConnect(timeoutMs);

            if (this.instance.readyState !== WebSocket.OPEN || this.isClosed) {
                throw new Error("MuCore WebSocket is not open");
            }

            const payload = JSON.stringify(jsonMsg);
            this.instance.send(payload);
            return this.receive(timeoutMs);
        };

        const run = this.sendChain
            .catch(() => undefined)
            .then(dispatch);

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
