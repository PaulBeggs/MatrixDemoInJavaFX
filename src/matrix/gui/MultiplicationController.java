package matrix.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import matrix.util.ErrorsAndSyntax;

import java.io.IOException;

public class MultiplicationController {
    @FXML
    TextField rowOne;
    @FXML
    TextField rowTwo;
    @FXML
    Button switchButton;
    @FXML
    ChoiceBox<Scenes> scenes;
    @FXML
    private void initialize() {

        scenes.getItems().setAll(Scenes.values());
        scenes.setValue(Scenes.RREF);

        scenes.setOnAction(event -> {
            Scenes selectedScene = scenes.getValue();
            try {
                selectedScene.switchScene(event);
            } catch (IOException e) {
                ErrorsAndSyntax.showErrorPopup("Cannot load the scene: " + selectedScene);
                throw new IllegalArgumentException(e);
            }
        });
    }

    @FXML
    private void handleRREFFunctionality() {

    }
}
