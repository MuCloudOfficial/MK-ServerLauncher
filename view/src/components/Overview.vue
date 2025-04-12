<script setup lang="ts">
import {onMounted, ref, shallowRef} from "vue";
import {useTransition} from "@vueuse/core";
import {MuWebSocket} from "./Shared.vue";

onMounted(() => {
  const ws = new MuWebSocket("Overview")
  let wsMsg = ref({})
  if(ws.getMsg() != undefined){
    wsMsg.value = ws.getMsg()
    processCoreData(wsMsg)
  }
})

let OnlineServerCount = shallowRef(0)
let StoppedServerCount = shallowRef(0)
let TotalServerCount = shallowRef(0)

function processCoreData(msg: any){
  TotalServerCount.value = msg.MuServer.Total
  OnlineServerCount.value = msg.MuServer.Running
  StoppedServerCount.value = TotalServerCount.value - OnlineServerCount.value
}

let OnlineServerCountAnime = useTransition(
    OnlineServerCount,
    { duration: 1500 }
)
let StoppedServerCountAnime = useTransition(
    StoppedServerCount,
    { duration: 1500 }
)
let TotalServerCountAnime = useTransition(
    TotalServerCount,
    { duration: 1500 }
)

// Watching Viewport Width to switch Columns Number
let cols = ref("")
addEventListener("resize", () => {
  if(window.innerWidth < 768){
    cols.value = ""
  }else{
    cols.value = "grid-cols-2"
  }
})

</script>

<template>
  <div class="grid gap-4" :class="cols">
    <el-card class="h-55">
      <template #header>Overview</template>
      <div class="flex flex-row gap-5 justify-around items-center">
        <el-progress type="dashboard">
          <template #default="{ percentage }">
            <span class="block text-xl">{{percentage}}%</span>
            <span class="block text-base">CPU</span>
          </template>
        </el-progress>
        <el-progress type="dashboard">
          <template #default="{ percentage }">
            <span class="block text-xl">{{percentage}}%</span>
            <span class="block text-base">Memory</span>
          </template>
        </el-progress>
      </div>
    </el-card >
    <el-card class="h-55" body-class="h-2/3">
      <template #header>Servers</template>
      <div class="flex flex-row gap-6 w-full justify-around items-center h-full">
        <div class="text-center text-lg">
          <el-statistic :value="OnlineServerCountAnime" class="mb-3"/>
          <span>Online</span>
        </div>
        <div class="text-center text-lg">
          <el-statistic :value="StoppedServerCountAnime" class="mb-3"/>
          <span>Stopped</span>
        </div>
        <div class="text-center text-lg">
          <el-statistic :value="TotalServerCountAnime" class="mb-3"/>
          <span>Total</span>
        </div>
      </div>
    </el-card>
    <el-card class="h-55">
      <template #header>Core Info</template>
      <div><span class="font-bold">Core:</span> MuCore MPE </div>
      <div><span class="font-bold">Version:</span> VoidLand V2 DEV 16 </div>
      <div><span class="font-bold">Plugin Count:</span> 0 </div>
      <div><span class="font-bold">TemplatePack Count:</span> 2 </div>
    </el-card>
  </div>
</template>

<style scoped>

</style>