<template>
  <!-- header -->
  <div class="nav-container page-component">
    <!--左侧导航 #start -->
    <div class="nav left-nav">
      <div class="nav-item selected">
  <span class="v-link selected dark"
        :onclick="'javascript:window.location=\'/hospital/'+hospital.hoscode+'\''">预约挂号 </span>
      </div>
      <div class="nav-item ">
        <span class="v-link clickable dark"
              :onclick="'javascript:window.location=\'/hospital/detail/'+hospital.hoscode+'\''"> 医院详情 </span>
      </div>
      <div class="nav-item">
        <span class="v-link clickable dark"
              :onclick="'javascript:window.location=\'/hospital/notice/'+hospital.hoscode+'\''"> 预约须知 </span>
      </div>
      <div class="nav-item "><span
        class="v-link clickable dark"> 停诊信息 </span>
      </div>
      <div class="nav-item "><span
        class="v-link clickable dark"> 查询/取消 </span>
      </div>
    </div>
    <!-- 左侧导航 #end -->
    <!-- 右侧内容 #start -->
    <div class="page-container">
      <div class="hospital-home">
        <div class="common-header">
          <div class="title-wrapper">
            <span class="hospital-title">{{ hospital.hosname }}</span>
            <div class="icon-wrapper">
              <span class="iconfont"></span>{{ hospital.param.hostypeString }}
            </div>
          </div>
          <div class="favorite-btn" @click="toggleFavorite">
            <i :class="favorited ? 'el-icon-star-on' : 'el-icon-star-off'"
               :style="{ color: favorited ? '#f59e0b' : '#94a3b8', fontSize: '22px' }"></i>
            <span>{{ favorited ? '已收藏' : '收藏医院' }}</span>
          </div>
        </div>
        <div class="info-wrapper">
          <img class="hospital-img" :src="'data:image/jpeg;base64,'+hospital.logoData" :alt="hospital.hosname">
          <div class="content-wrapper">
            <div> 挂号规则</div>
            <div class="line">
              <div><span class="label">预约周期：</span><span>{{ bookingRule.cycle }}天</span></div>
              <div class="space"><span class="label">放号时间：</span><span>{{ bookingRule.releaseTime }}</span></div>
              <div class="space"><span class="label">停挂时间：</span><span>{{ bookingRule.stopTime }}</span></div>
            </div>
            <div class="line"><span class="label">退号时间：</span>
              <span v-if="bookingRule.quitDay == -1">就诊前一工作日{{ bookingRule.quitTime }}前取消</span>
              <span v-if="bookingRule.quitDay == 0">就诊前当天{{ bookingRule.quitTime }}前取消</span>
            </div>
            <div style="margin-top:20px"> 医院预约规则</div>
            <div class="rule-wrapper">
              <ol>
                <li v-for="item in bookingRule.rule" :key="item">{{ item }}</li>
              </ol>
            </div>
          </div>
        </div>
        <div class="title select-title"> 选择科室</div>
        <div class="select-dept-wrapper">
          <div class="department-wrapper">
            <div class="hospital-department">
              <div class="dept-list-wrapper el-scrollbar" style="height: 100%;">
                <div class="dept-list el-scrollbar__wrap" style="margin-bottom: -17px; margin-right: -17px;">
                  <div class="el-scrollbar__view">
                    <div class="sub-item" v-for="(item,index) in departmentVoList" :key="item.id"
                         :class="index == activeIndex ? 'selected' : ''" @click="move(index,item.depcode)"> {{
                      item.depname }}
                    </div>
                  </div>
                </div>
                <div class="el-scrollbar__bar is-horizontal">
                  <div class="el-scrollbar__thumb" style="transform: translateX(0%);"></div>
                </div>
                <div class="el-scrollbar__bar is-vertical">
                  <div class="el-scrollbar__thumb" style="transform: translateY(0%); height: 91.4761%;"></div>
                </div>
              </div>
            </div>
          </div>
          <div class="sub-dept-container">
            <div v-for="(item,index) in departmentVoList" :key="item.id" :class="index == 0 ? 'selected' : ''"
                 class="sub-dept-wrapper" :id="item.depcode">
              <div class="sub-title">
                <div class="block selected"></div>
                {{ item.depname }}
              </div>
              <div class="sub-item-wrapper">
                <div v-for="it in item.children" :key="it.id" class="sub-item" @click="schedule(it.depcode)"><span
                  class="v-link clickable">{{ it.depname }} </span></div>
              </div>
            </div>
          </div>
        </div>

        <!-- 医生团队 -->
        <div class="title" style="margin-top:30px"> 医生团队</div>
        <div class="doctor-team">
          <div class="doctor-card" v-for="(doc, i) in doctorList" :key="i">
            <div class="doctor-avatar">{{ doc.name.charAt(0) }}</div>
            <div class="doctor-info">
              <div class="doctor-name">{{ doc.name }} <span class="doctor-title">{{ doc.title }}</span></div>
              <div class="doctor-dept">{{ doc.dept }}</div>
              <div class="doctor-desc">{{ doc.desc }}</div>
            </div>
            <span class="doctor-schedule" @click="schedule(doc.depcode)">预约挂号</span>
          </div>
        </div>

        <!-- 体检套餐 -->
        <div class="title" style="margin-top:30px"> 体检套餐</div>
        <div class="checkup-section">
          <div class="checkup-card" v-for="(pkg, i) in checkupList" :key="i">
            <div class="checkup-top">
              <h4 class="checkup-name">{{ pkg.name }}</h4>
              <span class="checkup-suitable">{{ pkg.suitable }}</span>
            </div>
            <div class="checkup-content">
              <span class="checkup-item" v-for="(item, j) in pkg.items" :key="j">{{ item }}</span>
            </div>
            <div class="checkup-bottom">
              <div class="checkup-price">
                <span class="price-current">¥{{ pkg.price }}</span>
                <span class="price-original" v-if="pkg.originalPrice">¥{{ pkg.originalPrice }}</span>
              </div>
              <span class="checkup-btn">立即预约</span>
            </div>
          </div>
        </div>

        <!-- 患者评价 -->
        <div class="title" style="margin-top:30px"> 患者评价
          <span class="rating-summary" v-if="evalRating.average > 0">
            <i class="el-icon-star-on" style="color:#f59e0b"></i>
            {{ evalRating.average }}分 · {{ evalRating.total }}条评价
          </span>
        </div>
        <div class="evaluation-section">
          <div class="eval-card" v-for="(ev, i) in evaluationList" :key="i">
            <div class="eval-header">
              <span class="eval-user">{{ ev.userName || '匿名用户' }}</span>
              <span class="eval-stars">
                <i class="el-icon-star-on" v-for="n in ev.rating" :key="n" style="color:#f59e0b;font-size:14px"></i>
                <i class="el-icon-star-off" v-for="n in (5 - ev.rating)" :key="'e' + n" style="color:#cbd5e1;font-size:14px"></i>
              </span>
              <span class="eval-date">{{ ev.createTime ? ev.createTime.substring(0, 10) : '' }}</span>
            </div>
            <div class="eval-body">
              <span class="eval-tag">{{ ev.depname }}</span>
              <span class="eval-tag" v-if="ev.title">{{ ev.title }}</span>
              <p class="eval-content">{{ ev.content }}</p>
            </div>
          </div>
          <div v-if="evaluationList.length === 0" class="eval-empty">
            <i class="el-icon-chat-line-round"></i>
            <p>暂无评价，成为第一个评价的人吧</p>
          </div>
        </div>
      </div>
    </div>
    <!-- 右侧内容 #end -->
  </div>
  <!-- footer -->
