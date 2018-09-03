package com.ljs.mamabike.security;/**
 * Created by wangjianbin on 2017/7/31.
 */





import javax.crypto.Cipher;


import java.io.InputStream;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;


/**
 * Author ljs
 * Description 加密解密key的工具类
 * Date 2018/9/3 10:12
 **/
public class RSAUtil {

    /**
     * 私钥字符串
     */
    private static String PRIVATE_KEY ="";
    /**
     * 公钥字符串
     */
    private static String PUBLIC_KEY ="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCEPB4Y7bd4ttV3phsm7VpR lmG0j19QUQWRG+MVCgw7f7ahvgwiXpwrqWP4hyZFxlFRUT4PlS11cKNut1Qm xjco1pYIxZUG6TfQj+a9rnUOGogdkyS76IpKi5/xal6MTmPqlfpE9SkBLvDc qLFX8FBo0+/ReoPrIPg3H4Saj99tOwIDAQAB";


    public static final String KEY_ALGORITHM = "RSA";


    /**
     * 读取密钥字符串
     * @throws Exception
     */

    public static void convert() throws Exception {
        byte[] data = null;

        try {
            InputStream is = RSAUtil.class.getResourceAsStream("/enc_pri");
            int length = is.available();
            data = new byte[length];
            is.read(data);
        } catch (Exception e) {
        }

        String dataStr = new String(data);
        try {
            PRIVATE_KEY = dataStr;
        } catch (Exception e) {
        }

        if (PRIVATE_KEY == null) {
            throw new Exception("Fail to retrieve key");
        }
    }



    /**
     * Author ljs
     * Description 私钥解密
     * Date 2018/9/3 10:15
     **/
    public static byte[] decryptByPrivateKey(byte[] data) throws Exception {
        convert();
        byte[] keyBytes = Base64Util.decode(PRIVATE_KEY);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        return cipher.doFinal(data);
    }

    /**
     * Author ljs
     * Description 公钥加密
     * Date 2018/9/3 10:15
     **/
    public static byte[] encryptByPublicKey(byte[] data, String key) throws Exception {
        byte[] keyBytes = Base64Util.decode(key);
        X509EncodedKeySpec pkcs8KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicKey = keyFactory.generatePublic(pkcs8KeySpec);

        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

//    /**
//     *  引入第三方密码工具包 处理编码
//     * @param stored
//     * @return
//     * @throws GeneralSecurityException
//     * @throws Exception
//     */
//    public static PrivateKey makePrivateKey(String stored) throws GeneralSecurityException, Exception {
//        /*byte[] data = Base64.getDecoder().decode(stored);
//        PKCS8EncodedKeySpec spec = new  PKCS8EncodedKeySpec(data);
//        KeyFactory fact = KeyFactory.getInstance("RSA");
//        return fact.generatePrivate(spec);*/
//        byte[] data = Base64Util.decode(stored);
//        ASN1EncodableVector v = new ASN1EncodableVector();
//        v.add(new ASN1Integer(0));
//        ASN1EncodableVector v2 = new ASN1EncodableVector();
//        v2.add(new ASN1ObjectIdentifier(PKCSObjectIdentifiers.rsaEncryption.getId()));
//        v2.add(DERNull.INSTANCE);
//        v.add(new DERSequence(v2));
//        v.add(new DEROctetString(data));
//        ASN1Sequence seq = new DERSequence(v);
//        byte[] privKey = seq.getEncoded("DER");
//        PKCS8EncodedKeySpec spec = new  PKCS8EncodedKeySpec(privKey);
//        KeyFactory fact = KeyFactory.getInstance("RSA");
//        PrivateKey key = fact.generatePrivate(spec);
//
//        return key;
//
//    }


    public static void main(String[] args) throws Exception {
//        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
//        keyPairGen.initialize(1024);
//        KeyPair keyPair = keyPairGen.generateKeyPair();
//        PrivateKey privateKey = keyPair.getPrivate();
//        PublicKey publicKey = keyPair.getPublic();
//        System.out.println(Base64Util.encode(privateKey.getEncoded()));
//        System.out.println(Base64Util.encode(publicKey.getEncoded()));



        byte[] enR = encryptByPublicKey("ljs".getBytes("utf-8"),PUBLIC_KEY);
        System.out.println(enR.toString());
        byte[] deR = decryptByPrivateKey(enR);
        System.out.println(new String(deR, "utf-8"));

    }

}
