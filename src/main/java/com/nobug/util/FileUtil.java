package com.nobug.util;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author 389561407@qq.com
 * @version 1.0
 * @since 2022-11-22
 */
public class FileUtil {
    public static void main(String[] args) {

        //输入路径  获取次文件 或者是获取这个文件夹中的文件

        File[] filesList = getFilesList("C:\\Users\\Administrator\\Desktop\\fileEncryption-v1.0/");

//        String decryptNameTime = getDecryptNameTime("C:\\Users\\Administrator\\Desktop\\fileEncryption-v1.0/readme.txt");
//        System.out.println(decryptNameTime);
//        String name = "sdad（12";
//        boolean matches = Pattern.matches(".*（(\\d+)）", name);
//        System.out.println(matches);
//        String s = reFileNamePath("C:\\Users\\Administrator\\Desktop\\fileEncryption-v1.0/readme.txt");
//        System.out.println(s);

    }

    public static File[] getFilesList(String path) {
        File file = new File(path);
        File[] files = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile();
            }
        });
        return files;
    }


    public static String getDecryptNameMD5(String path) {
        Path path1 = Paths.get(path);
        String s = path1.getParent().toString(); //文件夹
        return s+"/"+HashUtil.md5(System.currentTimeMillis()+"");
    }

    public static String getDecryptNameTime(String path) {
        Path path1 = Paths.get(path);
        String s = path1.getParent().toString(); //文件夹
        return s+"/"+System.currentTimeMillis();
    }


    public static String getDecryptName(String path) {
        int i = path.lastIndexOf(".");
        return path.substring(0, i)+"-已解密"+path.substring(i);
    }

    public static String getEncryptName(String path) {
        int i = path.lastIndexOf(".");
        return path.substring(0, i)+"-已加密"+path.substring(i);
    }


    //替换路径结尾 . 之后的类型
    public static String replaceType(String pathFile, String type) {
        int i = pathFile.lastIndexOf(".");
        return pathFile.substring(0, i+1)+type;
    }

    public static String reFileNamePath(String filePath) {
        return reFileNamePath(filePath,0);
    }

    public static String reFileNamePath(String filePath, int i) {
        File file = new File(filePath);
        String newPath = filePath;
        if(file.exists()){
            System.out.println("文件存在");
            //如果即将输出的文件存在 则修改一下名字
            Path path = file.toPath();
            Path parent = path.getParent();
            String fileName = file.getName();

            File file1 = null;
            do{
                i++;

                int index = fileName.lastIndexOf(".");
                String[] split = fileName.split("\\.");
                String name = fileName.substring(0,index);

                boolean matches = Pattern.matches(".*（(\\d+)）", name);
                if(matches){
                    int i1 = name.lastIndexOf("（");
                    name = name.substring(0,i1);
                }


                newPath =  parent.toString()+"/"+name;
                newPath = newPath+"（"+i+"）";
                if(split.length>1){
                    newPath = newPath+"."+fileName.substring(index);
                }

                file1 = new File(newPath);
                newPath = file1.getPath();
            }while (file1.exists());

        }

        return newPath;

    }


    public static String getOutPath(String path, String fileName) {
        String str = Paths.get(path).getParent().toString();
        return str+"/"+fileName;
    }


}
