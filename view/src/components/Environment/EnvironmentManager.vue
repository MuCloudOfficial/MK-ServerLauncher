<script setup lang="ts">
import {ref} from "vue"
import { apiClient } from "../Shared.vue";
import { ENV_LIST } from "../Shared.vue";
let search = ref("")

apiClient.get("/api/v1/envs", {
  headers: { "Accept": "application/json" }
}).then(res => ENV_LIST.value = res.data)

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
      Import
    </el-button>
    <el-table :data="ENV_LIST" stripe>
      <el-table-column prop="env_name" label="Name" min-width="100"/>
      <el-table-column prop="env_version" label="Version" min-width="80"/>
      <el-table-column prop="env_path" label="Path" min-width="100"/>
    </el-table>
  </el-card>

</template>

<style scoped>

</style>