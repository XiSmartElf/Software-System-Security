import java.nio.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.io.*;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;

public class Sign {

  public static void main(String[] args) {
    String signaturePath = getAbsoluteFilePath("sig.bin");
    String privateKeyPath = getAbsoluteFilePath("rsa_sign.bin");
    String filePath = getAbsoluteFilePath("hw1.zip");
    byte[] privateKeyData = convertFileDataToByteArray(privateKeyPath);
    byte[] hw1Data = convertFileDataToByteArray(filePath);

    // reference: http://docs.oracle.com/javase/tutorial/security/apisign/step3.html
    try {
      // http://stackoverflow.com/questions/19353748/how-to-convert-byte-array-to-privatekey-or-publickey-type
      KeyFactory kf = KeyFactory.getInstance("RSA");
      PrivateKey privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(privateKeyData));
      Signature signHW1 = Signature.getInstance("SHA256withRSA");
      signHW1.initSign(privateKey);
      signHW1.update(hw1Data);
      byte[] signature = signHW1.sign();
      FileOutputStream fos = new FileOutputStream(signaturePath);
      fos.write(signature);
      fos.close();
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
