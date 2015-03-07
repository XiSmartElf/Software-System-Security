import java.nio.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.io.*;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;

public class BruteForce {

  public static void main(String[] args) {
    long start = System.nanoTime();

    String path = getAbsoluteFilePath("hw1des.bin");
    byte[] cipherText = convertFileDataToByteArray(path);
    String iHaveADreamPath = getAbsoluteFilePath("plaintext.txt");
    String completeKeyPath = getAbsoluteFilePath("completekey.txt");
    byte[] iv = new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 };
    byte[] actualKey = null;
    actualKey = crackKey(cipherText, iv);
    byte[] plainText = null;
    if(actualKey != null) {
      try {
        SecretKeySpec DESKey = new SecretKeySpec(actualKey, "DES");
        IvParameterSpec initialVector = new IvParameterSpec(iv);
        Cipher c = Cipher.getInstance("DES/CBC/PKCS5Padding");
        c.init(Cipher.DECRYPT_MODE, DESKey, initialVector);
        plainText = c.doFinal(cipherText);
        
        FileOutputStream fosText =  new FileOutputStream(iHaveADreamPath);
        FileOutputStream fosKey = new FileOutputStream(completeKeyPath);
        fosText.write(plainText);
        StringBuffer keyString = new StringBuffer();
        for(int i = 0; i < 8; i++) {
          byte actualKeyi = actualKey[i];
          for(int j = 0; j < 8; j++)
            if((actualKeyi & (1 << j)) > 0)
              keyString.insert(8*i,"1");
            else
              keyString.insert(8*i,"0");
        }
        fosKey.write(keyString.toString().getBytes());
        fosText.close();
        fosKey.close();
        
      } catch( Exception e) {
        e.printStackTrace();
      }
    } else {
      try {
        FileOutputStream fosText =  new FileOutputStream(iHaveADreamPath);
        FileOutputStream fosKey = new FileOutputStream(completeKeyPath);
        fosText.write("cannot find plaintext".getBytes());
        fosKey.write("cannot find key".getBytes());
        fosText.close();
        fosKey.close();
      } catch( Exception e) {
        e.printStackTrace();
      }
    }

    long end = System.nanoTime();
    System.out.println("time to crack the key is: " + (end - start) + " nano second");
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

  private static boolean tryPotentialKey(byte[] cipherText, byte[] key, byte[] iv) {

    byte[] plainText = null;
    try {
      SecretKeySpec DESKey = new SecretKeySpec(key, "DES");
      IvParameterSpec initialVector = new IvParameterSpec(iv);
      Cipher c = Cipher.getInstance("DES/CBC/PKCS5Padding");
      c.init(Cipher.DECRYPT_MODE, DESKey, initialVector);
      plainText = c.doFinal(cipherText);
    } catch( Exception e) {
      return false;
      //e.printStackTrace();
    }
    // http://docs.oracle.com/javase/6/docs/api/java/nio/charset/Charset.html
    Charset charset = Charset.forName("US-ASCII");
    CharsetEncoder ASCIIEncoder = charset.newEncoder();
    String s = new String(plainText);
    boolean b = ASCIIEncoder.canEncode(s);
    return b;
  }
  private static byte[] crackKey(byte[] cipherText, byte[] iv) {
    String keyString = "011010010110111001110011011101000110";
    String[] ThreeBit = new String[8];
    String[] SevenBit = new String[128];
    for(int i = 0; i < 8; i++) {
      String temp = Integer.toString(i, 2);
      if(temp.length() == 1)
        temp = "00" + temp;
      else if(temp.length() == 2)
        temp = "0" + temp;
      ThreeBit[i] = temp;
    }
    for(int i = 0; i < 128; i++) {
      String temp = Integer.toString(i, 2);
      if(temp.length() == 1)
        temp = "000000" + temp;
      else if(temp.length() == 2)
        temp = "00000" + temp;
      else if(temp.length() == 3)
        temp = "0000" + temp;
      else if(temp.length() == 4)
        temp = "000" + temp;
      else if(temp.length() == 5)
        temp = "00" + temp;
      else if(temp.length() == 6)
        temp = "0" + temp;
      SevenBit[i] = temp;
    }

    for(int i = 0; i < 8; i++) {
      // System.out.println(ThreeBit[i]);
      assert(i == Integer.parseInt(ThreeBit[i], 2));
    }
    for(int i = 0; i < 128; i++) {
      // System.out.println(SevenBit[i]);
      assert(i != Integer.parseInt(SevenBit[i], 2));
    }
    int count = 0;
    for(int i = 0; i < 8; i++) {
      String a = ThreeBit[i];
      for(int j = 0; j < 128; j++) {
        String b = SevenBit[j];
        for(int k = 0; k < 128; k++) {
          String c = SevenBit[k];
          for(int l = 0; l < 128; l++) {
            String d = SevenBit[l];
            String all = keyString + a + "1" + b + "1" + c + "1" + d + "1";
            assert(all.length() == 64);
            byte[] potentialKey = new byte[8];
            int cur = 0;
            for(int m = 0; m < 64; m += 8)
              potentialKey[cur++] = (byte) Integer.parseInt(all.substring(m, m + 8), 2);
            if(tryPotentialKey(cipherText, potentialKey, iv))
              return potentialKey;
          }
        }
      }
    }
    return null;
  }
}
