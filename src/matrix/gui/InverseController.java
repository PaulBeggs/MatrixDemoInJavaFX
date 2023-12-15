package matrix.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import java.io.IOException;

public class InverseController {
    @FXML
    TextField multiplier;
    @FXML
    TextField sourceRow;
    @FXML
    TextField targetRow;
    @FXML
    Button compute;
    @FXML
    ChoiceBox<Scenes> scenes;
    @FXML
    private void initialize() {

        scenes.getItems().setAll(Scenes.values());
        scenes.setValue(Scenes.INVERSE);

        scenes.setOnAction(event -> {
            Scenes selectedScene = scenes.getValue();
            try {
                selectedScene.switchScene(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    public void handleInvertingFunctionality() {

    }
}
