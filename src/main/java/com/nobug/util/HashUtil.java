package com.nobug.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5,SHA1,SHA256等 哈希散列
 * @author wangbingchen
 * @Description
 * @create 2021-11-25 16:13
 */
public class HashUtil {

    enum HashUtilType{
        MD5("MD5"),
        SHA1("SHA-1"),
        SHA224("SHA-224"),
        SHA256("SHA-256"),
        SHA384("SHA-384"),
        SHA512("SHA-512"),
       ;

        private MessageDigest messageDigest;


        HashUtilType(String type){
            try {
                messageDigest = MessageDigest.getInstance(type);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }


    }



    private HashUtil(){

    }



    public static String md5(String str) {
        return md5(str.getBytes());
    }

    public static String md5(byte[] byteArray) {
        return hashcode(byteArray, HashUtilType.MD5);
    }

    public static String sha1(String str) {
        return sha1(str.getBytes());
    }

    public static String sha1(byte[] byteArray) {
        return hashcode(byteArray, HashUtilType.SHA1);
    }

    public static String sha224(String str) {
        return sha224(str.getBytes());
    }

    public static String sha224(byte[] byteArray) {
        return hashcode(byteArray, HashUtilType.SHA224);
    }

    public static String sha256(String str) {
        return sha256(str.getBytes());
    }

    public static String sha256(byte[] byteArray) {
        return hashcode(byteArray, HashUtilType.SHA256);
    }

    public static String sha384(String str) {
        return sha384(str.getBytes());
    }

    public static String sha384(byte[] byteArray) {
        return hashcode(byteArray, HashUtilType.SHA384);
    }

    public static String sha512(String str) {
        return sha512(str.getBytes());
    }

    public static String sha512(byte[] byteArray) {
        return hashcode(byteArray, HashUtilType.SHA512);
    }




    /**
     *
     * @param  byteArray
     * @return
     */
    public synchronized static String hashcode(byte[] byteArray, HashUtilType type) {
        try {

            byte[] md5Bytes = type.messageDigest.digest(byteArray);

            StringBuilder hexValue = new StringBuilder();
            for (byte md5Byte : md5Bytes) {
                int val = ((int) md5Byte) & 0xff;
                if (val < 16) {
                    hexValue.append("0");
                }
                hexValue.append(Integer.toHexString(val));
            }

            return hexValue.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }


    /**
     * 生成32位 小写
     *
     * @param str
     * @return
     */
    public static String hashcode(String str, HashUtilType type) {
        return hashcode(str.getBytes(),type);

    }

    /**
     * 生成16位 md5 小写
     *
     * @param str
     * @return
     */
    public static String md5Short(String str) {
        return md5(str).substring(8, 24);
    }



    /**
     * 测试方法
     * @param args
     */
    public static void main(String[] args) {
        String str = "123456";
        System.out.println("字符串："+str);
        System.out.println("16位 小写："+md5Short(str));
        System.out.println("32位 小写："+md5(str));
        System.out.println("40位 小写："+sha1(str));
        System.out.println("56位 小写："+sha224(str));
        System.out.println("64位 小写："+sha256(str));
        System.out.println("96位 小写："+sha384(str));
        System.out.println("128位 小写："+sha512(str));
    }



}
