package com.nobug.util;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author 389561407@qq.com
 * @version 1.0
 * @since 2022-11-16
 */
public class FileEncUtilBak {

    static String publicKey =  fileStringReader("publicKey.wbc");
    static String privateKey =  fileStringReader("privateKey.wbc");

    public static void main(String[] args) {


//        encryptRSA("C:\\Users\\Administrator\\Desktop\\dev1\\getanovel-v1.0.zip",
//                "C:\\Users\\Administrator\\Desktop\\dev1\\getanovel-v1.0.zip.out");
//
//
//
//        decryptRSA("C:\\Users\\Administrator\\Desktop\\dev1\\getanovel-v1.0.zip.out",
//                "C:\\Users\\Administrator\\Desktop\\dev1\\newfile666.zip");



//        //只能执行一次 秘钥
        RSAUtil.RSABean rsaBean = null;
        try {
            rsaBean = RSAUtil.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        fileByteWriter(rsaBean.getPrivateKey(), "privateKey.wbc");
        fileByteWriter(rsaBean.getPublicKey(), "publicKey.wbc");





    }

    private static void decryptRSA(String inFile, String outFile) {
//        List<FileEncUtilBean> fileEncUtilBeans1 = FileIOUtil.fileByteReader(inFile);
        List<FileEncUtilBean> fileEncUtilBeans1 = fileByteReader(inFile);
        System.out.println("成功读取到文件");


        //正在写入
        System.out.println("正在解密"+fileEncUtilBeans1.size());
        List<FileEncUtilBean> dataList = new ArrayList<>();
        int time = 0;
        for (FileEncUtilBean bean : fileEncUtilBeans1) {
            if(time++ % 10000 == 0){
                System.out.println(time);
            }
            byte[] bytes = bean.getBytes();
            try {
                byte[] decrypt = RSAUtil.decrypt(bytes, privateKey);
                dataList.add(new FileEncUtilBean(decrypt,decrypt.length));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("读取到所有的数据：开始写入文件");


        fileByteWriter(dataList,outFile);

    }

    private static void encryptRSA(String inFile, String outFile) {
        System.out.println("开始加密");
        List<FileEncUtilBean> fileEncUtilBeans = FileIOUtil.fileByteReader(inFile,117);
        //这里做一些改变
        try {
            // 存储密文
            List<FileEncUtilBean> mmList = new ArrayList<>();

            byte[] bytes =  new byte[117];
            int index = 0;
            for (FileEncUtilBean fileEncUtilBean : fileEncUtilBeans) {

                //获取到其中一段
                byte[] bytes1 = fileEncUtilBean.getBytes();

                for (byte b : bytes1) {
                    bytes[index++] = b;
                    if(index == 117){
                        index = 0;
                        //加密得到一个密文
                        byte[] encrypt = RSAUtil.encrypt(bytes, publicKey);
                        mmList.add(new FileEncUtilBean(encrypt,encrypt.length));
                    }
                }
            }
            if(index != 0){
                byte[] bytes1 = new byte[index];
                for (int i = 0; i < bytes1.length; i++) {
                    bytes1[i] =  bytes[i];
                }
                byte[] encrypt = RSAUtil.encrypt(bytes1, publicKey);
                System.out.println("最后一次明文："+index);
                System.out.println("最后一次密文："+encrypt.length);
                mmList.add(new FileEncUtilBean(encrypt,encrypt.length));
            }

            System.out.println("加密完成，开始写出文件...");

            //准备输出到文件
            fileByteWriter(mmList,outFile);

            System.out.println("加密完成,结束");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private static void fileByteWriter(String text, String path) {
        fileByteWriter(text.getBytes(),path);

    }

    private static void fileByteWriter(byte[] bytes, String path) {

        try(OutputStream outputStream = Files.newOutputStream(Paths.get(path))) {

            outputStream.write(bytes);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 写出文件
     * @param fileByte 文件字节码
     * @param path 输出路径
     */
    private static void fileByteWriter(List<FileEncUtilBean> fileByte, String path) {

        try(OutputStream outputStream = Files.newOutputStream(Paths.get(path))) {

            for (FileEncUtilBean bean : fileByte) {
                outputStream.write(bean.getBytes(),0,bean.getLen());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static String fileStringReader(String path) {
        StringBuilder sb = new StringBuilder();
        try(BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(path))) {

            String len;
            while ((len = bufferedReader.readLine()) != null){
                sb.append(len);
            }

        }catch (IOException e){
            e.printStackTrace();
        }
        return sb.toString();
    }


    /**
     * 通过 文件地址 获取文件字节
     * @param path 文件路径
     * @return 文件字节
     */
    private static List<FileEncUtilBean> fileByteReader(String path) {

        int indexLen = 128;

        List<FileEncUtilBean> fileByte = new ArrayList<>();

        try(InputStream inputStream = Files.newInputStream(Paths.get(path))) {

            byte[] bytes = new byte[indexLen];

            // 秘钥下标
            int len;
            while ((len = inputStream.read(bytes)) != -1){
                fileByte.add(new FileEncUtilBean(bytes,len));
                bytes = new byte[indexLen];
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileByte;
    }


    //解密文件
    public static String decoderFile(String dataFile, String password) {

        Path path = Paths.get(dataFile);
        Path parent = path.getParent();
        String outPath = parent.toString()+"/";
        String[] headList = new String[0];
        List<FileEncUtilBean> fileEncUtilBeans = new ArrayList<>();
        try(InputStream inputStream = Files.newInputStream(path)
        ) {

            // 数据缓存
            byte[] head = new byte[1024];

            int read = inputStream.read(head);
            String s1 = new String(head).trim();
            headList = s1.split(",");
            System.out.println("拿到头部长度："+headList.length);

            //切片长度
            int lon = Integer.parseInt(headList[0]);
            System.out.println("获取到拆解字节："+lon);
            //获取存在的 密码
            String password1 = headList[1];
            System.out.println("获取到密码："+password1);
            if(!password.equals(password1)){
                System.out.println("密码错误！结束");
                return "密码错误！结束";
            }

            //获取文件结尾 校验传入文件 需要一致
            String fileLast = headList[2];
            System.out.println("获取文件结尾："+fileLast);
            String string = path.toString();
            if(!string.endsWith(fileLast)){
                System.out.println("文件格式错误，结束");
                return "文件格式错误，结束";
            }

            //原文件名字用于恢复
            String fileName = headList[3];
//            System.out.println("获取文件结尾："+fileName);
            outPath += fileName;


            byte[] pass = new byte[lon];
            // 秘钥下标
            int len;
            while ((len = inputStream.read(pass)) != -1){
//                System.out.println("读取到长度："+len);
                fileEncUtilBeans.add(new FileEncUtilBean(pass,len));
                pass = new byte[lon];
            }

        } catch (Exception e) {
            return "解密错误";
        }


        Path outPat = Paths.get(outPath);
        File outFile = outPat.toFile();

        outPath = reFileNamePath(outFile.getPath());

        try(OutputStream outputStream = Files.newOutputStream(Paths.get(outPath))
        ) {

            for (int i = 4; i < headList.length; i++) {
                int index = Integer.parseInt(headList[i]);
                FileEncUtilBean fileEncUtilBean = fileEncUtilBeans.get(index);
                outputStream.write(fileEncUtilBean.getBytes(),0,fileEncUtilBean.getLen());
            }

//            System.out.println("解密结束。。。");
//            return "解密结束。。。";

        }catch (IOException e){
            e.printStackTrace();
        }
        return "解密结束，文件为："+outPath;

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
//    private static String reFileNamePath(File outFile, int i) {
//        i++;
//        Path path1 = outFile.toPath();
//        Path parent = path1.getParent();
//        String fileName = outFile.getName();
//
//        String str = parent.toString()+"/("+ i +")"+fileName;
//
//        if(new File(str).exists()){
//            //如果即将输出的文件存在 则修改一下名字
//            return reFileNamePath(outFile,i);
//
//        }
//
//        return str;
//    }


    //加密文件
    public static String encodeFile(String pathFile, String password, String fileLast) {

        Path path = Paths.get(pathFile);
        File file = path.toFile();
        String fileName = file.getName();
        long length = file.length();

        String out = pathFile+"."+fileLast;

        out = reFileNamePath(out);

        try(InputStream inputStream = Files.newInputStream(path);
            OutputStream outputStream = Files.newOutputStream(Paths.get(out))
        ) {

            int m = 1024;
            int n = (int)(length/1024/100);
            if(n==0){
                m = 3;
                n = 1;
            }
            byte[] bytes = new byte[m* n];


            List<FileEncUtilBean> fileEncUtilBeans = new ArrayList<>();


            int len;
            while ((len = inputStream.read(bytes)) != -1){
//                //读取到原始数据
                //保存其中的内容，替换其中的内容
//                outputStream.write(bytes,0,len);
                fileEncUtilBeans.add(new FileEncUtilBean(bytes,len));
                bytes = new byte[m* n];

            }

            List<FileEncUtilBean> fileEncUtilBeans1 = new ArrayList<>(fileEncUtilBeans);
            FileEncUtilBean last = fileEncUtilBeans1.get(fileEncUtilBeans1.size()-1);
            fileEncUtilBeans1.remove(last);
            Collections.shuffle(fileEncUtilBeans1);
            fileEncUtilBeans1.add(last);

            //需要保存进去的 排序
//            ArrayList<Integer> list = new ArrayList<>();

            //头部数据
            StringBuilder stringBuilder = new StringBuilder();

            //分片的 byte
            stringBuilder.append(m*n);
            stringBuilder.append(",");
            //密码 解密的时候需要检验
            stringBuilder.append(password);
            stringBuilder.append(",");
            //加密方式 解密文件的结尾 不能改
            stringBuilder.append(fileLast);
            stringBuilder.append(",");
            //原文件名字用于恢复
            stringBuilder.append(fileName);
            stringBuilder.append(",");


            for (FileEncUtilBean fileEncUtilBean : fileEncUtilBeans) {
                int i = fileEncUtilBeans1.indexOf(fileEncUtilBean);
                stringBuilder.append(i);
                stringBuilder.append(",");

            }

            byte[] bytes1 = new byte[1024];
            byte[] bytes2 = stringBuilder.toString().getBytes();
//            System.out.println("这个长度不能超过1024："+bytes2.length);

            for (int i = 0; i < bytes2.length; i++) {
                bytes1[i] = bytes2[i];
            }

            fileEncUtilBeans1.add(0,new FileEncUtilBean(bytes1,1024));

            for (FileEncUtilBean fileEncUtilBean : fileEncUtilBeans1) {
                outputStream.write(fileEncUtilBean.getBytes(),0,fileEncUtilBean.getLen());
            }

//            System.out.println("加密成功:"+stringBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "加密成功！加密文件为："+out;
    }




    //解密 encodeFileOut 加密的文件
    private static void getFileOut(String dataFile, String passwordFile) {
        Path data = Paths.get(dataFile);
//        String path = "C:\\Users\\Administrator\\Desktop\\dev1\\jiemi.zip";
        String path = data.toFile().getPath();
        path = path.substring(0,path.length()-5);
        try(InputStream inputStream = Files.newInputStream(Paths.get(dataFile));
            InputStream inputStreamPassword = Files.newInputStream(Paths.get(passwordFile));
            OutputStream outputStream = Files.newOutputStream(Paths.get(path))

        ) {
            byte[] passwordBytes = new byte[1024];
            inputStreamPassword.read(passwordBytes);

            int passwordBytesindex = 0;
            byte[] bytes = new byte[1024];
            int len;
            while ((len = inputStream.read(bytes)) != -1){
                //读取到数据
//                System.out.println(len);
                //保存其中的内容，替换其中的内容
                bytes[len -1] = passwordBytes[passwordBytesindex++];
                if(passwordBytesindex == 1024){
                    inputStreamPassword.read(passwordBytes);
                    passwordBytesindex = 0;
                }
                outputStream.write(bytes,0,len);
            }
            System.out.println("解密结束");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //加密文件 会有额外文件产生
    private static void encodeFileOut(String pathFile) {

        Path path = Paths.get(pathFile);
        File file = path.toFile();
        Path parent = path.getParent();

        String outpath = parent.toString()+"\\"+file.getName()+".wbc";
        String outpassword1 = parent.toString()+"\\"+file.getName()+".password";
        try(InputStream inputStream = Files.newInputStream(path);
            OutputStream outputStream = Files.newOutputStream(Paths.get(outpath));
            OutputStream outpassword = Files.newOutputStream(Paths.get(outpassword1))
        ) {

            byte[] bytes = new byte[1024];
            byte[] passwordBytes = new byte[1024];
            int passwordBytesindex = 0;
            int len;
            while ((len = inputStream.read(bytes)) != -1){
//                //读取到原始数据
//                System.out.println(len);
                //保存其中的内容，替换其中的内容
                passwordBytes[passwordBytesindex++] = bytes[len -1];
                bytes[len -1] = 0;
                outputStream.write(bytes,0,len);

                if(passwordBytesindex == 1024){
                    outpassword.write(passwordBytes,0, passwordBytesindex);
                    passwordBytesindex = 0;
                }

            }
            outpassword.write(passwordBytes,0, passwordBytesindex);
            System.out.println("读取结束");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

class FileEncUtilBeanBak {

    private byte[] bytes;

    private Integer len;


    public FileEncUtilBeanBak(byte[] bytes, int len) {
        this.bytes = bytes;
        this.len = len;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public Integer getLen() {
        return len;
    }

    public void setLen(Integer len) {
        this.len = len;
    }

    @Override
    public String toString() {
        return "FileEncUtilBean{" +
                "bytes=" + Arrays.toString(bytes) +
                ", len=" + len +
                '}';
    }
}


