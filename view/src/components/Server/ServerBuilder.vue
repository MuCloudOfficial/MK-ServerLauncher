<script setup lang="ts">
import {reactive, ref} from "vue";
import type {ComponentSize, FormInstance, FormRules} from "element-plus";

let step = ref(0)

const steps = reactive([ //Fixed Steps Route
  {
    name: "Basic",
    desc: "Complete Basic Information",
  }, {
    name: "Storage & Environment",
    desc: "Define Server Location & Environment",
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

// Step Template
interface Step0FormTemplate{
  serverName: string
  serverType: number
  serverPort: number
}

interface Step1FormTemplate{
  serverEnv: {
    envName: string;
    envPath: string;
  }
  serverFolder: string
}

// Step Form Rules
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

// Steps Form Data
const Step0FormData = reactive<Step0FormTemplate>({
  serverName: '',
  serverType: 0,
  serverPort: 25565,
})

const Step1FormData = reactive<Step1FormTemplate>({
  serverEnv: {
    envName: "",
    envPath: ""
  },
  serverFolder: ""
})

// Steps Form Size
const Step0FormSize = ref<ComponentSize>('default')
const Step1FormSize = ref<ComponentSize>('default')

// Steps Form Ref
const Step0Form = ref<FormInstance>()

const submitForm = async (step: number, form: FormInstance | undefined) => {
  if(!form) return
  await form.validate((v, f) => {
    if(v){
      console.log('submit!')
      if(sendCreateServerRequest(step)){
        process()
      }else{

      }
    }else{
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

const sendCreateServerRequest = (step: number): boolean => {
  let res
  fetch(`api/v1/server/create/${Step0FormData.serverName}/${step}`, {
    headers: { "Accept": "application/json" }

  }).then(msg => res = msg.status)
  return res == 200
}

</script>

<template>
<el-card>
  <el-steps align-center :active="step" finish-status="success">
    <el-step v-for="i in steps" :title="i.name" :description="i.desc"/>
  </el-steps>
  <div class="my-10 w-100 mx-auto">
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
        v-show="step == 1"
        ref="Step1Form"
        :model="Step1FormData"
        :size="Step1FormSize"
        label-position="top"
        status-icon>

    </el-form>
  </div>
  <div class="w-full my-5 text-center" v-show="step == 0">Already have a Server? Please <RouterLink class="font-bold underline" to="/server/import">Import Server</RouterLink>.</div>
</el-card>
</template>

<style scoped>

</style>