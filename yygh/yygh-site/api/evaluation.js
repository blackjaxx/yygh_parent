import request from '@/utils/request'

const api_name = '/api/user/evaluation'

export default {
  submitEvaluation(evaluation) {
    return request({
      url: `${api_name}/auth/submit`,
      method: 'post',
      data: evaluation
    })
  },
  getEvaluationList(hoscode, page, limit) {
    return request({
      url: `${api_name}/list/${hoscode}/${page}/${limit}`,
      method: 'get'
    })
  },
  getHospitalRating(hoscode) {
    return request({
      url: `${api_name}/rating/${hoscode}`,
      method: 'get'
    })
  },
  checkEvaluated(orderId) {
    return request({
      url: `${api_name}/auth/check/${orderId}`,
      method: 'get'
    })
  }
}