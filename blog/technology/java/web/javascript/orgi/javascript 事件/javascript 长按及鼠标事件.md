[TOC]



# javascript 长按及鼠标事件

## **一、判断鼠标左右键：**

```
<html>
<head>
    <title>js判断鼠标左、中、右键哪个被点击</title>
    <script type="text/javascript">
        function whichButton(event) {
            var btnNum = event.button;

            console.log("event.button==="+event.button);
            /*event.button=== 0：左键, 2：右键，*/

            console.log("event.which==="+event.which);
            /*event.which=== 1:左键，3:右键*/
            if (btnNum == 2) {//2为右键
                console.log("您点击了鼠标右键！");
            } else if (btnNum == 0) {//0为左键
                console.log("您点击了鼠标左键！");
            } else if (btnNum == 1) {//1为中键
                console.log("您点击了鼠标中键！");
            } else {
                console.log("您点击了" + btnNum + "号键，我不能确定它的名称。！");
            }
        }
    </script>
</head>
<body onmousedown="whichButton(event)">
<p>请在文档中点击鼠标。一个消息框会提示出您点击了哪个鼠标按键。</p>
</body>
</html>
```

## **二、鼠标点击事件：**

```
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
    <script>

        /*HTML DOM 事件
        http://www.runoob.com/jsref/dom-obj-event.html
        * */

        function dClick() {
            console.log("您双击了页面！");
            //您按下了页面！
            //您释放了页面！
            //您按下了页面！
            //您释放了页面！
            //您双击了页面！：即第二次释放后触发
        }

        function sClick() {
            console.log("您单击了页面！");
            //您按下了页面！
            //您释放了页面！
            //您单击了页面！//即第一次释放后触发
        }

        function down() {
            console.log("您按下了页面！");
            //按下的一瞬间
        }

        function up() {
            console.log("您释放了页面！");
            //松手的一瞬间
        }
    </script>
</head>

<body ondblclick="dClick()" onmousedown="down()" onmouseup="up()" onclick="sClick()">
<div>
    点我试试?
</div>
</body>


</html>
```

## **三、长按事件一：**

**2秒且松手后触发事件**

```
<!DOCTYPE html>
<html>
<head>
    <script>
        var last = 0;
        var now = 0;

        function mouseDown() {
            last = new Date();
        }

        function mouseUp() {
            now = new Date();
            if (now - last > 2 * 1000) {
                document.getElementById("p1").style.color = "red";
            }
        }
    </script>
</head>
<body>

<p id="p1" onmousedown="mouseDown()" onmouseup="mouseUp()">
    请点击文本！mouseDown() 函数当鼠标按钮在段落上被按下时触发。
    此函数把文本颜色设置为红色。
    mouseUp() 函数在鼠标按钮被释放时触发。mouseUp() 函数把文本的颜色设置为绿色。
</p>
</body>
</html>
```

## **长按事件二：（推荐此种方法）**

通过setInterval()方法可以按周期执行代码段，并返回一个标识性的值（可以理解为周期性调用函数的过程）。调用clearInterval(返回的参数) 方法可以结束该重复性过程。

效果：只要长按时间到达1000毫秒，无论是否弹起鼠标，都会触发。反之，如果不到1000毫秒，鼠标弹起的时候会结束。

clearTimeout() 方法可取消由 setTimeout() 方法设置的 timeout。

**down后指定时间内没有up，触发事件。**

```
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8"/>
    <title></title>
</head>

<body>
<input id="p1" type="button" onmousedown="holdDown()" onmouseup="holdUp()" value="鼠标长按"/>

<script type="text/javascript">
    //申明全局变量
    var timeStart, timeEnd, time;

    //获取此刻时间
    function getTimeNow() {
        var now = new Date();
        return now.getTime();
    }

    //鼠标按下时触发
    function holdDown() {
        //获取鼠标按下时的时间
        timeStart = getTimeNow();

        //setInterval会每100毫秒执行一次，也就是每100毫秒获取一次时间
        time = setInterval(function () {
            timeEnd = getTimeNow();

            //如果此时检测到的时间与第一次获取的时间差有1000毫秒
            if (timeEnd - timeStart > 1000) {
                //便不再继续重复此函数 （clearInterval取消周期性执行）
                clearInterval(time);
                //字体变红
                document.getElementById("p1").style.color = "red";
            }
        }, 100);
    }
    function holdUp() {
        //如果按下时间不到1000毫秒便弹起，
        clearInterval(time);
    }
</script>
</body>
</html>
```

## **参考：**

[Javascript 学习之路：鼠标长按事件](http://www.th7.cn/web/js/201604/162743.shtml)





https://blog.csdn.net/sinat_31057219/article/details/60965045