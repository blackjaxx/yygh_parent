<template>
  <div class="home-page">
    <!-- Hero 区域 -->
    <section class="hero-section">
      <div class="hero-overlay"></div>
      <div class="hero-content">
        <h1 class="hero-title">尚医通 · 预约挂号统一平台</h1>
        <p class="hero-subtitle">在线预约 · 无需排队 · 全国医院 · 一键挂号</p>
        <!-- 搜索框 -->
        <div class="hero-search">
          <el-autocomplete
            class="hero-search-input"
            prefix-icon="el-icon-search"
            v-model="state"
            :fetch-suggestions="querySearchAsync"
            placeholder="输入医院名称，快速查找..."
            @select="handleSelect"
            :trigger-on-focus="false"
          >
            <el-button slot="append" icon="el-icon-search" class="hero-search-btn">搜索</el-button>
          </el-autocomplete>
        </div>
        <!-- 数据统计条 -->
        <div class="hero-stats">
          <div class="stat-item">
            <span class="stat-number">500+</span>
            <span class="stat-label">合作医院</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-number">10万+</span>
            <span class="stat-label">累计挂号</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-number">30+</span>
            <span class="stat-label">覆盖省份</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 筛选 + 列表区域 -->
    <section class="main-section">
      <div class="main-container">
        <!-- 左侧：筛选 + 医院列表 -->
        <div class="main-left">
          <!-- 筛选区 -->
          <div class="filter-card">
            <div class="filter-header">筛选医院</div>
            <div class="filter-group">
              <span class="filter-label">等级</span>
              <div class="filter-tags">
                <span
                  class="filter-tag"
                  :class="{ active: hostypeActiveIndex === index }"
                  v-for="(item, index) in hostypeList"
                  :key="item.id"
                  @click="hostypeSelect(item.value, index)"
                >{{ item.name }}</span>
              </div>
            </div>
            <div class="filter-group">
              <span class="filter-label">省份</span>
              <div class="filter-tags">
                <span
                  class="filter-tag"
                  :class="{ active: provinceActiveIndex === index }"
                  v-for="(item, index) in provinceList"
                  :key="item.id"
                  @click="provinceSelect(item.value, index)"
                >{{ item.name }}</span>
              </div>
            </div>
            <div class="filter-group">
              <span class="filter-label">地区</span>
              <div class="filter-tags">
                <span
                  class="filter-tag"
                  :class="{ active: cityActiveIndex === index }"
                  v-for="(item, index) in districtList"
                  :key="item.id"
                  @click="districtSelect(item.value, index)"
                >{{ item.name }}</span>
              </div>
            </div>
          </div>

          <!-- 医院列表 -->
          <div class="hospital-list">
            <div
              class="hospital-card"
              v-for="item in list"
              :key="item.id"
              @click="show(item.hoscode)"
            >
              <div class="hospital-card-img">
                <img
                  :src="'data:image/jpeg;base64,' + item.logoData"
                  :alt="item.hosname"
                />
              </div>
              <div class="hospital-card-body">
                <h3 class="hospital-card-name">{{ item.hosname }}</h3>
                <div class="hospital-card-tags">
                  <span class="hos-tag level-tag">{{ item.param.hostypeString }}</span>
                  <span class="hos-tag time-tag">每天 {{ item.bookingRule.releaseTime }} 放号</span>
                </div>
                <div class="hospital-card-address">
                  <i class="el-icon-location-outline"></i>
                  {{ item.param.fullAddress || '地址暂无' }}
                </div>
              </div>
              <div class="hospital-card-action">
                <span class="go-btn">立即挂号 <i class="el-icon-arrow-right"></i></span>
              </div>
            </div>
            <div v-if="list.length === 0" class="empty-state">
              <i class="el-icon-search empty-icon"></i>
              <p>暂无匹配的医院，请调整筛选条件</p>
            </div>
          </div>
        </div>

        <!-- 右侧边栏 -->
        <div class="main-right">
          <!-- 常见科室 -->
          <div class="side-card">
            <div class="side-card-header">
              <span class="side-card-title">常见科室</span>
              <span class="side-card-more">全部 <i class="el-icon-arrow-right"></i></span>
            </div>
            <div class="dept-grid">
              <span class="dept-item">神经内科</span>
              <span class="dept-item">消化内科</span>
              <span class="dept-item">呼吸内科</span>
              <span class="dept-item">心血管内科</span>
              <span class="dept-item">神经外科</span>
              <span class="dept-item">妇科</span>
              <span class="dept-item">产科</span>
              <span class="dept-item">儿科</span>
              <span class="dept-item">骨科</span>
              <span class="dept-item">眼科</span>
              <span class="dept-item">皮肤科</span>
              <span class="dept-item">中医科</span>
            </div>
          </div>

          <!-- 平台公告 -->
          <div class="side-card">
            <div class="side-card-header">
              <span class="side-card-title">
                <i class="el-icon-bell side-title-icon blue"></i> 平台公告
              </span>
              <span class="side-card-more">全部 <i class="el-icon-arrow-right"></i></span>
            </div>
            <ul class="notice-list">
              <li class="notice-item">
                <span class="notice-dot blue"></span>
                <span class="notice-text">关于延长北京大学国际医院放假的通知</span>
              </li>
              <li class="notice-item">
                <span class="notice-dot blue"></span>
                <span class="notice-text">北京中医药大学东方医院部分科室医生调整</span>
              </li>
              <li class="notice-item">
                <span class="notice-dot blue"></span>
                <span class="notice-text">武警总医院号源暂停更新通知</span>
              </li>
            </ul>
          </div>

          <!-- 智能导诊 -->
          <div class="side-card symptom-card">
            <div class="side-card-header">
              <span class="side-card-title">
                <i class="el-icon-guide side-title-icon green"></i> 智能导诊
              </span>
            </div>
            <div class="symptom-search">
              <el-input
                v-model="symptomQuery"
                placeholder="输入症状，如：头痛、咳嗽..."
                prefix-icon="el-icon-search"
                @input="filterSymptoms"
                clearable
                size="small"
              />
            </div>
            <div v-if="symptomQuery && matchedSymptoms.length > 0" class="symptom-results">
              <div class="symptom-item" v-for="(item, i) in matchedSymptoms" :key="i">
                <div class="symptom-row">
                  <span class="symptom-name">{{ item.name }}</span>
                  <i class="el-icon-arrow-right"></i>
                </div>
                <div class="symptom-dept">建议科室：{{ item.dept }}</div>
              </div>
            </div>
            <div v-if="symptomQuery && matchedSymptoms.length === 0" class="symptom-empty">
              未找到匹配的症状，请尝试其他关键词
            </div>
            <div v-if="!symptomQuery" class="quick-symptoms">
              <span class="quick-label">快速导诊：</span>
              <span class="quick-tag" v-for="(item, i) in quickSymptoms" :key="i"
                    @click="symptomQuery = item.name; filterSymptoms()">{{ item.name }}</span>
            </div>
          </div>

          <!-- 停诊公告 -->
          <div class="side-card">
            <div class="side-card-header">
              <span class="side-card-title">
                <i class="el-icon-warning-outline side-title-icon orange"></i> 停诊公告
              </span>
              <span class="side-card-more">全部 <i class="el-icon-arrow-right"></i></span>
            </div>
            <ul class="notice-list">
              <li class="notice-item">
                <span class="notice-dot orange"></span>
                <span class="notice-text">海军总医院呼吸内科门诊停诊公告</span>
              </li>
              <li class="notice-item">
                <span class="notice-dot orange"></span>
                <span class="notice-text">北京潞河医院老年医学科门诊停诊公告</span>
              </li>
              <li class="notice-item">
                <span class="notice-dot orange"></span>
                <span class="notice-text">中日友好医院中西医结合心内科停诊</span>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script>