</template>

<script>
  import '~/assets/css/hospital_personal.css'
  import '~/assets/css/hospital.css'
  import userInfoApi from '@/api/userInfo'
  import cookie from 'js-cookie'
  import hospitalApi from '@/api/hosp'
  import favoriteApi from '@/api/favorite'
  import evaluationApi from '@/api/evaluation'

  export default {
    data() {
      return {
        hoscode: null,
        activeIndex: 0,
        favorited: false,
        doctorList: [],
        checkupList: [],
        evaluationList: [],
        evalRating: { average: 0, total: 0 },

        hospital: {
          param: {}
        },
        bookingRule: {},
        departmentVoList: []
      }
    },
    created() {
      this.hoscode = this.$route.params.hoscode
      this.init()
    },

    methods: {
      init() {
        hospitalApi.show(this.hoscode).then(response => {
          this.hospital = response.data.hospital
          this.bookingRule = response.data.bookingRule
        })

        hospitalApi.findDepartment(this.hoscode).then(response => {
          this.departmentVoList = response.data
        })

        this.checkFavoriteStatus()
        this.initDoctorList()
        this.loadEvaluations()
        this.initCheckupList()
      },

      checkFavoriteStatus() {
        let token = cookie.get('token')
        if (!token) return
        favoriteApi.checkFavorite(this.hoscode).then(response => {
          this.favorited = response.data || false
        }).catch(() => {})
      },

      toggleFavorite() {
        let token = cookie.get('token')
        if (!token) {
          loginEvent.$emit('loginDialogEvent')
          return
        }
        if (this.favorited) {
          favoriteApi.cancelFavorite(this.hoscode).then(() => {
            this.favorited = false
            this.$message.success('已取消收藏')
          })
        } else {
          favoriteApi.addFavorite(this.hoscode, this.hospital.hosname).then(() => {
            this.favorited = true
            this.$message.success('已收藏医院')
          })
        }
      },

      initDoctorList() {
        this.doctorList = [
          { name: '张明', title: '主任医师', dept: '心血管内科', desc: '擅长冠心病、高血压、心力衰竭等心血管疾病的诊治，具有30年临床经验', depcode: this.departmentVoList.length > 0 ? this.departmentVoList[0].depcode : '' },
          { name: '李芳', title: '副主任医师', dept: '神经内科', desc: '擅长脑血管病、癫痫、帕金森病等神经系统疾病的诊断与治疗', depcode: this.departmentVoList.length > 1 ? this.departmentVoList[1].depcode : '' },
          { name: '王建国', title: '主任医师', dept: '骨科', desc: '擅长关节置换、脊柱外科、运动医学等骨科手术及微创治疗', depcode: '' },
          { name: '陈晓燕', title: '主治医师', dept: '儿科', desc: '擅长儿童呼吸系统疾病、消化系统疾病及新生儿常见病的诊治', depcode: '' }
        ]
      },

      initCheckupList() {
        this.checkupList = [
          { name: '基础体检套餐', suitable: '20-40岁人群', price: 588, originalPrice: 980, items: ['一般检查', '血常规', '尿常规', '肝功能四项', '肾功能三项', '血脂二项', '空腹血糖', '心电图', '胸片', '腹部B超'] },
          { name: '精英体检套餐', suitable: '40岁以上人群', price: 1280, originalPrice: 1980, items: ['基础项目', '肿瘤标志物', '甲状腺功能', '颈动脉B超', '骨密度检测', '中医体质辨识'] },
          { name: '女性专项套餐', suitable: '成年女性', price: 888, originalPrice: 1380, items: ['基础体检', '妇科检查', '乳腺彩超', '宫颈TCT', 'HPV检测', '盆腔B超', '激素六项'] }
        ]
      },

      loadEvaluations() {
        evaluationApi.getEvaluationList(this.hoscode, 1, 5).then(response => {
          this.evaluationList = response.data || []
        }).catch(() => {})
        evaluationApi.getHospitalRating(this.hoscode).then(response => {
          this.evalRating = response.data || { average: 0, total: 0 }
        }).catch(() => {})
      },

      move(index, depcode) {
        this.activeIndex = index
        document.getElementById(depcode).scrollIntoView();
      },

      schedule(depcode) {
        // 登录判断
        let token = cookie.get('token')
        if (!token) {
          loginEvent.$emit('loginDialogEvent')
          return
        }

        //判断认证
        userInfoApi.getUserInfo().then(response => {
          let authStatus = response.data.authStatus
          // 状态为2认证通过
          if (!authStatus || authStatus != 2) {
            window.location.href = '/user'
            return
          }
        })
        window.location.href = '/hospital/schedule?hoscode=' + this.hoscode + "&depcode=" + depcode
      }
    }
  }
