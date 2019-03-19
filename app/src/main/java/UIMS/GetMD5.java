package UIMS;

import java.security.MessageDigest;

public class GetMD5 {

    public static String getMD5Str(String str)  {//很好用的MD5生成方式
        StringBuffer md5StrBuff = new StringBuffer();
        try {
            MessageDigest messageDigest = null;

            messageDigest = MessageDigest.getInstance("MD5");

            messageDigest.reset();

            messageDigest.update(str.getBytes("UTF-8"));

            byte[] byteArray = messageDigest.digest();


            for (int i = 0; i < byteArray.length; i++) {
                if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                    md5StrBuff.append("0").append(
                            Integer.toHexString(0xFF & byteArray[i]));
                else
                    md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return md5StrBuff.toString();
    }

}
