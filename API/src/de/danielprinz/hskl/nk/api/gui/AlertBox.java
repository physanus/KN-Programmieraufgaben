package de.danielprinz.hskl.nk.api.gui;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AlertBox {

    /**
     * Displays an alert box
     * @param title The title of the box
     * @param message The message of the box
     */
    public static void display(String title, String message) {
        Stage window = new Stage();
        try {
            window.getIcons().add(new Image(AlertBox.class.getResourceAsStream("/unlocked.png")));
        } catch (NullPointerException ignores) {}

        // Block events to other windows
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);

        // Grid pane
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);


        Label label = new Label();
        label.setText(message);
        GridPane.setHalignment(label, HPos.CENTER);
        GridPane.setConstraints(label, 0, 0);

        Button closeButton = new Button("OK");
        GridPane.setHalignment(closeButton, HPos.CENTER);
        closeButton.setOnAction(e -> window.close());
        closeButton.setOnKeyTyped(event -> window.close());
        GridPane.setConstraints(closeButton, 0, 1);


        grid.getChildren().addAll(label, closeButton);
        grid.setAlignment(Pos.CENTER);

        // Display window and wait for it to be closed before returning
        Scene scene = new Scene(grid);
        window.setScene(scene);
        window.showAndWait();
    }

}
