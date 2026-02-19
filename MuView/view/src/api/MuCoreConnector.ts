import axios from "axios";

interface RuntimeLocation {
    protocol: string;
    hostname: string;
    port: string;
    origin: string;
}

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
        const explicitPort = getEnvValue("VITE_MUCORE_PORT");
        if (explicitPort) {
            return `${location.protocol}//${location.hostname}:${explicitPort}`;
        }

        if (location.port) {
            return `${location.protocol}//${location.hostname}:${location.port}`;
        }

        return trimTrailingSlash(location.origin);
    }

    return "http://127.0.0.1";
};

const httpToWsOrigin = (httpOrigin: string): string => {
    try {
        const url = new URL(httpOrigin);
        if (url.protocol === "https:") {
            url.protocol = "wss:";
        } else if (url.protocol === "http:") {
            url.protocol = "ws:";
        }
        return trimTrailingSlash(url.toString());
    } catch {
        return httpOrigin;
    }
};

const buildCoreWebSocketBaseUrl = (httpBaseUrl: string): string => {
    const envOrigin = getEnvValue("VITE_MUCORE_WS_ORIGIN");
    if (envOrigin) {
        const normalizedEnvOrigin = tryNormalizeOrigin(envOrigin);
        if (normalizedEnvOrigin) {
            return normalizedEnvOrigin;
        }
    }

    return httpToWsOrigin(httpBaseUrl);
};

const coreHttpBaseUrl = buildCoreHttpBaseUrl();
const coreWebSocketBaseUrl = buildCoreWebSocketBaseUrl(coreHttpBaseUrl);

export const apiClient = axios.create({
    baseURL: coreHttpBaseUrl,
    allowAbsoluteUrls: true,
    timeout: 10000,
    headers: {
        "Content-Type": "application/json",
        "Accept": "application/json",
    },
});

const normalizePath = (path: string): string => {
    const cleaned = path.trim().replace(/^\/+/, "");
    return cleaned.length > 0 ? cleaned : "";
};

const joinUrl = (base: string, path: string): string => {
    const normalizedBase = trimTrailingSlash(base);
    const normalizedPath = normalizePath(path);
    return normalizedPath.length > 0 ? `${normalizedBase}/${normalizedPath}` : normalizedBase;
};

export class MuWebSocket {
    private readonly instance: WebSocket;
    private lastMessage: any;
    private isConnected = false;
    private readonly pendingResolvers: Array<(value: any) => void> = [];
    private readonly bufferedMessages: any[] = [];
    private openPromise: Promise<void>;

    constructor(path: string) {
        this.instance = new WebSocket(joinUrl(coreWebSocketBaseUrl, path));

        this.openPromise = new Promise((resolve, reject) => {
            this.instance.onopen = () => {
                this.isConnected = true;
                resolve();
                console.log("WebSocket connected");
            };

            this.instance.onerror = (e) => {
                if (this.instance.readyState !== WebSocket.OPEN) {
                    reject(new Error("MuCore WebSocket connection failed"));
                }
                console.error("WebSocket error:", e);
            };
        });

        this.instance.onmessage = (e) => {
            const raw = e.data;
            try {
                this.lastMessage = typeof raw === "string" ? JSON.parse(raw) : raw;
            } catch {
                this.lastMessage = raw;
            }

            const resolver = this.pendingResolvers.shift();
            if (resolver) {
                resolver(this.lastMessage);
                return;
            }

            this.bufferedMessages.push(this.lastMessage);
        };

        this.instance.onclose = (e) => {
            this.isConnected = false;
            console.warn("WebSocket closed:", e.reason || "no reason");

            while (this.pendingResolvers.length > 0) {
                const resolver = this.pendingResolvers.shift();
                resolver?.(undefined);
            }
        };
    }

    public getMsg(): any {
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

        if (this.instance.readyState === WebSocket.CLOSED || this.instance.readyState === WebSocket.CLOSING) {
            throw new Error("MuCore WebSocket is closed");
        }

        await Promise.race([
            this.openPromise,
            new Promise<void>((_, reject) => {
                setTimeout(() => reject(new Error("MuCore WebSocket connect timeout")), timeoutMs);
            }),
        ]);
    }

    public receive(timeoutMs: number = 10000): Promise<any> {
        if (this.bufferedMessages.length > 0) {
            return Promise.resolve(this.bufferedMessages.shift());
        }

        if (this.instance.readyState === WebSocket.CLOSED || this.instance.readyState === WebSocket.CLOSING) {
            return Promise.reject(new Error("MuCore WebSocket is closed"));
        }

        return new Promise((resolve, reject) => {
            const timeout = setTimeout(() => {
                const index = this.pendingResolvers.indexOf(wrappedResolve);
                if (index >= 0) {
                    this.pendingResolvers.splice(index, 1);
                }
                reject(new Error("MuCore WebSocket response timeout"));
            }, timeoutMs);

            const wrappedResolve = (value: any) => {
                clearTimeout(timeout);
                resolve(value);
            };

            this.pendingResolvers.push(wrappedResolve);
        });
    }

    public async send(jsonMsg: any, timeoutMs: number = 10000): Promise<any> {
        await this.waitForConnect(timeoutMs);

        try {
            this.instance.send(JSON.stringify(jsonMsg));
        } catch (error) {
            throw error;
        }

        return this.receive(timeoutMs);
    }

    public close() {
        if (this.instance.readyState === WebSocket.OPEN || this.instance.readyState === WebSocket.CONNECTING) {
            this.isConnected = false;
            this.instance.close();
        }
    }
}
