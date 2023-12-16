package matrix.gui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import matrix.fileManaging.FilePath;
import matrix.fileManaging.MatrixFileHandler;
import matrix.model.*;
import javafx.fxml.FXML;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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
    GridPane matrixGrid = new GridPane();
    private List<List<TextField>> matrixTextFields;
    private Matrix matrix;
    private MatrixView matrixView;
    private TriangularizationView tV;
    private boolean clockOn;
    private Stage stage;
    Timeline timeline;
    AtomicInteger counter = new AtomicInteger();

    @FXML
    private void initialize() {
        matrix = MatrixFileHandler.getMatrix(FilePath.MATRIX_PATH.getPath());

        matrixTextFields = new ArrayList<>();
        matrixView = new MatrixView(matrix, matrixGrid, matrixTextFields);
        setStage(stage);

        matrix = MatrixFileHandler.getMatrix(FilePath.MATRIX_PATH.getPath());

        tV = new TriangularizationView(matrix);
        setMatrixTextFields(matrixTextFields);

        tV.setMatrixView(matrixView);




        timeline = new Timeline(new KeyFrame(Duration.seconds(1), (ActionEvent t) -> {

            System.out.println(counter.get());
            matrix.setValue(counter.get(), 0, counter.getAndIncrement());

            for (int i = 0; i < matrix.getRows(); i++) {
                TextField tempTextField = (TextField) matrixGrid.getChildren().get(i);
                tempTextField.setText(Double.toString(matrix.getValue(i, 0)));
            }
        }));

        timeline.setCycleCount(matrix.getRows());

        start.setOnAction((t) -> {
            timeline.play();
        });

        loadFromFile();
    }

    public void loadFromFile() {
        populateMatrixFromData(FilePath.MATRIX_PATH.getPath());
        start();
    }

    public void populateMatrixFromData(String filePath) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            int numRows = 0;
            int numCols = 0;

            while (br.readLine() != null) {
                numRows++;
            }

            br.close();
            br = new BufferedReader(new FileReader(filePath));

            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split("\\s+");
                numCols = Math.max(numCols, values.length);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    public void setMatrixView(MatrixView matrixView) {
        this.matrixView = matrixView;
    }

    @FXML
    private void handleSignTextFieldUpdates() {
        signTextField.setText(String.valueOf(tV.getSign()));
    }

    @FXML
    private void handleProgressBar() {
        if (!clockOn) {
            progressBar.setOpacity(100);
        }
        progressBar.setOpacity(0);
    }

//
//    private void updateUIForStep(int step) {
//        double[][] matrixAtStep = determinantCalc.getMatrixAtStep(step);
//        matrixView.updateMatrixFromUI(matrixAtStep);
//    }

        public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setMatrixTextFields(List<List<TextField>> matrixTextFields) {
        this.matrixTextFields = matrixTextFields;
    }

    private void showErrorPopup() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("An error occurred!");

        alert.showAndWait();

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.close();
    }
}
