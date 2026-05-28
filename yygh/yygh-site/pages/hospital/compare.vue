<template>
  <div class="nav-container page-component">
    <div class="nav left-nav">
      <div class="nav-item selected">
        <span class="v-link selected dark" onclick="javascript:window.location='/'">返回首页</span>
      </div>
    </div>
    <div class="page-container">
      <div class="compare-page">
        <div class="compare-header">
          <h2 class="compare-title">医院对比</h2>
          <p class="compare-desc">选择2-3家医院，对比关键信息，帮您做出最佳选择</p>
        </div>

        <div class="compare-select-row">
          <div class="compare-select-item" v-for="(item, idx) in selectedHospitals" :key="idx">
            <el-autocomplete
              class="compare-input"
              v-model="item.query"
              :fetch-suggestions="(q, cb) => searchHospital(q, cb, idx)"
              placeholder="输入医院名称"
              @select="(val) => selectHospital(val, idx)"
              :trigger-on-focus="false"
            />
            <div v-if="item.data" class="selected-hospital-badge">
              <img :src="'data:image/jpeg;base64,' + item.data.logoData" />
              <span>{{ item.data.hosname }}</span>
              <i class="el-icon-close" @click="removeHospital(idx)"></i>
            </div>
          </div>
        </div>

        <div v-if="compareData.length > 0" class="compare-table-wrapper">
          <table class="compare-table">
            <thead>
              <tr>
                <th class="label-col">对比维度</th>
                <th v-for="(h, i) in compareData" :key="i" class="value-col">
                  <img :src="'data:image/jpeg;base64,' + h.logoData" class="compare-logo" />
                  <div class="compare-hosname">{{ h.hosname }}</div>
                </th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td class="label-col">医院等级</td>
                <td v-for="(h, i) in compareData" :key="i" class="value-col">
                  <span class="level-badge">{{ h.param.hostypeString }}</span>
                </td>
              </tr>
              <tr>
                <td class="label-col">地址</td>
                <td v-for="(h, i) in compareData" :key="i" class="value-col address-cell">{{ h.param.fullAddress }}</td>
              </tr>
              <tr>
                <td class="label-col">预约周期</td>
                <td v-for="(h, i) in compareData" :key="i" class="value-col">{{ h.bookingRule.cycle }}天</td>
              </tr>
              <tr>
                <td class="label-col">放号时间</td>
                <td v-for="(h, i) in compareData" :key="i" class="value-col">每天 {{ h.bookingRule.releaseTime }}</td>
              </tr>
              <tr>
                <td class="label-col">退号规则</td>
                <td v-for="(h, i) in compareData" :key="i" class="value-col">
                  <span v-if="h.bookingRule.quitDay == -1">就诊前一工作日{{ h.bookingRule.quitTime }}前</span>
                  <span v-else>就诊当天{{ h.bookingRule.quitTime }}前</span>
                </td>
              </tr>
              <tr>
                <td class="label-col">用户评分</td>
                <td v-for="(h, i) in compareData" :key="i" class="value-col">
                  <span class="rating-stars">
                    <i class="el-icon-star-on" v-for="n in 5" :key="n"
                       :style="{ color: n <= (ratings[i] || 0) ? '#f59e0b' : '#e2e8f0' }"></i>
                    <span class="rating-num">{{ ratings[i] || '-' }}</span>
                  </span>
                </td>
              </tr>
              <tr>
                <td class="label-col">操作</td>
                <td v-for="(h, i) in compareData" :key="i" class="value-col">
                  <span class="compare-go-btn" @click="goHospital(h.hoscode)">立即挂号</span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <div v-if="compareData.length === 0 && selectedHospitals.some(h => h.query)" class="empty-compare">
          <i class="el-icon-search"></i>
          <p>请在上方输入医院名称添加对比</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import '~/assets/css/hospital_personal.css'
import '~/assets/css/hospital.css'
import hospApi from '@/api/hosp'
import evaluationApi from '@/api/evaluation'

