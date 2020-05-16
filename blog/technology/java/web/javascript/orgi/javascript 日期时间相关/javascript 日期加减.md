# javascript 日期加减

 

JS中的日期加减使用以下方式：

var date = new Date();

对日期加减：

date.setDate(date.getDate()+n);

对月加减：

date.setMonth(date.getMonth()+n);

对年加减：

date.setFullYear(date.getFullYear()+n);

对小时、周等，都可以使用类似的方式修改。

同时如果对日加减的时候跨越了月、年，那么JS的date类型会自动的处理跨越问题，不需要我们处理。





https://blog.csdn.net/li_xiao_dai/article/details/20123173