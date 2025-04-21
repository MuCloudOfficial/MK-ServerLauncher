<script lang="ts">
export default {
  name: 'EnvironmentManager'
}

import {ref} from "vue"
export let ENV_LIST = ref()
</script>

<script setup lang="ts">
let search = ref("")

fetch("http://localhost:20038/api/v1/envs", {
  headers: { "Accept": "application/json" }
}).then(res => res.json().then(j => ENV_LIST.value = j))

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