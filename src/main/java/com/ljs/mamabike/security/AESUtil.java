package com.ljs.mamabike.security;

import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


public class AESUtil {


    public static final String KEY_ALGORITHM = "AES";
    public static final String KEY_ALGORITHM_MODE = "AES/CBC/PKCS5Padding";


    /**
     * AES对称加密
     *
     * @param data
     * @param key  key需要16位
     * @return
     */
    public static String encrypt(String data, String key) {
        try {
            SecretKeySpec spec = new SecretKeySpec(key.getBytes("UTF-8"), KEY_ALGORITHM);
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM_MODE);
            cipher.init(Cipher.ENCRYPT_MODE, spec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
            byte[] bs = cipher.doFinal(data.getBytes("UTF-8"));
            return Base64Util.encode(bs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * AES对称解密 key需要16位
     *
     * @param data
     * @param key
     * @return
     */
    public static String decrypt(String data, String key) {
        try {
            SecretKeySpec spec = new SecretKeySpec(key.getBytes("UTF-8"), KEY_ALGORITHM);
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM_MODE);
            cipher.init(Cipher.DECRYPT_MODE, spec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
            byte[] originBytes = Base64Util.decode(data);
            byte[] result = cipher.doFinal(originBytes);
            return new String(result, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void main(String[] args) throws Exception {
        /**AES加密数据，客户端操作开始**/
        String key = "123456789abcdefg";            //约定好的key
        String result = "{'mobile':'18319830032','code':'6666','platform':'android'}";
        //传输的数据
        String enResult = encrypt(result, key);     //加密
        System.out.println(enResult);
        /**RSA加密AES的密钥,客户端操作结束**/
        byte[] enKey = RSAUtil.encryptByPublicKey(key.getBytes(), "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCEPB4Y7bd4ttV3phsm7VpR lmG0j19QUQWRG+MVCgw7f7ahvgwiXpwrqWP4hyZFxlFRUT4PlS11cKNut1Qm xjco1pYIxZUG6TfQj+a9rnUOGogdkyS76IpKi5/xal6MTmPqlfpE9SkBLvDc qLFX8FBo0+/ReoPrIPg3H4Saj99tOwIDAQAB");
        //需要再转码不然在http传输会出问题，因为上面输出乱码
        String baseKey = Base64Util.encode(enKey);
        System.out.println(baseKey);

//        /**服务端RSA解密AES的key**/
//        byte[] de = Base64Util.decode(baseKey);
//        byte[] deResultKey = RSAUtil.decryptByPrivateKey(de);
//        System.out.println(new String(deResultKey, "utf-8"));
//        String deResult = decrypt(enResult, key);       //服务端解密数据
//        System.out.println(deResult);
    }
}
