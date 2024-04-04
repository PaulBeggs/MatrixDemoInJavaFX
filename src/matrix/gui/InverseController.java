package matrix.gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import matrix.fileManaging.FilePath;
import matrix.fileManaging.MatrixFileHandler;
import matrix.fileManaging.MatrixType;
import matrix.model.*;
import matrix.util.ErrorsAndSyntax;
import matrix.view.MatrixView;

import java.io.IOException;
import java.util.List;

public class InverseController implements DataManipulation {
    @FXML
    TextField multiplier;
    @FXML
    TextField sourceRow;
    @FXML
    TextArea directions;
    @FXML
    TextField targetRow;
    @FXML
    Button invert;
    @FXML
    Button RREF;
    @FXML
    ChoiceBox<Scenes> scenes;
    @FXML
    GridPane matrixGrid;
    @FXML
    GridPane identityMatrixGrid;
    private MatrixView matrixView;

    @FXML
    private void initialize() {
        update();
        matrixView = new MatrixView(matrixGrid);
        setupScenesDropdown();
        setupDirectionText();
        Matrix matrix = MatrixSingleton.getInstance();
        matrixView.updateViews(true, matrix);
    }

    @FXML
    public void handleRefFunctionality() {
        Matrix matrix = MatrixSingleton.getInstance().copy();
        matrix.convertToEchelonForm();
        matrix = MatrixFileHandler.matrices.get("REF");
        matrixView.updateViews(false, matrix);
        save(FilePath.REF_PATH.getPath(), matrix, MatrixType.REGULAR);
    }

    @FXML
    public void handleRRefFunctionality() {
        Matrix matrix = MatrixSingleton.getInstance().copy();
        matrix.convertToReducedEchelonForm();
        matrix = MatrixFileHandler.matrices.get("RREF");
        matrixView.updateViews(false, matrix);
        save(FilePath.RREF_PATH.getPath(), matrix, MatrixType.REGULAR);
    }

    @FXML
    public void handleInvertingFunctionality() {
        Matrix matrix = MatrixSingleton.getInstance().copy();
        if (!matrix.isSquare()) {
            ErrorsAndSyntax.showErrorPopup("Matrix must be square.");
            throw new IllegalArgumentException("Matrix must be square.");
        }
        matrix.convertToInvertedForm();
        matrix = MatrixFileHandler.matrices.get("Inverse");
        matrixView.updateViews(false, matrix);
        save(FilePath.REF_PATH.getPath(), matrix, MatrixType.REGULAR);
    }

    @Override
    @FXML
    public void handleSaveButton() {

    }

    @FXML
    public void handleLoadButton() {

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

        // Initialize Matrix model with the loaded data
        Matrix matrix = MatrixSingleton.getInstance();

        // Ensure the shared matrix has the correct dimensions
        MatrixSingleton.resizeInstance(numRows, numCols);

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
        System.out.println("This is the matrix that is initially created: \n" + matrix);

        // Create MatrixCells with the initialized Matrix model
        MatrixCell[][] matrixCells = new MatrixCell[numRows][numCols];
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                String cellValue = matrixData.get(row).get(col);
                matrixCells[row][col] = new MatrixCell(row, col, cellValue, false);
            }
        }
    }

    private void save(String filePath, Matrix matrix, MatrixType matrixType) {
        List<List<String>> matrixData = MatrixFileHandler.loadMatrixDataFromMatrix(matrix);
        MatrixFileHandler.saveMatrixDataToFile(filePath, "0", matrixData, matrixType);
    }

    @Override
    public void setupScenesDropdown() {
        scenes.getItems().setAll(Scenes.values());
        scenes.setValue(Scenes.INVERSE);
        scenes.setTooltip(new Tooltip("Pick the scene"));
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

    private void setupDirectionText() {
        String initialDirections = """
                Click 'REF' to produce the Reduced Echelon Form of the matrix. Use the other buttons accordingly.""";
        directions.setText(initialDirections);
        directions.setWrapText(true);
        directions.setEditable(false);
    }

    @Override
    public void setToolTips() {

    }
}
