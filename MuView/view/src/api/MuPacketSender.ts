import type { MuPacket } from "@api/MuPacket";

export class MuPacketSender {
    private readonly MP_ID: string

    constructor(mpid: string) {
        this.MP_ID = mpid
    }

    public muPack(data: any): MuPacket{
        return {
            MP_ID: this.MP_ID,
            MP_DATA: data
        }
    }
}
