import { createRouter, createWebHistory } from 'vue-router'
import store from '@/store'
import adminRoutes from './admin'

const routes = [
  // 管理员路由
  ...adminRoutes,
  
  // 原有用户路由
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/Register.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/reset-password',
    name: 'ResetPassword',
    component: () => import('@/views/auth/ResetPassword.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/user/Profile.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/edit-profile',
    name: 'EditProfile',
    component: () => import('@/views/user/EditProfile.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/user/:id',
    name: 'UserProfile',
    component: () => import('@/views/user/UserProfile.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/search',
    name: 'Search',
    component: () => import('@/views/Search.vue'),
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const isLoggedIn = store.getters['user/isLoggedIn']
  const isAdminLoggedIn = store.getters['admin/isLoggedIn']
  
  // 检查用户认证
  if (to.meta.requiresAuth && !isLoggedIn) {
    next('/login')
    return
  }
  
  // 检查管理员认证
  if (to.meta.requiresAdminAuth && !isAdminLoggedIn) {
    next('/admin/login')
    return
  }
  
  // 已登录用户访问登录页重定向
  if ((to.path === '/login' || to.path === '/register') && isLoggedIn) {
    next('/home')
    return
  }
  
  // 已登录管理员访问登录页重定向
  if (to.path === '/admin/login' && isAdminLoggedIn) {
    next('/admin/dashboard')
    return
  }
  
  // 权限检查（管理员路由）
  if (to.meta.permission && isAdminLoggedIn) {
    const hasPermission = store.dispatch('admin/hasPermission', to.meta.permission)
    if (!hasPermission) {
      next('/admin/dashboard')
      return
    }
  }
  
  next()
})

export default router
