package de.danielprinz.hskl.nk.rsa;

import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.Toolkit;
import de.danielprinz.hskl.nk.rsa.crypto.KryptoManager;
import de.danielprinz.hskl.nk.rsa.gui.AlertBox;
import de.danielprinz.hskl.nk.rsa.gui.KeySizeGUI;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.*;

public class Main extends Application {

    // ICON src
    // https://www.flaticon.com/free-icon/unlocked_149463

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static final Logger LOGGER = Logger.getLogger("1-RSA");
    private static final Level LOG_LEVEL = Level.ALL;

    private static Main instance;


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

                return color + "[" + record.getLoggerName() + "] [" + record.getLevel().getName() + "] " + record.getMessage() + ANSI_RESET + "\n";
            }
        });
        consoleHandler.setLevel(LOG_LEVEL);
        LOGGER.addHandler(consoleHandler);
        LOGGER.setLevel(LOG_LEVEL);

        launch(args);
    }

    private static Stage window;
    private static final String WINDOW_TITLE = "1-RSA";
    private static final int WINDOW_WIDTH = 870;
    private static final int WINDOW_HEIGHT = 225;

    private static ArrayList<Label> labels;
    private static ArrayList<TextField> textFields;
    private static ArrayList<Button> buttons;
    private static Button authenticate;
    private static Button encrypt;

    public static void generateKeyPairGUI(int keysize, int i) {
        try {
            KeyPair keyPair = KryptoManager.getFreshKeyPair(keysize);

            String publicKey = Arrays.toString(keyPair.getPublic().getEncoded());
            String privateKey = Arrays.toString(keyPair.getPrivate().getEncoded());

            String keyString = publicKey + "; " + privateKey;

            LOGGER.info("generated " + labels.get(i).getText() + ": " + keyString);
            textFields.get(i).setText(keyString);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        instance = this;

        window = primaryStage;
        window.setTitle(WINDOW_TITLE);
        try {
            window.getIcons().add(new Image(Main.class.getResourceAsStream("/unlocked.png")));
        } catch (NullPointerException ignored) {}
        // window.setAlwaysOnTop(true);
        // window.setX(Screen.getPrimary().getVisualBounds().getMinX() + Screen.getPrimary().getVisualBounds().getWidth() - WINDOWS_WIDTH);
        // window.setY(Screen.getPrimary().getVisualBounds().getMinY() + Screen.getPrimary().getVisualBounds().getHeight() - WINDOWS_HEIGHT - 38);

        GridPane mainPane = new GridPane();
        //mainPane.setPadding(new Insets(10, 10, 10, 10));
        mainPane.setPadding(new Insets(0, 0, 0, 0));
        mainPane.setVgap(8);
        mainPane.setHgap(10);

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);
        mainPane.add(grid, 0, 1);


        // Create all labels and buttons
        FontLoader fontLoader = Toolkit.getToolkit().getFontLoader();
        labels = new ArrayList<>();
        textFields = new ArrayList<>();
        buttons = new ArrayList<>();

        int i;
        for(i = 0; i <= 5; i++) {
            String title = "";
            if(i == 0) {
                title = "KeyPair Sender";
            } else if(i == 1) {
                title = "KeyPair Receiver";
            } else if (i == 2) {
                title = "Plaintext";
            } else if (i == 3) {
                title = "Encrypted [bytes]";
            } else if (i == 4) {
                title = "Encrypted [UTF-8]";
            } else if (i == 5) {
                title = "Decrypted";
            }

            Label label = new Label(title);
            label.setMinWidth(fontLoader.computeStringWidth(label.getText(), label.getFont()));
            GridPane.setConstraints(label, 0, i);
            labels.add(label);

            TextField textField = new TextField();
            textField.setPrefColumnCount(36);
            GridPane.setConstraints(textField, 1, i);

            if(i == 2) {
                textField.setText("This is top secret");
            }

            if (i >= 3) {
                textField.setEditable(false);
            }

            textFields.add(textField);


            if (i > 2) continue;

            if (i == 2) {

                Button encryptButton = new Button("Encrypt");
                encryptButton.setPrefWidth(145);
                GridPane.setConstraints(encryptButton, 2, i);

                encryptButton.setOnAction(e -> {

                    try {

                        String keyReceiver = textFields.get(1).getText();
                        String[] keyReceiverPublic = keyReceiver.split("; ")[0].replaceAll("\\[", "").replaceAll("]", "").split(", ");
                        String[] keyReceiverPrivate = keyReceiver.split("; ")[1].replaceAll("\\[", "").replaceAll("]", "").split(", ");

                        byte[] keyReceiverPublicBytes = new byte[keyReceiverPublic.length];
                        for(int j = 0; j < keyReceiverPublic.length; j++) {
                            keyReceiverPublicBytes[j] = Byte.valueOf(keyReceiverPublic[j]);
                        }
                        LOGGER.info("Found public key bytes: " + Arrays.toString(keyReceiverPublicBytes));

                        byte[] keyReceiverPrivateBytes = new byte[keyReceiverPrivate.length];
                        for(int j = 0; j < keyReceiverPrivate.length; j++) {
                            keyReceiverPrivateBytes[j] = Byte.valueOf(keyReceiverPrivate[j]);
                        }
                        LOGGER.info("Found private key bytes: " + Arrays.toString(keyReceiverPrivateBytes));


                        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

                        X509EncodedKeySpec x509EncodedKeySpecPublic = new X509EncodedKeySpec(keyReceiverPublicBytes);
                        PublicKey publicKeyReceiver = keyFactory.generatePublic(x509EncodedKeySpecPublic);

                        PKCS8EncodedKeySpec pkcs8EncodedKeySpecPrivate = new PKCS8EncodedKeySpec(keyReceiverPrivateBytes);
                        PrivateKey privateKeyReceiver = keyFactory.generatePrivate(pkcs8EncodedKeySpecPrivate);


                        // encrypt
                        byte[] encryptedEncryption = KryptoManager.encrypt(publicKeyReceiver, textFields.get(2).getText());
                        LOGGER.info("encryptedEncryption: " + Arrays.toString(encryptedEncryption));
                        LOGGER.info("encryptedEncryption: " + new String(encryptedEncryption, StandardCharsets.UTF_8));
                        textFields.get(3).setText(Arrays.toString(encryptedEncryption));
                        textFields.get(4).setText(new String(encryptedEncryption, StandardCharsets.UTF_8));

                        // decrypt
                        String decryptedEncryption = KryptoManager.decrypt(privateKeyReceiver, encryptedEncryption);
                        LOGGER.info("decryptedEncryption: " + decryptedEncryption + "\n");
                        textFields.get(5).setText(decryptedEncryption);


                    } catch (ArrayIndexOutOfBoundsException | NoSuchAlgorithmException | InvalidKeySpecException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException | InvalidKeyException e1) {
                        e1.printStackTrace();
                        AlertBox.display("Fehler", "Einer deiner Keys scheint kaputt zu sein.\nPrüfe bitte deren Korrektheit!");
                    }


                });

                buttons.add(encryptButton);


                Button authenticateButton = new Button("Authenticate");
                authenticateButton.setPrefWidth(155);
                GridPane.setConstraints(authenticateButton, 3, i);

                authenticateButton.setOnAction(e -> {



                });

                buttons.add(authenticateButton);
                continue;
            }

            if (i == 0) {
                title = "Import KeyPair Sender";
            } else {
                title = "Import KeyPair Receiver";
            }
            Button importButton = new Button(title);
            importButton.setPrefWidth(145);
            GridPane.setConstraints(importButton, 2, i);

            String finalTitle = title;
            importButton.setOnAction(e -> {

                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle(finalTitle);
                fileChooser.showOpenDialog(window);

                // TODO

            });

            buttons.add(importButton);



            if (i == 0) {
                title = "Generate KeyPair Sender";
            } else {
                title = "Generate KeyPair Receiver";
            }
            Button generateButton = new Button(title);
            generateButton.setPrefWidth(155);
            GridPane.setConstraints(generateButton, 3, i);

            String finalTitle1 = title;
            int finalI = i;
            generateButton.setOnAction(e -> {

                KeySizeGUI.display(finalTitle1, finalI);
                //KryptoManager.getFreshKeyPair();

            });

            buttons.add(generateButton);

        }

        /*authenticate.setOnAction((ActionEvent e) -> {
            if(textFields.get(0).getText().equals("")) {
                AlertBox.display("Fehler: Startpunkt fehlt", "Bitte gib einen Startpunk an, bevor du eine Route planst!");
                return;
            }
            if(textFields.get(1).getText().equals("")) {
                AlertBox.display("Fehler: Zielort fehlt", "Bitte gib mindestens einen Zielort ein!");
                return;
            }

            authenticate.setDisable(true);
            for(TextField textField : textFields) textField.setDisable(true);
            menuBar.setDisable(true);
            ArrayList<String> waypoints = new ArrayList<>(textFields.stream().map(TextInputControl::getText).filter(s -> !s.equals("")).collect(Collectors.toList()));

            new Thread(() -> {

                try {

                    ArrayList<RouteContainer> cheapestRoutes = GoogleAPI.analyze(waypoints);
                    Platform.runLater(() -> {
                        RouteViewer.display(cheapestRoutes);
                        releaseUILocks();
                    });

                } catch (IOException | InterruptedException | ApiException e1) {
                    Platform.runLater(() -> AlertBox.display("Fehler: Verbindung", "Es konnte keine Verbindung hergestellt werden!"));
                    e1.printStackTrace();
                    Platform.runLater(() -> {
                        releaseUILocks();
                    });
                    return;
                }

            }).start();


        });*/

        // Add everything to grid
        grid.getChildren().addAll(labels);
        grid.getChildren().addAll(textFields);
        grid.getChildren().addAll(buttons);


        Scene scene = new Scene(mainPane, WINDOW_WIDTH, WINDOW_HEIGHT);
        window.setScene(scene);
        window.show();

    }








    //        try {
