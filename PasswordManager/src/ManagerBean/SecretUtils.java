package ManagerBean;

/**
 *
 * @author Ronald
 */
 
import java.io.UnsupportedEncodingException;
 
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
 

public class SecretUtils {
 
    private static final String Algorithm = "DESede";    
    private static String PASSWORD_CRYPT_KEY = "";
    public SecretUtils(String username, String password){
        PASSWORD_CRYPT_KEY = username + "" + password;
    }
    
    public static byte[] encryptMode(byte[] src) {
        try {
             SecretKey deskey = new SecretKeySpec(build3DesKey(PASSWORD_CRYPT_KEY), Algorithm);    //generate key
             Cipher c1 = Cipher.getInstance(Algorithm);
             c1.init(Cipher.ENCRYPT_MODE, deskey);
             return c1.doFinal(src);
         } catch (java.security.NoSuchAlgorithmException e1) {
             e1.printStackTrace();
         } catch (javax.crypto.NoSuchPaddingException e2) {
             e2.printStackTrace();
         } catch (java.lang.Exception e3) {
             e3.printStackTrace();
         }
         return null;
     }
    
    public static byte[] decryptMode(byte[] src) {      
        try {
            SecretKey deskey = new SecretKeySpec(build3DesKey(PASSWORD_CRYPT_KEY), Algorithm);
            Cipher c1 = Cipher.getInstance(Algorithm);
            c1.init(Cipher.DECRYPT_MODE, deskey);
            return c1.doFinal(src);
        } catch (java.security.NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (javax.crypto.NoSuchPaddingException e2) {
            e2.printStackTrace();
        } catch (java.lang.Exception e3) {
            e3.printStackTrace();
        }
        return null;
     }
    
    public static byte[] build3DesKey(String keyStr) throws UnsupportedEncodingException{
        byte[] key = new byte[24];    //size = 24, default  value =0
        byte[] temp = keyStr.getBytes("UTF-8");    //string to byte[]
        
        if(key.length > temp.length){
            System.arraycopy(temp, 0, key, 0, temp.length);
        }else{
            //if key size > 24, ignore after index 24
            System.arraycopy(temp, 0, key, 0, key.length);
        }
        return key;
    }
}