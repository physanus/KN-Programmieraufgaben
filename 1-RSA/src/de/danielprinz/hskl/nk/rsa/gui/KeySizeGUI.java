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

    public static <a> void display(int i) {

        Stage window = new Stage();
        window.setTitle("Select Keysize");
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
            Main.generateKeyPairGUI(keysize, i, null);
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
            FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("DKLRSA (*.dklrsa)", "*.dklrsa");
            fileChooser.getExtensionFilters().add(extensionFilter);
            fileChooser.setSelectedExtensionFilter(extensionFilter);

            File selectedFile = fileChooser.showSaveDialog(window);

            window.close();
            Main.generateKeyPairGUI(keysize, i, selectedFile);


        });


        // Add everything to grid
        grid.getChildren().addAll(keysizeLabel, keysizeTextField, generate, generateAndSave);

        Scene scene = new Scene(grid, 240, 110);
        window.setScene(scene);
        window.showAndWait();

    }

}
