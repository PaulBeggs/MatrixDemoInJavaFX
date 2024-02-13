package matrix.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import matrix.model.MatrixInfo;
import matrix.util.ErrorsAndSyntax;
import matrix.view.MultiplicationView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiplicationController implements DataManipulation {
    @FXML
    private TextField matrixAMultiplier, matrixBMultiplier, resultMultiplier;
    @FXML
    private Pane pane;
    @FXML
    private Button operationSummaryButton, setMatrixButton, computeButton, clearSummaryButton;
    @FXML
    private ChoiceBox<Scenes> scenes;
    private MultiplicationView multiView;
    private Map<MatrixInfo, List<MatrixInfo>> connectedMatrices;
    private Map<Pane, MatrixInfo> matrixInfoMap;
    private List<Pane> allMatrices;

    @FXML
    private void initialize() {
        update();
        allMatrices = new ArrayList<>();
        connectedMatrices = new HashMap<>();
        matrixInfoMap = new HashMap<>();
        multiView = new MultiplicationView(pane, connectedMatrices, matrixInfoMap, allMatrices);
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
        multiView.createMatrixPane();
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

    private void populateMatrixUI(List<List<String>> matrixData) {

    }
}
