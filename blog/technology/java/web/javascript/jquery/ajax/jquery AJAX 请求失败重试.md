# jquery AJAX 请求失败重试

伪代码：

```javascript
$(document).ajaxError(function(e, xhr, options, error) {
  xhr.retry()
})
```

更好的是某种指数回退



## 答案

```js
$.ajax({
    url : 'someurl',
    type : 'POST',
    data :  ....,   
    tryCount : 0,
    retryLimit : 3,
    success : function(json) {
        //do something
    },
    error : function(xhr, textStatus, errorThrown ) {
        if (textStatus == 'timeout') {
            this.tryCount++;
            if (this.tryCount <= this.retryLimit) {
                //try again
                $.ajax(this);
                return;
            }            
            return;
        }
        if (xhr.status == 500) {
            //handle error
        } else {
            //handle error
        }
    }
});
```

http://stackoverflow.com/questions/10024469/whats-the-best-way-to-retry-an-ajax-request-on-failure-using-jquery





https://codeday.me/bug/20170719/42889.html