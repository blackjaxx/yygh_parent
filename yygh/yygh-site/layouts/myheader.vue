<template>
  <header class="app-header" id="myheader">
    <div class="header-inner">
      <!-- Logo -->
      <div class="header-logo" onclick="javascript:window.location='/'">
        <div class="logo-icon">
          <i class="el-icon-plus"></i>
        </div>
        <span class="logo-text">尚医通</span>
      </div>

      <!-- 导航菜单 -->
      <nav class="header-nav">
        <span class="nav-link" :class="{ active: currentRoute === 'index' }" onclick="javascript:window.location='/'">首页</span>
        <span class="nav-link" :class="{ active: currentRoute === 'hospital' }" onclick="javascript:window.location='/hospital'">预约挂号</span>
        <span class="nav-link" onclick="javascript:window.location='/hospital/compare'">医院对比</span>
        <span class="nav-link">就医指南</span>
        <span class="nav-link">健康资讯</span>
      </nav>

      <!-- 搜索 + 用户 -->
      <div class="header-right">
        <div class="header-search">
          <el-autocomplete
            v-model="hosname"
            :fetch-suggestions="querySearchAsync"
            placeholder="搜索医院"
            :trigger-on-focus="false"
            @select="handleSelect"
          />
        </div>

        <!-- 已登录 -->
        <div v-if="name" class="header-user">
          <el-dropdown @command="loginMenu">
            <span class="user-avatar">{{ name.charAt(0) }}</span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="/user">个人中心</el-dropdown-item>
              <el-dropdown-item command="/patient">就诊人管理</el-dropdown-item>
              <el-dropdown-item command="/order">我的订单</el-dropdown-item>
              <el-dropdown-item command="/favorite">我的收藏</el-dropdown-item>
              <el-dropdown-item divided command="/logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
          <span class="user-name">{{ name }}</span>
        </div>
        <!-- 未登录 -->
        <span v-else class="login-btn" @click="showDialog()">登录 / 注册</span>
      </div>
    </div>

    <!-- 登录弹窗 -->
    <el-dialog :visible.sync="dialogAtrr.showLoginDialog" :modal="true" width="420px" :before-close="closeDialog">
      <div slot="title" class="login-dialog-title">
        <div class="login-tabs">
          <span :class="{ active: dialogAtrr.showLoginType === 'weixin' }" @click="weixinLogin()">微信登录</span>
          <span :class="{ active: dialogAtrr.showLoginType === 'phone' }" @click="phoneLogin()">手机登录</span>
        </div>
      </div>

      <div class="login-body">
        <!-- 微信扫码 -->
        <div v-if="dialogAtrr.showLoginType === 'weixin'" class="weixin-login">
          <div id="weixinLogin"></div>
          <p class="weixin-tip">请使用微信扫描二维码登录<br/>"尚医通预约挂号平台"</p>
        </div>

        <!-- 手机登录 -->
        <div v-if="dialogAtrr.showLoginType === 'phone'" class="phone-login">
          <el-form :model="userInfo">
            <el-form-item>
              <el-input
                v-model="userInfo.phone"
                prefix-icon="el-icon-mobile-phone"
                placeholder="请输入手机号"
                maxlength="11"
              />
            </el-form-item>
            <el-form-item>
              <el-input
                v-model="userInfo.code"
                :placeholder="dialogAtrr.placeholder"
                :maxlength="dialogAtrr.maxlength"
              >
                <span slot="suffix" class="sms-btn" @click="getCodeFun()">
                  {{ dialogAtrr.sending ? '获取验证码' : dialogAtrr.second + 's' }}
                </span>
              </el-input>
            </el-form-item>
            <el-button
              type="primary"
              class="login-submit-btn"
              @click="login()"
              :loading="dialogAtrr.loginBtn === '正在提交...'"
              round
            >
              {{ dialogAtrr.loginBtn }}
            </el-button>
          </el-form>
          <p v-if="dialogAtrr.labelTips" class="sms-tip">{{ dialogAtrr.labelTips }}</p>
        </div>
      </div>
    </el-dialog>
  </header>
</template>

<script>
import cookie from 'js-cookie'
import Vue from 'vue'
import userInfoApi from '@/api/userInfo'
import smsApi from '@/api/msm'
import weixinApi from '@/api/weixin'
import hospApi from '@/api/hosp'

