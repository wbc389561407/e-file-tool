package com.nobug.util;

/**
 * @author wangbingchen
 * @Description 非对称加密 秘钥生成 加密 解密
 * @create 2021-11-30 13:51
 */

import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;


public class RSAUtil {
    private RSAUtil(){}
    private static final String KEY_ALGORITHM  = "RSA";
    private static final String CHARSET = "UTF-8";

    // 数字大于512 并且是 64的倍数 数字越大效率低 一般1024 需要更加安全使用 2048
    private static final int DEFAULT_KEY_SIZE = 1024;



    /**
     * 随机生成密钥对 只需要使用一次就好
     *
     * @throws NoSuchAlgorithmException
     */
    public static RSABean generateKeyPair() throws NoSuchAlgorithmException {
        // KeyPairGenerator 类用于生成公钥和私钥对，基于RSA算法生成对象
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        // 初始化密钥对生成器，密钥大小为 96-1024 位
        keyPairGen.initialize(DEFAULT_KEY_SIZE, new SecureRandom());
        // 生成一个密钥对，保存在 keyPair 中
        KeyPair keyPair = keyPairGen.generateKeyPair();
        // 得到私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        // 得到公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        String publicKeyString = new String(Base64.getEncoder().encode(publicKey.getEncoded()));
        // 得到私钥字符串
        String privateKeyString = new String(Base64.getEncoder().encode((privateKey.getEncoded())));

        return new RSABean(publicKeyString,privateKeyString);
    }


    /**
     * RSA公钥加密
     *
     * @param bytes       加密字符串
     * @param publicKey 公钥
     * @return 密文
     * @throws Exception 加密过程中的异常信息
     */
    public synchronized static byte[] encrypt(byte[] bytes, String publicKey) throws Exception {
        // base64 编码的公钥
        byte[] decoded = Base64.getDecoder().decode(publicKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance(KEY_ALGORITHM).generatePublic(new X509EncodedKeySpec(decoded));
        // RSA加密
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        // 公钥加密
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        return cipher.doFinal(bytes);
    }

    /**
     * RSA公钥加密
     *
     * @param data       加密字符串
     * @param publicKey 公钥
     * @return 密文
     * @throws Exception 加密过程中的异常信息
     */
    public static String encrypt(String data, String publicKey) throws Exception {
        // base64 编码的公钥
        byte[] decoded = Base64.getDecoder().decode(publicKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance(KEY_ALGORITHM).generatePublic(new X509EncodedKeySpec(decoded));
        // RSA加密
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        // 公钥加密
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes(CHARSET)));
    }

    /**
     * RSA私钥解密
     *
     * @param inputByte        加密字符串
     * @param privateKey 私钥
     * @return 铭文
     * @throws Exception 解密过程中的异常信息
     */
    public static byte[] decrypt(byte[] inputByte, String privateKey) throws Exception {
        // base64 编码的私钥
        byte[] decoded = Base64.getDecoder().decode(privateKey);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance(KEY_ALGORITHM).generatePrivate(new PKCS8EncodedKeySpec(decoded));
        // RSA 解密
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        // 私钥解密
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        return cipher.doFinal(inputByte);
    }

    /**
     * RSA私钥解密
     *
     * @param data        加密字符串
     * @param privateKey 私钥
     * @return 铭文
     * @throws Exception 解密过程中的异常信息
     */
    public static String decrypt(String data, String privateKey) throws Exception {
        byte[] inputByte = Base64.getDecoder().decode(data.getBytes(CHARSET));
        // base64 编码的私钥
        byte[] decoded = Base64.getDecoder().decode(privateKey);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance(KEY_ALGORITHM).generatePrivate(new PKCS8EncodedKeySpec(decoded));
        // RSA 解密
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        // 私钥解密
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        return new String(cipher.doFinal(inputByte));
    }


    static class RSABean{
        //公钥
        private String publicKey;
        //私钥
        private String privateKey;

        public RSABean(String publicKeyString, String privateKeyString) {
            publicKey = publicKeyString;
            privateKey = privateKeyString;
        }

        public String getPublicKey() {
            return publicKey;
        }

        public String getPrivateKey() {
            return privateKey;
        }


        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("publicKey:").append(publicKey);
            sb.append("\n");
            sb.append("privateKey:").append(privateKey);
            return sb.toString();
        }
    }

}

