package com.sirc.tlt.utils;

import android.text.TextUtils;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

public class Encrypt {

    public final static String md5(String s) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
                'E', 'F'};
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte[] bytes = digest.digest();
            int len = bytes.length;
            char chars[] = new char[len * 2];
            int index = 0;
            for (int i = 0; i < len; i++) {
                byte b = bytes[i];
                chars[index++] = hexDigits[b >>> 4 & 0xf];
                chars[index++] = hexDigits[b & 0xf];
            }
            return new String(chars);
        } catch (Exception e) {
        }
        return null;
    }

    public static String encryptBASE64(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            return new String(Base64.encode(str.getBytes("UTF-8"), Base64.DEFAULT), "UTF-8");
        } catch (UnsupportedEncodingException e) {
        }
        return null;
    }

    public static String decryptBASE64(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            return new String(Base64.decode(str.getBytes("UTF-8"), Base64.DEFAULT), "UTF-8");
        } catch (UnsupportedEncodingException e) {
        }
        return null;
    }

}
