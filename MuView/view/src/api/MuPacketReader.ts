import type { MuPacket } from "@api/MuPacket";

export class MuPacketReader {
    public static fromRaw(raw: string): MuPacket {
        return JSON.parse(raw) as MuPacket
    }

    public static safeFromRaw(raw: string): MuPacket | undefined {
        try {
            return this.fromRaw(raw)
        } catch {
            return undefined
        }
    }
}
