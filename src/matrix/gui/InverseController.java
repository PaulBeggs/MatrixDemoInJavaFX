package matrix.gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import matrix.fileManaging.FilePath;
import matrix.fileManaging.MatrixFileHandler;
import matrix.model.*;
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
    private MatrixCell[][] matrixCells;
    private MatrixView matrixView;

    @FXML
    private void initialize() {
        update();
        matrixView = new MatrixView(matrixGrid, matrixCells);
        setupScenesDropdown();
        setupDirectionText();
        Matrix matrix = MatrixSingleton.getInstance();
        matrixView.updateViews(true, matrix);
    }

    @FXML
    public void handleRefFunctionality() {
//        Matrix matrix = MatrixSingleton.getInstance();
//        matrix.toEchelonForm();
//        Matrix matrix = MatrixFileHandler.getMatrix("REF");
//        matrixView.updateViews(false, matrix);
    }

    @FXML
    public void handleRRefFunctionality() {
//        MatrixInvertingOperations MIO = new MatrixInvertingOperations();
//        MIO.toReducedEchelonForm();
//        Matrix matrix = MatrixFileHandler.getMatrix("RREF");
//        matrixView.updateViews(false, matrix);
    }

    @FXML
    public void handleInvertingFunctionality() {
//        MatrixInvertingOperations MIO = new MatrixInvertingOperations();
//        MIO.invert();
//        Matrix matrix = MatrixFileHandler.getMatrix("inverse");
//        matrixView.updateViews(true, matrix);
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
        matrixCells = new MatrixCell[numRows][numCols];
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                String cellValue = matrixData.get(row).get(col);
                matrixCells[row][col] = new MatrixCell(row, col, cellValue, false);
            }
        }
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
                e.printStackTrace();
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
}
