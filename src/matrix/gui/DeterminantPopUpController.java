package matrix.gui;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import matrix.model.Matrix;
import matrix.model.MatrixCell;
import matrix.view.TriangularizationView;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static matrix.fileManaging.MatrixFileHandler.matrices;

public class DeterminantPopUpController {
    @FXML
    Button start, stop;
    @FXML
    ProgressBar progressBar;
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

    private void setUpTriangularizationViews() {
        this.view = new TriangularizationView(matrixGrid);
    }

    private void handleTimer() {
        matrix.calculateDeterminant();
        int totalSteps = matrix.getTotalSteps();
        System.out.println("These are the total steps (handleTimer): \n" + totalSteps);

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), (ActionEvent t) -> {
            if (totalSteps == 0) {
                view.updateViews("initial");
            } else {
                String stepKey = "Matrix #" + (counter.getAndIncrement() + 1);
                System.out.println("Animating: " + stepKey);
                view.updateViews(stepKey);
            }
        }));

        timeline.setCycleCount(totalSteps - 1);

        // Set the onFinished event handler for the timeline
        timeline.setOnFinished(e -> {
            // Create a PauseTransition for a delay
            PauseTransition delay = new PauseTransition(Duration.seconds(3));
            delay.setOnFinished(event -> displayConsoleSummary()); // Call your method to display the summary
            delay.play();
        });

        start.setOnAction((t) -> timeline.play());
    }

    private void displayConsoleSummary() {
        List<String> summary = matrix.getOperationSummary();
        System.out.println(summary);
    }

    @FXML
    public void start() {
        timeline.play();
        clockOn = true;
    }

    @FXML
    public void stop() {
        timeline.stop();
        clockOn = false;
    }

    @FXML
    public void handleResetButton() {
        if (timeline != null) {
            timeline.stop(); // Stop the current timeline if it's running
        }
        counter.set(0); // Reset the counter to 0

        matrix = matrices.get("initial");
        view.updateViews("initial");
        handleTimer(); // Reinitialize and start the timer
    }


    @FXML
    public void handleSummarizeStepsButton() {
        List<String> stepSummary = matrix.getOperationSummary();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Matrix Transformation Summary");
        alert.setHeaderText("Steps to Make Matrix Triangular");

        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setText(String.join("\n", stepSummary));

        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
    }


    public void setMatrixGrid(GridPane matrixGrid) {
        this.matrixGrid = matrixGrid;
    }

    public void setMatrixCells (MatrixCell[][] matrixCells) {
        this.matrixCells = matrixCells;
    }

    public void uploadFromFile() {
        matrix = matrices.get("initial");
        setMatrixCells(matrixCells);
        setMatrixGrid(matrixGrid);
    }
}