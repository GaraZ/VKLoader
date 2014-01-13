package com.github.garaz.vkloader;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author GaraZ
 */
public class Crypt {
    private static final String ALGORITHM = "AES"; 
    private static final  String ENCODING = "UTF-8";

    private static SecretKey getKey() throws NoSuchAlgorithmException, 
            UnknownHostException, SocketException {
        InetAddress ip = InetAddress.getLocalHost();
        NetworkInterface network = NetworkInterface.getByInetAddress(ip);
        byte[] key = {1,0,2,0,3,0,4,0,5,0,6,0,7,0,8,0};
	byte[] mac = network.getHardwareAddress();
        int length = mac.length;
        for (int i = 0; i < length; i++) {
            if (i == 16) break;
            key[i] = mac[i];
        }
        SecretKeySpec keySpec = new SecretKeySpec(key,ALGORITHM);
        return keySpec;
    }

    private static byte[] crypter(byte[] bytes, int mode, Key key) 
            throws NoSuchAlgorithmException, NoSuchPaddingException, 
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(mode, key);
        return cipher.doFinal(bytes);
    }

    static String encrypt(String source) throws UnsupportedEncodingException, 
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, 
            IllegalBlockSizeException, BadPaddingException, UnknownHostException, 
            SocketException {
        if(source == null || source.trim().isEmpty()) 
            return null;
        byte[] bytes = source.getBytes(ENCODING);
        bytes = crypter(bytes, Cipher.ENCRYPT_MODE, getKey());
        return Base64.encodeBase64String(bytes);
    }

    static String dencrypt(String source) throws NoSuchAlgorithmException, 
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, 
            BadPaddingException, UnsupportedEncodingException, 
            UnknownHostException, SocketException {
        if(source == null || source.trim().isEmpty()) {
            return null;
        }
        byte[] bytes = Base64.decodeBase64(source);
        bytes = crypter(bytes, Cipher.DECRYPT_MODE, getKey());
        return new String(bytes,ENCODING);
    }
}