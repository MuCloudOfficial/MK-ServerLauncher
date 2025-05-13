<script setup lang="ts">
import "element-plus/es/components/notification/style/css";
import { type ComponentSize, ElNotification, type FormInstance, type FormRules } from "element-plus";
import { onMounted, reactive, ref } from "vue";
import { apiClient, ENV_LIST, SERVER_LIST, getServers,  } from "../Shared.vue";

let search = ref("")

onMounted(() => getServers())

const AvailableMCSType = [
  {value: "paper", label: "Paper & PaperSpigot", desc: "High Performance Server based on Spigot"},
  {value: "folia", label: "Folia" , desc: "High Performance Multi-Thread Server based on PaperSpigot"},
]
const AvailableMCSVersionLoading = ref(false)
const AvailableMCSVersion = ref([])

const AvailableJVMFlagsTemplate = [
  {value: 'none', label: 'No Flag', flag: ''},
  {value: 'aikar', label: "Aikar's Flags", flag: '-XX:+UseG1GC -XX:+ParallelRefProcEnabled -XX:MaxGCPauseMillis=200 -XX:+UnlockExperimentalVMOptions -XX:+DisableExplicitGC -XX:+AlwaysPreTouch -XX:G1NewSizePercent=30 -XX:G1MaxNewSizePercent=40 -XX:G1HeapRegionSize=8M -XX:G1ReservePercent=20 -XX:G1HeapWastePercent=5 -XX:G1MixedGCCountTarget=4 -XX:InitiatingHeapOccupancyPercent=15 -XX:G1MixedGCLiveThresholdPercent=90 -XX:G1RSetUpdatingPauseTimePercent=5 -XX:SurvivorRatio=32 -XX:+PerfDisableSharedMem -XX:MaxTenuringThreshold=1 -Dusing.aikars.flags=https://mcflags.emc.gs -Daikars.new.flags=true'},
  {value: 'custom', label: 'Custom', flag: ''},
]

const fetchMCSDetail = (value: any) => {
  ServerFormData.version = ''
  AvailableMCSVersionLoading.value = true
  apiClient.get(`https://api.papermc.io/v2/projects/${value}`).then(res => {
    if(!value.isEmptyValue){
      AvailableMCSVersion.value = res.data.versions.reverse()
      AvailableMCSVersionLoading.value = false
    }
  })
}

let onCreate = ref(false)
let onImport = ref(false)

interface ServerFormTemplate{
  name: string
  type: string
  port: number
  desc: string
  env: string
  version: string
  online: boolean
  whitelist: boolean
  max_player: number
  view_distance: number
  allow_nether: boolean
  spawn_protect: number
  jvm_flag_template: string
  jvm_aflags: string
  allow_gui: boolean
  minimum_mem: number
  maximum_mem: number
}

