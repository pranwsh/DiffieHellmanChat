package dh;

import javax.crypto.KeyAgreement;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;


public class DiffieHellman {

    public static final int KEY_SIZE = 2048; 
    

    public static KeyPair generateDHKeyPair() throws NoSuchAlgorithmException{
        
        KeyPairGenerator keysGen = KeyPairGenerator.getInstance("DH");
        keysGen.initialize(KEY_SIZE);
        return keysGen.genKeyPair();
    }

    public static byte[] computeSharedSecret(PrivateKey privKey, PublicKey received_pubKey)throws NoSuchAlgorithmException, InvalidKeyException {
        KeyAgreement keyagr = KeyAgreement.getInstance("DH");
        keyagr.init(privKey);
        keyagr.doPhase(received_pubKey, true); // true because this is the last (and first, in this case) phase of the agreement
        return keyagr.generateSecret();
    }

    public static byte[] getEncodedPubKey(PublicKey pubKey) {
        if (pubKey == null) {
            return null;
        }
        return pubKey.getEncoded();
    }

    public static PublicKey decodePublicKey(byte[] encodedKey) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("DH");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encodedKey);
        return keyFactory.generatePublic(keySpec);
    }
}