export default {
  data() {
    return {
      currentRoute: 'index',
      hosname: '',
      name: '',
      userInfo: { phone: '', code: '', openid: '' },
      dialogAtrr: {
        showLoginDialog: false,
        showLoginType: 'weixin',
        inputValue: '',
        placeholder: '请输入验证码',
        maxlength: 6,
        loginBtn: '马上登录',
        sending: true,
        second: 0,
        labelTips: '',
      },
      clearSmsTime: null,
    }
  },
  mounted() {
    this.showInfo()
  },
  methods: {
    showDialog() {
      this.dialogAtrr.showLoginDialog = true
    },
    showLogin() {
      this.dialogAtrr.showLoginType = 'phone'
      this.dialogAtrr.inputValue = ''
      this.dialogAtrr.placeholder = '请输入验证码'
      this.dialogAtrr.maxlength = 6
      this.dialogAtrr.loginBtn = '马上登录'
      this.dialogAtrr.sending = true
      this.dialogAtrr.second = 0
      this.dialogAtrr.labelTips = ''
    },
    login() {
      this.userInfo.code = this.dialogAtrr.inputValue
      if (this.dialogAtrr.loginBtn == '正在提交...') {
        this.$message.error('重复提交')
        return
      }
      if (this.userInfo.code == '') {
        this.$message.error('验证码必须输入')
        return
      }
      if (this.userInfo.code.length != 6) {
        this.$message.error('验证码格式不正确')
        return
      }
      this.dialogAtrr.loginBtn = '正在提交...'
      userInfoApi.login(this.userInfo).then(response => {
        this.setCookies(response.data.name, response.data.token)
      }).catch(() => {
        this.dialogAtrr.loginBtn = '马上登录'
      })
    },
    querySearchAsync(queryString, cb) {
      if (queryString === '') return
      hospApi.getByHosname(queryString).then(response => {
        for (let i = 0; i < response.data.length; i++) {
          response.data[i].value = response.data[i].hosname
        }
        cb(response.data)
      })
    },
    setCookies(name, token) {
      cookie.set('token', token, { domain: 'localhost' })
      cookie.set('name', name, { domain: 'localhost' })
      window.location.reload()
    },
    getCodeFun() {
      if (!(/^1[34578]\d{9}$/.test(this.userInfo.phone))) {
        this.$message.error('手机号码不正确')
        return
      }
      this.dialogAtrr.inputValue = ''
      this.dialogAtrr.placeholder = '请输入验证码'
      this.dialogAtrr.maxlength = 6
      this.dialogAtrr.loginBtn = '马上登录'
      if (!this.dialogAtrr.sending) return
      this.timeDown()
      this.dialogAtrr.sending = false
      smsApi.sendCode(this.userInfo.phone).then(() => {
        this.timeDown()
      }).catch(() => {
        this.$message.error('发送失败，重新发送')
        this.showLogin()
      })
    },
    timeDown() {
      if (this.clearSmsTime) clearInterval(this.clearSmsTime)
      this.dialogAtrr.second = 60
      this.dialogAtrr.labelTips = '验证码已发送至' + this.userInfo.phone
      this.clearSmsTime = setInterval(() => {
        --this.dialogAtrr.second
        if (this.dialogAtrr.second < 1) {
          clearInterval(this.clearSmsTime)
          this.dialogAtrr.sending = true
          this.dialogAtrr.second = 0
        }
      }, 1000)
    },
    closeDialog() {
      if (this.clearSmsTime) clearInterval(this.clearSmsTime)
    },
    showInfo() {
      let token = cookie.get('token')
      if (token) {
        this.name = cookie.get('name')
      }
    },
    loginMenu(command) {
      if ('/logout' == command) {
        cookie.set('name', '', { domain: 'localhost' })
        cookie.set('token', '', { domain: 'localhost' })
        window.location.href = '/'
      } else if ('/favorite' == command) {
        this.$message.info('收藏功能已集成在医院详情页，点击星星图标即可收藏')
      } else {
        window.location.href = command
      }
    },
    handleSelect(item) {
      window.location.href = '/hospital/' + item.hoscode
    },
    weixinLogin() {
      this.dialogAtrr.showLoginType = 'weixin'
      weixinApi.getLoginParam().then(response => {
        var obj = new WxLogin({
          self_redirect: true,
          id: 'weixinLogin',
          appid: response.data.appid,
          scope: response.data.scope,
          redirect_uri: response.data.redirectUri,
          state: response.data.state,
          style: 'black',
          href: '',
        })
      })
    },
    phoneLogin() {
      this.dialogAtrr.showLoginType = 'phone'
      this.showLogin()
    },
    loginCallback(name, token, openid) {
      if (openid != '') {
        this.userInfo.openid = openid
        this.showLogin()
      } else {
        this.setCookies(name, token)
      }
    },
  },
}
</script>

