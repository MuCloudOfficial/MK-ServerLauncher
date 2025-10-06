import {ref} from "vue";
import {apiClient} from "@shared/shared.ts";

// START > Shared
export let ENV_LIST = ref()

export const getEnvs = () => { apiClient.get("/api/v1/env/list").then(res => {
    ENV_LIST.value = res.data
})}
// END > Shared

export interface MuEnv{
    EV_NAME: string,
    EV_VER: string,
    EV_VER_CODE: number,
    EV_EXEC: string
}