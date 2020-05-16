# vue v-bind(:)弹窗默认值

:hidden

```html
<modal :hidden="hiddenModal==undefined?true:hiddenModal" title="填写备注" confirm-text="确定" cancel-text="取消"
       @cancel="editRemarkCancel"
       @confirm="editRemarkConfirm">
    <textarea placeholder="请填写备注信息" v-model="orderRemark"
              class="remarkInputClass"/>
</modal>
```

如果没有默认值,默认是显示. 页面切换会先出现 提示框,然后再隐藏

