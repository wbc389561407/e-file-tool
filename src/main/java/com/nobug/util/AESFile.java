package com.nobug.util;

import javax.swing.*;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author 389561407@qq.com
 * @version 1.0
 * @since 2022-11-22
 */
public class AESFile {

    public static void main(String[] args) {

    }

    /**
     *  V3 V2 解密 回复原文件名称
     * @param path
     * @param password
     * @return
     */
    public static String decrypt(String path, String password, String mode, JLabel show) {
        password = getPassword(password);
        show.setText("正在读取文件......");
        List<FileEncUtilBean> fileEncUtilBeans = FileIOUtil.fileByteReader(path, 10256);
        show.setText("文件读取成功......,开始解析文件...");

        long l = System.currentTimeMillis();
        for (FileEncUtilBean fileEncUtilBean : fileEncUtilBeans) {
            byte[] bytes = fileEncUtilBean.getBytes();
            byte[] decrypt = new byte[0];
            try {
                decrypt = AESUtil.decrypt(bytes, password);
            } catch (Exception e) {
                throw new RuntimeException("解密失败！");
            }
            fileEncUtilBean.setBytes(decrypt);
            fileEncUtilBean.setLen(decrypt.length);
        }
        show.setText("解析完成用时："+(System.currentTimeMillis() - l)+"毫秒，开始写出文件...");

        FileEncUtilBean remove = fileEncUtilBeans.remove(0);
        byte[] bytes = remove.getBytes();
        String data = new String(bytes, StandardCharsets.UTF_8).trim();
        String[] split = data.split(",");
        String fileName = split[0];
        if(split.length > 1){
            if(!split[1].equals(mode)){
                show.setText("解密失败！");
                throw new RuntimeException("解密失败！");
            }
        }

        String outPath = FileUtil.getOutPath(path,fileName);
        outPath = FileUtil.reFileNamePath(outPath);
        FileIOUtil.fileByteWriter(fileEncUtilBeans,outPath);
        show.setText("解密成功！");
        return outPath;
    }


