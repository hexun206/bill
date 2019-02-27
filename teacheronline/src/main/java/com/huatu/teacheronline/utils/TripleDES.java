package com.huatu.teacheronline.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class TripleDES {

    /**
     * 加密方式
     */
    private final static String DES = "DESede";
    private final static String KEY_NUVA_KEY = "0123456789QWEQWEEWQQ1234";

    /**
     * 密码加密
     *
     * @param data
     * @return
     * @throws Exception
     * @author Eric_Wang
     */
    public final static String encrypt(String data, String key) {
        try {
            return byte2hex(encrypt(data.getBytes(), key.getBytes()));
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 加密
     *
     * @param src 数据源
     * @param key 密钥，长度必须是24的倍数
     * @return 返回加密后的数据
     * @throws Exception
     * @author Eric_Wang
     */
    public static byte[] encrypt(byte[] src, byte[] key) throws Exception {
        SecretKeySpec securekey = new SecretKeySpec(key, DES);
        // Cipher对象实际完成加密操作
        Cipher cipher = Cipher.getInstance(DES);
        // 用密匙初始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, securekey);
        // 现在，获取数据并加密
        // 正式执行加密操作
        return cipher.doFinal(src);
    }

    /**
     * 解密
     *
     * @param src 数据源
     * @param key 密钥，长度必须是24的倍数
     * @return 返回解密后的原始数据
     * @throws Exception
     * @author Eric_Wang
     */
    public static byte[] decrypt(byte[] src, byte[] key) throws Exception {
        SecretKeySpec securekey = new SecretKeySpec(key, DES);
        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance(DES);
        // 用密匙初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey);
        // 现在，获取数据并解密
        // 正式执行解密操作
        byte[] a = cipher.doFinal(src);
        return a;
    }

    /**
     * 二行制转字符串
     *
     * @param b
     * @return
     * @author Eric_Wang
     */
    public static String byte2hex(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1)
                hs.append("0" + stmp);
            else
                hs.append(stmp);
        }
        return hs.toString().toUpperCase();
    }

    public static byte[] hex2byte(String hex) throws IllegalArgumentException {
        if (hex.length() % 2 != 0) {
            throw new IllegalArgumentException("长度不是偶数");
        }
        char[] arr = hex.toCharArray();
        byte[] b = new byte[hex.length() / 2];
        for (int i = 0, j = 0, l = hex.length(); i < l; i++, j++) {
            String swap = "" + arr[i++] + arr[i];
            int byteint = Integer.parseInt(swap, 16) & 0xFF;
            b[j] = new Integer(byteint).byteValue();
        }
        return b;
    }

    /**
     * key加密
     *
     * @param password
     * @return
     * @throws Exception
     */
    public final static String keyEncrypt(String password) {
        try {
            return byte2hex(encrypt(password.getBytes(),
                    KEY_NUVA_KEY.getBytes()));
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * key解密
     *
     * @param data
     * @return
     * @throws Exception
     */
    public final static String keyDecrypt(String data) {
        try {
            return new String(decrypt(hex2byte(data), KEY_NUVA_KEY.getBytes()));
        } catch (Exception e) {
        }
        return null;
    }
}
