package de.danielprinz.hskl.nk.rsa;

import de.danielprinz.hskl.nk.rsa.crypto.KryproManager;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

public class Main {

    public static final Logger LOGGER = Logger.getLogger("1-RSA");

    public static void main(String[] args) {

        try {
            KeyPair keyPair = KryproManager.getFreshKeyPair(2048);
            byte[] encrypted = KryproManager.encrypt(keyPair.getPublic(), "Top Secret message");
            System.out.println("encrypted: " + new String(encrypted, StandardCharsets.UTF_8));

            String decrypted = KryproManager.decrypt(keyPair.getPrivate(), encrypted);
            System.out.println("decrypted: " + decrypted);
        } catch (NoSuchAlgorithmException | IllegalBlockSizeException | NoSuchPaddingException | InvalidKeyException | BadPaddingException e) {
            e.printStackTrace();
        }

    }


}
