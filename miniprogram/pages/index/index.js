// index.js
Page({
  data: {
    msg: 'Weixin',
    nickname: '',
    url: '',
    code: ""
  },
  // 获取微信用户头像和昵称
  getUserInfo() {
    wx.getUserProfile({
      desc: "获取用户信息",
      success: (res) => {
        console.log(res.userInfo);
        // 为数据赋值
        this.setData({
          nickname: res.userInfo.nickName,
          url: res.userInfo.avatarUrl
        })
      }
    })
  },
  // 获取微信登录授权码
  wxPermission() {
    wx.login({
      success: (res) => {
        console.log(res.code);
        this.setData({
          code: res.code
        });
      }
    });
  },
  // 发送请求
  sendRequest() {
    wx.request({
      url: 'http://localhost:8080/user/shop/status',
      method: 'GET',
      success: (res) => {
        console.log(res.data);
      }
    })
  }
})