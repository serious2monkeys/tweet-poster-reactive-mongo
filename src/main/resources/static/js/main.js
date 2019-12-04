import Vue from 'vue'
import VueResource from 'vue-resource'
import App from 'pages/App.vue'
import Login from "pages/Login.vue"
import 'vuetify/dist/vuetify.min.css'
import Datetime from 'vue-datetime'
import 'vue-datetime/dist/vue-datetime.css'
import { Settings } from 'luxon'
import Vuetify from "vuetify"

Vue.use(Vuetify);
Vue.use(VueResource);
Vue.use(Datetime)
Settings.defaultLocale = 'ru'

new Vue({
    el: '#app',
    vuetify: new Vuetify({}),
    render: a => a(App)
});

new Vue({
    el: '#login',
    vuetify: new Vuetify({}),
    render: a => a(Login)
});