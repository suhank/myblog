# springmvc RestTemplate文件上传

在使用springmvc提供rest接口实现文件上传时，有时为了测试需要使用RestTemplate进行调用，那么在使用RestTemplate调用文件上传接口时有什么特别的地方呢？实际上只需要注意一点就行了，就是创建文件资源时需要使用org.springframework.core.io.FileSystemResource类，而不能直接使用java.io.File对象。

Controller中的rest接口代码如下：

```
@ResponseBody  
@RequestMapping(value = "/upload.do", method = RequestMethod.POST)  
public String upload(String fileName, MultipartFile jarFile) {  
    // 下面是测试代码  
    System.out.println(fileName);  
    String originalFilename = jarFile.getOriginalFilename();  
    System.out.println(originalFilename);  
    try {  
        String string = new String(jarFile.getBytes(), "UTF-8");  
        System.out.println(string);  
    } catch (UnsupportedEncodingException e) {  
        e.printStackTrace();  
    } catch (IOException e) {  
        e.printStackTrace();  
    }  
    // TODO 处理文件内容...  
    return "OK";  
}  
```

使用RestTemplate测试上传代码如下：

```
@Test  
public void testUpload() throws Exception {  
    String url = "http://127.0.0.1:8080/test/upload.do";  
    String filePath = "C:\\Users\\MikanMu\\Desktop\\test.txt";  
  
    RestTemplate rest = new RestTemplate();  
    FileSystemResource resource = new FileSystemResource(new File(filePath));  
    MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();  
    param.add("jarFile", resource);  
    param.add("fileName", "test.txt");  
  
    String string = rest.postForObject(url, param, String.class);  
    System.out.println(string);  
}  
```

其中：

```
String string = rest.postForObject(url, param, String.class);  
```

可以换成：

```
HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<MultiValueMap<String,Object>>(param);  
ResponseEntity<String> responseEntity = rest.exchange(url, HttpMethod.POST, httpEntity, String.class);  
System.out.println(responseEntity.getBody()); 
```



https://blog.csdn.net/mhmyqn/article/details/26395743