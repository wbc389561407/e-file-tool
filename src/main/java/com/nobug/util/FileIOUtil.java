package com.nobug.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 389561407@qq.com
 * @version 1.0
 * @since 2022-11-18
 */
public class FileIOUtil {


    /**
     * 写出文件
     * @param fileByte 文件字节码
     * @param path 输出路径
     */
    public static void fileByteWriter(List<FileEncUtilBean> fileByte, String path) {

        try(OutputStream outputStream = Files.newOutputStream(Paths.get(path))) {

            for (FileEncUtilBean bean : fileByte) {
                outputStream.write(bean.getBytes(),0,bean.getLen());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    /**
     * 通过 文件地址 获取文件字节
     * @param path 文件路径
     * @return 文件字节
     */
    public static List<FileEncUtilBean> fileByteReader(String path) {
        return fileByteReader(path,1024);
    }

    /**
     * 通过 文件地址 获取文件字节
     * @param path 文件路径
     * @return 文件字节
     */
    public static List<FileEncUtilBean> fileByteReader(String path, int indexLen) {

        List<FileEncUtilBean> fileByte = new ArrayList<>();

        try(InputStream inputStream = Files.newInputStream(Paths.get(path))) {

            byte[] bytes = new byte[indexLen];

            // 秘钥下标
            int len;
            while ((len = inputStream.read(bytes)) != -1){
                if(len != indexLen){
                    byte[] bytes1 = new byte[len];
                    for (int i = 0; i < len; i++) {
                        bytes1[i] = bytes[i];
                    }
                    fileByte.add(new FileEncUtilBean(bytes1,len));
                    break;
                }
                fileByte.add(new FileEncUtilBean(bytes,len));
                bytes = new byte[indexLen];
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileByte;
    }



    public static String fileStringReader(String path) {
        StringBuilder sb = new StringBuilder();
        try(BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(path))) {

            String len;
            while ((len = bufferedReader.readLine()) != null){
                sb.append(len);
            }

        }catch (IOException e){
            e.printStackTrace();
            return "";
        }
        return sb.toString();
    }



}
