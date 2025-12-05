import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // 根路径重定向到登录页
    {
      path: '/',
      redirect: '/login'  // 重定向到登录页
    },
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/LoginPage.vue')
    }
    // 其他路由...
  ],
})
export default router
