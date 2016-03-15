package cc.coocol.jinxiujob.utils;

import android.os.Message;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by coocol on 2016/3/9.
 */
public class StringUtil {

    public static String sha1String(String p) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            byte[] b = messageDigest.digest(p.getBytes());
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < b.length; i++) {
                String shaHex = Integer.toHexString(b[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
