<script lang="ts">
export class MuWebSocket {
  private backend = "ws://127.0.0.1"
  private port = 20038/*document.URL.split(new RegExp("/+"))[1].split(":")[1]*/
  private readonly instance: WebSocket;
  private msg: any
  private isConnected = false

  constructor(path: String) {
    this.instance = new WebSocket(`${this.backend}:${this.port}/${path}`)
    this.instance.onopen = (e) => {
      console.log("WebSocket Connected >> " + e)
      this.isConnected = true
    }
    this.instance.onerror = (e) => {
      console.log("Websocket Occurred an Error! >> " + e.type.toString())
      this.instance.close()
    }
    this.instance.onmessage = (e) => {
      this.msg = e.data
    }
    this.instance.onclose = (e) => {
      console.log("Websocket Closed! >> " + e.reason.toString())
      this.isConnected = false
    }
  }

  public getMsg(): any {
    if(this.isConnected){
      return JSON.parse(this.msg)
    }else{
      return undefined
    }
  }

  public isConnect(): boolean{
    return this.isConnected
  }

  public send(jsonMsg: any): any {
    if(this.instance && this.instance.readyState === WebSocket.OPEN){
      this.instance.send(JSON.stringify(jsonMsg))
      return this.getMsg()
    }else{
      console.warn("Warn in send MSG to MuCore, probably MuCore is OFFLINE")
    }
  }

  public close(){
    if(this.isConnected){
      this.isConnected = false
      this.instance.close()
    }
  }
}
</script>
