package de.danielprinz.hskl.nk.pgp;

import de.danielprinz.hskl.nk.pgp.crypto.KryptoManager;

import javax.crypto.KeyGenerator;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

public class Main {

    public static void main(String[] args) {

        try {

            KeyPair keyPair = KryptoManager.getFreshKeyPair(2048);

            KeyGenerator.getInstance("").gener


        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }

}
