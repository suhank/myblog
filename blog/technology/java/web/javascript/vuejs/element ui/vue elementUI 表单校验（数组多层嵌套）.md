[TOC]



# vue elementUI 表单校验（多层嵌套）

在使用vue element-ui form表单渲染的时候，会遇到这样的数据结构：

```json
{
"title":''123455,
"email":'123456@qq.com',
"extAmountConfig":{
  "amount":2
}
"list": [
            {
                "id": "quis consequat culpa ut pariatur",
                "name": "et quis irure dolore ullamco",
                "ompany": "sunt mollit",
                "address": "anim reprehenderit aliquip labore velit"
            },
            {
                "id": "",
                "name": "laborum magna",
                "company": "mollit esse ipsum quis",
                "address": "cillum dolore ex ut"
            },
        ]
}
```

在遇到某个字段值比如list是个数组，下面多个字段值还需要继续使用rules校验时候，直接给list下面的字段绑定prop="对应的字段值"，是不能校验成功的，解决办法有二：

## 1、在是数组的地方再套一个\<el-form :model="当前子对象" v-for="遍历list这个数组">\</el-form>

  给list数组下的字段直接还是绑定prop名称为原本的名称就可以；

示例代码如下：

```html
<el-form :model="item" v-for="(item,index) in dataFields.list :key="index">
   <el-form-item label="name" prop="name" :rules="{ required: true, message: 'Required', trigger: 'blur' }">
      <el-input placeholder="name" v-model="item.name"></el-input>
   </el-form-item>
</el-form>
```

 

## 2、直接给list数组下对象的字段名称绑定为  **数组下的名称**

示例代码如下：

```html
<div v-for="(item,index) in dataFields.list :key="index">
    <el-form-item label="name" :prop="`list[${index}].name`" :rules="{ required: true, message: 'Required', trigger: 'blur' }">
       <el-input placeholder="name" v-model="item.name"></el-input>
   </el-form-item>
</div>
```

这里list即为上面对象中的数组，datafields是最外层对象。

## 3 、对象嵌套



```html
<el-form-item :label="this.$t('couponTemplate.extAmountConfigAmount')" prop="extAmountConfig.amount"
              :rules="model.formRules['extAmountConfig.amount']">
    <el-input v-model="model.editForm.extAmountConfig.amount" auto-complete="off"
              v-bind:readonly="model.formReadonly"/>
</el-form-item>
```

model


```js

  formRules: any = {
   'extAmountConfig.amount': [
     {required: true, message: i18nUtil.t('message.msgRequired') as string + i18nUtil.t('couponTemplate.extTimeConfig') as string},
     {validator: validate.checkInteger, trigger: 'blur' }
   ],
   sourceType: [
     {required: true, message: i18nUtil.t('message.msgRequired') as string + i18nUtil.t('couponTemplate.sourceType') as string}
   ]
 }
```

参考链接：

https://segmentfault.com/a/1190000014366951







https://www.cnblogs.com/beileixinqing/p/10969828.html