<script setup lang="ts">
import {ref} from "vue";
import { apiClient } from "../Shared.vue";

let search = ref("")
let SERVER_LIST = ref()

const getServers = () => { apiClient.get("api/v1/servers").then(res => {
  SERVER_LIST.value = res.data
})}

</script>

<template>
  <el-card>
    <el-button size="default" type="success" @click.capture="$router.push('/server/create')">
      <el-icon class="mr-2" size="15">
        <svg class="stroke-2 stroke-white" fill="none" stroke-linecap="round" stroke-linejoin="round" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
          <line x1="12" x2="12" y1="5" y2="19"/>
          <line x1="5" x2="19" y1="12" y2="12"/>
        </svg>
      </el-icon>
      Create
    </el-button>
    <el-button size="default" type="success" @click.capture="$router.push('/server/import')">
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
      <el-table-column prop="serverType" label="Type" min-width="100"/>
      <el-table-column prop="version" label="Version" min-width="80"/>
      <el-table-column prop="running" align="right" min-width="100">
        <template #header>
          <el-input v-model="search" size="default" placeholder="Type to search"/>
        </template>
        <template #default="scope">
          <el-button size="small" :disabled="scope.row.running" type="success" v-text="scope.row.running ? 'Running' : 'Start'"/>
          <el-button size="small" :disabled="!scope.row.running" type="warning" v-text="!scope.row.running ? 'Stopped' : 'Stop'"/>
          <el-button size="small" type="primary">Edit</el-button>
          <el-button size="small" type="danger">Delete</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>

</template>

<style scoped>

</style>