# shell 获取本地ip地址

```sh
#!/bin/bash
#获取本地ip地址
echo "<--- current ip is ---> "
#ip="init value"
#echo ${ip}
#ip=$(ifconfig en0 | grep 'inet' | grep -v inet6 | sed 's/inet //g' | sed 's/netmask.*$//g' | sed 's/[[:space:]]//g')
#ip=`ifconfig en0 | grep 'inet' | grep -v inet6 | sed 's/inet //g' | sed 's/netmask.*$//g' | sed 's/[[:space:]]//g'`
#echo "<--- current ip is ${ip} --->"
ifconfig en0 | grep 'inet' | grep -v inet6 | sed 's/inet //g' | sed 's/netmask.*$//g' | sed 's/[[:space:]]//g's
```



