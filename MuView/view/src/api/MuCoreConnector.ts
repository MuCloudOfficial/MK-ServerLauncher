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

const getRuntimeLocation = (): RuntimeLocation | undefined => {
    if (typeof window === "undefined" || !window.location) {
        return undefined;
    }

    const { protocol, hostname, port, origin } = window.location;
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

const tryNormalizeOrigin = (value: string): string | undefined => {
    try {
        return trimTrailingSlash(new URL(value).toString());
    } catch {
        return undefined;
    }
};

const toWebSocketOrigin = (value: string): string | undefined => {
    try {
        const url = new URL(value);
        if (url.protocol === "https:") {
            url.protocol = "wss:";
        } else if (url.protocol === "http:") {
            url.protocol = "ws:";
        }

        if (url.protocol !== "ws:" && url.protocol !== "wss:") {
            return undefined;
        }

        return trimTrailingSlash(url.toString());
    } catch {
        return undefined;
    }
};

const buildRuntimeHttpOrigin = (location: RuntimeLocation): string => {
    const explicitPort = getEnvValue("VITE_MUCORE_PORT");
    if (explicitPort) {
        return `${location.protocol}//${location.hostname}:${explicitPort}`;
    }

    return trimTrailingSlash(location.origin);
};

const buildCoreHttpBaseUrl = (): string => {
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

    // SSR / build-time fallback only.
    return "http://localhost";
};

const buildCoreWebSocketBaseUrl = (httpBaseUrl: string): string => {
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

    const convertedHttpBase = toWebSocketOrigin(httpBaseUrl);
    if (convertedHttpBase) {
        return convertedHttpBase;
    }

    return "ws://localhost";
};

const resolveCoreHttpBaseUrl = (): string => buildCoreHttpBaseUrl();
const resolveCoreWebSocketBaseUrl = (): string => buildCoreWebSocketBaseUrl(resolveCoreHttpBaseUrl());

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
    config.baseURL = resolveCoreHttpBaseUrl();
    return config;
});

const normalizePath = (path: string): string => path.trim().replace(/^\/+/, "");

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

        this.instance = new WebSocket(joinUrl(resolveCoreWebSocketBaseUrl(), path));

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

        await Promise.race([
            this.openPromise,
            new Promise<void>((_, reject) => {
                setTimeout(() => reject(new Error("MuCore WebSocket connect timeout")), timeoutMs);
            }),
        ]);
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

            if (this.instance.readyState !== WebSocket.OPEN) {
                throw new Error("MuCore WebSocket is not open");
            }

            const payload = JSON.stringify(jsonMsg);
            this.instance.send(payload);
            return this.receive(timeoutMs);
        };

        const run = this.sendChain.catch(() => undefined).then(dispatch);
        this.sendChain = run;
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
