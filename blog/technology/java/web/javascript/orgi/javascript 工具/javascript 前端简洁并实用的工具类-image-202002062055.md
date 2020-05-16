[TOC]



# 前端简洁并实用的工具类

[火狼1](https://cloud.tencent.com/developer/user/3861934)发表于[前端小而全的知识归纳](https://cloud.tencent.com/developer/column/76044)订阅

## 前言

本文主要从日期,数组,对象,axios,promise和字符判断这几个方面讲工作中常用的一些函数进行了封装,确实可以在项目中直接引用,提高开发效率.

## 1.日期

日期在后台管理系统还是用的很多的,一般是作为数据存贮和管理的一个维度,所以就会涉及到很多对日期的处理

### 1.1 new Date转化为yyyy-MM-dd HH:mm:ss

![img](iimage-202002062055/1620.png)

DatePicker日期选择器默认获取到的日期默认是Date对象,但是我们后台需要用到的是yyyy-MM-dd,所以需要我们进行转化

方法一:将Fri Mar 23 2018 12:19:48 GMT+0800 (国际时间)转化为dd-MM-yyyy HH:mm:ss

```javascript
export const dateToFormat=(date)=>{
    date.toLocaleString("en-US", { hour12: false }).replace(/\b\d\b/g, '0$&').replace(new RegExp('/','gm'),'-')
}
```

方法二: 从element-UI的2.x版本提供了value-format属性,可以直接设置选择器返回的值

![img](iimage-202002062055/1620-20200206205423913.png)

### 1.2 将yyyy-MM-dd转化为new Date()

也就是转化为日期控件可以接受的类型

```javascript
export const forMatToDate=(date)=>{
       let dateArr=date.split(':');
  return new Date(2017,10,19,dateArr[0],dateArr[1],dateArr[2]);
    }
```

> 有个坑,日期中初始化默认比实际设置的值大一

### 1.3 获取当前的时间yyyy-MM-dd HH:mm:ss

没有满10就补0

```javascript
export default const obtainDate=()=>{
 let date = new Date();
      let year = date.getFullYear();
      let month = date.getMonth() + 1;
      let day=date.getDate();
      let hours=date.getHours();
      let minu=date.getMinutes();
      let second=date.getSeconds();
      //判断是否满10
      let arr=[month,day,hours,minu,second];
      arr.forEach(item=>{
        item< 10?"0"+item:item;
      })
      return year+'-'+arr[0]+'-'+arr[1]+' '+arr[2]+':'+arr[3]+':'+arr[4]      
}
```

### 1.4 将时间戳转化为yyyy-MM-dd HH:mm:ss

```javascript
export default const returnTimestamp=(strTime)=>{
  let middleDate=new Date(strTime)
  return middleDate.toLocaleString('zh-CN',{hour12:false}).replace(/\b\d\b/g, '0$&').replace(new RegExp('/','gm'),'-')
})   
```

### 1.5 比较yyyy-MM-dd时间大小

如果单个比较会比较复杂,这里直接处理成Number比较

```javascript
export default const compareTwo=(dateOne,dateTwo)=>{
    return Number(dateOne.replace(/\-/g,""))<Number(dateTwo.replace(/\-/g,""))
}
```

### 1.6 计算两个日期格式为(yyyy-MM-dd)相差几个月

export default const disparityFewMonth = (dateOne, dateTwo) => {

```javascript
    let datesOne = dateOne.split('-').map(item => Number(item));
    let datesTwo = dateTwo.split('-').map(item => Number(item));
    const diff = [0, 0, 0].map((value, index) => {
        return datesOne[index] - datesTwo[index]
    });
    return (diff[0] * 12 + diff[1]) + '月' + diff[2] + '天'
}
```

### 1.7 new Date对象可接受的参数

```javascript
1、new Date("month dd,yyyy hh:mm:ss"); 
2、new Date("month dd,yyyy"); 
3、new Date(yyyy,mth,dd,hh,mm,ss); 注意：这种方式下，必须传递整型；
4、new Date(yyyy,mth,dd); 
5、new Date(ms); 注意：ms:是需要创建的时间和 GMT时间1970年1月1日之间相差的毫秒数；当前时间与GMT1970.1.1之间的毫秒数：var mills = new Date().getTime();
注意:mth:用整数表示月份，从0（1月）到11（12月）
```

### 1.8 生成标识id

格式为时间戳的后8位加4位随机数

```javascript
export default const disparityFewMonth = (dateOne, dateTwo) => {
    let num='';
    for (var i = 0; i < 4; i++) {
        num += Math.floor(Math.random() * 10);
    }
    return imgId = String(new Date().getTime()).slice(-8)+String(num);
```

}

## 2.数组

### 2.1 检测是否是数组

```javascript
export default const judgeArr=(arr)=>{
        if(Array.isArray(arr)){
            return true;
        }
    }
```

### 2.2数组去重set方法

1.常见利用循环和indexOf(ES5的数组方法,可以返回值在数组中第一次出现的位置)这里就不再详写,这里介绍一种利用ES6的set实现去重.

2.set是新怎数据结构,似于数组，但它的一大特性就是所有元素都是唯一的.

3.set常见操作 大家可以参照下面这个:[新增数据结构Set的用法](https://www.cnblogs.com/kongxianghai/p/7250248.html)

4.set去重代码

```javascript
export const changeReArr=(arr)=>{
    return Array.from(new Set([1,2,2,3,5,4,5]))//利用set将[1,2,2,3,5,4,5]转化成set数据,利用array from将set转化成数组类型
}

或者
export const changeReArr=(arr)=>{
    return [...new Set([1,2,2,3,5,4,5])]//利用...扩展运算符将set中的值遍历出来重新定义一个数组,...是利用for...of遍历的
}
```

Array.from可以把带有lenght属性类似数组的对象转换为数组，也可以把字符串等可以遍历的对象转换为数组，它接收2个参数，转换对象与回调函数,...和Array.from都是ES6的方法

### 2.3 纯数组排序

常见有冒泡和选择,这里我写一下利用sort排序

```javascript
 export const orderArr=(arr)=>{
        arr.sort((a,b)=>{
            return a-b //将arr升序排列,如果是倒序return -(a-b)
        })
    }
```

### 2.4 数组对象排序

```javascript
export const orderArr=(arr)=>{
        arr.sort((a,b)=>{
            let value1 = a[property];
            let value2 = b[property];
            return value1 - value2;//sort方法接收一个函数作为参数，这里嵌套一层函数用
            //来接收对象属性名，其他部分代码与正常使用sort方法相同
        })
    }      
```

### 2.5 数组中的最大值

```javascript
export const maxArr=(arr)=>{
    return Math.max(...arr)
 }
 
 或者export const maxArr=(arr)=>{
    return Math.max.apply(null,arr)
 }
```

### 2.6 数组的"短路运算"every和some

数组短路运算这个名字是我自己加的,因为一般有这样一种需求,一个数组里面某个或者全部满足条件,就返回true

```javascript
情况一:全部满足

    export const allTrueArr=(arrs)=>{
          return arr.every((arr)=>{
             return arr>20;//如果数组的每一项都满足则返回true,如果有一项不满足返回false,终止遍历
          })  
    }

情况二:有一个满足
export default const OneTrueArr=(arrs)=>{
      return arr.some((arr)=>{
         return arr>20;//如果数组有一项满足则返回true,终止遍历,每一项都不满足则返回false
      })  
}
```

以上两种情景就和||和&&的短路运算很相似,所以我就起了一个名字叫短路运算,当然两种情况都可以通过遍历去判断每一项然后用break和return false 结束循环和函数.

### 2.7 数组过滤filter和处理map方法

filter:过滤满足某一条件的数组值,并返回新数组

```javascript
export const filterArr = (arr, operator, judgeVal) => {
      return arr.filter(item => {
        if (operator == '>') {
          return item > judgeVal;
        } else if (operator == '<') {
          return item > judgeVal;
        } else if (operator == '==') {
          return item == judgeVal;
        }
      })
    }
```

map:对数组进行处理返回一个新数组

```javascript
export const mapArr = (arr) => {
  return arr.map(item => item + 10;)//箭头函数的{}如果省略,则会默认返回,不用写return
 }
```

### 2.8将多维数组转化为一维的类

```javascript
Array.prototype.flat = function() {
    var arr = [];
    this.forEach((item,idx) => {
        if(Array.isArray(item)) {
            arr = arr.concat(item.flat()); //递归去处理数组元素
        } else {
            arr.push(item)   //非数组直接push进去
        }
    })
    return arr;   //递归出口
}
测试用例：
arr = [[2],[[2,3,[4,5,[7,8]]],[2]],3,4]
console.log(arr.flat());
```

## 3.对象

### 3.1 对象遍历

```javascript
export const traverseObj=(obj)=>{
        for(let variable in obj){
        //For…in遍历对象包括所有继承的属性,所以如果
         //只是想使用对象本身的属性需要做一个判断
        if(obj.hasOwnProperty(variable)){
            console.log(variable,obj[variable])
        }
        }
    }
```

### 3.2 对象的数据属性

1.对象属性分类:数据属性和访问器属性;

2.数据属性:包含数据值的位置,可读写,包含四个特性包含四个特性：

```javascript
configurable：表示能否通过delete删除属性从而重新定义属性，能否修改属性的特性，或能否把属性修改为访问器属性，默认为true
 enumerable:表示能否通过for-in循环返回属性
 writable：表示能否修改属性的值
 value：包含该属性的数据值。默认为undefined
```

3.修改数据属性的默认特性,利用Object.defineProperty()

```javascript
 export const modifyObjAttr=()=>{
  let person={name:'张三',age:30};
  Object.defineProperty(person,'name',{
    writable:false,
    value:'李四',
    configurable:false,//设置false就不能对该属性修改
    enumerable:false
  })
} 
```

### 3.3 对象的访问器属性

1.访问器属性的四个特性:

```javascript
configurable：表示能否通过delete删除属性从而重新定义属性，能否修改属性的特性，或能否把属性修改为访问器属性，默认为false

 enumerable:表示能否通过for-in循环返回属性,默认为false

 Get：在读取属性时调用的函数,默认值为undefined

 Set：在写入属性时调用的函数,默认值为undefined 
```

2.定义: 访问器属性只能通过要通过Object.defineProperty()这个方法来定义

```javascript
export const defineObjAccess=()=>{
let personAccess={
    _name:'张三',//_表示是内部属性,只能通过对象的方法修改
    editor:1
  }
  Object.defineProperty(personAccess,'name',{
    get:function(){
      return this._name;
    },
    set:function(newName){
      if(newName!==this._name){
        this._name=newName;
        this.editor++;
      }
    }
    //如果只定义了get方法则改对象只能读
  })
}
```

vue中最核心的响应式原理的核心就是通过defineProperty来劫持数据的getters和setter属性来改变数据的

### 3.4对象或对象数组的深度拷贝

原生方法一：

```javascript
export const deepClone=function(origin,target){
    var target = target || {}; //定义target
    for(var key in origin) {  //遍历原对象
        if(origin.hasOwnProperty(key)) {
            if(Array.isArray(origin[key])) { //如果是数组
                target[key] = [];
                deepClone(origin[key],target[key]) //递归
            } else if (typeof origin[key] === 'object' && origin[key] !== null) {
                target[key] = {};
                deepClone(origin[key],target[key]) //递归
            }
            target[key] = origin[key];
        }
    }
    return target;
}
```

方法二：Object.assign

```javascript
Object.assign(objOne,objTwo);
//该方法是一个伪深度拷贝，如果改变对象里面的数组值还是会改变被拷贝的值
```

方法三：JSON.stringify let objNew=JSON.parse(JSON.stringify(obj)) ;  //可以实现深度拷贝

## 3.5 找出字符中出现频次最多的字符

```javascript
export const findMaxStr=(str)=>>{
    let o = {};
    for (let char of str) {
      if (o[char]) { //char就是对象o的一个属性，o[char]是属性值，o[char]控制出现的次数
        o[char]++; //次数加1
      } else {
        o[char] = 1; //若第一次出现，次数记为1
      }
    }
    console.log(o); //输出的是完整的对象，记录着每一个字符及其出现的次数
    //遍历对象，找到出现次数最多的字符和次数
    let max = 0;
    let maxChar = null;
    for (let key in o) {
      if (max < o[key]) {
        max = o[key]; //max始终储存次数最大的那个
        maxChar = key; //那么对应的字符就是当前的key
      }
    }
    console.log("最多的字符是" + maxChar);
    console.log("出现的次数是" + max);
}
```

## 3.6 连续的对象数组插入其他类型

1.要求 一个对象数组objArr,类似[{type:'text',content:''},{type:'img',content:''},{type:'img',content:''},], 要求数据结构是一个type为text和type为img的项交替出现

2.算法思路: 定义一个新对象数组,比较最后一项的type是否和当前项type相等,不等则push该项 如果相等就插入另一个类型

```javascript
export const alternateObj=(objChange)=>{
    let objRule = [];
    objChange.map((item, index) => {
      //判断是否有连续同类型数据
      if (!index) {
        objRule.push(item);
      } else {
        // console.log("objRule值为", objRule);
        // console.log("item值为", item);
        if (objRule[objRule.length - 1].type == item.type) {
          if (item.type == "IMG") {
            objRule.push({ type: "TEXT", content: "" }, item);
          } else {
            objRule.push({ type: "IMG", content: "" }, item);
          }
        } else {
          objRule.push(item);
        }
      }
    });
    return objRule;
  }
```

## 4.axios

### 4.1 axios的get方法

```javascript
export const getAjax= function (getUrl,getAjaxData) {
  return axios.get(getUrl, {
    params: {
      'getAjaxDataObj1': getAjaxData.obj1,//obj1为getAjaxData的一个属性
      'getAjaxDataObj2': getAjaxData.obj2
    }
  }).then(data=>{
      //成功返回
  }).catch(err=>{
      //错误返回
  })
}
```

### 4.2 axios的post方法

```javascript
export const postAjax= function (getUrl,postAjaxData) {
  return axios.post(postUrl, {
      'postAjaxDataObj1': postAjaxData.obj1,//obj1为postAjaxData的一个属性
      'postAjaxDataObj2': postAjaxData.obj2
  }).then(data=>{
      //成功返回
  }).catch(err=>{
      //错误返回
  })
}
```

### 4.3 axios的拦截器

主要分为请求和响应两种拦截器,请求拦截一般就是配置对应的请求头信息(适用与常见请求方法,虽然ajax的get方法没有请求头,但是axios里面进行啦封装),响应一般就是对reponse进行拦截处理,如果返回结果为[]可以转化为0

1.请求拦截:将当前城市信息放入请求头中

```javascript
axios.interceptors.request.use(config => {
  config.headers.cityCode = window.sessionStorage.cityCode //jsCookie.get('cityCode')
  return config
}),
```

2.响应拦截:处理reponse的结果

```javascript
axios.interceptors.response.use((response) =>{
  let data = response.data
  if(response.request.responseType === 'arraybuffer'&&!data.length){
    reponse.date=0
  }
})
```

## 5.promise

promise是一种封装未来值的易于复用的异步任务管理机制,主要解决地狱回调和控制异步的顺序

### 5.1 应用方法一

```javascript
export const promiseDemo=()=>{
new Promise((resolve,reject)=>{
    resolve(()=>{
        let a=1;
        return ++a;
    }).then((data)=>{
        console.log(data)//data值为++a的值
    }).catch(()=>{//错误执行这个

    })
})
}
```

### 5.2 应用方法二

```javascript
export const promiseDemo=()=>{
Promise.resolve([1,2,3]).then((data)=>{//直接初始化一个Promise并执行resolve方法
    console.log(data)//data值为[1,2,3]
})
}
```

## 6.文本框的判断

### 6.1 全部为数字

方法一(最简单):

```javascript
export const judgeNum1=(num1)=>{
    if(typeof num1=='number'){
        return true;
    }else{
        return false;
    }
}
```

方法二:isNaN

```javascript
export const judgeNum1=(num1)=>{
    if(!isNaN(num1)){
        return true;
    }else{
        return false;
    }
}
```

注:当num1为[]（空数组）、“”（空字符串)和null会在过程中转换为数字类型的0,所以也会返回false,从而判断为数字,所以可以将用typeof将以上特殊情况剔除.

方法三:正则

```javascript
export const judgeNum1=(num1)=>{
  let reg=/^[0-9]*$/
  if(!reg.test(num1)){
    console.log('num1是0-9')
  }
}
```

### 6.2 只能为数字或字母

这个用正则判断 定义一个正则:let reg=/^[0-9a-zA-Z]*$/g

### 6.3 只能为数字,字母和英文逗号

因为存在输入多个编号,以英文逗号分隔的情况 定义一个正则:let reg=/^[0-9a-zA-Z,]*$/g

### 6.4 判断输入的位数不超过16位

直接利用字符串新加的length属性来判断

```javascript
export const judgeNum1=(num1)=>{
      if(num1.length>16){
        console.log('num1超过16位')
     }
 }
```

### 6.5 去掉字符左右空格

export const trimLeOrRi=(str)=>{ //删除左右两端的空格 return str.replace(/(^s*)|(s*$)/g, ""); }

## 7. 检测是浏览器还是客户端

其实本质都是利用navigator对象的userAgent属性 export const checkIosOrAndriod=(appMethod)=>{

```javascript
  let ua_ios = window.navigator.userAgent.toLowerCase().match(/lan1.0_iOS/i),
  ua_android = window.navigator.userAgent.toLowerCase().match(/RRJC3.0_Android/i),
  isAndroid = u.indexOf('Android') > -1 || u.indexOf('Linux') > -1; //android终端或者uc浏览器
  isiOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); //ios终端
  ios_browser=
  if(ua_ios || ua_android){
    appMethod;//调用原生的方法
  }else if(isAndroid){      
    return '安卓browser访问'
  }else if(isIOS){      
    return 'ios的browser访问'
  }
},
```

## 8.数字的处理

### 8.1四舍五入取值

```javascript
export const getFloat=(molecular,denominator,n){//molecular是分子，denominator是分母，n是保留的小数位
   let number=denominator==0?0:molecular/denominator;    
    n = n ? parseInt(n) : 0; 
    if (n <= 0) return Math.round(number); 
    number = Math.round(number * Math.pow(10, n)) / Math.pow(10, n);
    return number;   
}  
```

## 9.客户端类型

### 9.1 iPhoneX,iPhoneXS

```javascript
export let checkIphoneX=(function judgeIphone(){
    // iPhone X、iPhone XS
    let isIPhoneX = /iphone/gi.test(window.navigator.userAgent) && window.devicePixelRatio && window.devicePixelRatio === 3 && window.screen.width === 375 && window.screen.height === 812;
    // iPhone XS Max
    let isIPhoneXSMax = /iphone/gi.test(window.navigator.userAgent) && window.devicePixelRatio && window.devicePixelRatio === 3 && window.screen.width === 414 && window.screen.height === 896;
    // iPhone XR
    let isIPhoneXR = /iphone/gi.test(window.navigator.userAgent) && window.devicePixelRatio && window.devicePixelRatio === 2 && window.screen.width === 414 && window.screen.height === 896;
    return isIPhoneX || isIPhoneXSMax || isIPhoneXR;
})()
```

### 9.2 IOS和Android

```javascript
export function checkAgent () {
    var u = navigator.userAgent,
      Agent = '';
 
    var isAndroid = u.indexOf('Android') > -1 || u.indexOf('Linux') > -1; //android终端或者uc浏览器
    var isiOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); //ios终端
    if (isAndroid) {
      Agent = 'Android';
    } else if (isiOS) {
      Agent = 'IOS';
    }
    return Agent;
  }
```

### 9.3 判断是横屏

```javascript
export function judgeHOrV() {
      if (window.orientation == 180 || window.orientation == 0) {
          return 'landscape'
      }
    if (window.orientation == 90 || window.orientation == -90) {
         return 'portraitScreen'
   }
  }
```

## 结束

很开心你还能看到这里,这些类可能你现在用不到,但可以先收藏着. 大家可以一起交流,下次项目开发直接拿过去用,现在3月项目比较赶,这个真的可以提高开发效率哦!祝大家新年快乐哒.





https://cloud.tencent.com/developer/article/1414342