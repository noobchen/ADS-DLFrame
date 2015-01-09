package com.x.dtswz;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-6-27
 * Time: 下午4:44
 * To change this template use File | Settings | File Templates.
 */
public class EncryptUtil {

    private static final int CACHE_SIZE = 1024;

    private static byte[] bytes1 = {97, 97, 115, 100, 97, 113, 114, 49, 50, 51, 53, 49, 51, 52};         //aasdaqr1235134
    private static byte[] bytes2 = {65, 69, 83};                                                          //AES
    private static byte[] bytes3 = {83, 72, 65, 49, 80, 82, 78, 71};                                      //SHA1PRNG
    private static byte[] bytes4 = {67, 114, 121, 112, 116, 111};                                         //Crypto
    private static byte[] bytes5 = {65, 69, 83, 47, 69, 67, 66, 47, 80, 75, 67, 83, 53, 80, 97, 100, 100, 105, 110, 103};       //AES/ECB/PKCS5Padding

    private static String a = new String(bytes1);
    private static String b = new String(bytes2);
    private static String c = new String(bytes3);
    private static String d = new String(bytes4);
    private static String e = new String(bytes5);


    public static void copy(InputStream inputStream, String strOutFileName) throws IOException {
        File destFile = new File(strOutFileName);

        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }
        destFile.createNewFile();
        OutputStream myOutput = new FileOutputStream(destFile);
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(buffer)) != -1) {
            myOutput.write(buffer, 0, length);
            myOutput.flush();
        }


        inputStream.close();
        myOutput.close();
    }


    // 加密解密文件//
    public static boolean enOrDecryptFile(byte[] paramArrayOfByte,
                                          String sourceFilePath, String destFilePath, int mode) {
        File sourceFile = new File(sourceFilePath);
        File destFile = new File(destFilePath);
        CipherOutputStream cout = null;
        FileInputStream in = null;
        FileOutputStream out = null;
        if (sourceFile.exists() && sourceFile.isFile()) {
            if (!destFile.getParentFile().exists()) {
                destFile.getParentFile().mkdirs();
            }
            try {
                destFile.createNewFile();
                in = new FileInputStream(sourceFile);
                out = new FileOutputStream(destFile);
                // 获取密钥//
                KeyGenerator kgen = KeyGenerator.getInstance(b);
//                kgen.init(128, new SecureRandom("aasdaqr1235134".getBytes()));
                SecureRandom secureRandom = SecureRandom.getInstance(c, d);
                secureRandom.setSeed(a.getBytes());
                kgen.init(128, secureRandom);

                SecretKey secretKey = kgen.generateKey();
                byte[] enCodeFormat = secretKey.getEncoded();
                SecretKeySpec key = new SecretKeySpec(enCodeFormat, b);


//                Cipher cipher = Cipher.getInstance("AES");// 创建密码器 4.0 4.2 测试通过
                Cipher cipher = Cipher.getInstance(e);         //4.0 4.2 4.4 测试通过
                SecretKeySpec secretKeySpec = new SecretKeySpec(key.getEncoded(), b);

                cipher.init(mode, secretKeySpec);
                cout = new CipherOutputStream(out, cipher);
                byte[] cache = new byte[CACHE_SIZE];
                int nRead = 0;
                while ((nRead = in.read(cache)) != -1) {
                    cout.write(cache, 0, nRead);
                    cout.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return false;
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
                return false;
            } catch (InvalidKeyException e) {
                e.printStackTrace();
                return false;
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            } finally {
                if (cout != null) {
                    try {
                        cout.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return true;
        }
        return false;
    }


}
