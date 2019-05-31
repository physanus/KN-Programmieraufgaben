package de.danielprinz.hskl.nk.pgp;

import de.danielprinz.hskl.nk.api.crypto.KryptoManager;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

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

            String md5 = KryptoManager.getMD5(message);
            System.out.println(md5);
            System.out.println(Arrays.toString(KryptoManager.decodeHex(md5)));
            System.out.println(KryptoManager.encodeHex(KryptoManager.decodeHex(md5)));

        } catch (NoSuchAlgorithmException ignored) {}
    }

}
