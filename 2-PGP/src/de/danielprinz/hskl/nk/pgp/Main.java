package de.danielprinz.hskl.nk.pgp;

import de.danielprinz.hskl.nk.api.crypto.CryptoManager;
import de.danielprinz.hskl.nk.api.crypto.LoggerUtil;
import de.danielprinz.hskl.nk.api.crypto.pgp.PGPMessage;
import de.danielprinz.hskl.nk.api.crypto.pgp.PGPMessageHash;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.logging.Level;

public class Main {

    public static void main(String[] args) {

        LoggerUtil.log(Level.FINE, "Authentication");
        authenticate("This is a secret message");

        LoggerUtil.log(Level.FINE, "");
        LoggerUtil.log(Level.FINE, "Confidentiality");
        confidentiality("This is a secret message");

        LoggerUtil.log(Level.FINE, "");
        LoggerUtil.log(Level.FINE, "Full PGP");
        fullPGP("This is a secret message");

    }


    public static void authenticate(String message) {
        try {

            KeyPair keySender = CryptoManager.getFreshKeyPair(1024);
            KeyPair keyMitm = CryptoManager.getFreshKeyPair(1024);

            boolean manInTheMiddleAttack = false;
            PGPMessageHash pgpMessageHash;
            if(!manInTheMiddleAttack)
                pgpMessageHash = new PGPMessageHash(message, keySender.getPrivate());
            else
                pgpMessageHash = new PGPMessageHash(message, keyMitm.getPrivate());

            LoggerUtil.log(Level.FINE, "Encrypted PGP message: " + pgpMessageHash.getString());

            // send pgpMessageHash.getString()

            String md5Decrypted = pgpMessageHash.getMD5Decrypted(keySender.getPublic());
            String md5Expected = CryptoManager.getMD5(pgpMessageHash.getMessage());

            if(md5Decrypted.equals(md5Expected)) {
                LoggerUtil.log(Level.FINE, "PGP message was authenticated");
            } else {
                LoggerUtil.log(Level.FINE, "PGP message was NOT authenticated");
            }

        } catch (NoSuchAlgorithmException ignored) {} catch (BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }



    public static void confidentiality(String message) {
        try {
            String keySymmetric = UUID.randomUUID().toString();
            KeyPair keyReceiver = CryptoManager.getFreshKeyPair(1024);

            PGPMessageHash pgpMessageHash = new PGPMessageHash(message);
            //PGPMessageHash pgpMessageHash = new PGPMessageHash(message, keyMitm.getPrivate());
            PGPMessage pgpMessage = new PGPMessage(pgpMessageHash, keySymmetric, keyReceiver.getPublic());

            LoggerUtil.log(Level.FINE, "Encrypted PGP message: " + pgpMessage.getString());

            // send pgpMessage.getString()

            PGPMessageHash pgpMessageHashDecrypted = PGPMessage.getPGPMessage(pgpMessage.getString(), keyReceiver.getPrivate());
            LoggerUtil.log(Level.FINE, "Decrypted PGP message: " + pgpMessageHashDecrypted.getString());

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
    }


    public static void fullPGP(String message) {
        try {

            KeyPair keySender = CryptoManager.getFreshKeyPair(1024);
            KeyPair keyMitm = CryptoManager.getFreshKeyPair(1024);
            String keySymmetric = UUID.randomUUID().toString();
            KeyPair keyReceiver = CryptoManager.getFreshKeyPair(1024);

            boolean manInTheMiddleAttack = false;
            PGPMessageHash pgpMessageHash;
            if(!manInTheMiddleAttack)
                pgpMessageHash = new PGPMessageHash(message, keySender.getPrivate());
            else
                pgpMessageHash = new PGPMessageHash(message, keyMitm.getPrivate());
            PGPMessage pgpMessage = new PGPMessage(pgpMessageHash, keySymmetric, keyReceiver.getPublic());

            LoggerUtil.log(Level.FINE, "Encrypted PGP message: " + pgpMessage.getString());

            // send pgpMessage.getString()

            PGPMessageHash pgpMessageHashDecrypted = PGPMessage.getPGPMessage(pgpMessage.getString(), keyReceiver.getPrivate());
            LoggerUtil.log(Level.FINE, "Decrypted PGP message: " + pgpMessageHashDecrypted.getMessage());
            String md5Decrypted = pgpMessageHashDecrypted.getMD5Decrypted(keySender.getPublic());
            String md5Expected = CryptoManager.getMD5(pgpMessageHashDecrypted.getMessage());

            if(md5Decrypted.equals(md5Expected)) {
                LoggerUtil.log(Level.FINE, "PGP message was authenticated");
            } else {
                LoggerUtil.log(Level.FINE, "PGP message was NOT authenticated");
            }

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
    }

}
