package de.danielprinz.hskl.nk.api.crypto.pgp;

import de.danielprinz.hskl.nk.api.crypto.KryptoManager;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class PGPMessage {

    static final String SPLIT_STRING = ";.,;";

    private String pgpMessageHashString;
    private String keySymmetricEncrypted;

    /*public PGPMessage(String message, String md5Encrypted, String keySymmetricEncrypted) {
        if(message != null && message.equals("null")) message = null;
        if(md5Encrypted != null && md5Encrypted.equals("null")) md5Encrypted = null;
        if(keySymmetricEncrypted != null && keySymmetricEncrypted.equals("null")) keySymmetricEncrypted = null;

        this.pgpMessageHash = new PGPMessageHash(message, md5Encrypted);
        this.keySymmetricEncrypted = keySymmetricEncrypted;
    }

    public PGPMessage(String message, String md5Encrypted) {
        if(message != null && message.equals("null")) message = null;
        if(md5Encrypted != null && md5Encrypted.equals("null")) md5Encrypted = null;

        this.pgpMessageHash = new PGPMessageHash(message, md5Encrypted);
    }*/

    public PGPMessage(PGPMessageHash pgpMessageHash, String keySymmetric, PublicKey publicKey) throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        this.pgpMessageHashString = KryptoManager.encryptAES(KryptoManager.getAESKey(keySymmetric), pgpMessageHash.getString());
        this.keySymmetricEncrypted = KryptoManager.encryptRSA(publicKey, keySymmetric);
    }

    public static PGPMessageHash getPGPMessage(String pgpMessage, PrivateKey privateKey) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        String[] pgpMessageSplit = pgpMessage.split(SPLIT_STRING);
        String keySymmetricDecrypted = KryptoManager.decryptRSA(privateKey, pgpMessageSplit[1], 36);
        String pgpMessageHashString = KryptoManager.decryptAES(KryptoManager.getAESKey(keySymmetricDecrypted), pgpMessageSplit[0]);

        String[] pgpMessageHashStringSplit = pgpMessageHashString.split(SPLIT_STRING);
        if(pgpMessageHashStringSplit.length == 1) {
            return new PGPMessageHash(pgpMessageHashStringSplit[0]);
        } else {
            return new PGPMessageHash(pgpMessageHashStringSplit[0], pgpMessageHashStringSplit[1]);
        }
    }


    public String getString() {
        return pgpMessageHashString + SPLIT_STRING + keySymmetricEncrypted;
    }

    /*public boolean isAuthentication() {
        //return md5Encrypted != null && !md5Encrypted.isEmpty();
        return false;
    }

    public boolean isConfidentiality() {
        return keySymmetricEncrypted != null && !keySymmetricEncrypted.isEmpty();
    }*/

    /*public static PGPMessage getPGPMessage(String pgpMessage) {
        String[] pgpMessageSplit = pgpMessage.split(SPLIT_STRING);
        return new PGPMessage(pgpMessageSplit[0], pgpMessageSplit[1], pgpMessageSplit[2]);
    }*/


    @Override
    public String toString() {
        return "PGPMessage{" +
                //"pgpMessageHash='" + pgpMessageHash.toString() + '\'' +
                ", keySymmetricEncrypted='" + keySymmetricEncrypted + '\'' +
                ", getString()='" + getString() + '\'' +
                '}';
    }
}
