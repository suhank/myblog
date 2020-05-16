# spring restTemplate 返回泛型

 

有个这样的类:

```java
public class Wrapper<T> {  
  
 private String message;  
 private T data;  
  
 public String getMessage() {  
    return message;  
 }  
  
 public void setMessage(String message) {  
    this.message = message;  
 }  
  
 public T getData() {  
    return data;  
 }  
  
 public void setData(T data) {  
    this.data = data;  
 }  
  
}  
```



```java
ParameterizedTypeReference<List<MyModelClass>> typeRef = new ParameterizedTypeReference<List<MyModelClass>>() {    
};    

ResponseEntity<List<MyModelClass>> responseEntity = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(mvm), typeRef);    
List<MyModelClass> myModelClasses = responseEntity.getBody();  
```



https://blog.csdn.net/a294039255/article/details/73850472