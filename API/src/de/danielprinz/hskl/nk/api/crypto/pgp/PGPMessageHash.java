package de.danielprinz.hskl.nk.api.crypto.pgp;

import de.danielprinz.hskl.nk.api.crypto.KryptoManager;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Stores the PGP message and its hash
 */
public class PGPMessageHash {

    private String message;
    private String md5Encrypted;

    public PGPMessageHash(String message, String md5Encrypted) {
        this.message = message;
        this.md5Encrypted = md5Encrypted;
    }

    public PGPMessageHash(String message, PrivateKey privateKey) throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        this.message = message;
        this.md5Encrypted = KryptoManager.encryptRSA(privateKey, KryptoManager.getMD5(message));
    }

    public PGPMessageHash(String message) {
        this.message = message;
    }

    public String getMd5Decrypted(PublicKey publicKey) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        return KryptoManager.decryptRSA(publicKey, md5Encrypted, 32);
    }

    public String getMessage() {
        return message;
    }

    public String getMd5Encrypted() {
        return md5Encrypted;
    }

    public boolean isAuthentication() {
        return md5Encrypted != null && !md5Encrypted.isEmpty();
    }


    public String getString() {
        return this.md5Encrypted == null ? this.message : this.message + PGPMessage.SPLIT_STRING + this.md5Encrypted;
    }


    @Override
    public String toString() {
        return "PGPMessageHash{" +
                "message='" + message + '\'' +
                ", md5Encrypted='" + md5Encrypted + '\'' +
                ", getString()='" + getString() + '\'' +
                '}';
    }
}
