import java.nio.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.io.*;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;

public class GenPubPrivKey {

  public static void main(String[] args) {
    String privateKeyPath = getAbsoluteFilePath("rsa_sign.bin");
    String publicKeyPath = getAbsoluteFilePath("rsa_ver.bin");

    // reference: http://docs.oracle.com/javase/tutorial/security/apisign/step2.html
    try {
      KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
      SecureRandom random = new SecureRandom();
      keyGen.initialize(3072, random);
      KeyPair pair = keyGen.generateKeyPair();
      PrivateKey privateKey = pair.getPrivate();
      PublicKey publicKey = pair.getPublic();
      byte[] privateKeyByteArray = privateKey.getEncoded();
      byte[] publicKeyByteArray = publicKey.getEncoded();
      FileOutputStream fosPrivate = new FileOutputStream(privateKeyPath);
      FileOutputStream fosPublic = new FileOutputStream(publicKeyPath);
      fosPrivate.write(privateKeyByteArray);
      fosPublic.write(publicKeyByteArray);
      fosPrivate.close();
      fosPublic.close();
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  private static String getAbsoluteFilePath(String fileName) {
    String filePath = new File("").getAbsolutePath() + "/" + fileName;
    return filePath;
  }

}
