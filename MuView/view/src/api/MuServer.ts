import {ref} from "vue";
import {MuHTTPClient} from "@api/MuCoreConnector";
import {useStorage} from "@vueuse/core";

// START > Shared
export let SERVER_LIST = ref()

export const getServers = () => {
    new MuHTTPClient().get("/api/v1/server/list").then(r =>
        SERVER_LIST.value = r.MP_DATA
    )
}

export let usingServers = useStorage<Array<string>>('using-servers', [])
// END > Shared

