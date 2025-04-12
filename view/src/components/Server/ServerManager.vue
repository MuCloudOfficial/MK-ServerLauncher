<script setup lang="ts">
import {ref} from "vue";

const instance = "ws://127.0.0.1:20038/api/v1/servers"
const ws = new WebSocket(instance)
let wsMsg = ref([])
let search = ref("")

ws.onmessage = (event) => {
  wsMsg.value = JSON.parse(event.data)
  console.log(wsMsg)
}

</script>

<template>
  <el-card>
    <el-button size="default" type="success">Create Server</el-button>
    <el-button size="default" type="success">Add Server</el-button>
    <el-table :data="wsMsg" stripe>
      <el-table-column prop="name" label="Name" min-width="100"/>
      <el-table-column prop="serverType" label="Type" min-width="100"/>
      <el-table-column prop="version" label="Version" min-width="80"/>
      <el-table-column align="right" min-width="100">
        <template #header>
          <el-input v-model="search" size="default" placeholder="Type to search"/>
        </template>
        <el-button size="small" type="success">Start</el-button>
        <el-button size="small" type="warning">Stop</el-button>
        <el-button size="small" type="primary">Edit</el-button>
        <el-button size="small" type="danger">Delete</el-button>
      </el-table-column>
    </el-table>
  </el-card>

</template>

<style scoped>

</style>