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

        authenticate("This is a secret message");
        //confidentiality("This is a secret message");

    }





    public static void authenticate(String message) {
        try {

            KeyPair sender = KryptoManager.getFreshKeyPair(1024);
            KeyPair mitm = KryptoManager.getFreshKeyPair(1024);

            String md5Encrypted = KryptoManager.encryptRSA(sender.getPrivate(), KryptoManager.getMD5(message));
            //String md5Encrypted = KryptoManager.encryptRSA(mitm.getPrivate(), KryptoManager.getMD5(message));

            String pgpMessageSent = new PGPMessage(message, md5Encrypted, null).getString();

            // send

            PGPMessage pgpMessageReceived = PGPMessage.getPGPMessage(pgpMessageSent);
            System.out.println(pgpMessageReceived.getString());
            System.out.println("isAuthentication:  " + pgpMessageReceived.isAuthentication());
            System.out.println("isConfidentiality: " + pgpMessageReceived.isConfidentiality());

            String md5Decrypted = KryptoManager.decryptRSA(sender.getPublic(), pgpMessageReceived.getMd5Encrypted(), 32);
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

            String encryptAES = KryptoManager.encryptAES(KryptoManager.getAESKey(keySymmetric), message);
            String keySymmetricEncrypted = KryptoManager.encryptRSA(keyReceiver.getPublic(), keySymmetric);
            System.out.println("encryptAES: " + encryptAES);
            System.out.println("keySymmetricEncrypted: " + keySymmetricEncrypted);

            String pgpMessageSent = new PGPMessage(encryptAES, null, keySymmetricEncrypted).getString();

            // send

            PGPMessage pgpMessageReceived = PGPMessage.getPGPMessage(pgpMessageSent);
            System.out.println("isAuthentication:  " + pgpMessageReceived.isAuthentication());
            System.out.println("isConfidentiality: " + pgpMessageReceived.isConfidentiality());

            String keySymmetricDecrypted = KryptoManager.decryptRSA(keyReceiver.getPrivate(), pgpMessageReceived.getKeyEncrypted());
            String decryptAES = KryptoManager.decryptAES(KryptoManager.getAESKey(keySymmetric), pgpMessageReceived.getMessage());
            System.out.println("keySymmetricDecrypted: " + keySymmetricDecrypted);
            System.out.println("decryptAES: " + decryptAES);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
    }

}
