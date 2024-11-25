import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AESMaganement {
    public static byte[] hashKey(String key) throws Exception{
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] hashedKey = sha.digest(key.getBytes("UTF-8"));
        return hashedKey;
    }

    public static String encryptMessage(byte[] hashedKey, String message)throws Exception{
        // Uses the hashed key for AES
        java.util.Base64.Encoder encoder = java.util.Base64.getEncoder();
        SecretKey aesKey = new SecretKeySpec(hashedKey, 0, 32, "AES");

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);
        byte[] encryptedMessage = cipher.doFinal(message.getBytes());
        System.out.println("BYTES :" +encryptedMessage);
        System.out.println("SENDED BASE64: "+encoder.encodeToString(encryptedMessage));
        return encoder.encodeToString(encryptedMessage);
    }

    public static String decryptMessage(byte[] hashedKey, String encryptedMessage) throws Exception{
        // Uses the hashed key to decrypt message
        java.util.Base64.Decoder decoder = java.util.Base64.getDecoder();
        System.out.println("RECEIVED base64 MESSAGE: "+encryptedMessage);
        SecretKeySpec skey = new SecretKeySpec(hashedKey, 0, 32, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, skey);
        byte[] output = cipher.doFinal(decoder.decode(encryptedMessage));
        return new String(output);
    }

    public static String encrypt(String input, String key) {
    byte[] crypted = null;
    try {
    
        SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
        
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, skey);
        crypted = cipher.doFinal(input.getBytes());
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        java.util.Base64.Encoder encoder = java.util.Base64.getEncoder();
        
        return new String(encoder.encodeToString(crypted));
	}

	public static String decrypt(String input, String key) {
		byte[] output = null;
		try {
			java.util.Base64.Decoder decoder = java.util.Base64.getDecoder();
			SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, skey);
			output = cipher.doFinal(decoder.decode(input));
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return new String(output);
	}


    
}
