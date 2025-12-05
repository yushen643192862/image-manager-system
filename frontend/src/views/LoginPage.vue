<template>
  <div class="login-page">
    <!-- 背景模糊层 -->
    <div class="bg-blur" :style="bgStyle"></div>
    <!-- 登录容器 -->
    <div class="login-container">
      <div class="login-box" @mouseenter="onBoxHover(true)" @mouseleave="onBoxHover(false)">
        <!-- Logo和标题 -->
        <div class="logo-area">
          <div class="logo">
            <img src="@/assets/image.png" alt="Logo" width="128" height="122">
          </div>
          <h1>登录</h1>
          <p>实验项目 v1.0</p>
        </div>

        <!-- 登录表单 -->
        <form @submit.prevent="handleLogin" class="login-form">
          <!-- 邮箱输入 -->
          <div class="form-group" :class="{ 'error': emailError }">
            <label class="form-label" for="email">邮箱地址</label>
            <div class="input-with-icon">
              <i class="fas fa-envelope input-icon"></i>
              <input
                type="email"
                id="email"
                v-model="formData.email"
                class="form-input"
                placeholder="example@email.com"
                @blur="validateEmail"
                @input="clearError('email')"
                required
                autocomplete="email"
              >
            </div>
            <div v-if="emailError" class="error-message">{{ emailError }}</div>
          </div>

          <!-- 密码输入 -->
          <div class="form-group" :class="{ 'error': passwordError }">
            <label class="form-label" for="password">密码</label>
            <div class="input-with-icon">
              <i class="fas fa-lock input-icon"></i>
              <input
                :type="showPassword ? 'text' : 'password'"
                id="password"
                v-model="formData.password"
                class="form-input"
                placeholder="至少6位字符"
                @blur="validatePassword"
                @input="clearError('password')"
                required
                autocomplete="current-password"
                minlength="6"
              >
              <button
                type="button"
                class="password-toggle"
                @click="togglePasswordVisibility"
                tabindex="-1"
                :title="showPassword ? '隐藏密码' : '显示密码'"
              >
                <i :class="showPassword ? 'fas fa-eye-slash' : 'fas fa-eye'"></i>
              </button>
            </div>
            <div v-if="passwordError" class="error-message">{{ passwordError }}</div>
          </div>
          <div class="remember-forgot-row">
            <!-- 记住我 -->
            <label class="remember-label">
              <input
                type="checkbox"
                v-model="formData.rememberMe"
                class="remember-checkbox"
              >
              <span class="remember-text">记住我</span>
            </label>

            <!-- 忘记密码 -->
            <a href="#" class="forgot-link" @click.prevent="handleForgotPassword">
              忘记密码？
            </a>
          </div>

          <!-- 登录按钮 -->
          <button
            type="submit"
            class="login-btn"
            :disabled="isLoading"
            :class="{ 'loading': isLoading }"
          >
            <span v-if="!isLoading">登录</span>
            <span v-else class="loader"></span>
          </button>

          <!-- 注册提示 -->
          <div class="register-area">
            <span class="register-text">还没有账号？</span>
            <button
              type="button"
              class="register-link"
              @click="goToRegister"
              :disabled="isLoading"
            >
              立即注册
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'

// 路由
const router = useRouter()

// 响应式数据
const formData = reactive({
  email: '',
  password: '',
  rememberMe: false
})

const showPassword = ref(false)
const isLoading = ref(false)
const emailError = ref('')
const passwordError = ref('')
const isBoxHovered = ref(false)

import backgroundImage from '@/assets/login_img.png'
// 背景图片样式
const bgStyle = computed(() => ({
  backgroundImage: `url(${backgroundImage})`,
  filter: 'blur(20px) brightness(0.8)',
  opacity: isBoxHovered.value ? 0.8 : 0.7
}))