export default {
  data() {
    return {
      selectedHospitals: [
        { query: '', data: null },
        { query: '', data: null },
        { query: '', data: null }
      ],
      compareData: [],
      ratings: []
    }
  },
  methods: {
    searchHospital(queryString, cb, idx) {
      if (!queryString || queryString.trim() === '') {
        cb([])
        return
      }
      hospApi.getByHosname(queryString).then(response => {
        const list = (response.data || []).map(item => ({
          ...item,
          value: item.hosname
        }))
        cb(list)
      })
    },
    selectHospital(item, idx) {
      hospApi.show(item.hoscode).then(response => {
        const detail = response.data.hospital
        this.selectedHospitals[idx].data = detail
        this.selectedHospitals[idx].query = item.hosname
        this.refreshCompareData()
      })
    },
    removeHospital(idx) {
      this.selectedHospitals[idx].data = null
      this.selectedHospitals[idx].query = ''
      this.refreshCompareData()
    },
    refreshCompareData() {
      this.compareData = this.selectedHospitals
        .filter(h => h.data)
        .map(h => h.data)
      this.ratings = []
      this.compareData.forEach(h => {
        evaluationApi.getHospitalRating(h.hoscode).then(res => {
          this.ratings.push(res.data ? res.data.average : 0)
        }).catch(() => {
          this.ratings.push(0)
        })
      })
    },
    goHospital(hoscode) {
      window.location.href = '/hospital/' + hoscode
    }
  }
}
</script>

<style scoped>
.compare-page {
  max-width: 1000px;
  margin: 0 auto;
  padding: 24px;
}
.compare-header {
  margin-bottom: 30px;
  text-align: center;
}
.compare-title {
  font-size: 24px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 8px;
}
.compare-desc {
  font-size: 14px;
  color: #94a3b8;
  margin: 0;
}
.compare-select-row {
  display: flex;
  gap: 16px;
  margin-bottom: 30px;
}
.compare-select-item {
  flex: 1;
  min-width: 0;
}
.compare-input {
  width: 100%;
}
.compare-input >>> .el-input__inner {
  height: 44px;
  border-radius: 8px;
}
.selected-hospital-badge {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 10px;
  padding: 10px 14px;
  background: #f0f9ff;
  border: 1px solid #bae6fd;
  border-radius: 8px;
}
.selected-hospital-badge img {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
}
.selected-hospital-badge span {
  flex: 1;
  font-size: 14px;
  font-weight: 500;
  color: #1e293b;
}
.selected-hospital-badge i {
  font-size: 16px;
  color: #94a3b8;
  cursor: pointer;
  padding: 4px;
}
.selected-hospital-badge i:hover {
  color: #ef4444;
}
.compare-table-wrapper {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 1px 6px rgba(0,0,0,.08);
}
.compare-table {
  width: 100%;
  border-collapse: collapse;
}
.compare-table th,
.compare-table td {
  padding: 14px 18px;
  text-align: center;
  border-bottom: 1px solid #f1f5f9;
  font-size: 14px;
}
.compare-table th {
  background: #f8fafc;
  font-weight: 600;
  color: #475569;
}
.label-col {
  width: 130px;
  background: #f8fafc;
  font-weight: 600;
  color: #334155;
  text-align: center;
}
.value-col {
  color: #475569;
}
.compare-logo {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  margin-bottom: 6px;
}
.compare-hosname {
  font-size: 13px;
  font-weight: 600;
  color: #1e293b;
}
.level-badge {
  display: inline-block;
  padding: 3px 12px;
  background: #eff6ff;
  color: #2563eb;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}
.address-cell {
  font-size: 12px;
  line-height: 1.5;
}
.rating-stars {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 2px;
}
.rating-num {
  margin-left: 6px;
  font-weight: 600;
  color: #f59e0b;
  font-size: 14px;
}
.compare-go-btn {
  display: inline-block;
  padding: 6px 20px;
  background: linear-gradient(135deg, #0ea5e9, #2563eb);
  color: #fff;
  border-radius: 16px;
  font-size: 13px;
  cursor: pointer;
  transition: all .2s;
}
.compare-go-btn:hover {
  box-shadow: 0 4px 12px rgba(37,99,235,.35);
}
.empty-compare {
  text-align: center;
  padding: 60px 0;
  color: #94a3b8;
}
.empty-compare i {
  font-size: 48px;
  display: block;
  margin-bottom: 12px;
}
</style>