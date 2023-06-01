package com.nobug.util;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author 389561407@qq.com
 * @version 1.0
 * @since 2022-11-21
 */
public class RSATextUtil {
    //创建一个自定义的线程池
    private static Executor threadPool = Executors.newFixedThreadPool(400000);


    static String publicKey =  FileIOUtil.fileStringReader("publicKey.wbc");
    static String privateKey =  FileIOUtil.fileStringReader("privateKey.wbc");

    /**
     * 异步加密
     * @param inFile
     * @param password
     */
    public static void encryptRSASync(String inFile, String password) {
        String outPath = reFileNamePath(inFile);
        System.out.println("输出路径："+outPath);
        encryptRSASync(inFile, outPath , password);
    }


    /**
     * 异步解密
     * @param inFile
     * @param password
     */
    public static void decryptRSASync(String inFile, String password) {
        String outFile = reFileNamePath(inFile);
        System.out.println("输出路径："+outFile);
        decryptRSASync(inFile, outFile ,password);
    }


    /**
     * 异步解密
     * @param inFile
     * @param outFile
     */
    private static void decryptRSASync(String inFile, String outFile,String password) {
        System.out.println("开始解密...");
        List<FileEncUtilBean> fileEncUtilBeans = FileIOUtil.fileByteReader(inFile,128);
        System.out.println("成功读取到文件");

        FileEncUtilBean remove = fileEncUtilBeans.remove(0);
        boolean b = checkPassword(remove, password);
        if(!b){
            throw new RuntimeException("密码校验错误！");
        }

        //正在写入
        System.out.println("正在解密:"+fileEncUtilBeans.size());
        long l = System.currentTimeMillis();

        decryptSet(fileEncUtilBeans);

        System.out.println("读取到所有的数据：开始写入文件");
        System.out.println(System.currentTimeMillis() - l);


        FileIOUtil.fileByteWriter(fileEncUtilBeans,outFile);
        System.out.println("写入完成：解密成功");


    }

