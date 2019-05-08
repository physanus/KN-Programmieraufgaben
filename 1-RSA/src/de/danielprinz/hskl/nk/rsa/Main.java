package de.danielprinz.hskl.nk.rsa;

import de.danielprinz.hskl.nk.rsa.crypto.KryproManager;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
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
