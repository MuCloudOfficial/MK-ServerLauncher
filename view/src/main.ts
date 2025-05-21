import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import 'element-plus/theme-chalk/dark/css-vars.css'

import Overview from "./components/Overview.vue";
import ServerManager from "./components/Server/ServerManager.vue";
import EnvironmentManager from "./components/EnvironmentManager.vue";
import AboutPage from "./components/AboutPage.vue";
import Settings from "./components/Settings.vue";
import {createRouter, createWebHistory} from "vue-router";
import ServerPage from "./components/Server/ServerPage.vue";

const routes = [
    { path: '/', component: Overview },
    { path: '/servermanager', component: ServerManager },
    { path: '/server/:name', component: ServerPage, props: true },
    { path: '/envmanager', component: EnvironmentManager },
    { path: '/about', component: AboutPage },
    { path: '/settings', component: Settings },
]

const router = createRouter({
    history: createWebHistory(),
    routes,
})

createApp(App)
    .use(router)
    .mount('#app')
