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

    //测试
    public static void main(String[] args) {
        // 准备好需要传送的重要资料内容
        String originData = "这是一个需要加密的正文！这是一个需要加密的正文！这是一个需要加密的正文！这是一个需要加密的正文！这是一个需要加密的正文！这是一个需要加密的正文！这是一个需要加密的正文！这是一个需要加密的正文！这是一个需要加密的正文！这是一个需要加密的正文！这是一个需要加密的正文！这是一个需要加密的正文！这是一个需要加密的正文！这是一个需要加密的正文！这是一个需要加密的正文！这是一个需要加密的正文！这是一个需要加密的正文！";

        try {
            // 调用此方法得到随机密钥对
            RSABean rsaBean = RSAUtil.generateKeyPair();
            //publicKey:MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmepOEovX9vDxGl4W6e9DwwUpWtwJzi778nrYncexntzMfxd7Fw3hjFMwfNZoWpRCbA/LmbXGzduf/fKA8d8kEE5IVcflpo5DiFVg6hZDeHhEPngR/s6azFgUCSASiQ7XtPCs6QXDby8uyetZFq2W0ByOMN81FsPKmfQlz587LcDHjEqE6k8/9j/CIUnMmm1oWARwwa4OpHSU+s9wWaDq051ZxOEXXNC+hFHuYjEmXMZZJeloX7RV9AAV7FnOvfINgz5e/GAudlbQ5UQzGsJvQH5856DLh+z48nQiqou63ABmz4X5xYNpxHJRjuqTwF2SwUc1WLDzdbumAI+i3TXilwIDAQAB
            //privateKey:MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQCZ6k4Si9f28PEaXhbp70PDBSla3AnOLvvyetidx7Ge3Mx/F3sXDeGMUzB81mhalEJsD8uZtcbN25/98oDx3yQQTkhVx+WmjkOIVWDqFkN4eEQ+eBH+zprMWBQJIBKJDte08KzpBcNvLy7J61kWrZbQHI4w3zUWw8qZ9CXPnzstwMeMSoTqTz/2P8IhScyabWhYBHDBrg6kdJT6z3BZoOrTnVnE4Rdc0L6EUe5iMSZcxlkl6WhftFX0ABXsWc698g2DPl78YC52VtDlRDMawm9AfnznoMuH7PjydCKqi7rcAGbPhfnFg2nEclGO6pPAXZLBRzVYsPN1u6YAj6LdNeKXAgMBAAECggEBAJRXANXZF9T1FLpf8l+WbU15HQlnxr+EAukJQtqWSvccQK9Aty5Z/4aZ6sJ2dm7OM1Ioywai7i8bPBCpd2Mlt4FLoWWjWg9zT10HRHd366Eg8Ezwtg9JmmHVhcHhuluEBZtq1otjNZ9j7wlGnT9BIHgE/XnWtjjxjMNiTkrTYVrO0h8ZPXjN9B5AjqJJMlloTZ7dTnF45eFUcGFn6fv0JHl3yew3PcYZ1vmuQpNKrxgbMS7t6MvKGL/A9Ba6qC4S4i/om1LQWJvJTJ9N2APF86ODlU7CpzOc3X/bV/PsyT2zTrcJN4ED6GfxACxPscjSSI936lvNjpyY0qwt1ln+fOECgYEAyG5oKc5+NZsNU52xQHznbPv3vUDGhbUaX9s24j6FFf5tWtPauDF1iwAOIH3OPP7rdVexKxrItUX1wRsPgVsNmctFBfLwj9UprML7gOSZmEnYpY2ff4BKr4ew9I+jv4gRpQTg0JjlnLTxbS7rR4yzhSNzx5nKW+1wAQRHFvgaJXECgYEAxJZtXyY6HWy/cb+9N2KBxlVatlI+rmNLoD/BXPzxEgfaYOs5cI4slVBGqBNKbxWQgP4mcIyP0RkAGR44rA2uhpYTVKdjFP0uEEaa6PoyCMf37GS1jwI7IyhlJJ2tbm1c76+5sfp+yi3ysSGuzfkQza5s0cdx4DbebEivPUeMZIcCgYBNsV53Ni/+H4he7TWURdZ/jHxPxBgwJC+BELyJnqqACAvjtVOHKsThiLfxKFMVmcszxTZL+ynp+qB0pkSwY9c10nGDCNTvLDXnXf2XzQUs3xM3uVomVlidCgAyDKvwxpHwYZ+zvFDlGc6sAA/7wDKmXUO4D4k9cSCFhF76LFoWQQKBgQCYvegszZ7JzfzRfJCfv8HlP9xDXJOnGy9fSyoJ/d2wjLl4JEt00IKDYOQFIzfqJ28nSlDDkfMMG8ifu4N86wR3PZO3anbpj+2CZ9lku0C04eoXmggXHTyljHVJhdmCuyZCdN5rEp4AqMKwLeOL1cDgjm2ucLBh7T/UEoVVr4m9cQKBgQDHxseohckU4TUrGTN9HnFeBMhLTogMHmxkNplKfJK9FBUkq0mpYC9xTykeghrqwPRP3Y/odlkW+TmHz/LNV7oXSDVH/15DBKHZTTbK7e94NccRs+xgHJv7bXvg9F5a5yJphDwP5VxXTHNklpbg6ssTsgiXzjuSormImT5uoZSsRw==
            //publicKey:MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCVdu7g2fiHVm8UvuKjYHKUkNvziAxk00/O5UToDGQ8M7XMgMkDByYcO6eoJQPuLRzS8sWEyuWoeMDYr22xYLw2z9W64A0VmQhfCrOPIjg/B9JyDITnPi/5LnQyvi/0NbG9gToPPbzMYvIgEGNoZ8OKczPhPhF5GHv28mB1HHAINwIDAQAB
            //privateKey:MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJV27uDZ+IdWbxS+4qNgcpSQ2/OIDGTTT87lROgMZDwztcyAyQMHJhw7p6glA+4tHNLyxYTK5ah4wNivbbFgvDbP1brgDRWZCF8Ks48iOD8H0nIMhOc+L/kudDK+L/Q1sb2BOg89vMxi8iAQY2hnw4pzM+E+EXkYe/byYHUccAg3AgMBAAECgYB9gk7cAA3D0CAdQqHcYaw192LMIM2PiSa2bS4s0eMF7sJWBTtG0KT/BlUmTFb9lWJj/btBZIPvsd721nF9hubksHUBDqLB2bn6vbn37E3uH4C3LtzrA6x2XzJrjni61s1hCher+JrawumDaxlcIJU+qLgiDyXuyGyo6SfdqoGRwQJBAODIE/YCZUc8ZZj3uv5Kv/+SeuQ6DlhzJ2kbafw6hUCbs29ype2tzcAzTXb1UrAdpKSm4R9oUVvr7JCHUY8HL6ECQQCqOQXHdPJOm0Ef4M1tNR4G9RoBcfIhY7Nt1/gOpD2Buel9s3R+h8j6lBvUPRBHnQtVLGv5Wo4Ke21UnqFDqgjXAkBfnDsBVRuHJWWBnz3mlBz+tGuZUChx7ulAh+yY4Lt1M8UrDnUNl/QAYNH+W3MEflddwa3YEiMpGjzRbP1//OqBAkAE71pHihBsTvv4XKAYBm40bW6vlAodBbLyAEzkv8Y4uJcYWdeKw40EFr8nzA1oxhjKA1uuAv4TN7jXtzGdsJRJAkBJYCXQkjjHmB+SUMUoCDigaAA3xyYqteqj4lmc7l6dd4cxQY5Z3sNqfFt1YO9shCs0rZZvAnHlK5M/AtcPdm1C
            //打印密钥对 这是由需要把必要保存下来
            System.out.println(rsaBean);

            //保存的密钥对
            //publicKey:MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCXyrIUz773u5/JxuHl0ehZ2PPYsWcO96loCPR8QKdj25IjDA2VCpfOWppJ7VxHwvbXnu1wbzeVZBRlrk56OiZt5vbAs+Zit/ZDLqQBUFQZpQ9s7wFAOKq8dPFpjSqlD3Ne7RjIAPTi+Hu7g/QejhGQld+yUJ4KI+gCq+KaRpLr8wIDAQAB
            //privateKey:MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJfKshTPvve7n8nG4eXR6FnY89ixZw73qWgI9HxAp2PbkiMMDZUKl85amkntXEfC9tee7XBvN5VkFGWuTno6Jm3m9sCz5mK39kMupAFQVBmlD2zvAUA4qrx08WmNKqUPc17tGMgA9OL4e7uD9B6OEZCV37JQngoj6AKr4ppGkuvzAgMBAAECgYAct7WPaGp51h55/02XvGnYLcqckZus4kBDtYYDx/ujUdRJGyuqqfFkkhGksOqHjSREUQYdAn2inueZASxJxZylXab3ci8y2rDIUOVtks29Dx7DH9zvmJAN5RpZ+6yV6xJ30oLztVFglwJ7n8gLKpeWWsOjLdACYdxdZoYcVw2kqQJBAM3hUs5om/mJmymy4NX+Z5Xjz4qpXEewfTJldF8aqrhNvzbki09HJTvgIkRgViaFfHbJC+unP+M/4T+Tp09VP1cCQQC8voZW1D6Uj2eg5IXJfP1CBvYuWcKA+ciO33jtQsxTYZrmxSHJIbdeme8NFP3jJOhSf84xMtO2JHXZqgEgSYLFAkAj1ZdDJAQZ//CI5oCp3pkLBtbl7KqKM9bmwa/qqcqTP70LgeYZi2mYrV9phVcZZ1yNlFrFzpuZI1JCbaHOdikLAkBT3s3PU60jUr4XzrRsnm/joThPzG8T5OrsIf2YYLBu9NZiaYLj32meTgTd6VAQWVwrHMPBNPbC31zHBjZN5JyJAkEAmYO1VrAQmoPEYVA3GC6b41CHmUEOpDRQSWZgMsEtDXJTc48pK96QS+PZOzFSlzxi0qkzcNy1LEskWvrC8X9X+w==

            //使用公钥加密
            String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCXyrIUz773u5/JxuHl0ehZ2PPYsWcO96loCPR8QKdj25IjDA2VCpfOWppJ7VxHwvbXnu1wbzeVZBRlrk56OiZt5vbAs+Zit/ZDLqQBUFQZpQ9s7wFAOKq8dPFpjSqlD3Ne7RjIAPTi+Hu7g/QejhGQld+yUJ4KI+gCq+KaRpLr8wIDAQAB";

            //使用公钥加密 会到到一个密文 讲密文在网络上发送 就算被别人看到了也没有关系
            String encryData = encrypt(originData,publicKey);
            System.out.println("密文：" + encryData);
            // 将密文网络传输f0e4QKfvRWO657VmfBHcWCn7THw5If/7/Mgh4pIUQGkFtkcU7Ku5YcjqN64DfF1Kbw3Wcwvh7zwSx/8xgP2RRZmE65lrf5HGpknqqjkAJPxAGPmyhVw7OF4hDHKjfgxtE8hOEwXc7bhSj7WICWWaxgXDxIdrgcudWN7sqrdO3Jg=

            //接收到密文 用私钥解密 要使用与公钥配对的特定的私钥解密
            String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJfKshTPvve7n8nG4eXR6FnY89ixZw73qWgI9HxAp2PbkiMMDZUKl85amkntXEfC9tee7XBvN5VkFGWuTno6Jm3m9sCz5mK39kMupAFQVBmlD2zvAUA4qrx08WmNKqUPc17tGMgA9OL4e7uD9B6OEZCV37JQngoj6AKr4ppGkuvzAgMBAAECgYAct7WPaGp51h55/02XvGnYLcqckZus4kBDtYYDx/ujUdRJGyuqqfFkkhGksOqHjSREUQYdAn2inueZASxJxZylXab3ci8y2rDIUOVtks29Dx7DH9zvmJAN5RpZ+6yV6xJ30oLztVFglwJ7n8gLKpeWWsOjLdACYdxdZoYcVw2kqQJBAM3hUs5om/mJmymy4NX+Z5Xjz4qpXEewfTJldF8aqrhNvzbki09HJTvgIkRgViaFfHbJC+unP+M/4T+Tp09VP1cCQQC8voZW1D6Uj2eg5IXJfP1CBvYuWcKA+ciO33jtQsxTYZrmxSHJIbdeme8NFP3jJOhSf84xMtO2JHXZqgEgSYLFAkAj1ZdDJAQZ//CI5oCp3pkLBtbl7KqKM9bmwa/qqcqTP70LgeYZi2mYrV9phVcZZ1yNlFrFzpuZI1JCbaHOdikLAkBT3s3PU60jUr4XzrRsnm/joThPzG8T5OrsIf2YYLBu9NZiaYLj32meTgTd6VAQWVwrHMPBNPbC31zHBjZN5JyJAkEAmYO1VrAQmoPEYVA3GC6b41CHmUEOpDRQSWZgMsEtDXJTc48pK96QS+PZOzFSlzxi0qkzcNy1LEskWvrC8X9X+w==";
            String decryData = decrypt(encryData, privateKey);
            System.out.println("解密后的接结果：" + decryData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