// 方法
const validateEmail = () => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!formData.email) {
    emailError.value = '请输入邮箱地址'
  } else if (!emailRegex.test(formData.email)) {
    emailError.value = '请输入有效的邮箱地址'
  } else {
    emailError.value = ''
  }
}

const validatePassword = () => {
  const CharsRegex = /^[A-Za-z0-9_]+$/
  if (!formData.password) {
    passwordError.value = '请输入密码'
  } else if (formData.password.length < 6) {
    passwordError.value = '密码至少6位字符'
  } else if (!CharsRegex.test(formData.password)) {
    passwordError.value = '密码只能包含大小写字母、数字和下划线'
  } else {
    passwordError.value = ''
  }
}

const clearError = (field: 'email' | 'password') => {
  if (field === 'email') emailError.value = ''
  if (field === 'password') passwordError.value = ''
}

const togglePasswordVisibility = () => {
  showPassword.value = !showPassword.value
}
// 验证表单
const handleLogin = async () => {

  validateEmail()
  validatePassword()

  if (emailError.value || passwordError.value) {
    return
  }

  isLoading.value = true

  try {
    // 模拟API请求
    await new Promise(resolve => setTimeout(resolve, 1500))

    console.log('登录数据:', {
      email: formData.email,
      password: formData.password,
      rememberMe: formData.rememberMe
    })

    // 保存登录状态
    if (formData.rememberMe) {
      localStorage.setItem('userEmail', formData.email)
    }
    await router.push('/dashboard')

  } catch (error) {
    console.error('登录失败:', error)
    alert('登录失败，请检查邮箱和密码')
  } finally {
    isLoading.value = false
  }
}

const handleForgotPassword = () => {
  const email = prompt('请输入您的邮箱地址以重置密码:', formData.email || '')
  if (email) {
    alert(`重置密码链接已发送到 ${email}`)
  }
}

const goToRegister = () => {
  router.push('/register')
}

const onBoxHover = (hovered: boolean) => {
  isBoxHovered.value = hovered
}

</script>

<style scoped>
/* 全局样式 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: 'Segoe UI', 'Microsoft YaHei', sans-serif;
}

.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  position: relative;
}

/* 背景模糊效果 */
.bg-blur {
  position: fixed;
  top: -20px;
  left: -20px;
  right: -20px;
  bottom: -20px;
  background-size: cover;
  background-position: center;
  z-index: -1;
  transition: opacity 0.3s ease;
}

/* 登录容器 */
.login-container {
  width: 100%;
  max-width: 420px;
  position: relative;
  z-index: 1;
}

/* 登录框 */
.login-box {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 24px;
  padding: 48px 40px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
}

.login-box:hover {
  transform: translateY(-5px);
  box-shadow: 0 25px 80px rgba(0, 0, 0, 0.2);
}

/* Logo和标题 */
.logo-area {
  text-align: center;
  margin-bottom: 40px;
}

.logo-area h1 {
  font-size: 32px;
  font-weight: 700;
  color: #2d3748;
  margin-bottom: 8px;
  letter-spacing: -0.5px;
}
/*实验项目*/
.logo-area p {
  color: #718096;
  font-size: 15px;
  font-weight: 400;
}

/* 表单样式 */
.form-group {
  margin-bottom: 24px;
  position: relative;
}

.form-label {
  display: block;
  margin-bottom: 8px;
  color: #4a5568;
  font-size: 18px;
  font-weight: 500;
  transition: color 0.2s;
}

.input-with-icon {
  position: relative;
}

.input-icon {
  position: absolute;
  left: 16px;
  top: 50%;
  transform: translateY(-50%);
  color: #a0aec0;
  font-size: 18px;
  transition: color 0.2s;
  pointer-events: none;
}

.form-input {
  width: 100%;
  padding: 16px 16px 16px 52px;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  font-size: 16px;
  color: #2d3748;
  background: #f8fafc;
  transition: all 0.3s ease;
  outline: none;
}

