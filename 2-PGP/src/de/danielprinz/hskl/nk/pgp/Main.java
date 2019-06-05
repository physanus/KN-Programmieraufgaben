package de.danielprinz.hskl.nk.pgp;

import de.danielprinz.hskl.nk.api.crypto.KryptoManager;
import de.danielprinz.hskl.nk.api.crypto.LoggerUtil;

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

        //authenticate("This is a secret message");
        confidentiality("This is a secret message");
        //fullPGP("This is a secret message");

    }





    public static void authenticate(String message) {
        try {

            KeyPair keySender = KryptoManager.getFreshKeyPair(1024);
            KeyPair keyMitm = KryptoManager.getFreshKeyPair(1024);

            PGPMessageHash pgpMessageHash = new PGPMessageHash(message, keySender.getPrivate());
            //PGPMessageHash pgpMessageHash = new PGPMessageHash(message, keyMitm.getPrivate());

            LoggerUtil.getInstance().log(Level.INFO, "Encrypted PGP message: " + pgpMessageHash.getString());

            // send pgpMessageHash.getString()

            String md5Decrypted = pgpMessageHash.getMd5Decrypted(keySender.getPublic());
            String md5Expected = KryptoManager.getMD5(pgpMessageHash.getMessage());

            if(md5Decrypted.equals(md5Expected)) {
                LoggerUtil.getInstance().log(Level.INFO, "PGP message was authenticated");
            } else {
                LoggerUtil.getInstance().log(Level.INFO, "PGP message was NOT authenticated");
            }

        } catch (NoSuchAlgorithmException ignored) {} catch (BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }



    public static void confidentiality(String message) {
        try {

            KeyPair keySender = KryptoManager.getFreshKeyPair(1024);
            KeyPair keyMitm = KryptoManager.getFreshKeyPair(1024);
            String keySymmetric = UUID.randomUUID().toString();
            KeyPair keyReceiver = KryptoManager.getFreshKeyPair(1024);

            PGPMessageHash pgpMessageHash = new PGPMessageHash(message);
            //PGPMessageHash pgpMessageHash = new PGPMessageHash(message, keyMitm.getPrivate());
            PGPMessage pgpMessage = new PGPMessage(pgpMessageHash, keySymmetric, keyReceiver.getPublic());

            LoggerUtil.getInstance().log(Level.INFO, "Encrypted PGP message: " + pgpMessage.getString());

            // send pgpMessage.getString()

            PGPMessageHash pgpMessageHashDecrypted = PGPMessage.getPGPMessage(pgpMessage.getString(), keyReceiver.getPrivate());
            System.out.println(pgpMessageHashDecrypted.getMessage());
            LoggerUtil.getInstance().log(Level.INFO, "Decrypted PGP message: " + pgpMessage.getString());

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
    }


    public static void fullPGP(String message) {
        try {

            KeyPair keySender = KryptoManager.getFreshKeyPair(1024);
            KeyPair keyMitm = KryptoManager.getFreshKeyPair(1024);
            String keySymmetric = UUID.randomUUID().toString();
            KeyPair keyReceiver = KryptoManager.getFreshKeyPair(1024);

            PGPMessageHash pgpMessageHash = new PGPMessageHash(message, keySender.getPrivate());
            //PGPMessageHash pgpMessageHash = new PGPMessageHash(message, keyMitm.getPrivate());
            PGPMessage pgpMessage = new PGPMessage(pgpMessageHash, keySymmetric, keyReceiver.getPublic());

            LoggerUtil.getInstance().log(Level.INFO, "Encrypted PGP message: " + pgpMessage.getString());

            // send pgpMessage.getString()

            PGPMessageHash pgpMessageHashDecrypted = PGPMessage.getPGPMessage(pgpMessage.getString(), keyReceiver.getPrivate());
            String md5Decrypted = pgpMessageHashDecrypted.getMd5Decrypted(keySender.getPublic());
            String md5Expected = KryptoManager.getMD5(pgpMessageHashDecrypted.getMessage());

            if(md5Decrypted.equals(md5Expected)) {
                LoggerUtil.getInstance().log(Level.INFO, "PGP message was authenticated");
            } else {
                LoggerUtil.getInstance().log(Level.INFO, "PGP message was NOT authenticated");
            }

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
    }

}
