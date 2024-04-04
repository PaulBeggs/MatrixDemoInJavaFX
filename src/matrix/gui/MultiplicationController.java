package matrix.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import matrix.model.MatrixSingleton;
import matrix.util.ErrorsAndSyntax;
import matrix.view.MultiplicationView;

import java.io.IOException;
import java.util.List;

public class MultiplicationController implements DataManipulation {
    @FXML
    private Pane pane;
    @FXML
    private Button operationSummaryButton, setMatrixButton, computeButton, clearSummaryButton, initalizeButton;
    @FXML
    private ChoiceBox<Scenes> scenes;
    private MultiplicationView multiView;

    @FXML
    private void initialize() {
        update();
        multiView = new MultiplicationView(pane);
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
    public void handleOperationSummary() {

    }

    @FXML
    public void handleResetOperationSummary() {

    }

    @FXML
    public void performOperation() {

    }

    @FXML
    public void addMatrix() {
        multiView.createMatrixPane(MatrixSingleton.getInstance());
    }

    @Override
    public void handleSaveButton() {

    }

    @Override
    public void setupScenesDropdown() {

    }

    @Override
    public void setToolTips() {

    }

    @Override
    public void update() {

    }

    @FXML
    public void handleInitializeMatrix() {
        initializeMatrix();
    }

    private void populateMatrixUI(List<List<String>> matrixData) {

    }

    private void initializeMatrix() {
        Stage initStage = new Stage();
        initStage.setTitle("Matrix Selection");
        initStage.initModality(Modality.WINDOW_MODAL);
        initStage.initOwner(MatrixApp.getPrimaryStage());

        FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/initializeMatrixScene.fxml"));
        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            ErrorsAndSyntax.showErrorPopup("Cannot load the scene.");
            throw new IllegalArgumentException(e);
        }

        Scene initScene = new Scene(root);
        initScene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                initStage.close();
            }
        });
        MatrixApp.applyTheme(initScene);
        initStage.setScene(initScene);
        initStage.show();
    }
}