const ServerFormRules = reactive<FormRules<ServerFormTemplate>>({
  name: [
    { required: true, message: 'Please input Correct name', trigger: 'blur' },
    { min: 5, max: 20, message: 'Please input Correct Name', trigger: 'blur' }
  ],
  type: [
    { required: true, message: 'Please select Activity zone', trigger: 'change' },
  ],
  port: [
    { required: true, message: 'Please input Correct name', trigger: 'blur' }
  ],
  env: [
    { required: true, message: "Please Select a Valid Environment", trigger: "change" }
  ],
  version: [
    { required: true, message: "Please Select a Valid MC Server Version", trigger: "blur" },
  ],
  jvm_flag_template: [
    { required: true, message: "Please Select a Valid Flag", trigger: "blur" },
  ],
  max_player: [
    { required: true, message: "Please Select a Valid MC Server Max Player", trigger: "blur" },
    { validator: (rule, value, callback) => {
        if(!Number.isInteger(value)){
          callback(new Error('Please input a Number'))
        }else{
          if(value <= 0){
            callback(new Error('Do not set this under the 1'))
          }else{
            callback()
          }
        }
      }, trigger: 'blur'},
  ],
  view_distance: [
    { required: true, message: "Please Select a Valid MC Server View Distance", trigger: "blur" },
    { validator: (rule, value, callback) => {
        if(!Number.isInteger(value)){
          callback(new Error('Please input a Number'))
        }else{
          if(value < 4 || value > 10){
            callback(new Error('Do not set this in 4~10'))
          }else{
            callback()
          }
        }
      }, trigger: 'blur' },
  ],
  spawn_protect: [
    { required: true, message: "Please Select a Valid MC Server Spawn Protect Range", trigger: "blur" },
    { validator: (rule, value, callback) => {
        if(!Number.isInteger(value)){
          callback(new Error('Please input a Number'))
        }else{
          if(value < 0){
            callback(new Error('Do not set this under the 0'))
          }else{
            callback()
          }
        }
      }, trigger: 'blur'},
  ],
  maximum_mem: [
    { required: true, message: "Please input a Valid Number", trigger: "blur" },
    { validator: (rule, value, callback) => {
        if(!Number.isInteger(value)){
          callback(new Error('Please input a Number'))
        }else{
          if(value < ServerFormData.minimum_mem){
            callback(new Error('Do not set this less than Minimum Value'))
          }else{
            callback()
          }
        }
      }, trigger: 'blur'},
  ],
  minimum_mem: [
    { required: true, message: "Please input a Valid Number", trigger: "blur" },
    { validator: (rule, value, callback) => {
        if(!Number.isInteger(value)){
          callback(new Error('Please input a Number'))
        }else{
          if(value < 512){
            callback(new Error('Do not set this under the 512'))
          }else{
            callback()
          }
        }
      }, trigger: 'blur'},
  ],
})

let ServerFormData = reactive<ServerFormTemplate>({
  name: '',
  type: '',
  port: 25565,
  desc: '',
  env: '',
  version: '',
  online: true,
  whitelist: false,
  max_player: 20,
  view_distance: 10,
  allow_nether: true,
  spawn_protect: 10,
  jvm_flag_template: 'none',
  jvm_aflags: '',
  allow_gui: false,
  minimum_mem: 512,
  maximum_mem: 512,
})

const ServerFormSize = ref<ComponentSize>('default')
const ServerFormRef = ref<FormInstance>()

const submitForm = async (form: FormInstance | undefined, data: any) => {
  if(!form) return
  await form.validate((v, f) => {
    if (v) {
      sendCreateServerRequest(data).then(res => {
        if(res){
          console.log("submit!")
          ElNotification({
            title: 'Create Success',
            type: 'success',
            duration: 5000,
            offset: 100,
          })
          onCreate.value = false
        }else{
          console.log("Handled Error!")
        }
      })
    } else {
      console.log('error submit!', f)
    }
  })
}

const sendCreateServerRequest = async (data: any): Promise<boolean> => {
  return apiClient.post(`/api/v1/server/create`, data)
      .then(r => r.status === 200)
      .catch(e => {
        ElNotification({
          title: 'Create Error',
          message: e.response.data,
          type: 'error',
          duration: 5000,
          offset: 100,
        })
        return false
      }).finally(() => {
        getServers()
      })
}

const cancelCreateServer = () => {
  onCreate.value = false
  ServerFormData = reactive<ServerFormTemplate>({
    name: '',
    type: '',
    port: 25565,
    desc: '',
    env: '',
    version: '',
    online: true,
    whitelist: false,
    max_player: 20,
    view_distance: 10,
    allow_nether: true,
    spawn_protect: 10,
    jvm_flag_template: 'none',
    jvm_aflags: '',
    allow_gui: false,
    minimum_mem: 512,
    maximum_mem: 512,
  })
}

</script>

