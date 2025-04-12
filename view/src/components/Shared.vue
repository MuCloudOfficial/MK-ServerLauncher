<script lang="ts">
export class MuWebSocket{
  private backend = "ws://127.0.0.1"
  private port = document.URL.split(new RegExp("/+"))[1].split(":")[1]
  private instance: WebSocket;
  private msg: any
  public getMsg(): any{
    return this.msg
  }

  constructor(path: String) {
    this.instance = new WebSocket(`${this.backend}:${this.port}/api/v1/${path}`)
    this.instance.onerror = (e) => {
      console.log("Websocket Occurred an Error! >> " + e.type)
      this.instance.close()
    }
    this.instance.onmessage = (e) => {
      this.msg = e.data
    }
  }
}
</script>
