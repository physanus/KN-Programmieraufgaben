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
import javafx.stage.Modality;
import javafx.stage.Stage;

public class KeySizeGUI {

    private static Button save;

    public static void display(String title, int i) {

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
            save.fire();
        });
        GridPane.setConstraints(keysizeTextField, 1, 0);


        save = new Button(title);
        save.setPrefWidth(300);
        GridPane.setConstraints(save, 1, 1);
        save.setOnAction(event -> {

            int keysize = Integer.parseInt(keysizeTextField.getText());

            // TODO show popup
            if(keysize < 512) {
                keysizeTextField.setText("512");
                return;
            } else if(keysize > 16384) {
                keysizeTextField.setText("16384");
                return;
            }

            window.close();

            Main.generateKeyPairGUI(keysize, i);
        });


        // Add everything to grid
        grid.getChildren().addAll(keysizeLabel, keysizeTextField, save);

        Scene scene = new Scene(grid, 240, 80);
        window.setScene(scene);
        window.showAndWait();

    }

}
