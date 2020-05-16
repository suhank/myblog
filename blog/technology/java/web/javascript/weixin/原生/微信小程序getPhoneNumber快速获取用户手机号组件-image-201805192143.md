# 微信小程序getPhoneNumber快速获取用户手机号组件

 

[小程序](http://www.wxapp-union.com/)中有很多地方都会用到注册用户信息的地方，用户需要填写手机号等，有了这个组件可以快速获取微信绑定手机号码，无须用户填写。

1.getPhoneNumber这个组件通过button来实现（别的标签无效）

。将button中的open-type=“getPhoneNumber”,并且绑定bindgetphonenumber事件获取回调。

```
  <button open-type="getPhoneNumber" bindgetphonenumber="getPhoneNumber"> </button>
```

2.在使用这个组件之前必须先调用login接口，如果没有调用login点击button时会提示先调用login。

```
App({  
    onLaunch: function () {  
        wx.login({  
            success: function (res) {  
                if (res.code) {  
                    //发起网络请求  
                    console.log(res.code)  
                } else {  
                    console.log('获取用户登录态失败！' + res.errMsg)  
                }  
            }  
        });  
    }  
}) 
```

3.通过bindgetphonenumber绑定的事件来获取回调。回调的参数有三个， 

 errMsg：用户点击取消或授权的信息回调。  

iv：加密算法的初始向量（如果用户没有同意授权则为undefined）。 

 encryptedData： 用户信息的加密数据（如果用户没有同意授权同样返回undefined）[![img](image-201805192143/074715iuw16szqaugxqlvb.jpg)](http://www.wxapp-union.com/data/attachment/portal/201708/30/074715iuw16szqaugxqlvb.jpg) 

```
getPhoneNumber: function(e) {   
    console.log(e.detail.errMsg)   
    console.log(e.detail.iv)   
    console.log(e.detail.encryptedData)   
    if (e.detail.errMsg == 'getPhoneNumber:fail user deny'){  
      wx.showModal({  
          title: '提示',  
          showCancel: false,  
          content: '未授权',  
          success: function (res) { }  
      })  
    } else {  
      wx.showModal({  
          title: '提示',  
          showCancel: false,  
          content: '同意授权',  
          success: function (res) { }  
      })  
    }  
  }  
```

4.最后我们需要根据自己的业务逻辑来进行处理，如果用户不同意授权的话可能我们会有一个让他手动输入的界面，如果不是强制获取手机号的话可以直接跳转页面进行下一步。（用户不同意授权errMsg返回‘getPhoneNumber:fail user deny’）  

5.用户同意授权，我们可以根据login时获取到的code来通过后台以及微信处理拿到session_key，最后通过app_id，session_key,iv,encryptedData（用户同意授权errMsg返回‘getPhoneNumber:ok’）  

6.解密的方法可以去微信官方开发文档查看，有很详细说明。





[加密数据解密算法（官方文档）](https://mp.weixin.qq.com/debug/wxadoc/dev/api/signature.html)





http://www.wxapp-union.com/portal.php?mod=view&aid=2941