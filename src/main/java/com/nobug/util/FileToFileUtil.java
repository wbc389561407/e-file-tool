package com.nobug.util;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author 389561407@qq.com
 * @version 1.0
 * @since 2022-11-21
 */
public class FileToFileUtil {

    public static void main(String[] args) {

//        encodeFileRSA("C:\\Users\\Administrator\\Desktop\\dev1\\新建文本.txt","123","mp4");

        decoderFileRSA("C:\\Users\\Administrator\\Desktop\\dev1\\新建文本.mp4","123","mp4");

    }



    static String publicKey =  FileIOUtil.fileStringReader("publicKey.wbc");
    static String privateKey =  FileIOUtil.fileStringReader("privateKey.wbc");

    //解密文件 todo 有bug
    public static String decoderFileRSA(String dataFile, String password, String mode) {

        Path path = Paths.get(dataFile);
        Path parent = path.getParent();
        String outPath = parent.toString()+"/";
        String[] headList = new String[0];
        List<FileEncUtilBean> fileEncUtilBeans = new ArrayList<>();
        try(InputStream inputStream = Files.newInputStream(path)
        ) {

            // 数据缓存
            byte[] head = new byte[1024*10];

            int read = inputStream.read(head);
            String s1 = new String(head).trim();
            headList = s1.split(",");
            System.out.println("拿到头部长度："+headList.length);

            //切片长度
            int lon = Integer.parseInt(headList[0]);
            System.out.println("获取到拆解字节："+lon);
            //获取存在的 密码
            String password1 = headList[1];
            password = HashUtil.md5(password + publicKey + privateKey);
            if(!password.equals(password1)){
                System.out.println("密码错误！结束");
                throw new RuntimeException("密码错误！结束");
            }

            //获取文件结尾 校验传入文件 需要一致
//            String fileLast = headList[2];
//            System.out.println("获取文件结尾："+fileLast);
//            String string = path.toString();
//            if(!string.endsWith(fileLast)){
//                System.out.println("文件格式错误，结束");
//                return "文件格式错误，结束";
//            }

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

    //加密文件 todo 有bug
    public static String encodeFileRSA(String pathFile, String password, String mode) {

        Path path = Paths.get(pathFile);
        File file = path.toFile();
        String fileName = file.getName();
        long length = file.length();

        String out = replaceType(pathFile, mode);

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
            password = HashUtil.md5(password + publicKey + privateKey);
            stringBuilder.append(password);
            stringBuilder.append(",");
            //加密方式 解密文件的结尾 不能改
            stringBuilder.append(mode);
            stringBuilder.append(",");
            //原文件名字用于恢复
            stringBuilder.append(fileName);
            stringBuilder.append(",");


            for (FileEncUtilBean fileEncUtilBean : fileEncUtilBeans) {
                int i = fileEncUtilBeans1.indexOf(fileEncUtilBean);
                stringBuilder.append(i);
                stringBuilder.append(",");

            }

            byte[] bytes1 = new byte[1024*10];
            byte[] bytes2 = stringBuilder.toString().getBytes();
//            System.out.println("这个长度不能超过1024："+bytes2.length);

            for (int i = 0; i < bytes2.length; i++) {
                bytes1[i] = bytes2[i];
            }

            fileEncUtilBeans1.add(0,new FileEncUtilBean(bytes1,1024*10));

            for (FileEncUtilBean fileEncUtilBean : fileEncUtilBeans1) {
                outputStream.write(fileEncUtilBean.getBytes(),0,fileEncUtilBean.getLen());
            }

//            System.out.println("加密成功:"+stringBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "加密成功！加密文件为："+out;
    }



    /**
     *  解密
     * @param dataFile
     * @param password
     * @return
     */
    public static String newDecoderFile(String dataFile, String password, String type) {

        log("读取文件");
        List<FileEncUtilBean> fileEncUtilBeans = FileIOUtil.fileByteReader(dataFile, 128);
        log("文件读取成功！"+fileEncUtilBeans.size());
        String headData = getHeadData(fileEncUtilBeans);
        log("头部数据解析成功！");
        System.out.println(headData);
        assert headData != null;
        String[] headList = headData.split(",");

        //切片长度
        int lon = Integer.parseInt(headList[0]);
        log("获取到拆解字节："+lon);
        //获取存在的 密码
        String password1 = headList[1];
        log("获取到密码："+password1);
        if(!password.equals(password1)){
            log("密码错误！结束");
            return "密码错误！结束";
        }

        //获取文件结尾 校验传入文件 需要一致
        String fileLast = headList[2];
        log("获取文件结尾："+fileLast);
        if(!dataFile.endsWith(fileLast)){
            log("文件格式错误，结束");
            return "文件格式错误，结束";
        }

        //原文件名字用于恢复
        String fileNameType = headList[3];
        log("需要恢复的原文件名称："+fileNameType);

        String outPath = replaceType(dataFile,fileNameType);

        log("获取data数据"+fileEncUtilBeans.size());
        List<FileEncUtilBean> fileEncUtilBeans1 = formatList(fileEncUtilBeans,lon);
        log("解析获得数据段数："+fileEncUtilBeans1.size());
        List<FileEncUtilBean> fileEncUtilBeans2 = new ArrayList<>();
        for (int i = 4; i < headList.length; i++) {
            int index = Integer.parseInt(headList[i]);
            FileEncUtilBean fileEncUtilBean = fileEncUtilBeans1.get(index);
            fileEncUtilBeans2.add(fileEncUtilBean);
        }

        log("输出文件："+outPath);
        FileIOUtil.fileByteWriter(fileEncUtilBeans2,outPath);

        log("成功："+outPath);
        return "解密结束，文件为：";

    }

    //将数转化为 指定长度 字节
    private static List<FileEncUtilBean> formatList(List<FileEncUtilBean> fileEncUtilBeans, int lon) {
        List<FileEncUtilBean> relist = new ArrayList<>();

        int index = 0;
        byte[] bytes = new byte[lon];
        for (FileEncUtilBean fileEncUtilBean : fileEncUtilBeans) {
            for (byte aByte : fileEncUtilBean.getBytes()) {

                bytes[index++] = aByte;

                if (index % lon == 0){
                    bytes = new byte[lon];
                    index = 0;
                    relist.add(new FileEncUtilBean(bytes,lon));
                }
            }
        }
        if(index != 0){
            relist.add(new FileEncUtilBean(bytes,index));
        }

        return relist;
    }

    private static String getHeadData(List<FileEncUtilBean> fileEncUtilBeans) {
        FileEncUtilBean remove = fileEncUtilBeans.remove(0);
        byte[] bytes = remove.getBytes();
        StringBuilder sb = new StringBuilder();

        try {
            byte[] decrypt = RSAUtil.decrypt(bytes, privateKey);
            String s = new String(decrypt);
            System.out.println("获取后面几段："+s);
            for (int i = Integer.parseInt(s); i >= 0; i--) {
                FileEncUtilBean remove1 = fileEncUtilBeans.remove(i);
                byte[] decrypt1 = RSAUtil.decrypt(remove1.getBytes(), privateKey);
                sb.append(new String(decrypt1));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 快速加密方式 不适用于小文件 适用于大文件
     * @param pathFile
     * @param password
     * @param type
     * @return
     */
    public static String newEncodeFile(String pathFile, String password, String type) {

        log("开始加密");
        Path path = Paths.get(pathFile);
        File file = path.toFile();
        String fileName = file.getName();
        long length = file.length();

        String out = replaceType(pathFile,type);
        log("将要输出的文件："+out);

        try(InputStream inputStream = Files.newInputStream(path)
        ) {

            int m = 1024;
            int n = (int)(length/1024/100);
            if(n==0){
                m = 3;
                n = 1;
            }
            byte[] bytes = new byte[m* n];


            List<FileEncUtilBean> fileEncUtilBeans = new ArrayList<>();

            log("开始读取文件。。。");
            int len;
            while ((len = inputStream.read(bytes)) != -1){
//                //读取到原始数据
                //保存其中的内容，替换其中的内容
//                outputStream.write(bytes,0,len);
                fileEncUtilBeans.add(new FileEncUtilBean(bytes,len));
                bytes = new byte[m* n];

            }
            log("文件读取完成。。。");

            List<FileEncUtilBean> fileEncUtilBeans1 = new ArrayList<>(fileEncUtilBeans);
            FileEncUtilBean last = fileEncUtilBeans1.get(fileEncUtilBeans1.size()-1);
            fileEncUtilBeans1.remove(last);
            Collections.shuffle(fileEncUtilBeans1);
            fileEncUtilBeans1.add(last);
            log("进行加密计算完成。。。");

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
            stringBuilder.append(type);
            stringBuilder.append(",");
            //原文件名字用于恢复
            stringBuilder.append(fileName);
            stringBuilder.append(",");



            for (FileEncUtilBean fileEncUtilBean : fileEncUtilBeans) {
                int i = fileEncUtilBeans1.indexOf(fileEncUtilBean);
                stringBuilder.append(i);
                stringBuilder.append(",");

            }


            System.out.println("头部字数："+stringBuilder.toString().length());
            String headData = stringBuilder.toString();

            int headNum = 0;
            int length1 = headData.length();
            while (true){

                int startIndex = 40 * headNum;
                int endIndex = 40 * (headNum+1);
                endIndex = Math.min(endIndex,length1);

                String str = headData.substring(startIndex, endIndex);

                byte[] encrypt = RSAUtil.encrypt(str.getBytes(), publicKey);
                fileEncUtilBeans1.add(0,new FileEncUtilBean(encrypt,encrypt.length));
                if(endIndex == length1){
                    break;
                }
                headNum++;
            }

            //写入头部段数 数量 第一段获取数量  然后获取头部数据
            String s = String.valueOf(headNum);
            System.out.println("头部段数："+s);
            byte[] bytes1 = s.getBytes(StandardCharsets.UTF_8);
            byte[] encrypt = RSAUtil.encrypt(bytes1, publicKey);
            fileEncUtilBeans1.add(0,new FileEncUtilBean(encrypt,encrypt.length));

            System.out.println("一共写出"+fileEncUtilBeans1.size());
            FileIOUtil.fileByteWriter(fileEncUtilBeans1,out);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "加密成功！加密文件为："+out;
    }

    //替换路径结尾 . 之后的类型
    private static String replaceType(String pathFile, String type) {
        int i = pathFile.lastIndexOf(".");
        return pathFile.substring(0, i+1)+type;
    }

    private static void log(String log) {
        System.out.println(log);
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

}
