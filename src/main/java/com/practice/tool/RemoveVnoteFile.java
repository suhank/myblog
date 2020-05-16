package com.practice.base64;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.DefaultResourceLoader;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Timer;

/**
 * <B>Description:</B> 删除/Users/jerryye/backup/studio/workspace/myblog目录下的所有 _vnote.json <br>
 * <B>Create on:</B> 2017/11/27 下午2:26 <br>
 *
 * @author xiangyu.ye
 * @version 1.0
 */
public class RemoveVnoteFile {

    public static void main(String[] args) {
//        String path = "/Users/jerryye/backup/studio/work_lib/red_lib";
        String path = "/Users/jerryye/backup/studio/workspace/myblog/blog";
        ArrayList<File> _vnoteFiles = new ArrayList();
        File file = new File(path);

        Collection<File> tempList = FileUtils.listFiles(new File(path), new String[]{"json"}, true);

        for (File tempFile : tempList) {
            if (tempFile.isFile() && tempFile.getName().equals("_vnote.json")) {
//              System.out.println("文     件：" + tempList[i]);
                _vnoteFiles.add(tempFile);
                if (FileUtils.deleteQuietly(tempFile)) {
                    System.out.println("删除:" + tempFile.toString());
                }
            } else {
//                System.out.println(tempFile);
            }

        }
        System.out.println();
    }

    /**
     * <B>Description:</B> 获取项目路径 <br>
     * <B>Create on:</B> 2019-04-25 22:12 <br>
     *
     * @author xiangyu.ye
     */
    public static String getProjectPath() {
        // 获取文件分隔符
        String separator = File.separator;

        // 获取工程路径
        File projectPath = null;
        try {
            projectPath = new DefaultResourceLoader().getResource("").getFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //F:\backup\studio\AvailableCode\framework\freemarker\jeesite_hibernate\src\main\webapp\WEB-INF\classes
//        System.out.println(projectPath);

        while (!new File(projectPath.getPath() + separator + "src" + separator + "main").exists()) {
            projectPath = projectPath.getParentFile();
        }

        //Project Path: {}F:\backup\studio\AvailableCode\framework\freemarker\jeesite_hibernate
//        System.out.println("Project Path: {}" + projectPath);
        return projectPath.getPath();
    }

}