import hospApi from '@/api/hosp'
import dictApi from '@/api/dict'

export default {
  asyncData({ params, error }) {
    return hospApi.getPageList(1, 10, null).then(response => {
      return {
        list: response.data.content,
        pages: response.data.totalPages,
      }
    })
  },
  data() {
    return {
      state: null,
      searchObj: {},
      page: 1,
      limit: 10,
      hosname: '',
      hostypeList: [],
      provinceList: [],
      districtList: [],
      hostypeActiveIndex: 0,
      provinceActiveIndex: 0,
      cityActiveIndex: 0,
      symptomQuery: '',
      matchedSymptoms: [],
      quickSymptoms: [
        { name: '头痛', dept: '神经内科' },
        { name: '咳嗽', dept: '呼吸内科' },
        { name: '胸痛', dept: '心血管内科' },
        { name: '腹痛', dept: '消化内科' },
        { name: '关节痛', dept: '骨科' },
        { name: '发热', dept: '发热门诊' },
      ],
      allSymptoms: [
        { name: '头痛', dept: '神经内科' },
        { name: '头晕', dept: '神经内科' },
        { name: '失眠', dept: '神经内科' },
        { name: '咳嗽', dept: '呼吸内科' },
        { name: '哮喘', dept: '呼吸内科' },
        { name: '胸闷', dept: '呼吸内科' },
        { name: '胸痛', dept: '心血管内科' },
        { name: '心悸', dept: '心血管内科' },
        { name: '高血压', dept: '心血管内科' },
        { name: '腹痛', dept: '消化内科' },
        { name: '腹泻', dept: '消化内科' },
        { name: '便秘', dept: '消化内科' },
        { name: '胃痛', dept: '消化内科' },
        { name: '关节痛', dept: '骨科' },
        { name: '腰痛', dept: '骨科' },
        { name: '骨折', dept: '骨科' },
        { name: '视力模糊', dept: '眼科' },
        { name: '眼睛红肿', dept: '眼科' },
        { name: '皮疹', dept: '皮肤科' },
        { name: '皮肤瘙痒', dept: '皮肤科' },
        { name: '发热', dept: '发热门诊' },
        { name: '咽痛', dept: '耳鼻喉科' },
        { name: '耳鸣', dept: '耳鼻喉科' },
        { name: '牙痛', dept: '口腔科' },
        { name: '月经不调', dept: '妇科' },
        { name: '孕检', dept: '产科' },
        { name: '小儿发热', dept: '儿科' },
        { name: '疲劳', dept: '中医科' },
        { name: '焦虑', dept: '心理科' },
      ],
    }
  },
  created() {
    this.init()
  },
  methods: {
    init() {
      dictApi.findByDictCode('Hostype').then(response => {
        this.hostypeList = [{ name: '全部', value: '' }]
        for (let i = 0; i < response.data.length; i++) {
          this.hostypeList.push(response.data[i])
        }
      })
      dictApi.findByDictCode('Province').then(response => {
        this.provinceList = [{ name: '全部', value: '' }]
        for (let i = 0; i < response.data.length; i++) {
          this.provinceList.push(response.data[i])
        }
      })
      dictApi.findByDictCode('Beijing').then(response => {
        this.districtList = [{ name: '全部', value: '' }]
        for (let i in response.data) {
          this.districtList.push(response.data[i])
        }
      })
    },

    getList() {
      hospApi.getPageList(this.page, this.limit, this.searchObj).then(response => {
        for (let i in response.data.content) {
          this.list.push(response.data.content[i])
        }
        this.page = response.data.totalPages
      })
    },

    hostypeSelect(hostype, index) {
      this.list = []
      this.page = 1
      this.hostypeActiveIndex = index
      this.searchObj.hostype = hostype
      this.getList()
    },

    provinceSelect(districtCode, index) {
      this.list = []
      this.page = 1
      this.provinceActiveIndex = index
      this.searchObj.provinceCode = districtCode
      this.getList()
    },

    districtSelect(districtCode, index) {
      this.list = []
      this.page = 1
      this.cityActiveIndex = index
      this.searchObj.districtCode = districtCode
      this.getList()
    },

    querySearchAsync(queryString, cb) {
      this.searchObj = []
      if (queryString === '') return
      hospApi.getByHosname(queryString).then(response => {
        for (let i = 0; i < response.data.length; i++) {
          response.data[i].value = response.data[i].hosname
        }
        cb(response.data)
      })
    },

    handleSelect(item) {
      window.location.href = '/hospital/' + item.hoscode
    },

    filterSymptoms() {
      if (!this.symptomQuery || this.symptomQuery.trim() === '') {
        this.matchedSymptoms = []
        return
      }
      const q = this.symptomQuery.trim().toLowerCase()
      this.matchedSymptoms = this.allSymptoms.filter(s =>
        s.name.includes(q) || s.dept.includes(q)
      )
    },

    show(hoscode) {
      window.location.href = '/hospital/' + hoscode
    },
  },
}
</script>

