package matrix.gui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import matrix.fileManaging.FilePath;
import matrix.fileManaging.MatrixFileHandler;
import matrix.model.*;
import javafx.fxml.FXML;
import matrix.operators.MatrixDeterminantOperations;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class DeterminantPopUpController {
    @FXML
    Button start, stop;
    @FXML
    ProgressBar progressBar;
    @FXML
    TextField signTextField;
    @FXML
    GridPane matrixGrid;
    private List<List<TextField>> matrixTextFields;
    private Matrix matrix;
    private TriangularizationView view;
    private boolean clockOn;
    private Stage stage;
    private MatrixDeterminantOperations operations;
    Timeline timeline;
    AtomicInteger counter = new AtomicInteger();

    @FXML
    private void initialize() {
        uploadFromFile();
        operations = new MatrixDeterminantOperations(matrix);
        setUpTriangularizationViews();
        operations.calculateDeterminant();
        System.out.println("this is the initial matrix inside of the popup controller: \n" + matrix);
        matrixTextFields = new ArrayList<>();
        setStage(stage);
        view.updateViews("initial");
        handleTimer();
    }


    private void updateSignChangesDisplay() {
        String signChanges = String.valueOf((matrix.getSign()));
        signTextField.setText(signChanges);
    }

    private void setUpTriangularizationViews() {
        this.view = new TriangularizationView(matrix, matrixGrid);
    }

    private void handleTimer() {
        int totalSteps = operations.getTotalSteps();
        System.out.println("These are the total steps (handleTimer): \n" + totalSteps);

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), (ActionEvent t) -> {
            if (totalSteps == 0) {

            } else {
                String stepKey = "step_" + counter.getAndIncrement();
                System.out.println("Animating: " + stepKey);
//            updateSignChangesDisplay();

                view.updateViews(stepKey);
            }

        }));

        timeline.setCycleCount(totalSteps);
        start.setOnAction((t) -> timeline.play());
    }

    @FXML
    public void start() {
        timeline.play();
        clockOn = true;
        handleProgressBar();
    }

    @FXML
    public void stop() {
        timeline.stop();
        clockOn = false;
        handleProgressBar();
    }

    public void setMatrixGrid(GridPane matrixGrid) {
        this.matrixGrid = matrixGrid;
    }

    @FXML
    private void handleProgressBar() {
        if (!clockOn) {
            progressBar.setOpacity(100);
        }
        progressBar.setOpacity(0);
    }

        public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setMatrixTextFields(List<List<TextField>> matrixTextFields) {
        this.matrixTextFields = matrixTextFields;
    }

    public void uploadFromFile() {
        matrix = MatrixFileHandler.getMatrix("initial");
        setMatrixTextFields(matrixTextFields);
        setMatrixGrid(matrixGrid);
    }
}