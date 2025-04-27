<script setup lang="ts">
import "element-plus/es/components/notification/style/css";
import {reactive, ref} from "vue";
import {
  type ComponentSize,
  ElNotification,
  type FormInstance,
  type FormRules
} from "element-plus";
import { apiClient } from "../Shared.vue";
import { ENV_LIST } from "../EnvironmentManager.vue";

let step = ref(0)

const steps = reactive([ //Fixed Steps Route
  {
    name: "Basic",
    desc: "Complete Basic Information",
  }, {
    name: "Environment",
    desc: "Define Server Environment",
  }, {
    name: "Detail",
    desc: "Complete Detail Information",
  }, {
    name: "Permission",
    desc: "Add the user to manage this server",
  }, {
    name: "Setup",
    desc: "Just Wait and enjoy new server",
  },
])

const AvailableType = [ /* Fetch from MuCore */ //TODO
  {
    value: "mc",
    label: "Minecraft/Java Edition",
  }
]

// Step 0 Template
interface Step0FormTemplate{
  serverName: string
  serverType: string
  serverPort: number
}

const Step0FormRules = reactive<FormRules<Step0FormTemplate>>({
  serverName: [
    { required: true, message: 'Please input Correct name', trigger: 'blur' },
    { min: 5, max: 20, message: 'Please input Correct Name', trigger: 'blur' }
  ],
  serverType: [
    {
      required: true,
      message: 'Please select Activity zone',
      trigger: 'change',
    },
  ],
  serverPort: [
    {
      required: true,
      message: 'Please input Correct name',
      trigger: 'blur'
    }
  ]
})

const Step0FormData = reactive<Step0FormTemplate>({
  serverName: '',
  serverType: "mc",
  serverPort: 25565,
})

const Step0FormSize = ref<ComponentSize>('default')
const Step0Form = ref<FormInstance>()

// Step 1 Template
interface Step1FormTemplate{
  serverEnv: string
}

const Step1FormData = reactive<Step1FormTemplate>({
  serverEnv: ''
})

const Step1FormRule = reactive<FormRules<Step1FormTemplate>>({
  serverEnv: [
    { required: true, trigger: "change" }
  ]
})

const Step1FormSize = ref<ComponentSize>('default')
const Step1Form = ref<FormInstance>()

const submitForm = async (step: number, form: FormInstance | undefined) => {
  if(!form) return
  await form.validate((v, f) => {
    if (v) {
      sendCreateServerRequest(step).then(res => {
        if(res){
          console.log("submit!")
          process()
        }else{
          console.log("Handled Error!")
        }
      })
    } else {
      console.log('error submit!', f)
    }
  })
}

const process = () => {
  if(step.value++ > steps.length){
    step.value = steps.length
  }
}

const reverse = () => {
  if(step.value-- < 0){
    step.value = 0
  }
}

const sendCreateServerRequest = async (step: number): Promise<boolean> => {
  return apiClient.post(`/api/v1/server/create/${Step0FormData.serverName}/${step}`,)
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
      })
}

</script>

<template>
<el-card>
  <el-steps align-center :active="step" finish-status="success">
    <el-step v-for="i in steps" :title="i.name" :description="i.desc"/>
  </el-steps>
  <div class="my-10 w-100 mx-auto">
<!--    Step 1 Form Area-->
    <el-form
        v-show="step == 0"
        ref="Step0Form"
        :model="Step0FormData"
        :rules="Step0FormRules"
        :size="Step0FormSize"
        label-position="top"
        status-icon>
      <el-form-item prop="serverName">
        <template #label><span class="text-base">Name</span></template>
        <el-input
            minlength="5"
            maxlength="20"
            show-word-limit
            v-model="Step0FormData.serverName"
            placeholder="Length limit in 5~20"/>
      </el-form-item>
      <el-form-item prop="serverType">
        <template #label><span class="text-base">Type</span></template>
        <el-select v-model="Step0FormData.serverType">
          <el-option
              v-for="i in AvailableType"
              :label="i.label"
              :value="i.value"/>
        </el-select>
      </el-form-item>
      <el-form-item prop="serverPort">
        <template #label><span class="text-base">Port</span></template>
        <el-input
            min="1024"
            max="65534"
            type="number"
            v-model="Step0FormData.serverPort"
            placeholder="input a number in 1024~65534"/>
      </el-form-item>
      <el-form-item>
        <div class="w-full flex flex-row justify-center">
          <el-button type="primary"
                     @click="submitForm(0, Step0Form)">
            Next
          </el-button>
        </div>
      </el-form-item>
    </el-form>
<!--    Step 2 Form Area -->
    <el-form
        v-show="step == 1 && ENV_LIST.length != 0"
        ref="Step1Form"
        :model="Step1FormData"
        :size="Step1FormSize"
        label-position="top"
        status-icon>
      <el-form-item prop="serverEnv">
        <template #label><span class="text-base">Environment</span></template>
        <el-select v-model="Step1FormData.serverEnv">
          <el-option v-for="i in ENV_LIST"
                     :key="i.env_name"
                     :label="`${i.env_name} (${i.env_version})`"
                     :value="i.env_name"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <div class="w-full flex flex-row justify-center">
          <el-button type="warning"
                     @click="reverse">
            Previous
          </el-button>
          <el-button type="primary"
                     @click="submitForm(1, Step1Form)">
            Next
          </el-button>
        </div>
      </el-form-item>
    </el-form>
    <div v-show="step == 1 && ENV_LIST.length === 0" class="mx-auto my-5">
      <span class="text-2xl font-bold">You not have any Environment!</span>
      <div class="my-5 w-full flex flex-row justify-center" v-if="true"><!-- TODO -->
        <RouterLink to="/envmanager"><span class="text-xl underline">Please import a Environment before.</span></RouterLink>
      </div>
    </div>
<!--    Step 3 Form Area-->
    <el-form
        v-show="step == 2"
    >
      <el-form-item>

      </el-form-item>
    </el-form>
  </div>
  <div class="w-full my-5 text-center" v-show="step == 0">Already have a Server? Please <RouterLink class="font-bold underline" to="/server/import">Import Server</RouterLink>.</div>
</el-card>
</template>

<style scoped>

</style>