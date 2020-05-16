[TOC]



#  java通过反射批量生成set设置值

现在可以直接使用idea插件: GenerateAllSetter

## 简介

对于大批量属性的类，里面有有很多属性，这个时候一个个写还是很痛苦的，通过java反射来获取这些类里面的方法，从而得到自己想要的set属性的字符串再好不过了。 

下面是一个demo设置值的，直接反射生成即可 

## 执行结果

```java
project.setName("name");
project.setSeatPicUrl("seatPicUrl");
project.setId("id");
project.setCategoryID("categoryID");
project.setSonCategoryID("sonCategoryID");
project.setSiteID("siteID");
project.setSiteName("siteName");
project.setDescription("description");
project.setSubHead("subHead");
project.setIsETicket("isETicket");
project.setMinPrice("minPrice");
project.setMaxPrice("maxPrice");
project.setPriceStr("priceStr");
project.setSellEndTime("sellEndTime");
project.setShowTime("showTime");
project.setCanSell("canSell");
project.setIsTest("isTest");
project.setVenueName("venueName");
project.setVenueID("venueID");
project.setIsXuanZuo("isXuanZuo");
project.setUpdateTime("updateTime");
project.setStatus("status");
project.setPerformIdList("performIdList");
```



## 反射工具类

```java
/**
 * @author: (le.qiao)
 * @e-mail: qiaolevip@gmail.com
 * @myblog: <a href="http://qiaolevip.iteye.com">http://qiaolevip.iteye.com</a>
 * @date: 2012-11-30
 *
 */

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectBeanSetUtils {

    public static void main(String[] args) throws Exception {
        /*
         * 实列化类 方法2
         */
        Project bean = new Project();
        // bean.setAge(100);
        // bean.setBirthday(new Date());
        // bean.setName("武汉");

        // 得到类对象
        @SuppressWarnings("rawtypes")
        Class cls = (Class) bean.getClass();

        /*
         * 得到类中的所有属性集合
         */
        Field[] fs = cls.getDeclaredFields();
        for (int i = 0; i < fs.length; i++) {
            Field f = fs[i];
            f.setAccessible(true); // 设置些属性是可以访问的
            // Object val = f.get(bean);// 得到此属性的值

            // System.out.println("name:" + f.getName() + "\t value = " + val);

            String type = f.getType().toString();// 得到此属性的类型
            if (type.endsWith("String")) {
                // System.out.println(f.getType() + "\t是String");
                f.set(bean, "12");        // 给属性设值
            } else if (type.endsWith("int") || type.endsWith("Integer")) {
                // System.out.println(f.getType() + "\t是int");
                f.set(bean, 12);       // 给属性设值
            } else {
                // System.out.println(f.getType() + "\t");
            }

        }

        /*
         * 得到类中的方法
         */
        Method[] methods = cls.getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            // if (method.getName().startsWith("get")) {
            // System.out.print("methodName:" + method.getName() + "\t");
            // System.out.println("value:" + method.invoke(bean));// 得到get 方法的值
            // }

            if (method.getName().startsWith("set")) {
                System.out.print(firstToLower(cls.getSimpleName()) + "." + method.getName() + "(\""
                        + firstToLower(method.getName().substring(3)) + "\");\n");
            }
        }
    }

    /**
     * @param val
     * @return
     */
    public static String firstToLower(String val) {
        return val.substring(0, 1).toLowerCase() + val.substring(1);
    }

}

```



## bean对象

```java

import java.util.ArrayList;
import java.util.List;

/**
 * @author: (le.qiao)
 * @e-mail: qiaolevip@gmail.com
 * @myblog: <a href="http://qiaolevip.iteye.com">http://qiaolevip.iteye.com</a>
 * @date: 2012-8-13
 */
public class Project {

    // 项目ID
    private String id;

    // 项目名称
    private String name;

    // 所属父类别
    private String categoryID;

    // 所属子类别
    private String sonCategoryID;

    // 城市ID
    private String siteID;

    // 城市名称
    private String siteName;

    // 项目详情描述
    private String description;

    // 一句话描述
    private String subHead;

    // 是否支持电子票(1-支持;0-不支持)
    private String isETicket;

    // 最低价格
    private String minPrice;

    // 最高价格
    private String maxPrice;

    // 价格快照
    private String priceStr;

    // 销售结束时间
    private String sellEndTime;

    // 演出时间
    private String showTime;

    // 是否可以购买(1-可以购买；0-不允许购买）
    private String canSell;

    // 是否是测试项目（1-是，0-不是）
    private String isTest;

    // 场馆名称
    private String venueName;

    // 场馆ID
    private String venueID;

    // 是否支持选座(1-支持选座;0-不支持选座)
    private String isXuanZuo;

    // 相关艺人信息节点
    // private String artistinfo;

    // 最后更新时间
    private String updateTime;

    // 商品状态(3,销售中;4,结束;7,预定;8,预售;10,测试;6,禁止显示)
    private String status;

    /**
     * 座位表图片路径
     */
    private String seatPicUrl;

    private List<String> performIdList = new ArrayList<String>();

    public String getSeatPicUrl() {
        return seatPicUrl;
    }

    public void setSeatPicUrl(String seatPicUrl) {
        this.seatPicUrl = seatPicUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    public String getSonCategoryID() {
        return sonCategoryID;
    }

    public void setSonCategoryID(String sonCategoryID) {
        this.sonCategoryID = sonCategoryID;
    }

    public String getSiteID() {
        return siteID;
    }

    public void setSiteID(String siteID) {
        this.siteID = siteID;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubHead() {
        return subHead;
    }

    public void setSubHead(String subHead) {
        this.subHead = subHead;
    }

    public String getIsETicket() {
        return isETicket;
    }

    public void setIsETicket(String isETicket) {
        this.isETicket = isETicket;
    }

    public String getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(String minPrice) {
        this.minPrice = minPrice;
    }

    public String getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(String maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getPriceStr() {
        return priceStr;
    }

    public void setPriceStr(String priceStr) {
        this.priceStr = priceStr;
    }

    public String getSellEndTime() {
        return sellEndTime;
    }

    public void setSellEndTime(String sellEndTime) {
        this.sellEndTime = sellEndTime;
    }

    public String getShowTime() {
        return showTime;
    }

    public void setShowTime(String showTime) {
        this.showTime = showTime;
    }

    public String getCanSell() {
        return canSell;
    }

    public void setCanSell(String canSell) {
        this.canSell = canSell;
    }

    public String getIsTest() {
        return isTest;
    }

    public void setIsTest(String isTest) {
        this.isTest = isTest;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getVenueID() {
        return venueID;
    }

    public void setVenueID(String venueID) {
        this.venueID = venueID;
    }

    public String getIsXuanZuo() {
        return isXuanZuo;
    }

    public void setIsXuanZuo(String isXuanZuo) {
        this.isXuanZuo = isXuanZuo;
    }

    // public String getArtistinfo() {
    // return artistinfo;
    // }
    //
    // public void setArtistinfo(String artistinfo) {
    // this.artistinfo = artistinfo;
    // }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getPerformIdList() {
        return performIdList;
    }

    public void setPerformIdList(List<String> performIdList) {
        this.performIdList = performIdList;
    }

}

```







http://qiaolevip.iteye.com/blog/1739176