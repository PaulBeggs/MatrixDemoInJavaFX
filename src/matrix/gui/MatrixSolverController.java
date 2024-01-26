package matrix.gui;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Tooltip;
import matrix.util.ErrorsAndSyntax;

import java.io.IOException;

public class MatrixSolverController implements DataManipulation {

    @FXML
    private ChoiceBox<Scenes> scenes;
    @FXML
    public void initialize() {
        update();
        setupScenesDropdown();

    }

    @Override
    public void handleSaveButton() {

    }

    @Override
    public void update() {

    }

    @Override
    public void setupScenesDropdown() {
        scenes.getItems().setAll(Scenes.values());
        scenes.setValue(Scenes.SOLVE);
        scenes.setTooltip(new Tooltip("Pick the scene"));
        scenes.setOnAction(event -> {
            Scenes selectedScene = scenes.getValue();
            try {
                selectedScene.switchScene(event);
            } catch (IOException e) {
                ErrorsAndSyntax.showErrorPopup("Unable to load the scene: " + scenes.getValue());
                throw new IllegalArgumentException(e);
            }
        });
    }

    @Override
    public void setToolTips() {

    }
}
