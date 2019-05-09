package de.danielprinz.hskl.nk.rsa;

import de.danielprinz.hskl.nk.rsa.crypto.KryptoManager;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.logging.*;

public class Main {

    private static final Level LOG_LEVEL = Level.ALL;

    public static final Logger LOGGER = Logger.getLogger("1-RSA");

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";


    public static void main(String[] args) {

        for(Handler handler : LOGGER.getParent().getHandlers()) {
            LOGGER.getParent().removeHandler(handler);
        }

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {

                String color;
                if(record.getLevel().intValue() <= Level.CONFIG.intValue())
                    color = ANSI_WHITE;
                else if(record.getLevel().intValue() == Level.INFO.intValue())
                    color = ANSI_PURPLE;
                else if(record.getLevel().intValue() == Level.WARNING.intValue())
                    color = ANSI_YELLOW;
                else
                    color = ANSI_RED;

                return color + "[" + record.getLoggerName() + "] [" + record.getLevel().getName() + "] " + record.getMessage() + "\n";
            }
        });
        consoleHandler.setLevel(LOG_LEVEL);
        LOGGER.addHandler(consoleHandler);
        LOGGER.setLevel(LOG_LEVEL);


        try {

            String plaintext = "Top Secret message";
            int keysize = 2048;


            /*
             * ENCRYPTION
             */


            LOGGER.info("ENCRYPTION");

            KeyPair keyPair = KryptoManager.getFreshKeyPair(keysize);
            byte[] encryptedEncryption = KryptoManager.encrypt(keyPair.getPublic(), plaintext);
            LOGGER.info("encryptedEncryption: " + Arrays.toString(encryptedEncryption));
            LOGGER.info("encryptedEncryption: " + new String(encryptedEncryption, StandardCharsets.UTF_8));

            String decryptedEncryption = KryptoManager.decrypt(keyPair.getPrivate(), encryptedEncryption);
            LOGGER.info("decryptedEncryption: " + decryptedEncryption + "\n");

            // p, q, e and n, phi, d calculation
            // BigInteger p = KryptoManager.getP(keyPair.getPublic());
            // BigInteger q = KryptoManager.getQ(keyPair.getPrivate());
            // BigInteger e = KryptoManager.getE(keyPair.getPublic());
            // BigInteger n = p.multiply(q);
            // BigInteger phi = p.subtract(BigInteger.valueOf(1)).multiply(q.subtract(BigInteger.valueOf(1)));
            // BigInteger d = e.modInverse(phi);
            // LOGGER.fine("p   = " + p);
            // LOGGER.fine("q   = " + q);
            // LOGGER.fine("e   = " + e);
            // LOGGER.fine("n   = " + n);
            // LOGGER.fine("phi = " + phi);
            // LOGGER.fine("d   = " + d);


            /*
             * NORMAL AUTHENTICATION
             */

            LOGGER.info("NORMAL AUTHENTICATION");

            KeyPair senderNormalAuthentication = KryptoManager.getFreshKeyPair(keysize);

            byte[] encryptedNormalAuthentication = KryptoManager.encrypt(senderNormalAuthentication.getPrivate(), plaintext);
            LOGGER.info("encryptedNormalAuthentication: " + Arrays.toString(encryptedEncryption));
            LOGGER.info("encryptedNormalAuthentication: " + new String(encryptedEncryption, StandardCharsets.UTF_8));

            String decryptedNormalAuthentication = KryptoManager.decrypt(senderNormalAuthentication.getPublic(), encryptedNormalAuthentication);
            LOGGER.info("decryptedNormalAuthentication: " + decryptedNormalAuthentication + "\n");


            /*
             * FALSE AUTHENTICATION
             */

            LOGGER.info("FALSE AUTHENTICATION");

            KeyPair senderFalseAuthentication = KryptoManager.getFreshKeyPair(keysize);
            KeyPair receiverFalseAutehntication = KryptoManager.getFreshKeyPair(keysize);

            byte[] encryptedFalseAuthentication = KryptoManager.encrypt(senderFalseAuthentication.getPrivate(), plaintext);
            LOGGER.info("encryptedFalseAuthentication: " + Arrays.toString(encryptedEncryption));
            LOGGER.info("encryptedFalseAuthentication: " + new String(encryptedEncryption, StandardCharsets.UTF_8));

            String decryptedFalseAuthentication = KryptoManager.decrypt(receiverFalseAutehntication.getPublic(), encryptedFalseAuthentication);
            LOGGER.info("decryptedFalseAuthentication: " + decryptedFalseAuthentication);




        } catch (NoSuchAlgorithmException | IllegalBlockSizeException | NoSuchPaddingException | InvalidKeyException | BadPaddingException e) {
            e.printStackTrace();
        }

    }


}
