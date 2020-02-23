package com.xiaoi.wxgzh.utils;

import java.security.MessageDigest;
import java.util.Arrays;

/**
 * sha1加密
 *
 * @author kevin.zhu
 * @date 2020/2/15 21:27
 */
public class EncryptionUtil {

    /**
     * sha1加密
     *
     * @param [str]
     * @return java.lang.String
     * @author kevin.zhu
     * @date 2020/2/18 15:28
     */
    public static String getSha1(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
            mdTemp.update(str.getBytes("UTF-8"));

            byte[] md = mdTemp.digest();
            int j = md.length;
            char buf[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
                buf[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(buf);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 校验微信签名
     *
     * @param [param, signature]
     * @return boolean
     * @author kevin.zhu
     * @date 2020/2/18 15:28
     */
    public static boolean checkSign(String[] param, String signature) {
        Arrays.sort(param);
        if (signature.equals(EncryptionUtil.getSha1(param[0] + param[1] + param[2]))) {
            return true;
        }
        return false;
    }
}
