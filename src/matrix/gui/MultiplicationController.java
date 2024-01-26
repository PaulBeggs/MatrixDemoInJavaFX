package matrix.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import matrix.util.ErrorsAndSyntax;

import java.io.IOException;

public class MultiplicationController {
    @FXML
    private TextField matrixAMultiplier, matrixBMultiplier, resultMultiplier;
    @FXML
    private GridPane matrixGrid;
    @FXML
    private Button operationSummaryButton, setMatrixButton, computeButton, clearSummaryButton;
    @FXML
    private ChoiceBox<Scenes> scenes;

    @FXML
    private void initialize() {

        scenes.getItems().setAll(Scenes.values());
        scenes.setValue(Scenes.MULTIPLICATION);

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
    void handleOperationSummary() {

    }

    @FXML
    void handleResetOperationSummary() {

    }

    @FXML
    void performOperation() {

    }

}
