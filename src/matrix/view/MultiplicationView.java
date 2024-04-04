package matrix.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import matrix.gui.InitializeMatrixController;
import matrix.gui.MatrixApp;
import matrix.gui.MatrixSelectionCallback;
import matrix.model.Matrix;
import matrix.util.ErrorsAndSyntax;
import matrix.view.paneInMyAss.MatrixConnection;
import matrix.view.paneInMyAss.PaneAssociation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MultiplicationView implements MatrixSelectionCallback {
    private Pane mainPane;
    private List<PaneAssociation> paneAssociations;
    private List<MatrixConnection> matrixConnections;
    private List<Pane> allMatrices;
    private int counter = 0;

    public MultiplicationView(Pane pane) {
        this.mainPane = pane;
        this.matrixConnections = new ArrayList<>(); // Initialize internally
        this.paneAssociations = new ArrayList<>(); // Initialize internally
        this.allMatrices = new ArrayList<>();
    }
    
    public void resetMatrices() {
        
    }

    @Override
    public void onMatrixSelected(Matrix matrix) {
        createMatrixPane(matrix);
    }

    public void createMatrixPane(Matrix selectedMatrix) {
        if (mainPane == null) {
            throw new IllegalArgumentException("Main pane is null");
        }

        Pane matrixPane = new Pane();
        matrixPane.getStyleClass().add("matrix-pane");

        // Dynamically calculate grid positions instead of random placement
        int gridX = counter % 5; // Example grid layout: 5 columns
        int gridY = counter / 10; // Adjust '5' based on your needs or dynamic calculations
        double paneWidth = 100; // Set fixed or dynamic size
        double paneHeight = 100; // Set fixed or dynamic size

        double x = gridX * (paneWidth);
        double y = gridY * (paneHeight); // Adjust margins as needed

        matrixPane.setLayoutX(x);
        matrixPane.setLayoutY(y);
        matrixPane.setPrefSize(paneWidth, paneHeight);

        // Set up mouse drag for moving panes around
        setupDraggableMatrixPane(matrixPane);

        // Add context menu for more interactivity
        attachContextMenu(matrixPane);

        // Add to mainPane and keep track in allMatrices
        mainPane.getChildren().add(matrixPane);
        allMatrices.add(matrixPane);

        counter++; // Increment counter to keep track of the next pane's grid position
    }

    private void attachContextMenu(Pane matrixPane) {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem removeItem = new MenuItem("Remove");
        removeItem.setOnAction(e -> removeMatrixPane(matrixPane));

        MenuItem editItem = new MenuItem("Edit");
        editItem.setOnAction(e -> callEditMethod(matrixPane));

        MenuItem loadItem = new MenuItem("Load from File");
        loadItem.setOnAction(e -> callLoadMethod(matrixPane));

        MenuItem viewItem = new MenuItem("View");
        editItem.setOnAction(e -> callViewMethod(matrixPane));

        contextMenu.getItems().addAll(removeItem, editItem, loadItem, viewItem);

        matrixPane.setOnContextMenuRequested(event -> {
            contextMenu.show(matrixPane, event.getScreenX(), event.getScreenY());
        });
    }

    private void setupDraggableMatrixPane(Pane matrixPane) {
        matrixPane.setOnMousePressed(event -> {
            // Store initial mouse click position relative to the pane
            matrixPane.setUserData(new double[]{event.getSceneX() - matrixPane.getLayoutX(),
                    event.getSceneY() - matrixPane.getLayoutY()});
        });

        matrixPane.setOnMouseDragged(event -> {
            // Allow free dragging without snapping
            double[] offset = (double[]) matrixPane.getUserData();
            matrixPane.setLayoutX(event.getSceneX() - offset[0]);
            matrixPane.setLayoutY(event.getSceneY() - offset[1]);
        });

        matrixPane.setOnMouseReleased(event -> {
            // Snap to the closest pane upon release
            snapToClosestPane(matrixPane);
        });
    }

    private void snapToClosestPane(Pane matrixPane) {
        final double snapThreshold = 70;
        Pane closestPane = null;
        double closestDistance = Double.MAX_VALUE;
        boolean shouldSnapToLeft = false;
        double deltaX = 0, deltaY = 0; // Declare deltaX here for scope visibility

        for (Pane otherPane : allMatrices) {
            if (otherPane == matrixPane) continue; // Skip the current pane

            // Calculate distances
            deltaX = matrixPane.getLayoutX() - otherPane.getLayoutX();
            deltaY = matrixPane.getLayoutY() - otherPane.getLayoutY();
            double distance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));

            if (distance < closestDistance) {
                closestPane = otherPane;
                closestDistance = distance;
                shouldSnapToLeft = deltaX < 0; // Determine the snap direction based on X-axis
            }
        }

        // Check if both deltaX and deltaY are within the snapThreshold
        if (closestPane != null && Math.abs(deltaX) <= snapThreshold && Math.abs(deltaY) <= snapThreshold) {
            // Snap logic requires both X and Y to be within threshold
            if (shouldSnapToLeft) {
                // Snap to the left side of the closest pane
                matrixPane.setLayoutX(closestPane.getLayoutX() - matrixPane.getWidth());
            } else {
                // Snap to the right side of the closest pane
                matrixPane.setLayoutX(closestPane.getLayoutX() + closestPane.getWidth());
            }
            matrixPane.setLayoutY(closestPane.getLayoutY()); // Keep Y position aligned
        }
        updateViews();
    }




    private void removeMatrixPane(Pane matrixPane) {
        // Remove the pane from the display
        mainPane.getChildren().remove(matrixPane);

        // Remove the pane from allMatrices list
        allMatrices.remove(matrixPane);

        // Remove associated connections from matrixConnections list
        // This assumes that you have updated MatrixConnection to directly relate to Pane objects.
        matrixConnections.removeIf(connection -> connection.getPane().equals(matrixPane));

        // Remove associations from paneAssociations list
        // Now considering the updated PaneAssociation class that might handle more than two panes.
        paneAssociations.forEach(association -> association.removePane(matrixPane));
        paneAssociations.removeIf(association -> association.getPanes().isEmpty());

        updateViews(); // Refresh UI or perform necessary cleanup
    }


    private void editMatrix(Pane matrixPane) {

    }

    private void callLoadMethod(Pane matrix) {

    }

    private void callEditMethod(Pane matrix) {
        Stage initStage = new Stage();
        initStage.setTitle("Matrix Selection");
        initStage.initModality(Modality.WINDOW_MODAL);
        initStage.initOwner(MatrixApp.getPrimaryStage());

        FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/initializeMatrixScene.fxml"));
        Parent root;
        try {
            root = loader.load();

            InitializeMatrixController controller = loader.getController();
            controller.setSelectionCallback(selectedMatrix -> {
                // Here, update matrixPane with selectedMatrix
                // placeholder:
                Pane matrixPane = null;
                updateMatrixPane(matrixPane, selectedMatrix);
            });

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

    private void callViewMethod(Pane matrix) {

    }

    private void updateMatrixPane(Pane matrixPane, Matrix selectedMatrix) {

    }

    public void updateViews() {
        // First, sort panes to prioritize which ones to move in case of overlap
        allMatrices.sort(Comparator.comparingDouble(Pane::getLayoutX).thenComparingDouble(Pane::getLayoutY));

        for (int i = 0; i < allMatrices.size(); i++) {
            Pane currentPane = allMatrices.get(i);
            for (int j = i + 1; j < allMatrices.size(); j++) {
                Pane comparePane = allMatrices.get(j);
                if (panesOverlap(currentPane, comparePane) && !arePanesAssociated(currentPane, comparePane)) {
                    // If panes overlap and are not associated, adjust position
                    adjustPanePosition(comparePane, currentPane);
                }
            }
        }
    }

    private boolean panesOverlap(Pane pane1, Pane pane2) {
        return pane1.getBoundsInParent().intersects(pane2.getBoundsInParent());
    }

    private boolean arePanesAssociated(Pane pane1, Pane pane2) {
        // Check if there's an association that contains both panes
        return paneAssociations.stream().anyMatch(association ->
                association.getPanes().contains(pane1) && association.getPanes().contains(pane2));
    }


    private void adjustPanePosition(Pane paneToAdjust, Pane referencePane) {
        // Logic to adjust pane position based on referencePane and prevent overlap
        // This is a simplified example; you might need a more complex logic
        double newX = referencePane.getLayoutX() + referencePane.getWidth(); // Example adjustment
        double newY = referencePane.getLayoutY(); // Keep Y the same; adjust as necessary
        paneToAdjust.setLayoutX(newX);
        paneToAdjust.setLayoutY(newY);
    }

}
