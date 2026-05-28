<template>
  <div>
    <myheader />
    <div :style="'min-height:' + minHeight">
      <nuxt />
    </div>
    <!-- 页脚 -->
    <footer class="site-footer" v-if="!hideFooter">
      <div class="footer-inner">
        <div class="footer-col">
          <h4>关于尚医通</h4>
          <p>尚医通是专业的网上预约挂号平台，致力于为患者提供便捷、高效的在线挂号服务。</p>
        </div>
        <div class="footer-col">
          <h4>快捷入口</h4>
          <p><span class="footer-link" onclick="javascript:window.location='/'">首页</span></p>
          <p><span class="footer-link" onclick="javascript:window.location='/hospital'">预约挂号</span></p>
          <p><span class="footer-link" onclick="javascript:window.location='/order'">我的订单</span></p>
        </div>
        <div class="footer-col">
          <h4>联系我们</h4>
          <p>客服电话：400-888-8888</p>
          <p>服务时间：周一至周日 8:00-20:00</p>
        </div>
      </div>
      <div class="footer-bottom">
        <span>© 2025 尚医通 yuyue.com 京ICP备xxxxxxxx号</span>
      </div>
    </footer>
  </div>
</template>

<script>
import myheader from './myheader'
import '~/assets/css/main.css'

export default {
  components: {
    myheader,
  },
  data() {
    return {
      minHeight: '',
      hideFooter: false,
    }
  },
  mounted() {
    this.minHeight = document.documentElement.clientHeight - 200 + 'px'
  },
}
</script>

<style>
.site-footer {
  background: #1e293b;
  color: #cbd5e1;
  padding: 40px 0 0;
  margin-top: 60px;
}
.footer-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  gap: 60px;
}
.footer-col {
  flex: 1;
}
.footer-col h4 {
  color: #fff;
  font-size: 15px;
  margin: 0 0 12px;
}
.footer-col p {
  font-size: 13px;
  line-height: 2;
  margin: 0;
}
.footer-link {
  cursor: pointer;
  transition: color .15s;
}
.footer-link:hover {
  color: #fff;
}
.footer-bottom {
  text-align: center;
  padding: 20px 0;
  border-top: 1px solid rgba(255, 255, 255, .08);
  margin-top: 32px;
  font-size: 12px;
  color: #64748b;
}
</style>