<style scoped>
/* ===== Hero 区域 ===== */
.hero-section {
  position: relative;
  background: linear-gradient(135deg, #0ea5e9 0%, #2563eb 40%, #7c3aed 100%);
  padding: 60px 0 70px;
  overflow: hidden;
}
.hero-overlay {
  position: absolute;
  inset: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1440 320"><defs><linearGradient id="g" x1="0" y1="0" x2="1" y2="0"><stop offset="0%" stop-color="rgba(255,255,255,.08)"/><stop offset="100%" stop-color="rgba(255,255,255,.02)"/></linearGradient></defs><path fill="url(%23g)" d="M0,160 C360,320 1080,0 1440,160 L1440,320 L0,320 Z"/></svg>') bottom/cover no-repeat;
  pointer-events: none;
}
.hero-content {
  position: relative;
  max-width: 900px;
  margin: 0 auto;
  text-align: center;
  padding: 0 20px;
}
.hero-title {
  color: #fff;
  font-size: 36px;
  font-weight: 700;
  margin: 0 0 12px;
  letter-spacing: 2px;
  text-shadow: 0 2px 8px rgba(0, 0, 0, .15);
}
.hero-subtitle {
  color: rgba(255, 255, 255, .85);
  font-size: 16px;
  margin: 0 0 32px;
}
.hero-search {
  max-width: 620px;
  margin: 0 auto 40px;
}
.hero-search >>> .el-input__inner {
  height: 50px;
  border-radius: 25px;
  border: none;
  font-size: 15px;
  padding-left: 50px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, .15);
}
.hero-search >>> .el-input__prefix {
  left: 18px;
  font-size: 20px;
  color: #94a3b8;
}
.hero-search >>> .el-input-group__append {
  border-radius: 25px;
  border: none;
  background: transparent;
  padding: 0;
}
.hero-search-btn {
  height: 50px;
  border-radius: 0 25px 25px 0;
  background: linear-gradient(135deg, #f97316, #ef4444);
  border: none;
  color: #fff;
  font-size: 15px;
  padding: 0 28px;
  letter-spacing: 1px;
}
.hero-search-btn:hover {
  background: linear-gradient(135deg, #ea580c, #dc2626);
}

/* 统计数字 */
.hero-stats {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 0;
}
.stat-item {
  padding: 0 36px;
}
.stat-number {
  display: block;
  color: #fff;
  font-size: 28px;
  font-weight: 700;
}
.stat-label {
  display: block;
  color: rgba(255, 255, 255, .7);
  font-size: 13px;
  margin-top: 2px;
}
.stat-divider {
  width: 1px;
  height: 36px;
  background: rgba(255, 255, 255, .25);
}

/* ===== 主内容区 ===== */
.main-section {
  background: #f1f5f9;
  padding: 30px 0 60px;
}
.main-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  gap: 24px;
  align-items: flex-start;
}
.main-left {
  flex: 1;
  min-width: 0;
}
.main-right {
  width: 280px;
  flex-shrink: 0;
}

/* ===== 筛选卡片 ===== */
.filter-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px 24px;
  margin-bottom: 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, .06);
}
.filter-header {
  font-size: 15px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f1f5f9;
}
.filter-group {
  display: flex;
  align-items: flex-start;
  margin-bottom: 14px;
}
.filter-group:last-child {
  margin-bottom: 0;
}
.filter-label {
  width: 50px;
  flex-shrink: 0;
  font-size: 13px;
  color: #94a3b8;
  line-height: 28px;
}
.filter-tags {
  flex: 1;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.filter-tag {
  display: inline-block;
  padding: 3px 14px;
  font-size: 13px;
  color: #475569;
  background: #f8fafc;
  border-radius: 20px;
  cursor: pointer;
  transition: all .2s;
  border: 1px solid transparent;
}
.filter-tag:hover {
  color: #2563eb;
  border-color: #bfdbfe;
}
.filter-tag.active {
  color: #fff;
  background: #2563eb;
  border-color: #2563eb;
}

/* ===== 医院卡片列表 ===== */
.hospital-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}
.hospital-card {
  display: flex;
  align-items: center;
  background: #fff;
  border-radius: 12px;
  padding: 18px 20px;
  cursor: pointer;
  box-shadow: 0 1px 3px rgba(0, 0, 0, .05);
  transition: all .25s;
  border: 1px solid transparent;
}
.hospital-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, .1);
  border-color: #e2e8f0;
}
.hospital-card-img {
  width: 72px;
  height: 72px;
  border-radius: 36px;
  overflow: hidden;
  flex-shrink: 0;
  background: #f8fafc;
  border: 2px solid #e2e8f0;
}
.hospital-card-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.hospital-card-body {
  flex: 1;
  padding: 0 18px;
  min-width: 0;
}
.hospital-card-name {
  margin: 0 0 8px;
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
}
.hospital-card-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 6px;
}
.hos-tag {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 4px;
}
.level-tag {
  background: #eff6ff;
  color: #2563eb;
}
.time-tag {
  background: #fef3c7;
  color: #b45309;
}
.hospital-card-address {
  font-size: 12px;
  color: #94a3b8;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.hospital-card-action {
  flex-shrink: 0;
}
.go-btn {
  display: inline-block;
  padding: 8px 20px;
  background: linear-gradient(135deg, #0ea5e9, #2563eb);
  color: #fff;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
  transition: all .2s;
}
.hospital-card:hover .go-btn {
  box-shadow: 0 4px 12px rgba(37, 99, 235, .35);
}

/* ===== 空状态 ===== */
.empty-state {
  text-align: center;
  padding: 60px 0;
  color: #94a3b8;
}
.empty-icon {
  font-size: 48px;
  margin-bottom: 12px;
  display: block;
}

/* ===== 右侧卡片 ===== */
.side-card {
  background: #fff;
  border-radius: 12px;
  padding: 18px 20px;
  margin-bottom: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, .05);
}
.side-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f1f5f9;
}
.side-card-title {
  font-size: 15px;
  font-weight: 600;
  color: #1e293b;
  display: flex;
  align-items: center;
  gap: 6px;
}
.side-title-icon.blue {
  color: #3b82f6;
  font-size: 18px;
}
.side-title-icon.orange {
  color: #f97316;
  font-size: 18px;
}
.side-title-icon.green {
  color: #10b981;
  font-size: 18px;
}
.side-card-more {
  font-size: 12px;
  color: #94a3b8;
  cursor: pointer;
}
.side-card-more:hover {
  color: #2563eb;
}

