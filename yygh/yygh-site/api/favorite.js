import request from '@/utils/request'

const api_name = '/api/user/favorite'

export default {
  getFavoriteList() {
    return request({
      url: `${api_name}/auth/list`,
      method: 'get'
    })
  },
  checkFavorite(hoscode) {
    return request({
      url: `${api_name}/auth/check/${hoscode}`,
      method: 'get'
    })
  },
  addFavorite(hoscode, hosname) {
    return request({
      url: `${api_name}/auth/add/${hoscode}/${encodeURIComponent(hosname)}`,
      method: 'post'
    })
  },
  cancelFavorite(hoscode) {
    return request({
      url: `${api_name}/auth/cancel/${hoscode}`,
      method: 'delete'
    })
  }
}