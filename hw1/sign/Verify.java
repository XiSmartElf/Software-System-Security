import java.nio.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.io.*;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;

public class Verify {

  public static void main(String[] args) {
    String signaturePath = getAbsoluteFilePath("sig.bin");
    String publicKeyPath = getAbsoluteFilePath("rsa_ver.bin");
    String filePath = getAbsoluteFilePath("hw1.zip");
    byte[] signatureData = convertFileDataToByteArray(signaturePath);
    byte[] publicKeyByteArray = convertFileDataToByteArray(publicKeyPath);
    byte[] hw1Data = convertFileDataToByteArray(filePath);

    // http://docs.oracle.com/javase/tutorial/security/apisign/vstep2.html
    // http://docs.oracle.com/javase/tutorial/security/apisign/vstep4.html
    try {
      // http://stackoverflow.com/questions/19353748/how-to-convert-byte-array-to-privatekey-or-publickey-type
      KeyFactory kf = KeyFactory.getInstance("RSA");
      PublicKey publicKey = kf.generatePublic(new X509EncodedKeySpec(publicKeyByteArray));
      Signature verifyHW1 = Signature.getInstance("SHA256withRSA");
      verifyHW1.initVerify(publicKey);
      verifyHW1.update(hw1Data);
      boolean verified = verifyHW1.verify(signatureData);
      System.out.println("Signature Verified: " + verified);
    } catch (SignatureException s) {
      System.out.println("Signature Verified: false");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static String getAbsoluteFilePath(String fileName) {
    String filePath = new File("").getAbsolutePath() + "/" + fileName;
    return filePath;
  }

  private static byte[] convertFileDataToByteArray(String filepath) {
    Path path = Paths.get(filepath);
    byte[] data = null;
    try {
      data = Files.readAllBytes(path);
    } catch(Exception e) {
      e.printStackTrace();
    }
    return data;
  }

}


