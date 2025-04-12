<script setup lang="ts">
import {ref} from "vue";
import Overview from "./components/Overview.vue";
import ServerManager from "./components/Server/ServerManager.vue";
import ServerBuilder from "./components/Server/ServerBuilder.vue";
import EnvironmentManager from "./components/Environment/EnvironmentManager.vue";
import EnvironmentBuilder from "./components/Environment/EnvironmentBuilder.vue";
import AdditionalViews from "./components/Plugins/AdditionalViews.vue";
import AdditionalMenus from "./components/Plugins/AdditionalMenus.vue";
import AboutPage from "./components/AboutPage.vue";
import Settings from "./components/Settings.vue";

let isCollapse = ref(true);
let currentLMISelected = ref("2-1"); // Current Selected Page
const whenLMISelected = (key: string) => {
  currentLMISelected.value = key;
  console.log(key)
}

const linkToDoc = () => {
  open("https://mucloudofficial.github.io/MuCloudProjectDoc","_blank", undefined)
}
</script>

<template>
<el-container class="h-lvh">
  <el-header class="main-nav" height="60px">
    <el-row class="h-full" align="middle">
      <img class="mr-5 h-16 w-16" src="./assets/icon.png" alt="LOGO" @click.prevent="isCollapse = !isCollapse">
      <p class="text-2xl mx-5">MK-ServerLauncher<sup>MPE</sup></p>
    </el-row>
  </el-header>
  <el-container class="main-view">
    <el-menu
        :default-active="currentLMISelected"
        class="left-menu"
        :collapse="isCollapse"
        @select="whenLMISelected"
    >
      <el-menu-item index="1">
        <el-icon size="32">
          <svg class="stroke-2 stroke-black" fill="none" stroke-linecap="round" stroke-linejoin="round" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
            <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"/>
          </svg>
        </el-icon>
        <template #title><span class="mx-4">Overview</span></template>
      </el-menu-item>
      <el-sub-menu index="2">
        <template #title>
          <el-icon size="32">
            <svg class="stroke-2 stroke-black" fill="none" stroke-linecap="round" stroke-linejoin="round" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
              <rect height="8" rx="2" ry="2" width="20" x="2" y="2"/>
              <rect height="8" rx="2" ry="2" width="20" x="2" y="14"/>
              <line x1="6" x2="6.01" y1="6" y2="6"/>
              <line x1="6" x2="6.01" y1="18" y2="18"/>
            </svg>
          </el-icon>
          <span class="mx-4">Server</span>
        </template>
        <el-menu-item index="2-1">Manage</el-menu-item>
        <el-menu-item index="2-2">Add & Create</el-menu-item>
        <el-menu-item index="2-3">Link & Server Group</el-menu-item>
      </el-sub-menu>
      <el-sub-menu index="3">
        <template #title>
          <el-icon size="32">
            <svg class="stroke-2 stroke-black" fill="none" stroke-linecap="round" stroke-linejoin="round" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
              <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"/>
              <polyline points="3.27 6.96 12 12.01 20.73 6.96"/>
              <line x1="12" x2="12" y1="22.08" y2="12"/>
            </svg>
          </el-icon>
          <span class="mx-4">Environment</span>
        </template>
        <el-menu-item index="3-1">Manage</el-menu-item>
        <el-menu-item index="3-2">Create</el-menu-item>
      </el-sub-menu>
      <el-menu-item index="4">
        <el-icon size="32">
          <svg class="stroke-2 stroke-black" fill="none" stroke-linecap="round" stroke-linejoin="round" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
            <circle cx="12" cy="12" r="3"/>
            <path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"/>
          </svg>
        </el-icon>
        <template #title><span class="mx-4">Setting</span></template>
      </el-menu-item>
      <el-divider/>
      <el-menu-item @click.capture="linkToDoc()"><!-- index="5" | Non set index to prevent Change Color when Clicked -->
        <el-icon size="32">
          <svg class="stroke-2 stroke-black" fill="none" stroke-linecap="round" stroke-linejoin="round" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
            <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/>
            <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/>
          </svg>
        </el-icon>
        <template #title>
          <span class="mx-4">
            WIKI
            <el-icon size="15">
              <svg class="stroke-2 stroke-gray-500" fill="none" stroke-linecap="round" stroke-linejoin="round" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                <path d="M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6"/>
                <polyline points="15 3 21 3 21 9"/>
                <line x1="10" x2="21" y1="14" y2="3"/>
              </svg>
            </el-icon>
          </span>
        </template>
      </el-menu-item>
      <el-menu-item index="6">
        <el-icon size="32">
          <svg class="stroke-2 stroke-black" fill="none" stroke-linecap="round" stroke-linejoin="round" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
            <circle cx="12" cy="12" r="10"/>
            <line x1="12" x2="12" y1="16" y2="12"/>
            <line x1="12" x2="12.01" y1="8" y2="8"/>
          </svg>
        </el-icon>
        <template #title><span class="mx-4">About</span></template>
      </el-menu-item>
      <AdditionalMenus></AdditionalMenus>
    </el-menu>
    <el-main>
      <el-scrollbar>
        <Overview v-show="currentLMISelected === '1'"/>
        <ServerManager v-show="currentLMISelected ==='2-1'"/>
        <ServerBuilder v-show="currentLMISelected === '2-2'"/>
        <EnvironmentManager v-show="currentLMISelected === '3-1'"/>
        <EnvironmentBuilder v-show="currentLMISelected === '3-2'"/>
        <AdditionalViews v-show="currentLMISelected === 'custom'"></AdditionalViews>
        <Settings v-show="currentLMISelected === '4'"/>
        <AboutPage v-show="currentLMISelected === '6'"/>
      </el-scrollbar>
    </el-main>
  </el-container>
</el-container>
</template>

<style scoped>
.el-menu--collapse{
  box-shadow: var(--el-box-shadow);
  border: 0;
}
.left-menu:not(.el-menu--collapse){
  width: 200px;
  min-height: 400px;
  box-shadow: var(--el-box-shadow);
  border: 0;
}
.main-nav{
  padding: 0;
  box-shadow: var(--el-box-shadow);
}
.main-view{
  height: calc(100dvh - 60px);
}
</style>