/* 科室网格 */
.dept-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.dept-item {
  padding: 5px 12px;
  font-size: 12px;
  color: #475569;
  background: #f8fafc;
  border-radius: 6px;
  cursor: pointer;
  transition: all .2s;
}
.dept-item:hover {
  color: #2563eb;
  background: #eff6ff;
}

/* 公告列表 */
.notice-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.notice-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 7px 0;
  cursor: pointer;
}
.notice-dot {
  width: 5px;
  height: 5px;
  border-radius: 50%;
  flex-shrink: 0;
}
.notice-dot.blue {
  background: #3b82f6;
}
.notice-dot.orange {
  background: #f97316;
}
.notice-text {
  font-size: 13px;
  color: #475569;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.notice-item:hover .notice-text {
  color: #2563eb;
}

/* ===== 智能导诊 ===== */
.symptom-card {
  border: 1px solid #d1fae5;
}
.symptom-search {
  margin-bottom: 12px;
}
.symptom-results {
  max-height: 200px;
  overflow-y: auto;
}
.symptom-item {
  padding: 8px 12px;
  margin-bottom: 6px;
  background: #f0fdf4;
  border-radius: 8px;
  border: 1px solid #bbf7d0;
  cursor: pointer;
  transition: all .2s;
}
.symptom-item:hover {
  background: #dcfce7;
}
.symptom-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2px;
}
.symptom-name {
  font-size: 13px;
  font-weight: 500;
  color: #1e293b;
}
.symptom-row i {
  font-size: 11px;
  color: #94a3b8;
}
.symptom-dept {
  font-size: 12px;
  color: #059669;
}
.symptom-empty {
  text-align: center;
  padding: 12px 0;
  font-size: 12px;
  color: #94a3b8;
}
.quick-symptoms {
  padding-top: 6px;
}
.quick-label {
  font-size: 11px;
  color: #94a3b8;
  display: block;
  margin-bottom: 8px;
}
.quick-tag {
  display: inline-block;
  padding: 3px 10px;
  margin: 0 6px 6px 0;
  background: #f0fdf4;
  color: #059669;
  font-size: 12px;
  border-radius: 12px;
  cursor: pointer;
  transition: all .2s;
}
.quick-tag:hover {
  background: #059669;
  color: #fff;
}

/* 响应式 */
@media (max-width: 960px) {
  .main-container {
    flex-direction: column;
  }
  .main-right {
    width: 100%;
  }
  .hero-title {
    font-size: 24px;
  }
  .hero-stats {
    flex-wrap: wrap;
    gap: 16px;
  }
  .stat-divider {
    display: none;
  }
}
</style>