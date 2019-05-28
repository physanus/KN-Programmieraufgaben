package de.danielprinz.hskl.nk.rsa;

import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.Toolkit;
import de.danielprinz.hskl.nk.api.crypto.KryptoManager;
import de.danielprinz.hskl.nk.api.crypto.LoggerUtil;
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
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.*;

public class Main extends Application {

    // ICON src
    // https://www.flaticon.com/free-icon/unlocked_149463

    private static Main instance;
    private static LoggerUtil logger;

    public static void main(String[] args) {

        logger = LoggerUtil.getInstance();
        logger.setPrefix("1-RSA");

        launch(args);
    }

    private static Stage window;
    private static final String WINDOW_TITLE = "1-RSA";
    private static final int WINDOW_WIDTH = 870;
    private static final int WINDOW_HEIGHT = 225;

    private static ArrayList<Label> labels;
    private static ArrayList<TextField> textFields;
    private static ArrayList<Button> buttons;


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

                        KeyPair keyPairReceiver = KryptoManager.getKeysFromString(textFields.get(1).getText());

                        // encrypt
                        byte[] encryptedEncryption = KryptoManager.encrypt(keyPairReceiver.getPublic(), textFields.get(2).getText());
                        logger.log(Level.INFO, "encryptedEncryption: " + Arrays.toString(encryptedEncryption));
                        logger.log(Level.INFO, "encryptedEncryption: " + new String(encryptedEncryption, StandardCharsets.UTF_8));
                        textFields.get(3).setText(Arrays.toString(encryptedEncryption));
                        textFields.get(4).setText(new String(encryptedEncryption, StandardCharsets.UTF_8));

                        // decrypt
                        String decryptedEncryption = KryptoManager.decrypt(keyPairReceiver.getPrivate(), encryptedEncryption);
                        logger.log(Level.INFO, "decryptedEncryption: " + decryptedEncryption + "\n");
                        textFields.get(5).setText(decryptedEncryption);


                    } catch (ArrayIndexOutOfBoundsException | NoSuchAlgorithmException | InvalidKeySpecException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException | InvalidKeyException e1) {
                        e1.printStackTrace();
                        AlertBox.display("Fehler", "One of the keys seems to be broken.\nPlease, check their correctness!");
                    }


                });

                buttons.add(encryptButton);


                Button authenticateButton = new Button("Authenticate");
                authenticateButton.setPrefWidth(155);
                GridPane.setConstraints(authenticateButton, 3, i);

                authenticateButton.setOnAction(e -> {

                    try {

                        KeyPair keyPairSender = KryptoManager.getKeysFromString(textFields.get(0).getText());
                        KeyPair keyPairReceiver = KryptoManager.getKeysFromString(textFields.get(1).getText());

                        // randomize whether the authentication should be successful or not (MITM-Attack)
                        boolean correctAuthentication = ThreadLocalRandom.current().nextBoolean();

                        byte[] encryptedAuthentication = KryptoManager.encrypt(correctAuthentication ? keyPairSender.getPrivate() : keyPairReceiver.getPrivate(), textFields.get(2).getText());
                        logger.log(Level.INFO, "encryptedAuthentication: " + Arrays.toString(encryptedAuthentication));
                        logger.log(Level.INFO, "encryptedAuthentication: " + new String(encryptedAuthentication, StandardCharsets.UTF_8));
                        textFields.get(3).setText(Arrays.toString(encryptedAuthentication));
                        textFields.get(4).setText(new String(encryptedAuthentication, StandardCharsets.UTF_8));

                        String decryptedAuthentication = KryptoManager.decrypt(keyPairSender.getPublic(), encryptedAuthentication);
                        logger.log(Level.INFO, "decryptedAuthentication: " + decryptedAuthentication + "\n");
                        textFields.get(5).setText(decryptedAuthentication);

                        AlertBox.display("Info",
                                "If the sender was authenticated successfully,\n" +
                                        "the message in 'Decrypted' is readable\n" +
                                        "Here: " + (correctAuthentication ? "Authenticated / readable" : "Not authenticated / not readable")
                        );


                    } catch (NoSuchAlgorithmException | InvalidKeySpecException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException | InvalidKeyException e1) {
                        e1.printStackTrace();
                        AlertBox.display("Fehler", "One of the keys seems to be broken.\nPlease, check their correctness!");
                    }

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
            int finalI1 = i;
            importButton.setOnAction(e -> {

                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle(finalTitle);
                fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
                FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("KNRSA (*.knrsa)", "*.knrsa");
                fileChooser.getExtensionFilters().add(extensionFilter);
                fileChooser.setSelectedExtensionFilter(extensionFilter);

                File selectedFile = fileChooser.showOpenDialog(window);
                if(selectedFile == null) return;
                if(!selectedFile.exists()) {
                    AlertBox.display("Error", "The supplied file does not exist");
                    return;
                }

                try {
                    String keyString = new String(Files.readAllBytes(Paths.get(selectedFile.getAbsolutePath())));
                    try {
                        KryptoManager.getKeysFromString(keyString);
                    } catch (NoSuchAlgorithmException | InvalidKeySpecException e1) {
                        AlertBox.display("Error", "The key is invalid");
                        return;
                    }

                    textFields.get(finalI1).setText(keyString);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

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

            int finalI = i;
            generateButton.setOnAction(e -> KeySizeGUI.display(finalI));
            buttons.add(generateButton);

        }

        // Add everything to grid
        grid.getChildren().addAll(labels);
        grid.getChildren().addAll(textFields);
        grid.getChildren().addAll(buttons);


        Scene scene = new Scene(mainPane, WINDOW_WIDTH, WINDOW_HEIGHT);
        window.setScene(scene);
        window.show();

    }


    /**
     * Fetches a new KeySet, inputs it into the GUI and saves it to the file if specified
     * @param keysize The size of the key which should be generated, range: 512-16385
     * @param i The no of the textfield being triggered on
     * @param file The file where the keys should be saved to OR null if the keys should not be saved
     */
    public static void generateKeyPairGUI(int keysize, int i, File file) {
        try {
            KeyPair keyPair = KryptoManager.getFreshKeyPair(keysize);

            String publicKey = Arrays.toString(keyPair.getPublic().getEncoded());
            String privateKey = Arrays.toString(keyPair.getPrivate().getEncoded());

            String keyString = publicKey + "; " + privateKey;

            logger.log(Level.INFO, "generated " + labels.get(i).getText() + ": " + keyString);
            textFields.get(i).setText(keyString);

            if(file != null) {
                try (PrintStream out = new PrintStream(new FileOutputStream(file))) {
                    out.print(keyString);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }




}
