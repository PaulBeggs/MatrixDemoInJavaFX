package matrix.gui;

import javafx.animation.Timeline;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import matrix.fileManaging.MatrixFileHandler;
import matrix.model.*;
import javafx.fxml.FXML;
import matrix.view.TriangularizationView;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DeterminantPopUpController {
    @FXML
    Button start, stop;
    @FXML
    ProgressBar progressBar;
    @FXML
    TextField signTextField;
    @FXML
    GridPane matrixGrid;
    private MatrixCell[][] matrixCells;
    private Matrix matrix;
    private TriangularizationView view;
    private boolean clockOn;
    Timeline timeline;
    AtomicInteger counter = new AtomicInteger();

    @FXML
    private void initialize() {
        uploadFromFile();
        setUpTriangularizationViews();
        System.out.println("this is the initial matrix inside of the popup controller: \n" + matrix);
        view.updateViews("initial");
        handleTimer();
    }

    private void updateSignChangesDisplay() {
//        String signChanges = String.valueOf((matrix.getSign()));
//        signTextField.setText(signChanges);
    }

    private void setUpTriangularizationViews() {
        this.view = new TriangularizationView(matrixGrid);
    }

    private void handleTimer() {
//        int totalSteps = operations.getTotalSteps();
//        System.out.println("These are the total steps (handleTimer): \n" + totalSteps);
//
//        timeline = new Timeline(new KeyFrame(Duration.seconds(1), (ActionEvent t) -> {
//            if (totalSteps == 0) {
//                view.updateViews("initial");
//            } else {
//                String stepKey = "step_" + counter.getAndIncrement();
//                System.out.println("Animating: " + stepKey);
//                view.updateViews(stepKey);
//            }
//        }));
//
//        timeline.setCycleCount(totalSteps);
//
//        // Set the onFinished event handler for the timeline
//        timeline.setOnFinished(e -> {
//            // Create a PauseTransition for a delay
//            PauseTransition delay = new PauseTransition(Duration.seconds(3));
//            delay.setOnFinished(event -> displayConsoleSummary()); // Call your method to display the summary
//            delay.play();
//        });
//
//        start.setOnAction((t) -> timeline.play());
    }

    private void displayConsoleSummary() {
//        List<String> summary = operations.getOperationSummary();
//        System.out.println(summary);
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

    @FXML
    public void handleResetButton() {
        view.updateViews("initial");
        handleTimer();
    }

    @FXML
    public void handleSummarizeStepsButton() {
//        List<String> stepSummary = operations.getOperationSummary();
//        displaySummary(stepSummary);
    }

    public void displaySummary(List<String> summary) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Matrix Transformation Summary");
        alert.setHeaderText("Steps to Make Matrix Triangular");

        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setText(String.join("\n", summary));

        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
    }

    public void setMatrixGrid(GridPane matrixGrid) {
        this.matrixGrid = matrixGrid;
    }

    private void handleProgressBar() {
        if (!clockOn) {
            progressBar.setOpacity(75);
        }
        progressBar.setOpacity(40);
    }

    public void setMatrixCells (MatrixCell[][] matrixCells) {
        this.matrixCells = matrixCells;
    }

    public void uploadFromFile() {
        matrix = MatrixFileHandler.getMatrix("initial");
        setMatrixCells(matrixCells);
        setMatrixGrid(matrixGrid);
    }
}