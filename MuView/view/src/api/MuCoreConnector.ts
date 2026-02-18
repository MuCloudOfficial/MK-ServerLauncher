import axios from "axios";

interface RuntimeLocation {
    protocol: string;
    hostname: string;
    port: string;
    origin: string;
}

const DEFAULT_CORE_PORT = "20038";
const DEFAULT_HTTP_PROTOCOL = "http:";

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

const buildCoreHttpBaseUrl = (): string => {
    const envOrigin = getEnvValue("VITE_MUCORE_HTTP_ORIGIN");
    if (envOrigin) {
        return envOrigin;
    }

    const location = getRuntimeLocation();
    if (!location) {
        return `${DEFAULT_HTTP_PROTOCOL}//127.0.0.1:${DEFAULT_CORE_PORT}`;
    }

    const explicitPort = getEnvValue("VITE_MUCORE_PORT");
    if (explicitPort) {
        return `${location.protocol}//${location.hostname}:${explicitPort}`;
    }

    if (location.port) {
        return `${location.protocol}//${location.hostname}:${location.port}`;
    }

    return location.origin;
};

const buildCoreWebSocketBaseUrl = (): string => {
    const envOrigin = getEnvValue("VITE_MUCORE_WS_ORIGIN");
    if (envOrigin) {
        return envOrigin;
    }

    const location = getRuntimeLocation();
    if (!location) {
        return `ws://127.0.0.1:${DEFAULT_CORE_PORT}`;
    }

    const wsProtocol = location.protocol === "https:" ? "wss:" : "ws:";
    const explicitPort = getEnvValue("VITE_MUCORE_PORT");

    if (explicitPort) {
        return `${wsProtocol}//${location.hostname}:${explicitPort}`;
    }

    if (location.port) {
        return `${wsProtocol}//${location.hostname}:${location.port}`;
    }

    return `${wsProtocol}//${location.hostname}`;
};

const coreHttpBaseUrl = buildCoreHttpBaseUrl();
const coreWebSocketBaseUrl = buildCoreWebSocketBaseUrl();

export const apiClient = axios.create({
    baseURL: coreHttpBaseUrl,
    allowAbsoluteUrls: true,
    timeout: 10000,
    headers: {
        "Content-Type": "application/json",
        "Accept": "application/json",
    },
});

export class MuWebSocket {
    private readonly instance: WebSocket;
    private lastMessage: any;
    private isConnected = false;
    private readonly pendingResolvers: Array<(value: any) => void> = [];

    constructor(path: string) {
        const normalizedPath = path.startsWith("/") ? path.slice(1) : path;
        this.instance = new WebSocket(`${coreWebSocketBaseUrl}/${normalizedPath}`);

        this.instance.onopen = () => {
            this.isConnected = true;
            console.log("WebSocket connected");
        };

        this.instance.onerror = (e) => {
            console.error("WebSocket error:", e);
        };

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
            }
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

    public send(jsonMsg: any, timeoutMs: number = 10000): Promise<any> {
        if (!this.instance || this.instance.readyState !== WebSocket.OPEN) {
            return Promise.reject(new Error("MuCore WebSocket is not connected"));
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

            try {
                this.instance.send(JSON.stringify(jsonMsg));
            } catch (error) {
                clearTimeout(timeout);
                const index = this.pendingResolvers.indexOf(wrappedResolve);
                if (index >= 0) {
                    this.pendingResolvers.splice(index, 1);
                }
                reject(error);
            }
        });
    }

    public close() {
        if (this.instance.readyState === WebSocket.OPEN || this.instance.readyState === WebSocket.CONNECTING) {
            this.isConnected = false;
            this.instance.close();
        }
    }
}
