# css position_让两个div重叠_图片上叠加一个2%的黑色,让白色商品看得到区域

 

 

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
    <style>

        .outside-div {
            width: 100px;
            height: 100px;
            /*background: #29ff3a;*/
            /*position: relative*/
            /*padding: 10px;*/
        }

        .inside-img {
            /*如果原来图片有float 则需要把相关配置复制到outside-div*/
            width: 100px;
            height: 100px;
            position: absolute;
        }

        .inside-div {
            width: 100px;
            height: 100px;
            position: absolute;
            background-color: rgba(0, 0, 0, 0.2); 
            border-radius: 15px;
        }

    </style>
</head>
<body>

<div class="outside-div">
    <!--图片层-->
    <img class="inside-img" src="img/e93a908d1823fb7c66453e55b25ec136@_320w_320h_1e_1c_0i_90Q_1x_2o.jpg">
    <!--遮罩层 放下面会在上面那一层的上面,否则得设置 z-index-->
    <div class="inside-div"></div>
</div>

</body>
</html>
```



