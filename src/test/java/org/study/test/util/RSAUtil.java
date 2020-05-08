package org.study.test.util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * RSA 是非对称的密码算法，密钥分公钥和私钥，公钥用来加密，私钥用于解密
 */
public class RSAUtil {
    /**
     * 生成密钥对：密钥对中包含公钥和私钥
     *
     * @return 包含 RSA 公钥与私钥的 KeyPair 对象
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static KeyPair genKeyPair() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        // 获得RSA密钥对的生成器实例
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        // 说的一个安全的随机数
        SecureRandom secureRandom = new SecureRandom(String.valueOf(System.currentTimeMillis()).getBytes("utf-8"));
        // 这里可以是1024、2048 初始化一个密钥对
        keyPairGenerator.initialize(2048, secureRandom);
        // 获得密钥对
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        return keyPair;
    }

    /**
     * 获取公钥
     *
     * @param keyPair 密钥对
     * @return 经 Base64 编码后的公钥字符串
     */
    public static String getPublicKey(KeyPair keyPair) {
        PublicKey publicKey = keyPair.getPublic();
        byte[] bytes = publicKey.getEncoded();
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * 获取私钥
     *
     * @param keyPair
     * @return 经 Base64 编码后的私钥字符串
     */
    public static String getPrivateKey(KeyPair keyPair) {
        PrivateKey privateKey = keyPair.getPrivate();
        byte[] bytes = privateKey.getEncoded();
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * 将经 Base64 编码后的公钥字符串转换成 PublicKey 对象
     *
     * @param publicKeyStr
     * @return PublicKey
     */
    public static PublicKey string2PublicKey(String publicKeyStr) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] bytes = Base64.getDecoder().decode(publicKeyStr);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(bytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    /**
     * 将经 Base64 编码后的私钥字符串转换成 PrivateKey 对象
     *
     * @param privateKeyStr
     * @return PrivateKey
     */
    public static PrivateKey string2PrivateKey(String privateKeyStr) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] bytes = Base64.getDecoder().decode(privateKeyStr);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(bytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    /**
     * 公钥加密
     *
     * @param content   待加密的内容
     * @param publicKey 加密所需的 PublicKey 对象
     * @return 加密得到的字节数组
     */
    public static byte[] encrypt(byte[] content, PublicKey publicKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] bytes = cipher.doFinal(content);
        return bytes;
    }

    /**
     * 私钥解密
     *
     * @param content    待解密的内容
     * @param privateKey 解密需要的 PrivateKey 对象
     * @return 解密得到的字节数组
     */
    public static byte[] decrypt(byte[] content, PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] bytes = cipher.doFinal(content);
        return bytes;
    }

    public static void main(String[] args)
            throws UnsupportedEncodingException, NoSuchAlgorithmException,
            InvalidKeySpecException, IllegalBlockSizeException,
            InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        String content = "abcdefg456+-=";
        System.out.println("原始字符串是：" + content);

        // 获得密钥对
        KeyPair keyPair = RSAUtil.genKeyPair();
        // 获得进行Base64 加密后的公钥和私钥 String
        String publicKeyStr = RSAUtil.getPublicKey(keyPair);
        String privateKeyStr = RSAUtil.getPrivateKey(keyPair);
        System.out.println("Base64处理后的公钥：" + publicKeyStr);
        System.out.println("Base64处理后的私钥：" + privateKeyStr);

        // 获得原始的公钥和私钥，并以字符串形式打印出来
        PublicKey publicKey = RSAUtil.string2PublicKey(publicKeyStr);
        PrivateKey privateKey = RSAUtil.string2PrivateKey(privateKeyStr);

        // 公钥加密/私钥解密
        byte[] publicEncryptBytes = RSAUtil.encrypt(content.getBytes(), publicKey);
        System.out.println("公钥加密：" + Base64.getEncoder().encodeToString(publicEncryptBytes));
        byte[] privateDecryBytes = RSAUtil.decrypt(publicEncryptBytes, privateKey);
        System.out.println("私钥解密：" + new String(privateDecryBytes, "utf-8"));
    }
}
