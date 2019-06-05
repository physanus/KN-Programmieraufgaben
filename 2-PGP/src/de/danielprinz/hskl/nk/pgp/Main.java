package de.danielprinz.hskl.nk.pgp;

import de.danielprinz.hskl.nk.api.crypto.KryptoManager;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class Main {

    public static void main(String[] args) {

//        try {

            // Signieren

//            KeyPair keyPair = KryptoManager.getFreshKeyPair(2048);
//
//            KeyGenerator.getInstance("").gener


//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }

        //authenticate("This is a secret message");
        //confidentiality("This is a secret message");
        fullPGP("This is a secret message");

    }





    /*public static void authenticate(String message) {
        try {

            KeyPair keySender = KryptoManager.getFreshKeyPair(1024);
            KeyPair keyMitm = KryptoManager.getFreshKeyPair(1024);

            String md5Encrypted = KryptoManager.encryptRSA(keySender.getPrivate(), KryptoManager.getMD5(message));
            //String md5Encrypted = KryptoManager.encryptRSA(keyMitm.getPrivate(), KryptoManager.getMD5(message));

            String pgpMessageSent = new PGPMessage(message, md5Encrypted).getString();

            // send

            PGPMessage pgpMessageReceived = PGPMessage.getPGPMessage(pgpMessageSent);
            System.out.println(pgpMessageReceived.getString());
            System.out.println("isAuthentication:  " + pgpMessageReceived.isAuthentication());
            System.out.println("isConfidentiality: " + pgpMessageReceived.isConfidentiality());

            String md5Decrypted = KryptoManager.decryptRSA(keySender.getPublic(), pgpMessageReceived.getMd5Encrypted(), 32);
            String md5Expected = KryptoManager.getMD5(pgpMessageReceived.getMessage());

            if(md5Decrypted.equals(md5Expected)) {
                System.out.println("Authenticated");
            } else {
                System.out.println("NOT authenticated");
            }

        } catch (NoSuchAlgorithmException ignored) {} catch (BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }



    public static void confidentiality(String message) {
        try {

            String keySymmetric = UUID.randomUUID().toString();
            KeyPair keyReceiver = KryptoManager.getFreshKeyPair(1024);

            String messageEncryptedAES = KryptoManager.encryptAES(KryptoManager.getAESKey(keySymmetric), message);
            String keySymmetricEncrypted = KryptoManager.encryptRSA(keyReceiver.getPublic(), keySymmetric);
            System.out.println("messageEncryptedAES: " + messageEncryptedAES);
            System.out.println("keySymmetricEncrypted: " + keySymmetricEncrypted);

            String pgpMessageSent = new PGPMessage(messageEncryptedAES, null, keySymmetricEncrypted).getString();

            // send

            PGPMessage pgpMessageReceived = PGPMessage.getPGPMessage(pgpMessageSent);
            System.out.println("isAuthentication:  " + pgpMessageReceived.isAuthentication());
            System.out.println("isConfidentiality: " + pgpMessageReceived.isConfidentiality());

            String keySymmetricDecrypted = KryptoManager.decryptRSA(keyReceiver.getPrivate(), pgpMessageReceived.getKeySymmetricEncrypted());
            String messageDecryptedAES = KryptoManager.decryptAES(KryptoManager.getAESKey(keySymmetric), pgpMessageReceived.getMessage());
            System.out.println("keySymmetricDecrypted: " + keySymmetricDecrypted);
            System.out.println("messageDecryptedAES: " + messageDecryptedAES);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
    }*/


    public static void fullPGP(String message) {
        try {

            KeyPair keySender = KryptoManager.getFreshKeyPair(1024);
            KeyPair keyMitm = KryptoManager.getFreshKeyPair(1024);
            String keySymmetric = UUID.randomUUID().toString();
            KeyPair keyReceiver = KryptoManager.getFreshKeyPair(1024);

//            String md5Encrypted = KryptoManager.encryptRSA(keySender.getPrivate(), KryptoManager.getMD5(message));
            //String md5Encrypted = KryptoManager.encryptRSA(keyMitm.getPrivate(), KryptoManager.getMD5(message));

            PGPMessageHash pgpMessageHash = new PGPMessageHash(message, keySender.getPrivate());
            //PGPMessageHash pgpMessageHash = new PGPMessageHash(message, keyMitm.getPrivate());
            PGPMessage pgpMessage = new PGPMessage(pgpMessageHash, keySymmetric, keyReceiver.getPublic());

            System.out.println("pgpMessage.getString(): " + pgpMessage.getString());

//            System.out.println("");


//            String messageEncryptedAES = KryptoManager.encryptAES(KryptoManager.getAESKey(keySymmetric), message + PGPMessage.SPLIT_STRING + md5Encrypted);
//            String keySymmetricEncrypted = KryptoManager.encryptRSA(keyReceiver.getPublic(), keySymmetric);



//            System.out.println("message: " + message);
//            System.out.println("md5: " + KryptoManager.getMD5(message));
//            System.out.println("md5Encrypted: " + md5Encrypted);
//            System.out.println("keySymmetric: " + keySymmetric);
//            System.out.println("keySymmetric: " + keySymmetric.length());

//            System.out.println("MESSAGE ENCRYPTED: " + messageEncryptedAES + PGPMessage.SPLIT_STRING + keySymmetricEncrypted);
            System.out.println("");
            System.out.println("===== SEND =====");
            System.out.println("");

            // send pgpMessage.getString()

            PGPMessageHash pgpMessageHashDecrypted = PGPMessage.getPGPMessage(pgpMessage.getString(), keyReceiver.getPrivate());

//            String keySymmetricDecrypted = KryptoManager.decryptRSA(keyReceiver.getPrivate(), keySymmetricEncrypted, 36);
//            System.out.println("keySymmetricDecrypted: " + keySymmetricDecrypted);
//            String messageDecryptedAES = KryptoManager.decryptAES(KryptoManager.getAESKey(keySymmetricDecrypted), messageEncryptedAES);
//            System.out.println("messageDecryptedAES: " + messageDecryptedAES);



//            String messageDecrypted = messageDecryptedAES.split(PGPMessage.SPLIT_STRING)[0];
//            String md5Decrypted = KryptoManager.decryptRSA(keySender.getPublic(), messageDecryptedAES.split(PGPMessage.SPLIT_STRING)[1], 32);
            String md5Decrypted = pgpMessageHashDecrypted.getMd5Decrypted(keySender.getPublic());

//            System.out.println("messageDecrypted: " + messageDecrypted);
            System.out.println("md5Decrypted: " + md5Decrypted);

            String md5Expected = KryptoManager.getMD5(pgpMessageHashDecrypted.getMessage());

            if(md5Decrypted.equals(md5Expected)) {
                System.out.println("Authenticated");
            } else {
                System.out.println("NOT authenticated");
            }

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
    }

}
