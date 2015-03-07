import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.io.*;
import java.nio.file.*;

public class HybridDecrypt {

  private static String getAbsoluteFilePath(String fileName) {
    String filePath = new File("").getAbsolutePath() + "/" + fileName;
    return filePath;
  }

  // reference: http://stackoverflow.com/questions/858980/file-to-byte-in-java
  private static byte[] convertFileDataToByteArray(String filePath) {
    Path path = Paths.get(filePath);
    byte[] data = null;
    try {
      data = Files.readAllBytes(path);
    } catch(Exception e) {
      e.printStackTrace();
    }
    return data;
  }

  private static byte[] getSessionKey(String privkeyPath, String sesskeyPath) {
    byte[] sessionKey = null;
    byte[] privkeyFileData;
    byte[] sesskeyFileData;
    privkeyFileData = convertFileDataToByteArray(privkeyPath);
    sesskeyFileData = convertFileDataToByteArray(sesskeyPath);

    try {
      // http://stackoverflow.com/questions/19353748/how-to-convert-byte-array-to-privatekey-or-publickey-type
      KeyFactory kf = KeyFactory.getInstance("RSA");
      // pubkey: The RSA public key that was used to encrypt the session key. The file contains the raw bytes of an X.509 encoding of the key.
      // privkey: The 3072-bit RSA private key corresponding to pubkey. The file contains the raw bytes of a PKCS8 encoding of the key.
      PrivateKey privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(privkeyFileData));

      // sesskey: The 128-bit AES session key encrypted with RSA and OAEPwithSHA-256andMGF1 padding. The file contains the raw bytes of the key.
      Cipher c = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
      c.init(Cipher.DECRYPT_MODE, privateKey);
      sessionKey = c.doFinal(sesskeyFileData);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return sessionKey;
  }
  private static void getPlainText(String privkeyPath, String sesskeyPath, String ivPath, String ciphertextPath, String plaintextPath) {
    byte[] sessionKey = getSessionKey(privkeyPath, sesskeyPath);
    SecretKeySpec AESKey = new SecretKeySpec(sessionKey, "AES");
    byte[] iv = convertFileDataToByteArray(ivPath);
    byte[] cipherText = convertFileDataToByteArray(ciphertextPath);
    // The plaintext encrypted with the AES session key in CBC mode with PKCS5 padding.
    byte[] plainText = null;
    try {
      IvParameterSpec initialVector = new IvParameterSpec(iv);
      Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
      c.init(Cipher.DECRYPT_MODE, AESKey, initialVector);
      plainText = c.doFinal(cipherText);
      FileOutputStream fos = new FileOutputStream(plaintextPath);
      fos.write(plainText);
      fos.close();
    } catch( Exception e) {
      e.printStackTrace();
    }
  }
  public static void main(String[] args) {
    String pubkeyPath = getAbsoluteFilePath(args[0]);
    String privkeyPath = getAbsoluteFilePath(args[1]);
    String sesskeyPath = getAbsoluteFilePath(args[2]);
    String ivPath = getAbsoluteFilePath(args[3]);
    String ciphertextPath = getAbsoluteFilePath(args[4]);
    String plaintextPath = getAbsoluteFilePath("hw1.II.html");
    /*
    System.out.println(pubkeyPath);
    System.out.println(privkeyPath);
    System.out.println(sesskeyPath);
    System.out.println(ivPath);
    System.out.println(ciphertextPath);
    System.out.println(plaintextPath);
    */
    getPlainText(privkeyPath, sesskeyPath, ivPath, ciphertextPath, plaintextPath);
  }
}

