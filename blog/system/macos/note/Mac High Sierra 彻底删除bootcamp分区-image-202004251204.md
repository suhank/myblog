[TOC]



[TOC]



# Mac High Sierra 彻底删除bootcamp分区

 发表于 2018-05-05 | [评论数： 0 Comments](http://mpwang.github.io/2018/05/05/delete-bootcamp-win10/#comments)

 本文字数： 8.7k | 阅读时长 ≈ 8 分钟

## bootcamp 无法删除 Windows 10分区

很久之前在Mac上使用bootcamp安装了windows10, Mac升级到10.13.4 High Sierra之后发现自己几乎没用过win10. 于是想删除bootcamp分区, 理想情况下使用bootcamp助理应该可以无痛删除win10分区. 然而mac在升级之后应该是使用了新的文件系统格式APFS.

打开bootcamp助理后, 提示错误: `启动磁盘不能被分区或恢复成单个分区`, 无法继续.

官方说明: [将 Boot Camp 升级到 Windows 8.1 或更高版本时，您可能会看到一则警告信息“启动磁盘不能被分区或恢复成单个分区”](https://support.apple.com/zh-cn/HT203913)。

于是开始折腾. 在没有做数据备份的情况下, 一路有惊无险, 记录解决过程如下.

复制

```
警告：磁盘操作具有破坏性、高风险，建议操作前做好备份. 你必须十分清楚自己在做什么.
```

## 分区情况

找开磁盘工具查看分区表, 这里借用网上找到的一张图片, 操作之前没有截图.

![分区](image-202004251204/bootcamp.jpg)

我的情况是除了mac APFS分区顺时针有一个8G的未格式化可用空间, 40G的windows10分区, 以及一个800M的disk0s4额外分区(Microsoft 保留或者Windows 恢复分区).

使用`diskutil`工具查看分区表

复制

```
╰─ diskutil list
/dev/disk0 (internal):
   #:                       TYPE NAME                    SIZE       IDENTIFIER
   0:      GUID_partition_scheme                         251.0 GB   disk0
   1:                        EFI EFI                     314.6 MB   disk0s1
   2:                 Apple_APFS Container disk1         201.4 GB   disk0s2
   3:       Microsoft Basic Data BOOTCAMP                39.8 GB    disk0s3
   4:           Windows Recovery                         892.3 MB   disk0s4

/dev/disk1 (synthesized):
   #:                       TYPE NAME                    SIZE       IDENTIFIER
   0:      APFS Container Scheme -                      +201.4 GB   disk1
                                 Physical Store disk0s2
   1:                APFS Volume Macintosh HD            166.1 GB   disk1s1
   2:                APFS Volume Preboot                 22.7 MB    disk1s2
   3:                APFS Volume Recovery                517.8 MB   disk1s3
   4:                APFS Volume VM                      3.2 GB     disk1s4
```

**这里注意到8G的未格式化可用空间的分区在分区表中找不到**.

这里也决定了我按照官方的操作方法无法成功. 在磁盘工具分区里删除选择disk04分区, 点击上图`-`号后, 应用里提示错误: `无法找到其中一个分区`. 我推测这里应该是找不到8G空间那个分区导致无法使用磁盘工具正常删除disk0s4分区.

![删除失败](image-202004251204/bootcamp1.png)

## diskutil eraseVolume

使用命令行工具删除disk0s4分区, 然后删除disk0s3分区(bootcamp分区). 这里顺序**很重要**.

复制

```
╰─ sudo diskutil eraseVolume JHFS+ deleteme /dev/disk0s4
Password:
Started erase on disk0s4
Unmounting disk
Erasing
Initialized /dev/rdisk0s4 as a 851 MB case-insensitive HFS Plus volume with a 8192k journal
Mounting disk
Finished erase on disk0s4 deleteme

╰─ sudo diskutil eraseVolume JHFS+ deleteme /dev/disk0s3
Started erase on disk0s3 BOOTCAMP
Unmounting disk
Erasing
Initialized /dev/rdisk0s3 as a 37 GB case-insensitive HFS Plus volume with a 8192k journal
Mounting disk
Finished erase on disk0s3 deleteme
```

这时使用磁盘工具, 选择根卷分区, 选择disk0s4分区, 点击减号, 选择disk0s3分区, 点击减号. 可以成功合并原来disk0s4 disk0s3的空间.

复制

```
╰─ diskutil list
/dev/disk0 (internal):
   #:                       TYPE NAME                    SIZE       IDENTIFIER
   0:      GUID_partition_scheme                         251.0 GB   disk0
   1:                        EFI EFI                     314.6 MB   disk0s1
   2:                 Apple_APFS Container disk1         201.4 GB   disk0s2
   3:                  Apple_HFS deleteme                40.5 GB    disk0s3

/dev/disk1 (synthesized):
   #:                       TYPE NAME                    SIZE       IDENTIFIER
   0:      APFS Container Scheme -                      +201.4 GB   disk1
                                 Physical Store disk0s2
   1:                APFS Volume Macintosh HD            166.1 GB   disk1s1
   2:                APFS Volume Preboot                 22.7 MB    disk1s2
   3:                APFS Volume Recovery                517.8 MB   disk1s3
   4:                APFS Volume VM                      2.1 GB     disk1s4
```

这时已经将bootcamp分区 disk0s3 格式化成 HFS+ 格式. 但是由于那个8G空间分区无法在分区表中找到. 始终无法使用磁盘工具将8G空间分区和bootcamp分区合并到mac APFS分区中.

## diskutil mergePartitions

根据网上找到的资料, 开始作死使用`diskutil mergePartitions`命令尝试合并分区.

复制

```
╰─ diskutil mergePartitions --help
Usage:  diskutil mergePartitions [force] format name
        DiskIdentifier|DeviceNode DiskIdentifier|DeviceNode
Merge two or more pre-existing partitions into one. The first disk parameter
is the starting partition; the second disk parameter is the ending partition;
this given range of two or more partitions will be merged into one.
All partitions in the range, except for the first one, must be unmountable.
All data on merged partitions other than the first will be lost; data on the
first partition will be lost as well if the "force" argument is given.
If "force" is not given, and the first partition has a resizable file system
(e.g. JHFS+), it will be grown in a data-preserving manner, even if a different
file system is specified (in fact, your file system and volume name parameters
are both ignored in this case). Also, if "force" is not given, and the first
partition is not resizable, you will be prompted if you want to erase.
However, if "force" is given, the first partition is always formatted. You
should do this if you wish to reformat to a new file system type.
Merged partitions are required to be ordered sequentially on disk.
See `diskutil list` for the actual on-disk ordering; BSD slice identifiers
may in certain circumstances not always be in numerical order but the
top-to-bottom order given by diskutil list is always the on-disk order.
Ownership of the affected disk is required.
Example: diskutil mergePartitions JHFS+ NewName disk3s4 disk3s7
         This example will merge all partitions *BETWEEN* disk3s4 and disk3s7,
         preserving data on disk3s4 but destroying data on disk3s5, disk3s6,
         disk3s7 and any invisible free space partitions between those disks;
         disk3s4 will be grown to cover the full space if possible.
```

发现无法成功

复制

```
╰─ diskutil mergePartitions APFS randall disk0s2 disk0s3
You cannot merge disks into an APFS Physical Store
Instead, you can delete the partitions following the APFS Physical Store by
using "diskutil eraseVolume free n <disk>" for all such partitions, and
then by growing the corresponding APFS Container by its APFS Physical Store
to fill the gap by using "diskutil apfs resizeContainer disk0s2 0"
```

## diskutil apfs resizeContainer

不得不赞mac命令行工具的提示真的做得非常好, 在这里发现了关键性的信息:

复制

```
APFS格式分区可以使用diskutil apfs resizeContainer命令自动扩展空间到下一个可用分区.
```

8G空间分区本身是未格式化的, 符合要求.

复制

```
╰─ diskutil apfs resizeContainer disk0s2 0
Started APFS operation
Aligning grow delta to 8,650,686,464 bytes and targeting a new physical store size of 210,007,728,128 bytes
Determined the maximum size for the targeted physical store of this APFS Container to be 210,007,728,128 bytes
Resizing APFS Container designated by APFS Container Reference disk1
The specific APFS Physical Store being resized is disk0s2
Verifying storage system
Using live mode
Performing fsck_apfs -n -x -l /dev/disk0s2
Checking volume
Checking the container superblock
Checking the EFI jumpstart record
Checking the space manager
Checking the object map
Checking the APFS volume superblock
Checking the object map
Checking the fsroot tree
Checking the snapshot metadata tree
Checking the extent ref tree
Checking the snapshots
Checking the APFS volume superblock
Checking the object map
Checking the fsroot tree
Checking the snapshot metadata tree
Checking the extent ref tree
Checking the snapshots
Checking the APFS volume superblock
Checking the object map
Checking the fsroot tree
Checking the snapshot metadata tree
Checking the extent ref tree
Checking the snapshots
Checking the APFS volume superblock
Checking the object map
Checking the fsroot tree
Checking the snapshot metadata tree
Checking the extent ref tree
Checking the snapshots
Verifying allocated space
The volume /dev/disk0s2 appears to be OK
Storage system check exit code is 0
Growing APFS Physical Store disk0s2 from 201,357,041,664 to 210,007,728,128 bytes
Modifying partition map
Growing APFS data structures
Finished APFS operation
```

运行成功, 打分磁盘工具, 查看根卷分区, 发现8G空间分区已经被合并至APFS分区, 只剩下一个40G的bootcamp分区.

选择bootcamp分区, 点击减号, 应用成功. 成功将所有分区合并成一个完整大分区.

现在Mac只有一个分区了.

复制

```
╰─ diskutil list
/dev/disk0 (internal):
   #:                       TYPE NAME                    SIZE       IDENTIFIER
   0:      GUID_partition_scheme                         251.0 GB   disk0
   1:                        EFI EFI                     314.6 MB   disk0s1
   2:                 Apple_APFS Container disk1         250.7 GB   disk0s2

/dev/disk1 (synthesized):
   #:                       TYPE NAME                    SIZE       IDENTIFIER
   0:      APFS Container Scheme -                      +250.7 GB   disk1
                                 Physical Store disk0s2
   1:                APFS Volume Macintosh HD            168.9 GB   disk1s1
   2:                APFS Volume Preboot                 22.7 MB    disk1s2
   3:                APFS Volume Recovery                517.8 MB   disk1s3
   4:                APFS Volume VM                      2.1 GB     disk1s4
```

Done.

------

参考: [BOOTCAMP 分区无法删除](https://www.chadou.me/p/206)

相关文章

- [using emacs under mac](http://mpwang.github.io/2013/08/10/using-emacs-under-mac/)





http://mpwang.github.io/2018/05/05/delete-bootcamp-win10/