<template>
  <el-card>
    <el-button size="default" type="success" @click.capture="onCreate = true">
      <el-icon class="mr-2" size="15">
        <svg class="stroke-2 stroke-white" fill="none" stroke-linecap="round" stroke-linejoin="round" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
          <line x1="12" x2="12" y1="5" y2="19"/>
          <line x1="5" x2="19" y1="12" y2="12"/>
        </svg>
      </el-icon>
      Create
    </el-button>
    <el-button size="default" type="success" @click.capture="onImport = true">
      <el-icon class="mr-2" size="15">
        <svg class="stroke-2 stroke-white" fill="none" stroke-linecap="round" stroke-linejoin="round" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
          <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
          <polyline points="7 10 12 15 17 10"/>
          <line x1="12" x2="12" y1="15" y2="3"/>
        </svg>
      </el-icon>
      Import
    </el-button>
    <el-table :data="SERVER_LIST" stripe>
      <el-table-column prop="name" label="Name" min-width="100"/>
      <el-table-column prop="type" label="Type" min-width="100"/>
      <el-table-column prop="version" label="Version" min-width="80"/>
      <el-table-column prop="running" align="right" min-width="100">
        <template #header>
          <el-input v-model="search" size="default" placeholder="Type to search"/>
        </template>
        <template #default="scope">
          <el-button size="small" :disabled="scope.row.running" type="success" v-text="scope.row.running ? 'Running' : 'Start'"/>
          <el-button size="small" :disabled="!scope.row.running" type="warning" v-text="!scope.row.running ? 'Stopped' : 'Stop'"/>
          <el-button size="small" type="danger">Delete</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog
      v-model="onCreate"
      title="Create Server"
      width="600"
      align-center
    >
      <el-scrollbar
        height="800"
        class="p-4"
      >
        <el-form
            ref="ServerFormRef"
            :model="ServerFormData"
            :rules="ServerFormRules"
            :size="ServerFormSize"
            label-position="top"
            status-icon
        >
          <span class="text-lg font-bold">| Base</span>
          <el-form-item prop="name">
            <template #label><span class="text-base">Name</span></template>
            <el-input
                minlength="5"
                maxlength="20"
                show-word-limit
                v-model="ServerFormData.name"
                placeholder="Length limit in 5~20"/>
          </el-form-item>
          <el-form-item prop="port">
            <template #label><span class="text-base">Port</span></template>
            <el-input
                min="1024"
                max="65534"
                type="number"
                v-model="ServerFormData.port"
                placeholder="input a number in 1024~65534"/>
          </el-form-item>
          <el-form-item>
            <template #label><span class="text-base">Description</span></template>
            <el-input type="textarea" v-model="ServerFormData.desc"/>
          </el-form-item>
          <el-form-item prop="env">
            <template #label><span class="text-base">Environment</span></template>
            <el-select v-model="ServerFormData.env" :disabled="ENV_LIST.length === 0">
              <el-option v-for="i in ENV_LIST"
                         :key="i.env_name"
                         :label="`${i.env_name} (${i.env_version})`"
                         :value="i.env_name"
              />
            </el-select>
            <span v-show="ENV_LIST.length === 0" class="text-sm text-red-500">You not have any Environment! Import First.</span>
          </el-form-item>
          <el-form-item prop="type">
            <template #label><span class="text-base">Minecraft Server Type</span></template>
            <el-select v-model="ServerFormData.type" @change="fetchMCSDetail">
              <el-option v-for="a in AvailableMCSType" :label="a.label" :value="a.value">
                <span class="float-left">{{ a.label }}</span>
                <span class="ml-5 float-right text-(--el-text-color-secondary) text-[13px]">{{ a.desc }}</span>
              </el-option>
            </el-select>
            <span class="text-sm text-gray-400">Not listed you want? Attempt to <a class="underline" @click="onCreate = false; onImport = true">Import Server</a> using Custom Type MC Server</span>
          </el-form-item>
          <el-form-item prop="version">
            <template #label><span class="text-base">Minecraft Server Version</span></template>
            <el-select :disabled="AvailableMCSVersion.length == 0" v-model="ServerFormData.version" v-loading="AvailableMCSVersionLoading">
              <el-option v-for="a in AvailableMCSVersion" :label="a" :value="a"/>
            </el-select>
          </el-form-item>
          <span class="text-lg font-bold">| Startup</span><br/>
          <el-space class="w-full mt-3">
            <el-tooltip placement="right">
              <template #content><span class="text-green-500">Tip: 1024M = 1G</span></template>
              <span class="test-base mr-5">Memory<sup>*</sup></span>
            </el-tooltip>
            <el-form-item prop="minimum_mem">
              <template #label><span class="text-base">Minimum</span></template>
              <el-input-number style="width: 180px;" v-model="ServerFormData.minimum_mem">
                <template #suffix>M</template>
              </el-input-number>
            </el-form-item>
            <span class="text-lg font-bold mx-4">~</span>
            <el-form-item prop="maximum_mem">
              <template #label><span class="text-base">Maximum</span></template>
              <el-input-number style="width: 180px;" v-model="ServerFormData.maximum_mem">
                <template #suffix>M</template>
              </el-input-number>
            </el-form-item>
          </el-space>
          <el-form-item>
            <el-tooltip placement="right">
              <template #content>
                <span class="text-yellow-400">Warning! GUI Mode is not Necessary. Keep close is Recommended</span>
              </template>
              <span class="text-base mr-5">Show GUI<sup>*</sup></span>
            </el-tooltip>
            <el-switch v-model="ServerFormData.allow_gui"/>
          </el-form-item>
          <span class="text-lg font-bold mb-5">| Configuration</span>
          <el-form-item>
            <el-space :size="30" direction="horizontal">
              <span class="text-sm">Online Server <el-switch v-model="ServerFormData.online"/></span>
              <span class="text-sm">White List <el-switch v-model="ServerFormData.whitelist"/></span>
              <span class="text-sm">Allow Nether <el-switch v-model="ServerFormData.allow_nether"/></span>
            </el-space>
          </el-form-item>
          <el-form-item prop="max_player">
            <template #label><span class="text-base">Max Player</span></template>
            <el-input type="number" v-model="ServerFormData.max_player"/>
          </el-form-item>
          <el-form-item prop="view_distance">
            <template #label><span class="text-base">View Distance</span></template>
            <el-input type="number" v-model="ServerFormData.view_distance"/>
          </el-form-item>
          <el-form-item prop="spawn_protect">
            <template #label><span class="text-base">Spawn Protect Range</span></template>
            <el-input type="number" v-model="ServerFormData.spawn_protect"/>
          </el-form-item>
          <div class="mb-5">
            <div class="text-lg font-bold">| Advance Settings</div>
            <el-text type="danger" tag="b">Do not change if you not clear the usage about this setting.</el-text>
          </div>
          <el-form-item prop="jvm_flag_template">
            <template #label><span class="text-base">JVM Startup Flags</span></template>
            <el-select v-model="ServerFormData.jvm_flag_template" @change="(value: any) => { ServerFormData.jvm_aflags = AvailableJVMFlagsTemplate.find((v) => v.value == value)?.flag as string }">
              <el-option v-for="i in AvailableJVMFlagsTemplate" :value="i.value" :label="i.label"/>
            </el-select>
          </el-form-item>
          <el-form-item>
            <template #label><span class="text-base">Additional JVM Args</span></template>
            <el-input :disabled="ServerFormData.jvm_flag_template != 'custom'" type="textarea" v-model="ServerFormData.jvm_aflags"/>
          </el-form-item>
        </el-form>
      </el-scrollbar>
      <div class="w-full p-4">
        <div class="float-end">
          <el-link class="mr-3" type="primary" @click.prevent="cancelCreateServer">Cancel</el-link>
          <el-button type="primary" @click.prevent="submitForm(ServerFormRef, ServerFormData)">Create</el-button>
        </div>

      </div>
    </el-dialog>
    <el-dialog
      v-model="onImport"
      title="Import Server"
      width="600"
    >

    </el-dialog>

  </el-card>

</template>

<style scoped>

</style>