    /**
     * 多线程解密
     * @param fileEncUtilBeans
     */
    private static void decryptSet(List<FileEncUtilBean> fileEncUtilBeans) {
        int size = fileEncUtilBeans.size();
        int len =  size/8;

        if(size > 80000){
            // 拆分
            List<FileEncUtilBean> fileEncUtilBeans1 = fileEncUtilBeans.subList(0, len);
            List<FileEncUtilBean> fileEncUtilBeans2 = fileEncUtilBeans.subList(len, len*2);
            List<FileEncUtilBean> fileEncUtilBeans3 = fileEncUtilBeans.subList(len*2, len*3);
            List<FileEncUtilBean> fileEncUtilBeans4 = fileEncUtilBeans.subList(len*3, len*4);
            List<FileEncUtilBean> fileEncUtilBeans5 = fileEncUtilBeans.subList(len*4, len*5);
            List<FileEncUtilBean> fileEncUtilBeans6 = fileEncUtilBeans.subList(len*5, len*6);
            List<FileEncUtilBean> fileEncUtilBeans7 = fileEncUtilBeans.subList(len*6, len*7);
            List<FileEncUtilBean> fileEncUtilBeans8 = fileEncUtilBeans.subList(len*7, fileEncUtilBeans.size());
            CompletableFuture<Void> voidCompletableFuture = CompletableFuture.allOf(
                    CompletableFuture.runAsync(() -> {
                        decryptSet(fileEncUtilBeans1);
                    }, threadPool),
                    CompletableFuture.runAsync(() -> {
                        decryptSet(fileEncUtilBeans2);
                    }, threadPool),
                    CompletableFuture.runAsync(() -> {
                        decryptSet(fileEncUtilBeans3);
                    }, threadPool),
                    CompletableFuture.runAsync(() -> {
                        decryptSet(fileEncUtilBeans4);
                    }, threadPool),
                    CompletableFuture.runAsync(() -> {
                        decryptSet(fileEncUtilBeans5);
                    }, threadPool),
                    CompletableFuture.runAsync(() -> {
                        decryptSet(fileEncUtilBeans6);
                    }, threadPool),
                    CompletableFuture.runAsync(() -> {
                        decryptSet(fileEncUtilBeans7);
                    }, threadPool),
                    CompletableFuture.runAsync(() -> {
                        decryptSet(fileEncUtilBeans8);
                    }, threadPool)
            );
            voidCompletableFuture.join();
            System.out.println("异步完成");

        }else {
            for (FileEncUtilBean bean : fileEncUtilBeans) {
                try {
                    byte[] decrypt = RSAUtil.decrypt(bean.getBytes(), privateKey);
                    bean.setBytes(decrypt);
                    bean.setLen(decrypt.length);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }



    /**
     * 异步加密
     * @param inFile
     * @param outFile
     */
    private static void encryptRSASync(String inFile, String outFile,String password) {
        String hash = HashUtil.md5(HashUtil.md5(password));
        System.out.println("开始读取文件");
        long l = System.currentTimeMillis();
        List<FileEncUtilBean> fileEncUtilBeans = FileIOUtil.fileByteReader(inFile,117);
        System.out.println("开始文件用时："+(System.currentTimeMillis() - l));

        byte[] bytes = hash.getBytes(StandardCharsets.UTF_8);
        fileEncUtilBeans.add(0,new FileEncUtilBean(bytes,bytes.length));


        long l1 = System.currentTimeMillis();
        System.out.println("开始加密：");
        encryptSet(fileEncUtilBeans);
        System.out.println("加密用时:"+(System.currentTimeMillis() - l1));

        FileIOUtil.fileByteWriter(fileEncUtilBeans,outFile);

    }

    private static void encryptSet(List<FileEncUtilBean> fileEncUtilBeans) {
        int size = fileEncUtilBeans.size();
        int len =  size/8;

        if(size > 80000){
            System.out.println("多线程执行加密...");
            // 拆分
            List<FileEncUtilBean> fileEncUtilBeans1 = fileEncUtilBeans.subList(0, len);
            List<FileEncUtilBean> fileEncUtilBeans2 = fileEncUtilBeans.subList(len, len*2);
            List<FileEncUtilBean> fileEncUtilBeans3 = fileEncUtilBeans.subList(len*2, len*3);
            List<FileEncUtilBean> fileEncUtilBeans4 = fileEncUtilBeans.subList(len*3, len*4);
            List<FileEncUtilBean> fileEncUtilBeans5 = fileEncUtilBeans.subList(len*4, len*5);
            List<FileEncUtilBean> fileEncUtilBeans6 = fileEncUtilBeans.subList(len*5, len*6);
            List<FileEncUtilBean> fileEncUtilBeans7 = fileEncUtilBeans.subList(len*6, len*7);
            List<FileEncUtilBean> fileEncUtilBeans8 = fileEncUtilBeans.subList(len*7, fileEncUtilBeans.size());
            CompletableFuture<Void> voidCompletableFuture = CompletableFuture.allOf(
                    CompletableFuture.runAsync(() -> {
                        encryptSet(fileEncUtilBeans1);
                    }, threadPool),
                    CompletableFuture.runAsync(() -> {
                        encryptSet(fileEncUtilBeans2);
                    }, threadPool),
                    CompletableFuture.runAsync(() -> {
                        encryptSet(fileEncUtilBeans3);
                    }, threadPool),
                    CompletableFuture.runAsync(() -> {
                        encryptSet(fileEncUtilBeans4);
                    }, threadPool),
                    CompletableFuture.runAsync(() -> {
                        encryptSet(fileEncUtilBeans5);
                    }, threadPool),
                    CompletableFuture.runAsync(() -> {
                        encryptSet(fileEncUtilBeans6);
                    }, threadPool),
                    CompletableFuture.runAsync(() -> {
                        encryptSet(fileEncUtilBeans7);
                    }, threadPool),
                    CompletableFuture.runAsync(() -> {
                        encryptSet(fileEncUtilBeans8);
                    }, threadPool)
            );
            voidCompletableFuture.join();
            System.out.println("异步完成");

        }else {
            for (FileEncUtilBean bean : fileEncUtilBeans) {
                try {
                    byte[] encrypt = RSAUtil.encrypt(bean.getBytes(), publicKey);
                    bean.setBytes(encrypt);
                    bean.setLen(encrypt.length);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static String reFileNamePath(String filePath) {
        return reFileNamePath(filePath,0);
    }

    private static String reFileNamePath(String filePath, int i) {
        File file = new File(filePath);
        String newPath = filePath;
        if(file.exists()){
            //如果即将输出的文件存在 则修改一下名字
            Path path = file.toPath();
            Path parent = path.getParent();
            String fileName = file.getName();
            i++;

            File file1 = null;
            do{
                i++;
                newPath =  parent.toString()+"/("+ i +")"+fileName;
                file1 = new File(newPath);
            }while (file1.exists());

        }

        return newPath;

    }

    private static boolean checkPassword(FileEncUtilBean remove, String password) {
        byte[] bytes = remove.getBytes();
        String hash = HashUtil.md5(HashUtil.md5(password));
        try {
            byte[] decrypt = RSAUtil.decrypt(bytes, privateKey);
            return hash.equals(new String(decrypt,StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //替换路径结尾 . 之后的类型
    private static String replaceType(String pathFile, String type) {
        int i = pathFile.lastIndexOf(".");
        return pathFile.substring(0, i+1)+type;
    }

}