    /**
     * 根据 mode 对于输出文件的改变
     *@param path
     * @param password
     * @return
     */
    public static String encrypt(String path, String password, String mode, JLabel show) {
        password = getPassword(password);
        System.out.println("开始读取文件......");
        List<FileEncUtilBean> fileEncUtilBeans = FileIOUtil.fileByteReader(path, 1024 * 10);
        show.setText("读取成功......,开始加密文件......");

        // 添加一些数据到头部
        File file = Paths.get(path).toFile();
        StringBuilder sb = new StringBuilder();
        String fileName = file.getName();
        sb.append(fileName);
        sb.append(",");
        sb.append(mode);
        byte[] headByte = stringToBytes(sb.toString(),1024*10);
        FileEncUtilBean bean = new FileEncUtilBean(headByte, headByte.length);
        fileEncUtilBeans.add(0,bean);
        // 添加一些数据到头部


        long l = System.currentTimeMillis();

        for (FileEncUtilBean fileEncUtilBean : fileEncUtilBeans) {
            byte[] bytes = fileEncUtilBean.getBytes();
            byte[] encrypt = new byte[0];
            try {
                encrypt = AESUtil.encrypt(bytes, password);
            } catch (Exception e) {
                show.setText("加密失败！");
                throw new RuntimeException("加密失败！");
            }
            fileEncUtilBean.setBytes(encrypt);
            fileEncUtilBean.setLen(encrypt.length);
        }
        show.setText("加密完成用时："+(System.currentTimeMillis() - l)+"毫秒，开始写出文件...");

        String outPath = path;

        //输出文件改为 MD5
        if("R2M".equals(mode)){
            outPath = FileUtil.getDecryptNameMD5(path);
        }
        //输出文件改为 时间戳
        if("R2T".equals(mode)){
            outPath = FileUtil.getDecryptNameTime(path);
        }

        if("RT2TM".equals(mode)){
            outPath = FileUtil.getDecryptNameTime(path)+".mp4";
        }

        if("RT2MM".equals(mode)){
            outPath = FileUtil.getDecryptNameMD5(path)+".mp4";
        }

        if("V2Z".equals(mode)){
            outPath = FileUtil.replaceType(path, "zybfq");
        }


        outPath = FileUtil.reFileNamePath(outPath);
        FileIOUtil.fileByteWriter(fileEncUtilBeans,outPath);
        show.setText("写出成功！");

        return outPath;
    }


//    /**
//            V3 加密 原文件名称存入文件 加密文件改为 时间戳
//     *@param path
//     * @param password
//     * @return
//     */
//    public static String encryptR2T(String path,String password, JLabel show) {
//        password = getPassword(password);
//        List<FileEncUtilBean> fileEncUtilBeans = FileIOUtil.fileByteReader(path, 1024 * 10);
//        show.setText("读取成功");
//
//        // 添加一些数据到头部
//        File file = Paths.get(path).toFile();
//        String fileName = file.getName();
//        byte[] headByte = stringToBytes(fileName,1024*10);
//        FileEncUtilBean bean = new FileEncUtilBean(headByte, headByte.length);
//        fileEncUtilBeans.add(0,bean);
//        // 添加一些数据到头部
//
//
//        long l = System.currentTimeMillis();
//
//        for (FileEncUtilBean fileEncUtilBean : fileEncUtilBeans) {
//            byte[] bytes = fileEncUtilBean.getBytes();
//            byte[] encrypt = new byte[0];
//            try {
//                encrypt = AESUtil.encrypt(bytes, password);
//            } catch (Exception e) {
//                throw new RuntimeException("加密失败！");
//            }
//            fileEncUtilBean.setBytes(encrypt);
//            fileEncUtilBean.setLen(encrypt.length);
//        }
//        show.setText("加密完成："+(System.currentTimeMillis() -l));
//
//        String outPath = FileUtil.getDecryptNameTime(path);
//        FileIOUtil.fileByteWriter(fileEncUtilBeans,outPath);
//        show.setText("写出成功！");
//
//        return outPath;
//    }



//    /**
//        V2 加密 原文件名称存入文件
//     * @param path
//     * @param password
//     * @return
//     */
//    public static String encryptV2(String path,String password) {
//        password = getPassword(password);
//        List<FileEncUtilBean> fileEncUtilBeans = FileIOUtil.fileByteReader(path, 1024 * 10);
//        show.setText("读取成功");
//
//        // 添加一些数据到头部
//        File file = Paths.get(path).toFile();
//        String fileName = file.getName();
//        byte[] headByte = stringToBytes(fileName,1024*10);
//        FileEncUtilBean bean = new FileEncUtilBean(headByte, headByte.length);
//        fileEncUtilBeans.add(0,bean);
//        // 添加一些数据到头部
//
//
//        long l = System.currentTimeMillis();
//
//        for (FileEncUtilBean fileEncUtilBean : fileEncUtilBeans) {
//            byte[] bytes = fileEncUtilBean.getBytes();
//            byte[] encrypt = new byte[0];
//            try {
//                encrypt = AESUtil.encrypt(bytes, password);
//            } catch (Exception e) {
//                throw new RuntimeException("加密失败！");
//            }
//            fileEncUtilBean.setBytes(encrypt);
//            fileEncUtilBean.setLen(encrypt.length);
//        }
//        show.setText("加密完成："+(System.currentTimeMillis() -l));
//
//        String outPath = FileUtil.getDecryptName(path);
//        FileIOUtil.fileByteWriter(fileEncUtilBeans,outPath);
//        show.setText("写出成功！");
//
//        return outPath;
//    }