<style scoped>
.app-header {
  position: sticky;
  top: 0;
  z-index: 1000;
  background: rgba(255, 255, 255, .92);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid rgba(0, 0, 0, .06);
  box-shadow: 0 1px 4px rgba(0, 0, 0, .04);
}
.header-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  height: 60px;
  display: flex;
  align-items: center;
  gap: 0;
}

/* Logo */
.header-logo {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  flex-shrink: 0;
}
.logo-icon {
  width: 34px;
  height: 34px;
  border-radius: 8px;
  background: linear-gradient(135deg, #0ea5e9, #2563eb);
  display: flex;
  align-items: center;
  justify-content: center;
}
.logo-icon i {
  color: #fff;
  font-size: 16px;
  font-weight: bold;
}
.logo-text {
  font-size: 20px;
  font-weight: 700;
  color: #1e293b;
  letter-spacing: 1px;
}

/* 导航 */
.header-nav {
  display: flex;
  gap: 0;
  margin-left: 48px;
}
.nav-link {
  padding: 8px 20px;
  font-size: 14px;
  color: #475569;
  cursor: pointer;
  border-radius: 6px;
  transition: all .2s;
  font-weight: 500;
}
.nav-link:hover {
  color: #2563eb;
  background: #eff6ff;
}
.nav-link.active {
  color: #2563eb;
  font-weight: 600;
}

/* 右侧 */
.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-left: auto;
}
.header-search >>> .el-input__inner {
  width: 200px;
  height: 36px;
  border-radius: 18px;
  background: #f1f5f9;
  border: 1px solid transparent;
  font-size: 13px;
  padding-left: 16px;
  transition: all .2s;
}
.header-search >>> .el-input__inner:focus {
  background: #fff;
  border-color: #2563eb;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, .1);
  width: 240px;
}
.header-search >>> .el-input__inner::placeholder {
  color: #94a3b8;
}

/* 用户 */
.header-user {
  display: flex;
  align-items: center;
  gap: 10px;
}
.user-avatar {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background: linear-gradient(135deg, #0ea5e9, #7c3aed);
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}
.user-name {
  font-size: 13px;
  color: #475569;
}
.login-btn {
  font-size: 14px;
  color: #2563eb;
  font-weight: 500;
  cursor: pointer;
  padding: 6px 18px;
  border-radius: 20px;
  border: 1px solid #2563eb;
  transition: all .2s;
}
.login-btn:hover {
  background: #2563eb;
  color: #fff;
}

/* 登录弹窗 */
.login-dialog-title {
  text-align: center;
}
.login-tabs {
  display: inline-flex;
  gap: 32px;
}
.login-tabs span {
  font-size: 16px;
  color: #94a3b8;
  cursor: pointer;
  padding-bottom: 8px;
  border-bottom: 2px solid transparent;
  transition: all .2s;
}
.login-tabs span.active {
  color: #1e293b;
  font-weight: 600;
  border-bottom-color: #2563eb;
}

.login-body {
  padding: 10px 0;
}
.weixin-login {
  text-align: center;
}
.weixin-tip {
  font-size: 13px;
  color: #94a3b8;
  margin-top: 16px;
  line-height: 1.8;
}

.phone-login {
  padding: 0 10px;
}
.phone-login >>> .el-input__inner {
  height: 44px;
  border-radius: 8px;
  font-size: 14px;
}
.sms-btn {
  font-size: 13px;
  color: #2563eb;
  cursor: pointer;
  white-space: nowrap;
  padding: 0 8px;
}
.login-submit-btn {
  width: 100%;
  height: 44px;
  font-size: 15px;
  background: linear-gradient(135deg, #0ea5e9, #2563eb) !important;
  border: none !important;
  letter-spacing: 2px;
}
.sms-tip {
  text-align: center;
  font-size: 12px;
  color: #94a3b8;
  margin-top: 12px;
}
</style>