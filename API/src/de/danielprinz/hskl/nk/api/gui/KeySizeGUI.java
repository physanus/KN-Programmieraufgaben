package de.danielprinz.hskl.nk.api.gui;

import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.Toolkit;
import de.danielprinz.hskl.nk.api.crypto.KryptoManager;
import de.danielprinz.hskl.nk.api.crypto.LoggerUtil;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.logging.Level;

public class KeySizeGUI {

    private static Button generate;

    /**
     * Displays a GUI for selection of the keysize and a file to save the generated keys to
     * @param i The no of the textfield being triggered on
     */
    public static void display(int i, ArrayList<Label> labels, ArrayList<TextField> textFields) {

        Stage window = new Stage();
        window.setTitle("Select Keysize");
        try {
            window.getIcons().add(new Image(KeySizeGUI.class.getResourceAsStream("/unlocked.png")));
        } catch (NullPointerException ignored) {}
        window.initModality(Modality.APPLICATION_MODAL);

        FontLoader fontLoader = Toolkit.getToolkit().getFontLoader();

        // Grid pane
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

        Label keysizeLabel = new Label("Keysize");
        keysizeLabel.setMinWidth(fontLoader.computeStringWidth(keysizeLabel.getText(), keysizeLabel.getFont()));
        GridPane.setConstraints(keysizeLabel, 0, 0);

        TextField keysizeTextField = new TextField();
        keysizeTextField.setPrefColumnCount(13);
        keysizeTextField.setText("2048");
        // force the field to be numeric only
        keysizeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.equals("")) return;
            try {
                Integer.parseInt(newValue);
            } catch (NumberFormatException e) {
                keysizeTextField.setText(oldValue);
            }
        });

        keysizeTextField.setOnAction(event -> {
            generate.fire();
        });
        GridPane.setConstraints(keysizeTextField, 1, 0);


        generate = new Button("Generate");
        generate.setPrefWidth(300);
        GridPane.setConstraints(generate, 1, 1);
        generate.setOnAction(event -> {

            int keysize = Integer.parseInt(keysizeTextField.getText());

            if(keysize < 512) {
                AlertBox.display("Fehler", "The keysize must be greater than or equal 512 Bit");
                return;
            } else if(keysize > 16384) {
                AlertBox.display("Fehler", "The keysize must be less than or equal 16384 Bit");
                return;
            }

            window.close();
            generateKeyPairGUI(keysize, i, null, labels, textFields);
        });


        Button generateAndSave = new Button("Generate and Save");
        generateAndSave.setPrefWidth(300);
        GridPane.setConstraints(generateAndSave, 1, 2);
        generateAndSave.setOnAction(event -> {

            int keysize = Integer.parseInt(keysizeTextField.getText());

            if(keysize < 512) {
                AlertBox.display("Fehler", "The keysize must be greater than or equal 512 Bit");
                return;
            } else if(keysize > 16384) {
                AlertBox.display("Fehler", "The keysize must be less than or equal 16384 Bit");
                return;
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select file");
            fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
            FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("KNRSA (*.knrsa)", "*.knrsa");
            fileChooser.getExtensionFilters().add(extensionFilter);
            fileChooser.setSelectedExtensionFilter(extensionFilter);

            File selectedFile = fileChooser.showSaveDialog(window);

            if(selectedFile != null) {
                window.close();
                generateKeyPairGUI(keysize, i, selectedFile, labels, textFields);
            }


        });


        // Add everything to grid
        grid.getChildren().addAll(keysizeLabel, keysizeTextField, generate, generateAndSave);

        Scene scene = new Scene(grid, 240, 110);
        window.setScene(scene);
        window.showAndWait();

    }



    /**
     * Fetches a new KeySet, inputs it into the GUI and saves it to the file if specified
     * @param keysize The size of the key which should be generated, range: 512-16385
     * @param i The no of the textfield being triggered on
     * @param file The file where the keys should be saved to OR null if the keys should not be saved
     */
    private static void generateKeyPairGUI(int keysize, int i, File file, ArrayList<Label> labels, ArrayList<TextField> textFields) {
        try {
            KeyPair keyPair = KryptoManager.getFreshKeyPair(keysize);

            String publicKey = KryptoManager.encodeHex(keyPair.getPublic().getEncoded());
            String privateKey = KryptoManager.encodeHex(keyPair.getPrivate().getEncoded());

            String keyString = publicKey + ", " + privateKey;

            LoggerUtil.log(Level.INFO, "generated " + labels.get(i).getText() + ": " + keyString);
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
