import axios, {type AxiosInstance} from "axios";
import type {MuPacket} from "@api/MuPacket.ts";

type MuCoreSite = {
    protocol: string,
    host: string,
    port: string,
}

const currentSite = (): MuCoreSite => {
    let { protocol, host, port } = window.location
    return { protocol, host, port }
}

export class MuHTTPClient{
    private readonly base: AxiosInstance

    constructor() {
        let site = currentSite()
        this.base = axios.create({
            baseURL: `${site.protocol}://${site.host}`,
            timeout: 30000,
            withCredentials: true,
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
            },
        })
    }

    public async get(api: string): Promise<MuPacket>{
        return await this.base.get<MuPacket>(api).then(i => {
            return i.data
        })
    }
}

export class MuWSClient{
    private readonly base: WebSocket
    private finalMSG: MuPacket | undefined

    constructor() {
        let site = currentSite()
        let wsProtocol = site.protocol == "http" ? "ws" : "wss"
        this.base = new WebSocket(`${wsProtocol}://${site.host}:${site.port}`)
        this.base.onopen = (e) => {
            console.log("WebSocket Connected >> " + e)
        }
        this.base.onerror = (e) => {
            console.log("Websocket Occurred an Error! >> " + e.type.toString())
            this.base.close()
        }
        this.base.onmessage = (e) => {
            this.finalMSG = e.data
        }
        this.base.onclose = (e) => {
            console.log("Websocket Closed! >> " + e.reason.toString())
        }
    }

    public getMsg(): MuPacket | undefined {
        if(this.isConnected()){
            return this.finalMSG
        }else{
            return undefined
        }
    }

    public isConnected(): boolean{
        return this.base.readyState == this.base.OPEN
    }

    public send(jsonMsg: any): any {
        if(this.base && this.isConnected()){
            this.base.send(JSON.stringify(jsonMsg))
            return this.getMsg()
        }else{
            console.warn("Error occurred while send MSG to MuCore, probably MuCore OFFLINE")
        }
    }

    public close(){
        if(this.isConnected()){
            this.base.close()
        }
    }
}