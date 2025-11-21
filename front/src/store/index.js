import { createStore } from 'vuex'
import user from './modules/user'
import admin from './modules/admin'

export default createStore({
  modules: {
    user,
    admin
  }
})
