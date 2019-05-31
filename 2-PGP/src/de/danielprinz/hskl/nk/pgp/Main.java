package de.danielprinz.hskl.nk.pgp;

import de.danielprinz.hskl.nk.api.crypto.KryptoManager;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

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

    }





    public static void authenticate(String message) {
        try {

            KeyPair sender = KryptoManager.getFreshKeyPair(1024);
            KeyPair mitm = KryptoManager.getFreshKeyPair(1024);

            String md5Encrypted = KryptoManager.encrypt(sender.getPrivate(), KryptoManager.getMD5(message));
            //String md5Encrypted = KryptoManager.encrypt(mitm.getPrivate(), KryptoManager.getMD5(message));

            // send

            String md5Decrypted = KryptoManager.decrypt(sender.getPublic(), md5Encrypted, 32);
            String md5Expected = KryptoManager.getMD5(message);

            if(md5Decrypted.equals(md5Expected)) {
                System.out.println("Authenticated");
            } else {
                System.out.println("NOT authenticated");
            }

        } catch (NoSuchAlgorithmException ignored) {} catch (BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

}
