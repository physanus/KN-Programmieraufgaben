package de.danielprinz.hskl.nk.rsa.gui;

import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.Toolkit;
import de.danielprinz.hskl.nk.rsa.Main;
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

public class KeySizeGUI {

    private static Button generate;

    /**
     * Displays a GUI for selection of the keySize and a file to save the generated keys to
     * @param i The no of the textfield being triggered on
     */
    public static void display(int i) {

        Stage window = new Stage();
        window.setTitle("Select key size");
        try {
            window.getIcons().add(new Image(Main.class.getResourceAsStream("/unlocked.png")));
        } catch (NullPointerException ignored) {}
        window.initModality(Modality.APPLICATION_MODAL);

        FontLoader fontLoader = Toolkit.getToolkit().getFontLoader();

        // Grid pane
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

        Label keySizeLabel = new Label("Key size");
        keySizeLabel.setMinWidth(fontLoader.computeStringWidth(keySizeLabel.getText(), keySizeLabel.getFont()));
        GridPane.setConstraints(keySizeLabel, 0, 0);

        TextField keySizeTextField = new TextField();
        keySizeTextField.setPrefColumnCount(13);
        keySizeTextField.setText("2048");
        // force the field to be numeric only
        keySizeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.equals("")) return;
            try {
                Integer.parseInt(newValue);
            } catch (NumberFormatException e) {
                keySizeTextField.setText(oldValue);
            }
        });

        keySizeTextField.setOnAction(event -> generate.fire());
        GridPane.setConstraints(keySizeTextField, 1, 0);


        generate = new Button("Generate");
        generate.setPrefWidth(300);
        GridPane.setConstraints(generate, 1, 1);
        generate.setOnAction(event -> {

            int keySize = Integer.parseInt(keySizeTextField.getText());

            if(keySize < 512) {
                AlertBox.display("Error", "The key size must be greater than or equal 512 Bit");
                return;
            } else if(keySize > 16384) {
                AlertBox.display("Error", "The key size must be less than or equal 16384 Bit");
                return;
            }

            window.close();
            Main.generateKeyPairGUI(keySize, i, null);
        });


        Button generateAndSave = new Button("Generate and Save");
        generateAndSave.setPrefWidth(300);
        GridPane.setConstraints(generateAndSave, 1, 2);
        generateAndSave.setOnAction(event -> {

            int keySize = Integer.parseInt(keySizeTextField.getText());

            if(keySize < 512) {
                AlertBox.display("Error", "The key size must be greater than or equal 512 Bit");
                return;
            } else if(keySize > 16384) {
                AlertBox.display("Error", "The key size must be less than or equal 16384 Bit");
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
                Main.generateKeyPairGUI(keySize, i, selectedFile);
            }


        });


        // Add everything to grid
        grid.getChildren().addAll(keySizeLabel, keySizeTextField, generate, generateAndSave);

        Scene scene = new Scene(grid, 240, 110);
        window.setScene(scene);
        window.showAndWait();

    }

}
