package matrix.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import matrix.fileManaging.FilePath;
import matrix.fileManaging.MatrixFileHandler;
import matrix.fileManaging.MatrixType;
import matrix.model.Matrix;
import matrix.model.MatrixCell;
import matrix.model.MatrixSingleton;
import matrix.util.ErrorsAndSyntax;
import matrix.util.ExpressionEvaluator;
import matrix.view.MatrixView;

import java.io.IOException;
import java.util.List;

public class DeterminantController implements DataManipulation {
    @FXML
    BorderPane borderPane;
    @FXML
    Button saveButton;
    @FXML
    TextField determinantValue;
    @FXML
    GridPane matrixGrid;
    @FXML
    ChoiceBox<Scenes> scenes;
    @FXML
    TextArea directions;
    @FXML
    CheckBox checkBox;
    private MatrixView matrixView;
    private MatrixCell[][] matrixCells;
    private String determinant;
    private final String initialDirections =
            """
                    Additive:\s
                    det(B) = det(A)\s
                    \s
                    Interchangeability:\s
                    det(B) = -det(A);\s
                    \s
                    Scalar Multiplication:\s
                    det(B) = k * det(A)""";

    @FXML
    private void initialize() {
        update();
        matrixView = new MatrixView(matrixGrid, matrixCells);
        setupDirectionText();
        setupScenesDropdown();
        Matrix matrix = MatrixSingleton.getInstance();
        matrixView.updateViews(true, matrix);
    }

    private void setupDirectionText() {
        directions.setText(initialDirections);
        directions.setWrapText(true);
        directions.setEditable(false);
    }

    @Override
    public void setupScenesDropdown() {
        scenes.getItems().setAll(Scenes.values());
        scenes.setValue(Scenes.DETERMINANT);
        scenes.setTooltip(new Tooltip("Pick the scene"));
        scenes.setOnAction(event -> {
            Scenes selectedScene = scenes.getValue();
            try {
                selectedScene.switchScene(event);
            } catch (IOException e) {
                ErrorsAndSyntax.showErrorPopup("Cannot load the scene.");
                throw new IllegalArgumentException(e);
            }
        });
    }

    @FXML
    public void handleDeterminantFunctionality() {
        try {
            Matrix matrix = MatrixSingleton.getInstance();
            if (matrix.isSquare()) {
                this.determinant = (matrix.calculateDeterminant());
                determinantValue.setText((determinant));
                save();
                matrixView.updateViews(true, MatrixSingleton.getTriangularInstance());
            } else {
                updateDirections();
                ErrorsAndSyntax.showErrorPopup("Matrix is not square; it does not have a defined determinant.");
            }
        } catch (IllegalArgumentException e) {
            e.getMessage();
            return;
        }

        if (checkBox.isSelected()) {
            showDeterminantAnimation();
        }
    }


    @Override
    @FXML
    public void handleSaveButton() {
        if (determinant == null) {
            ErrorsAndSyntax.showErrorPopup("Cannot save a matrix with an undefined determinant using this option.");
            return;
        }
        if (Double.compare(ExpressionEvaluator.evaluate(determinant), 0) == 0) {
            determinantValue.setText("0.0");
            determinant = String.valueOf(0.0);
        }
        Stage saveStage = new Stage();
        saveStage.setTitle("Save Menu");
        saveStage.initModality(Modality.WINDOW_MODAL);
        saveStage.initOwner(MatrixApp.getPrimaryStage());

        FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/SaveScene.fxml"));
        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            ErrorsAndSyntax.showErrorPopup("Cannot load the scene.");
            throw new IllegalArgumentException(e);
        }

        Scene saveScene = new Scene(root);
        saveScene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                saveStage.close();
            }
        });

        SaveController saveController = loader.getController();
        saveController.setDeterminantValue((determinant));

        MatrixApp.applyTheme(saveScene);
        saveStage.setScene(saveScene);
        saveStage.showAndWait();
    }

    private void showDeterminantAnimation() {
        Stage animationStage = new Stage();
        animationStage.setTitle("Determinant Animation");
        animationStage.initModality(Modality.WINDOW_MODAL);
        animationStage.initOwner(MatrixApp.getPrimaryStage());

        FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/determinantAnimation.fxml"));
        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            ErrorsAndSyntax.showErrorPopup("Cannot load the scene.");
            throw new IllegalArgumentException(e);
        }

        DeterminantPopUpController determinantPopUpController = loader.getController();
        determinantPopUpController.setMatrixCells(matrixCells);
        determinantPopUpController.setMatrixGrid(matrixGrid);

        Scene animationScene = new Scene(root);
        MatrixApp.setupGlobalEscapeHandler(animationScene);
        MatrixApp.applyTheme(animationScene);
        animationStage.setScene(animationScene);
        animationStage.show();
    }

    @Override
    public void update() {
        List<List<String>> matrixData = MatrixFileHandler.loadMatrixDataFromFile(FilePath.MATRIX_PATH.getPath());

        if (!matrixData.isEmpty()) {
            populateMatrixUI(matrixData);
        } else {
            MatrixFileHandler.populateMatrixIfEmpty();
        }
    }


    private void populateMatrixUI(List<List<String>> matrixData) {
        int numRows = matrixData.size();
        int numCols = matrixData.getFirst().size();

        // Initialize your Matrix model with the loaded data
        Matrix matrix = MatrixSingleton.getInstance();

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                try {
                    String cellValue = matrixData.get(row).get(col);
                    matrix.setValue(row, col, cellValue);
                } catch (NumberFormatException e) {
                    MatrixFileHandler.populateMatrixIfEmpty();
                }
            }
        }

        // Create MatrixCells with the initialized Matrix model
        matrixCells = new MatrixCell[numRows][numCols];
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                String cellValue = matrixData.get(row).get(col);
                matrixCells[row][col] = new MatrixCell(row, col, cellValue, true);
            }
        }
    }

    private void updateDirections() {
        directions.setText("Matrix is not square; it does not have a defined determinant.");
//
//        // Schedule resetting the directions text area after 6 seconds
//        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
//        executorService.schedule(() -> {
//            Platform.runLater(() -> directions.setText(initialDirections));
//            executorService.shutdown(); // Important to shut down the executor to prevent resource leaks
//        }, 7, TimeUnit.SECONDS);
    }

    private void save() {
        List<List<String>> matrixData = MatrixFileHandler.loadMatrixDataFromMatrix(MatrixSingleton.getInstance());

        MatrixFileHandler.saveMatrixDataToFile(
                FilePath.DETERMINANT_PATH.getPath(), determinant,
                matrixData, MatrixType.DETERMINANT);
        MatrixFileHandler.saveMatrixDataToFile(FilePath.TRIANGULAR_PATH.getPath(),
                "0", matrixData, MatrixType.TRIANGULAR);
    }
}