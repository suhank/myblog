# element-ui form表单里面label文字加空格

```html
<el-form-item prop="bjrxm">
     <label slot="label">报&nbsp;&nbsp;警&nbsp;&nbsp;人</label>
     <el-input @change="valueChange" :disabled="disabled" size="small" v-model.trim="form.name" />
</el-form-item>
```

或者

```html
<el-form-item prop="bjrxm">
  	 <template v-slot:label>报&nbsp;&nbsp;警&nbsp;&nbsp;人</template>
     <el-input @change="valueChange" :disabled="disabled" size="small" v-model.trim="form.name" />
</el-form-item>
```



https://juejin.im/post/5d54fef451882508d738a54a