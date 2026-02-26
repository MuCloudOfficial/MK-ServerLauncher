import axios, {type AxiosInstance} from "axios";

type MuCoreSite = {
    protocol: string,
    host: string,
}

const currentSite = (): MuCoreSite => {
    let protocol = "http:"
    let host = "localhost:20038"
    return { protocol, host }
}

console.log(`Current Site: ${currentSite().protocol}//${currentSite().host}`)

export class MuHTTPClient{
    private readonly base: AxiosInstance

    constructor() {
        let site = currentSite()
        this.base = axios.create({
            baseURL: `${site.protocol}//${site.host}/`,
            allowAbsoluteUrls: true,
            timeout: 30000,
            headers: {
                "Content-Type": "application/json",
            },
        })
    }

    public async get(api: string): Promise<any>{
        return await this.base.get<any>(api).then(i => {
            return i.data
        })
    }

    public async post(api: string, data: any): Promise<any>{
        return await this.base.post<any>(api, data).then(i => {
            return i.data
        })
    }
}

export class MuWSConnection{
    private readonly base: WebSocket
    private finalMSG: any

    constructor(api: string) {
        let site = currentSite()
        let wsProtocol = site.protocol == "http:" ? "ws" : "wss"
        console.log(`${wsProtocol}://${site.host}/${api}`)
        this.base = new WebSocket(`${wsProtocol}://${site.host}/${api}`)
        this.base.onopen = (e) => {
            console.log("WebSocket Connected >> " + e)
        }
        this.base.onerror = (e) => {
            console.log("Websocket Occurred an Error! >> " + e.type.toString())
            this.base.close()
        }
        this.base.onmessage = (e) => {
            this.finalMSG = e.data
            console.log(e.data)
            console.log(this.finalMSG)
            console.log("Receive?")
        }
        this.base.onclose = (e) => {
            console.log("Websocket Closed! >> " + e.reason.toString())
        }
    }

    public getMsg(): any {
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