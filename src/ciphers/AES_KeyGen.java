package ciphers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AES_KeyGen {

    public static SecretKey deriveAESKey(byte[] sharedSecret) throws NoSuchAlgorithmException {
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = sha256.digest(sharedSecret); // output = 256 bit
        return new SecretKeySpec(keyBytes, 0, 16, "AES"); // taglia 128 bit
    }
}
