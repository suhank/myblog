# Docker DeviceMapper占用空间太大解决方案

2018年04月07日 10:44:24

 

Docker的所有镜像、缓存资源都会存储在devicemapper这个目录下，所以会导致这个目录占用磁盘极大，甚至会耗尽所有的服务器硬盘。如下图所示：

------

解决方案1：失败

- 问题主要在于原先分配的空间过大导致的，使用“docker info”查看：

- 首先备份需要的容器和镜像，使用“docker save”或者“docker export”.
- 然后暂停docker;
- 删除/var/lib/docker目录；
- 重建目录，空间分配

mkdir -p /var/lib/docker/devicemapper/devicemapper



dd if=/dev/zero of=/var/lib/docker/devicemapper/devicemapper/data bs=1M count=0 seek=8192







建立的文件最大为  1M * 8192 = 8G

- 重启docker

。。。。设置失败，无语了。



------

解决方案2：成功

- 停止docker!!!这一步很关键，否则下面的设置会失败。
- 编辑以下文件：

vim /lib/systemd/system/docker.service



原内容如下：

ExecStart=/usr/bin/dockerd-current \

​          --add-runtime docker-runc=/usr/libexec/docker/docker-runc-current \

​          --default-runtime=docker-runc \

​          --exec-opt native.cgroupdriver=systemd \

​          --userland-proxy-path=/usr/libexec/docker/docker-proxy-current \

​          $OPTIONS \

​          $DOCKER_STORAGE_OPTIONS \

​          $DOCKER_NETWORK_OPTIONS \

​          $ADD_REGISTRY \

​          $BLOCK_REGISTRY \

​          $INSECURE_REGISTRY\

​          $REGISTRIES





然后修改为以下：

ExecStart=/usr/bin/dockerd-current \

​          --add-runtime docker-runc=/usr/libexec/docker/docker-runc-current \

​          --default-runtime=docker-runc \

​          --exec-opt native.cgroupdriver=systemd \

​          --userland-proxy-path=/usr/libexec/docker/docker-proxy-current \

​          --storage-opt dm.loopdatasize=8G \

​          --storage-opt dm.loopmetadatasize=4G \

​          --storage-opt dm.basesize=8G \

​          $OPTIONS \

​          $DOCKER_STORAGE_OPTIONS \

​          $DOCKER_NETWORK_OPTIONS \

​          $ADD_REGISTRY \

​          $BLOCK_REGISTRY \

​          $INSECURE_REGISTRY\

​          $REGISTRIES





也就是多加以下3行：

​          --storage-opt dm.loopdatasize=8G \

​          --storage-opt dm.loopmetadatasize=4G \

​          --storage-opt dm.basesize=8G \



设置devicemapper的data为8G，metadata为4G，镜像的大小不能大于8G。



- 删除原有docker，并使用dd命令进行空间分配

rm -rf /var/lib/docker 

mkdir -p /var/lib/docker/devicemapper/devicemapper/ 

dd if=/dev/zero of=/var/lib/docker/devicemapper/devicemapper/data bs=1M count=0 seek=8192

dd if=/dev/zero of=/var/lib/docker/devicemapper/devicemapper/metadata bs=1M count=0 seek=4096



- 完成上述步骤后

systemctl daemon-reload 

systemctl start docker 

docker info





<https://blog.csdn.net/chenyufeng1991/article/details/79839497>