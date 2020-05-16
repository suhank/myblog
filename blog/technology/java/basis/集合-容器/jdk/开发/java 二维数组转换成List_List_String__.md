# java 二维数组转换成List<List<String>>





​          在拿到二维数组的数据时，需要将它转换成嵌套的list，如下工具类：

```
public class CollectionUtils extends org.springframework.util.CollectionUtils {

    /**
     * <B>Description:</B> 字符串二维数组转list <br>
     * <B>Create on:</B> 2018/5/30 下午10:47 <br>
     *
     * @author xiangyu.ye
     */
    public static List<List<String>> arrayToList(String[][] datas) {

        List<List<String>> resultList = new ArrayList<List<String>>();

        if (datas == null || datas.length == 0) {
            return resultList;
        }


        for (int i = 0; i < datas.length; i++) {
            List<String> columnList = new ArrayList<String>();


            String[] data = datas[i];
            if (data == null || data.length == 0) {
                continue;
            }


            for (int j = 0; j < data.length; j++) {
                columnList.add(j, data[j]);
            }
            resultList.add(i, columnList);
        }

        return resultList;
    }

}
```



```
import java.util.ArrayList;
import java.util.List;

/**
 * 将二维数组转换成List<List<String>>形式工具
 * 
 * @author qiulinhe
 *
 *         2017年3月1日 下午3:09:52
 */
public class ArrayToListTest {

	public static void main(String[] args) {
		String[][] strCe = new String[2][2];
		strCe[0][0] = "1";
		strCe[0][1] = "2";
		strCe[1][0] = "3";
		strCe[1][1] = "4";

		List<List<String>> listTest = new ArrayList<List<String>>();
		for (int i = 0; i < strCe.length; i++) {
			List<String> columnList = new ArrayList<String>();
			for (int j = 0; j < strCe[i].length; j++) {

				columnList.add(j, strCe[i][j]);

			}
			listTest.add(i, columnList);
		}

		System.out.println(listTest);
		System.out.println(strCe);

	}
}
```

​          如果你是要传接送给后台的话，也可以直接使用fastjson进行转换：

```

	/**
	 * 将二维数组转换成List<List<String>>形式工具
	 * 
	 * @param retireStringArray
	 *            excel拼接的字段内容
	 * @return
	 */
	public static List<List<String>> parseStringToList(String[][] retireStringArray) {

		// List<List<String>> listTest = new ArrayList<List<String>>();
		// for (int i = 0; i < retireStringArray.length; i++) {
		// List<String> columnList = new ArrayList<String>();
		// for (int j = 0; j < retireStringArray[i].length; j++) {
		//
		// columnList.add(j, retireStringArray[i][j]);
		//
		// }
		// listTest.add(i, columnList);
		// }
		// return listTest;

		// 使用fastjson进行转换
		List<List<String>> lists = new ArrayList<List<String>>();
		lists = (List<List<String>>) JSON.parseObject(JSON.toJSONString(retireStringArray),
				new TypeReference<List<List<String>>>() {
				});
		return lists;

	}
```

http://www.voidcn.com/article/p-fmpbgpph-gn.html