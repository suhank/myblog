[TOC]



# vue项目国际化 vue-i18n 使用

/Users/jerryye/backup/studio/AvailableCode/web/javascript/vue/vue2_orgi_demo/example/vue-i18n

## 目的

项目需要支持多语言，我们需要提取出项目中使用的静态文本，使用语言包进行管理， 当切换语言设置的时候，可以自动切换整个项目的文字显示。

发现Vue项目中有对应的组件`vue-i18n`，而且对项目的代码修改不大，于是就使用了这个组件去修改项目中的代码。

## 安装

```
// script 引入
<script src="https://unpkg.com/vue/dist/vue.js"></script>
<script src="https://unpkg.com/vue-i18n/dist/vue-i18n.js"></script>


// npm 安装
npm install vue-i18n

// yarn 安装
yarn add vue-i18n 
```

一般一个项目中使用都是通过安装包的方式去运行的，`script`引入的较少。

## 使用

### 项目中配置i18n

```
import VueI18n from 'vue-i18n'
Vue.use(VueI18n)

const i18n = new VueI18n({
    local: 'cn', // 设置语言
    messages // 语言包
})

new Vue({
    el: '#app',
    ...
    i18n
})

// messages 大概的使用格式
    const messages = {
        cn: {
            name: '名字'
        },
        us: {
            name: 'Name'
        }
    }
```

### 使用i18n

#### 简单使用

```
// html 需要使用 {{}} 将 name包装起来
{{$t('name')}}

// js
$t('name')
```

#### 可传入变量

```
// 命名传入参数
// messages:
{
    cn: {
        name: '名字：{name}'
    },
    us: {
        name: 'Name: {name}'
    }
}

$t('name', {name: 'Jack'}) // Name：Jack


// 列表传入参数
// messages: 
{
    cn: {
        name: '名字：{0}{1}'
    },
    us: {
        name: 'Name: {0}{1}'
    }
}

// array
$t('name', ['Jack', 'Job'])
// object
$t('name', {'0':'Jack', '1': 'Job'])
复制代码
```

#### 复数格式

使用分隔符 `|`

```
// messages: 
{
    us: {
        car: 'car | cars | {count} cars'
    }
}

$tc('car', x) // 选用不同的car类型

复制代码
```

#### 时间格式

```
const dateTimeFormats = {
  'us': {
    short: {
      year: 'numeric', month: 'short', day: 'numeric'
    },
    long: {
      year: 'numeric', month: 'short', day: 'numeric',
      weekday: 'short', hour: 'numeric', minute: 'numeric'
    }
  },
  cn: {
      xxx
  }
}

// 需要放入配置项中
const i18n = new VueI18n({
    locale: '',
    messages,
    dateTimeFormats
})

// 使用
$d(new Date(), 'short')
$d(new Date(), 'long')
$d(new Date(), 'short', 'cn')
复制代码
```

#### 金额符号

```
const numberFormats = {
  'en-US': {
    currency: {
      style: 'currency', currency: 'USD'
    }
  },
  'ja-JP': {
    currency: {
      style: 'currency', currency: 'JPY', currencyDisplay: 'symbol'
    }
  }
}

// 同样也要加入配置项中
const i18n = new VueI18n({
  numberFormats
})

// 使用
$n(100, 'currency') // $100.00 
$n(100, 'currency', 'ja-JP') // ￥100

复制代码
```

#### 提供一个默认语言设置`fallbackLocale`

当某个语言不存在时，提供一个默认全的语言去处理

```
const messages = {
  cn: {
    name: 'Name:'
  }
  us: {
  }
}

// us 的语言包中不存在 name , 我们默认cn，当us不存在时，使用cn的值
const i18n = new VueI18n({
  locale: 'us',
  fallbackLocale: 'cn',
  messages
})
 
```

#### `v-t` 可以用于变量的引用，类似于`$t`

`v-t` 指令

```html
// 官网的列子
new Vue({
  i18n: new VueI18n({
    locale: 'en',
    messages: {
      en: { hello: 'hi {name}!' },
      ja: { hello: 'こんにちは、{name}！' }
    }
  }),
  computed: {
    nickName () { return 'kazupon' }
  },
  data: { path: 'hello' }
}).$mount('#object-syntax')


<div id="object-syntax">
  <!-- literal -->
  <p v-t="{ path: 'hello', locale: 'ja', args: { name: 'kazupon' } }"></p>
  <!-- data biniding via data -->
  <p v-t="{ path: path, args: { name: nickName } }"></p>
</div>

<div id="object-syntax">
  <p>こんにちは、kazupon！</p>
  <p>hi kazupon!</p>
</div> 
```

`$t` 与 `v-t` 对比

- `$t` 是方法调用，`v-t` 是一个指令
- `v-t` 性能比`$t`更好，有自定义指令的缓存
- `$t` 使用更灵活，使用方法更简单