    private static byte[] stringToBytes(String fileName, int len) {
        byte[] bytes = fileName.getBytes(StandardCharsets.UTF_8);
        byte[] headByte = new byte[len];
        for (int i = 0; i < bytes.length; i++) {
            headByte[i] = bytes[i];
        }
        return headByte;
    }


//    /**
//     *  V3 V2 解密 回复原文件名称
//     * @param path
//     * @param password
//     * @return
//     */
//    public static String decryptV2(String path,String password) {
//        password = getPassword(password);
//        List<FileEncUtilBean> fileEncUtilBeans = FileIOUtil.fileByteReader(path, 10256);
//        show.setText("读取成功"+fileEncUtilBeans.size());
//
//        long l = System.currentTimeMillis();
//        for (FileEncUtilBean fileEncUtilBean : fileEncUtilBeans) {
//            byte[] bytes = fileEncUtilBean.getBytes();
//            byte[] decrypt = new byte[0];
//            try {
//                decrypt = AESUtil.decrypt(bytes, password);
//            } catch (Exception e) {
//                throw new RuntimeException("解密失败！");
//            }
//            fileEncUtilBean.setBytes(decrypt);
//            fileEncUtilBean.setLen(decrypt.length);
//        }
//        show.setText("解密完成："+(System.currentTimeMillis() - l));
//
//        FileEncUtilBean remove = fileEncUtilBeans.remove(0);
//        byte[] bytes = remove.getBytes();
//        show.setText("获取到头部数据：");
//        String fileName = new String(bytes, StandardCharsets.UTF_8).trim();
//        System.out.println(fileName);
//
//        String outPath = FileUtil.getOutPath(path,fileName);
//        outPath = FileUtil.reFileNamePath(outPath);
//        FileIOUtil.fileByteWriter(fileEncUtilBeans,outPath);
//        show.setText("写出成功！");
//        return outPath;
//    }


    /**
     * v1.0
     * @param path
     * @param password
     * @return
     */
    public static String decrypt(String path,String password,JLabel show) {
        password = getPassword(password);
        show.setText("开始读取文件...");
        List<FileEncUtilBean> fileEncUtilBeans = FileIOUtil.fileByteReader(path, 10256);
        show.setText("开始解析文件...");
        long l = System.currentTimeMillis();
        for (FileEncUtilBean fileEncUtilBean : fileEncUtilBeans) {
            byte[] bytes = fileEncUtilBean.getBytes();
            byte[] decrypt = new byte[0];
            try {
                decrypt = AESUtil.decrypt(bytes, password);
            } catch (Exception e) {
                show.setText("解密失败!");
                throw new RuntimeException("解密失败！");
            }
            fileEncUtilBean.setBytes(decrypt);
            fileEncUtilBean.setLen(decrypt.length);
        }
        show.setText("解析完成用时："+(System.currentTimeMillis() - l)+"毫秒，开始写出文件...");
        String outPath = FileUtil.getDecryptName(path);
        FileIOUtil.fileByteWriter(fileEncUtilBeans,outPath);
        show.setText("写出成功！");
        return outPath;
    }


    /**
     * v1.0 校验密码，直接加密
     * @param path
     * @param password
     * @return
     */
    public static String encrypt(String path, String password, JLabel show) {
        password = getPassword(password);
        show.setText("开始读取文件...");
        List<FileEncUtilBean> fileEncUtilBeans = FileIOUtil.fileByteReader(path, 1024 * 10);
        show.setText("读取成功！开始解析文件...");
        long l = System.currentTimeMillis();

        for (FileEncUtilBean fileEncUtilBean : fileEncUtilBeans) {
            byte[] bytes = fileEncUtilBean.getBytes();
            byte[] encrypt = new byte[0];
            try {
                encrypt = AESUtil.encrypt(bytes, password);
            } catch (Exception e) {
                show.setText("加密失败！");
                throw new RuntimeException("加密失败！");
            }
            fileEncUtilBean.setBytes(encrypt);
            fileEncUtilBean.setLen(encrypt.length);
        }
        show.setText("解析完成用时："+(System.currentTimeMillis() - l)+"毫秒，开始写出文件...");
        String outPath = FileUtil.getEncryptName(path);
        FileIOUtil.fileByteWriter(fileEncUtilBeans,outPath);
        show.setText("写出成功！");

        return outPath;
    }

    private static String getPassword(String password) {
        return HashUtil.md5(password);
    }


}
