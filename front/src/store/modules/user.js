import { login, logout } from '@/api/auth'
import { getUserProfile } from '@/api/user'
import { getToken, setToken, removeToken } from '@/utils/auth'

const state = {
  token: getToken(),
  userInfo: null,
  isLoggedIn: false
}

const mutations = {
  SET_TOKEN: (state, token) => {
    state.token = token
  },
  SET_USER_INFO: (state, userInfo) => {
    state.userInfo = userInfo
    state.isLoggedIn = true
  },
  CLEAR_USER: (state) => {
    state.token = null
    state.userInfo = null
    state.isLoggedIn = false
  }
}

const actions = {
  // 用户登录
  login({ commit }, loginForm) {
    return new Promise((resolve, reject) => {
      login(loginForm).then(response => {
        const { data } = response
        commit('SET_TOKEN', data.token)
        setToken(data.token)
        commit('SET_USER_INFO', data.userInfo)
        resolve(data)
      }).catch(error => {
        reject(error)
      })
    })
  },

  // 用户登出
  logout({ commit }) {
    return new Promise((resolve, reject) => {
      logout().then(() => {
        commit('CLEAR_USER')
        removeToken()
        resolve()
      }).catch(error => {
        reject(error)
      })
    })
  },

  // 获取用户信息
  getInfo({ commit, state }) {
    return new Promise((resolve, reject) => {
      if (!state.token) {
        reject(new Error('未登录'))
        return
      }
      
      getUserProfile().then(response => {
        const { data } = response
        commit('SET_USER_INFO', data)
        resolve(data)
      }).catch(error => {
        reject(error)
      })
    })
  },

  // 重置用户状态
  resetUser({ commit }) {
    commit('CLEAR_USER')
    removeToken()
  }
}

const getters = {
  token: state => state.token,
  userInfo: state => state.userInfo,
  isLoggedIn: state => state.isLoggedIn,
  userId: state => state.userInfo?.id,
  username: state => state.userInfo?.username,
  nickname: state => state.userInfo?.nickname,
  avatar: state => state.userInfo?.avatar
}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}