还有一些其他的用法，具体的请参考[官方文档](https://link.juejin.im/?target=https%3A%2F%2Fkazupon.github.io%2Fvue-i18n)。。

切换语言的话，可以使用内置变量：

```
// 通过设置 locale 来切换语言
this.$i18n.locale = cn | us
复制代码
```

#### 插入组件

如果遇到这样的场景，如何去处理？

```
<p>I accept xxx <a href="/term">Terms of Service Agreement</a></p>
复制代码
```

我的第一反应是分成**两个字段**，a标签不属于翻译的内容，只要写成：

```
<p>{{ $t('xx1') }}<a href="/term">{{ $t('xx2') }}</a></p>
复制代码
```

看了官网的介绍，说这种处理太笨拙了，可以通过**组件**的方式去处理

```
// 这里使用了i81n 组件
<i18n path="term" tag="label" for="tos">
    <a :href="url" target="_blank">{{ $t('tos') }}</a>
</i18n>

const messages = {
  en: {
    tos: 'Term of Service',
    term: 'I accept xxx {0}.'
  }
}

new Vue({
  el: '#app',
  i18n,
  data: {
    url: '/term'
  }
})
复制代码
```

可以看到，仍然使用了两个变量存储信息，但是通过`tag`来生产标签，`path`来制定标签的内容。

这种方式来说，使用起来较为复杂，相比使用**两个$t('')**的方式，对我来说可能暂时不需要，因为没有看实际的实现方法，没有对比过性能，无法直接给出结论，后续深入的话，还需要仔细对比。

更高级的用法，可以控制html元素的**插入位置**，通过`place`来指定出现在html中的位置。

```
<i18n path="info" tag="p">
    <span place="limit">{{ changeLimit }}</span>
    <a place="action" :href="changeUrl">{{ $t('change') }}</a>
</i18n>

const messages = {
  en: {
    info: 'You can {action} until {limit} minutes from departure.',
    change: 'change your flight',
    refund: 'refund the ticket'
  }
}

const i18n = new VueI18n({
  locale: 'en',
  messages
})
new Vue({
  i18n,
  data: {
    changeUrl: '/change',
    refundUrl: '/refund',
    changeLimit: 15,
    refundLimit: 30
  }
}).$mount('#app')

// result
<p>
    You can <a href="/change">change your flight</a> until <span>15</span> minutes from departure.
</p>

复制代码
```

保留了语句的一体性，但是如果只是针对名词进行多语言的翻译，不对语法进行要求的话，可能使用不到。

### 动态加载语言包

一次加载所有的语言包是没有必要的，特别是语言包过的情况下，之前我也提出了这个问题，发现官网上是给了解决方式的。

```
//i18n-setup.js
import Vue from 'vue'
import VueI18n from 'vue-i18n'
import messages from '@/lang' // 语言包的地址，随项目本身设置修改
import axios from 'axios' // 根据项目中使用api请求模块去设置，不一定是axios

Vue.use(VueI18n)

export const i18n = new VueI18n({
  locale: 'en', // set locale
  fallbackLocale: 'en', // 默认语言设置，当其他语言没有的情况下，使用en作为默认语言
  messages // set locale messages
})

const loadedLanguages = ['en'] // our default language that is prelaoded 

function setI18nLanguage (lang) {
  i18n.locale = lang
  axios.defaults.headers.common['Accept-Language'] = lang // 设置请求头部
  document.querySelector('html').setAttribute('lang', lang) // 根元素增加lang属性
  return lang
}

export function loadLanguageAsync (lang) {
  if (i18n.locale !== lang) {
    if (!loadedLanguages.includes(lang)) {
      return import(/* webpackChunkName: "lang-[request]" */ `@/lang/${lang}`).then(msgs => {
        i18n.setLocaleMessage(lang, msgs.default)
        loadedLanguages.push(lang)
        return setI18nLanguage(lang)
      })
    } 
    return Promise.resolve(setI18nLanguage(lang))
  }
  return Promise.resolve(lang)
}


// 在vue-router的beforeEach的全局钩子了处理
router.beforeEach((to, from, next) => {
  const lang = to.params.lang
  loadLanguageAsync(lang).then(() => next())
})
复制代码
```

### 语言包的生成 & 替换项目中原有的静态文本

这一步最关键，

- 抽离出所有出现的汉字
- 替换成`$t('xxx')`,`$n`,`$d`,`v-t`等，根据合适的情况自己选择
- 维护多个版本的语言文件

语言包这边是这么处理的，项目下新增一个目录languages

```
--languages
    --lib
        -- cn.js // 中文语言包
        -- us.js // 英文语言包
        -- .. // 其他语言，暂未实践
    -- index.js // 导出语言包
复制代码
```

`cn.js`

```
export default {
    common: {
        message: '消息'
    },
    xxx: {

    }
}
复制代码
```

`us.js`

```
export default {
    common: {
        message: 'Messages'
    },
    xxx: {

    }
}
复制代码
```

`index.js`

```
import cn from './lib/cn.js'

export default {
    cn,
    us
}
复制代码
```

替换文本

```
<template>
    ...
    <div>{{$t('message')}}</div>
    ...
</template>

复制代码
```

## 问题

- 不同的语言，格式不同，长度不同，可能需要调整项目的样式，以保正常
- 对于一个已经在使用的项目，生成语言包这一步工作量大，浪费时间





https://juejin.im/post/5aa7e18ff265da2384404334