.form-input:focus {
  border-color: #4299e1;
  background: white;
  box-shadow: 0 0 0 4px rgba(66, 153, 225, 0.15);
}

.form-input:focus + .input-icon {
  color: #4299e1;
}

/* 密码显示切换 */
.password-toggle {
  position: absolute;
  right: 16px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: #718096;
  cursor: pointer;
  font-size: 18px;
  padding: 4px;
  transition: color 0.2s;
  z-index: 3;
}

/* 悬停颜色 */
.password-toggle:hover {
  color: #2d3748;
}

/* 记住我和忘记密码*/
.remember-forgot-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
  font-size: 18px;
}

/* 记住我 */
.remember-label {
  display: flex;
  align-items: center;
  cursor: pointer;
  user-select: none;
}

.remember-checkbox {
  width: 18px;
  height: 18px;
  margin-right: 10px;
  cursor: pointer;
  accent-color: #4299e1;
}

.remember-text {
  color: #4a5568;
  font-size: 18px;
  font-weight: 500;
}

.remember-checkbox:checked + .remember-text {
  color: #2d3748;
}

/* 忘记密码 */
.forgot-link {
  color: #4299e1;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.2s;
  cursor: pointer;
}

.forgot-link:hover {
  color: #3182ce;
  text-decoration: underline;
}

/* 登录按钮 */
.login-btn {
  width: 100%;
  padding: 16px;
  background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 20px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-bottom: 24px;
  letter-spacing: 1px;
  position: relative;
  overflow: hidden;
}

.login-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 10px 25px rgba(106, 17, 203, 0.4);
}

.login-btn:active:not(:disabled) {
  transform: translateY(0);
}

.login-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none !important;
  box-shadow: none !important;
}

.login-btn.loading {
  background: #a0aec0;
}

/* 注册区域 */
.register-area {
  text-align: center;
  padding-top: 24px;
  border-top: 1px solid #e2e8f0;
  color: #718096;
  font-size: 16px;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 6px;
}

.register-text {
  color: #718096;
}

.register-link {
  background: none;
  border: none;
  color: #4299e1;
  font-weight: 600;
  cursor: pointer;
  transition: color 0.2s;
  position: relative;
  padding: 0;
  font-size: 17px;
}

.register-link:hover:not(:disabled) {
  color: #3182ce;
}

.register-link:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.register-link:after {
  content: '';
  position: absolute;
  width: 100%;
  height: 2px;
  bottom: -2px;
  left: 0;
  background: #4299e1;
  transform: scaleX(0);
  transition: transform 0.2s;
}

.register-link:hover:not(:disabled):after {
  transform: scaleX(1);
}

/* 加载动画 */
.loader {
  display: inline-block;
  width: 20px;
  height: 20px;
  border: 3px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top-color: white;
  animation: spin 1s ease-in-out infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 错误提示 */
.error-message {
  color: #e53e3e;
  font-size: 13px;
  margin-top: 6px;
  animation: fadeIn 0.3s ease;
}

.error .form-input {
  border-color: #e53e3e;
}

.error .input-icon {
  color: #e53e3e;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(-5px); }
  to { opacity: 1; transform: translateY(0); }
}
/*-------非pc设备-----------*/
/* 响应式设计 */
@media (max-width: 480px) {
  .login-box {
    padding: 36px 28px;
    border-radius: 20px;
  }

  .logo {
    width: 56px;
    height: 56px;
  }

  .logo-area h1 {
    font-size: 24px;
  }

  .logo-area p {
    font-size: 14px;
  }

  .form-input {
    padding: 14px 14px 14px 48px;
    font-size: 15px;
  }

  .remember-forgot-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .register-area {
    flex-direction: column;
    gap: 8px;
  }
}

/* 移动端优化 */
@media (max-width: 768px) {
  .login-page {
    padding: 16px;
  }

  .bg-blur {
    filter: blur(20px) brightness(0.9);
  }

  .login-box {
    padding: 32px 24px;
  }
}
</style>