</script>

<style scoped>
  .favorite-btn {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 8px 18px;
    border: 1px solid #e2e8f0;
    border-radius: 20px;
    cursor: pointer;
    transition: all 0.3s ease;
    font-size: 14px;
    color: #64748b;
    background: #fff;
    white-space: nowrap;
  }
  .favorite-btn:hover {
    border-color: #f59e0b;
    color: #f59e0b;
    box-shadow: 0 2px 8px rgba(245, 158, 11, 0.15);
  }
  .doctor-team {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
    margin-top: 16px;
  }
  .doctor-card {
    display: flex;
    align-items: center;
    gap: 14px;
    padding: 20px;
    background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
    border-radius: 12px;
    border: 1px solid #e2e8f0;
    transition: all 0.3s ease;
  }
  .doctor-card:hover {
    border-color: #60a5fa;
    box-shadow: 0 4px 16px rgba(96, 165, 250, 0.12);
    transform: translateY(-2px);
  }
  .doctor-avatar {
    width: 52px;
    height: 52px;
    border-radius: 50%;
    background: linear-gradient(135deg, #3b82f6, #2563eb);
    color: #fff;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 20px;
    font-weight: 700;
    flex-shrink: 0;
  }
  .doctor-info {
    flex: 1;
    min-width: 0;
  }
  .doctor-name {
    font-size: 15px;
    font-weight: 600;
    color: #1e293b;
    margin-bottom: 4px;
  }
  .doctor-title {
    font-size: 12px;
    color: #3b82f6;
    background: #eff6ff;
    padding: 2px 8px;
    border-radius: 10px;
    margin-left: 8px;
    font-weight: 500;
  }
  .doctor-dept {
    font-size: 13px;
    color: #64748b;
    margin-bottom: 4px;
  }
  .doctor-desc {
    font-size: 12px;
    color: #94a3b8;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  .doctor-schedule {
    flex-shrink: 0;
    padding: 6px 16px;
    background: #3b82f6;
    color: #fff;
    border-radius: 16px;
    font-size: 13px;
    cursor: pointer;
    transition: background 0.2s;
  }
  .doctor-schedule:hover {
    background: #2563eb;
  }
  .title-wrapper {
    display: flex;
    align-items: center;
    gap: 12px;
  }

  /* ===== 体检套餐 ===== */
  .checkup-section {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 16px;
    margin-top: 16px;
  }
  .checkup-card {
    background: #fff;
    border: 1px solid #e2e8f0;
    border-radius: 12px;
    padding: 20px;
    transition: all .3s;
    display: flex;
    flex-direction: column;
  }
  .checkup-card:hover {
    border-color: #0ea5e9;
    box-shadow: 0 4px 16px rgba(14, 165, 233, .12);
    transform: translateY(-2px);
  }
  .checkup-top {
    margin-bottom: 14px;
  }
  .checkup-name {
    margin: 0 0 4px;
    font-size: 16px;
    font-weight: 600;
    color: #1e293b;
  }
  .checkup-suitable {
    font-size: 12px;
    color: #64748b;
    background: #f1f5f9;
    padding: 2px 10px;
    border-radius: 10px;
  }
  .checkup-content {
    display: flex;
    flex-wrap: wrap;
    gap: 6px;
    flex: 1;
    margin-bottom: 16px;
  }
  .checkup-item {
    font-size: 11px;
    padding: 2px 8px;
    background: #eff6ff;
    color: #3b82f6;
    border-radius: 4px;
  }
  .checkup-bottom {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-top: 14px;
    border-top: 1px solid #f1f5f9;
  }
  .checkup-price {
    display: flex;
    align-items: baseline;
    gap: 6px;
  }
  .price-current {
    font-size: 20px;
    font-weight: 700;
    color: #ef4444;
  }
  .price-original {
    font-size: 13px;
    color: #94a3b8;
    text-decoration: line-through;
  }
  .checkup-btn {
    padding: 6px 16px;
    background: linear-gradient(135deg, #0ea5e9, #2563eb);
    color: #fff;
    border-radius: 16px;
    font-size: 13px;
    cursor: pointer;
    transition: box-shadow .2s;
  }
  .checkup-btn:hover {
    box-shadow: 0 4px 12px rgba(37, 99, 235, .35);
  }

  /* ===== 患者评价 ===== */
  .rating-summary {
    font-size: 13px;
    font-weight: 400;
    color: #f59e0b;
    margin-left: 12px;
  }
  .evaluation-section {
    margin-top: 16px;
  }
  .eval-card {
    background: #fff;
    border: 1px solid #e2e8f0;
    border-radius: 10px;
    padding: 16px 20px;
    margin-bottom: 12px;
    transition: all .2s;
  }
  .eval-card:hover {
    border-color: #cbd5e1;
    box-shadow: 0 2px 8px rgba(0,0,0,.04);
  }
  .eval-header {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 10px;
  }
  .eval-user {
    font-size: 14px;
    font-weight: 600;
    color: #334155;
  }
  .eval-stars {
    display: flex;
    gap: 2px;
  }
  .eval-date {
    margin-left: auto;
    font-size: 12px;
    color: #94a3b8;
  }
  .eval-body {
    display: flex;
    flex-wrap: wrap;
    align-items: flex-start;
    gap: 8px;
  }
  .eval-tag {
    font-size: 11px;
    padding: 2px 8px;
    background: #f1f5f9;
    color: #64748b;
    border-radius: 4px;
  }
  .eval-content {
    width: 100%;
    margin: 4px 0 0;
    font-size: 13px;
    color: #475569;
    line-height: 1.6;
  }
  .eval-empty {
    text-align: center;
    padding: 40px 0;
    color: #94a3b8;
  }
  .eval-empty i {
    font-size: 40px;
    display: block;
    margin-bottom: 8px;
  }

  @media (max-width: 768px) {
    .checkup-section {
      grid-template-columns: 1fr;
    }
    .doctor-team {
      grid-template-columns: 1fr;
    }
  }
</style>
