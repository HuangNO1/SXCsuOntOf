import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import { BootstrapVue, BootstrapVueIcons } from 'bootstrap-vue'
import axios from 'axios'
import VueAxios from 'vue-axios'
import VeLine from 'v-charts/lib/line.common'
import VueLocalStorage from 'vue-localstorage'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
import  mavonEditor from 'mavon-editor'
import 'mavon-editor/dist/css/index.css'

// mockJS
require('./mock/mock')

Vue.use(BootstrapVue)
Vue.use(BootstrapVueIcons)
Vue.use(VueAxios, axios)
Vue.use(mavonEditor)
Vue.use(VueLocalStorage)

Vue.component(VeLine.name, VeLine)

Vue.config.productionTip = false

new Vue({
    router,
    store,
    render: h => h(App)
}).$mount('#app')