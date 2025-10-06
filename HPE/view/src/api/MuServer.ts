import {ref} from "vue";
import {apiClient} from "@shared/shared.ts";
import {useStorage} from "@vueuse/core";
import type {MuEnv} from "@api/MuEnv.ts";

// START > Shared
export let SERVER_LIST = ref()

export const getServers = () => { apiClient.get("/api/v1/server/list").then(res => {
    SERVER_LIST.value = res.data
})}

export let usingServers = useStorage<Array<string>>('using-servers', [])
// END > Shared

export interface MuServer{
    SV_NAME: string
    SV_PORT: number
    SV_DESC: string
    SV_VER: string
    SV_RUNNING: boolean
    SV_CONF: MuServerConf
    SV_BE_WORKS: string[]
    SV_EV: MuEnv
    SV_FOLDER: string
    SV_LST_LAUNCH: Date
}

export interface MuServerConf{
    MSC_KV: {key: string, value: any}[]
    MSC_VER_CODE: number
    MSC_VER: string
}

