import {ref} from "vue";
import {apiClient} from "@shared/shared.ts";
import {useStorage} from "@vueuse/core";

// START > Shared
export let SERVER_LIST = ref()

export const getServers = () => { apiClient.get("/api/v1/server/list").then(res => {
    SERVER_LIST.value = res.data
})}

export let usingServers = useStorage<Array<string>>('using-servers', [])
// END > Shared

let MuServer: {
    server_name: string,
    port: number,

}

