package com.nobug.util;

/**
 * @author 389561407@qq.com
 * @version 1.0
 * @since 2022-11-22
 */

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;


public class AESUtil {


    private static final String ALGORITHM = "AES";

    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";


    /**
     * 字符串加密返回字符串
     * @param message 需要加密的明文
     * @param password 密钥长度 32位 或者 16位
     * @return 密文 Base64 字符串
     * @throws Exception
     */
    public static String encrypt(String message, String password) throws Exception {
        if (message == null ){
            return null;
        }
        byte[] encrypt = encrypt(message.getBytes(StandardCharsets.UTF_8), password);
        // 转base64
        return Base64.getEncoder().encodeToString(encrypt);
    }

    /**
     *
     * @param byteContent 需要加密的数据
     * @param password 密钥长度 32位 或者 16位
     * @return 密文数据
     * @throws Exception
     */
    public static byte[] encrypt(byte[] byteContent, String password) throws Exception {

        // AES专用密钥
        SecretKeySpec secretKey = new SecretKeySpec(password.getBytes(StandardCharsets.UTF_8), ALGORITHM);
        // 创建密码器
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        // 初始化为加密模式的密码器
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        // 加密后的byte数据
        return cipher.doFinal(byteContent);
    }


    /**
     *
     * @param data 密文 Base64 字符串
     * @param password 密钥长度 32位 或者 16位
     * @return 明文
     * @throws Exception
     */
    public static String decrypt(String data, String password)throws Exception {

        byte[] decodeArray = Base64.getDecoder().decode(data);

        byte[] decrypt = decrypt(decodeArray, password);

        return new String(decrypt, StandardCharsets.UTF_8);

    }


    /**
     *
     * @param decodeArray 密文数据
     * @param password 密钥长度 32位 或者 16位
     * @return 明文数据
     * @throws Exception
     */
    public static byte[] decrypt(byte[] decodeArray, String password)throws Exception {
        // AES专用密钥
        SecretKeySpec secretKey = new SecretKeySpec(password.getBytes(StandardCharsets.UTF_8), ALGORITHM);
        // 创建密码器
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        // 初始化为解密模式的密码器
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(decodeArray);

    }

    public static void main(String[] args) throws Exception {
        String password = "5d5aaaafceae4023296c698248097364";
        String data = "这个是需要加密的数据！这个是需要加密的数据！这个是需要加密的数据！这个是需要加密的数据！这个是需要加密的数据！这个是需要加密的数据！这个是需要加密的数据！这个是需要加密的数据！这个是需要加密的数据！这个是需要加密的数据！这个是需要加密的数据！这个是需要加密的数据！这个是需要加密的数据！这个是需要加密的数据！这个是需要加密的数据！这个是需要加密的数据！";
        String encrypt = encrypt(data, password);
        System.out.println("密文："+encrypt);
        String decrypt = decrypt(encrypt, password);
        System.out.println("明文："+decrypt);

    }

}