//
//            String plaintext = "Top Secret message";
//            int keysize = 2048;
//
//
//            /*
//             * ENCRYPTION
//             */
//
//
//            LOGGER.info("ENCRYPTION");
//
//            KeyPair keyPair = KryptoManager.getFreshKeyPair(keysize);
//            byte[] encryptedEncryption = KryptoManager.encrypt(keyPair.getPublic(), plaintext);
//            LOGGER.info("encryptedEncryption: " + Arrays.toString(encryptedEncryption));
//            LOGGER.info("encryptedEncryption: " + new String(encryptedEncryption, StandardCharsets.UTF_8));
//
//            String decryptedEncryption = KryptoManager.decrypt(keyPair.getPrivate(), encryptedEncryption);
//            LOGGER.info("decryptedEncryption: " + decryptedEncryption + "\n");
//
//            // p, q, e and n, phi, d calculation
//            // BigInteger p = KryptoManager.getP(keyPair.getPublic());
//            // BigInteger q = KryptoManager.getQ(keyPair.getPrivate());
//            // BigInteger e = KryptoManager.getE(keyPair.getPublic());
//            // BigInteger n = p.multiply(q);
//            // BigInteger phi = p.subtract(BigInteger.valueOf(1)).multiply(q.subtract(BigInteger.valueOf(1)));
//            // BigInteger d = e.modInverse(phi);
//            // LOGGER.fine("p   = " + p);
//            // LOGGER.fine("q   = " + q);
//            // LOGGER.fine("e   = " + e);
//            // LOGGER.fine("n   = " + n);
//            // LOGGER.fine("phi = " + phi);
//            // LOGGER.fine("d   = " + d);
//
//
//            /*
//             * NORMAL AUTHENTICATION
//             */
//
//            LOGGER.info("NORMAL AUTHENTICATION");
//
//            KeyPair senderNormalAuthentication = KryptoManager.getFreshKeyPair(keysize);
//
//            byte[] encryptedNormalAuthentication = KryptoManager.encrypt(senderNormalAuthentication.getPrivate(), plaintext);
//            LOGGER.info("encryptedNormalAuthentication: " + Arrays.toString(encryptedEncryption));
//            LOGGER.info("encryptedNormalAuthentication: " + new String(encryptedEncryption, StandardCharsets.UTF_8));
//
//            String decryptedNormalAuthentication = KryptoManager.decrypt(senderNormalAuthentication.getPublic(), encryptedNormalAuthentication);
//            LOGGER.info("decryptedNormalAuthentication: " + decryptedNormalAuthentication + "\n");
//
//
//            /*
//             * FALSE AUTHENTICATION
//             */
//
//            LOGGER.info("FALSE AUTHENTICATION");
//
//            KeyPair senderFalseAuthentication = KryptoManager.getFreshKeyPair(keysize);
//            KeyPair receiverFalseAutehntication = KryptoManager.getFreshKeyPair(keysize);
//
//            byte[] encryptedFalseAuthentication = KryptoManager.encrypt(senderFalseAuthentication.getPrivate(), plaintext);
//            LOGGER.info("encryptedFalseAuthentication: " + Arrays.toString(encryptedEncryption));
//            LOGGER.info("encryptedFalseAuthentication: " + new String(encryptedEncryption, StandardCharsets.UTF_8));
//
//            String decryptedFalseAuthentication = KryptoManager.decrypt(receiverFalseAutehntication.getPublic(), encryptedFalseAuthentication);
//            LOGGER.info("decryptedFalseAuthentication: " + decryptedFalseAuthentication);
//
//
//
//
//        } catch (NoSuchAlgorithmException | IllegalBlockSizeException | NoSuchPaddingException | InvalidKeyException | BadPaddingException e) {
//            e.printStackTrace();
//        }





}
