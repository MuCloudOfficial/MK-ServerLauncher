import {ref} from "vue";
import {MuHTTPClient} from "@api/MuCoreConnector";

export let ENV_LIST = ref()

export const getEnvs = () => {
    new MuHTTPClient().get("/api/v1/env/list").then(r => {
        ENV_LIST.value = r.MP_DATA
    })
}

getEnvs()
