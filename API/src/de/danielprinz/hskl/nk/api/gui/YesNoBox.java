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

public class YesNoBox {

    /**
     * Displays a yes-no box
     * @param title The title of the box
     * @param message The message of the box
     * @param yes The runnable for the yes-option
     * @param no The runnable for the no-option
     */
    public static void display(String title, String message, Runnable yes, Runnable no) {
        Stage window = new Stage();
        try {
            window.getIcons().add(new Image(YesNoBox.class.getResourceAsStream("/unlocked.png")));
        } catch (NullPointerException ignores) {}

        // Block events to other windows
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);

        // Grid pane
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(8);
        grid.setHgap(10);

        GridPane optionsPane = new GridPane();
        optionsPane.setPadding(new Insets(10, 10, 10, 10));
        optionsPane.setVgap(8);
        optionsPane.setHgap(10);
        optionsPane.setAlignment(Pos.CENTER);
        GridPane.setConstraints(optionsPane, 0, 1);


        Label label = new Label();
        label.setText(message);
        GridPane.setHalignment(label, HPos.CENTER);
        GridPane.setConstraints(label, 0, 0);

        Button yesButton = new Button("Yes");
        GridPane.setHalignment(yesButton, HPos.CENTER);
        yesButton.setOnAction(e -> {
            window.close();
            yes.run();
        });
        yesButton.setOnKeyTyped(event -> {
            window.close();
            yes.run();
        });
        GridPane.setConstraints(yesButton, 0, 0);

        Button nobutton = new Button("No");
        GridPane.setHalignment(nobutton, HPos.CENTER);
        nobutton.setOnAction(e -> {
            window.close();
            no.run();
        });
        nobutton.setOnKeyTyped(event -> {
            window.close();
            no.run();
        });
        GridPane.setConstraints(nobutton, 1, 0);


        optionsPane.getChildren().addAll(yesButton, nobutton);

        grid.getChildren().addAll(label, optionsPane);
        grid.setAlignment(Pos.CENTER);

        // Display window and wait for it to be closed before returning
        Scene scene = new Scene(grid);
        window.setScene(scene);
        window.showAndWait();
    }

}
