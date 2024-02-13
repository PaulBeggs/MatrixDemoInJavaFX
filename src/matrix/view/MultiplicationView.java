package matrix.view;

import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import matrix.model.MatrixInfo;

import java.util.*;
import java.util.stream.Collectors;

public class MultiplicationView {
    private Pane mainPane;
    private Map<MatrixInfo, List<MatrixInfo>> connectedMatrices;
    private Map<Pane, MatrixInfo> matrixInfoMap;
    private int counter = 0;
    private List<Pane> allMatrices;


    public MultiplicationView(Pane pane, Map<MatrixInfo, List<MatrixInfo>> connectedMatrices,  Map<Pane, MatrixInfo> matrixInfoMap, List<Pane> allMatrices) {
        this.mainPane = pane;
        this.connectedMatrices = connectedMatrices;
        this.matrixInfoMap = matrixInfoMap;
        this.allMatrices = allMatrices;

        // Initialize some matrices to begin with.
        Platform.runLater(() -> {
            createMatrixPane();
            createMatrixPane();
            createMatrixPane();
        });
    }

    public void updateViews() {

    }
    
    public void resetMatrices() {
        
    }

    public void createMatrixPane() {
        if (mainPane == null) {
            throw new IllegalArgumentException("Main pane is null");
        }

        double mainPaneWidth = mainPane.getWidth();
        double mainPaneHeight = mainPane.getHeight();
        System.out.println("This is the main pane width and height: " + mainPaneWidth + ", " + mainPaneHeight);

        Pane matrixPane = new Pane();
        matrixPane.setStyle("-fx-border-color: black; -fx-background-color: lightgray;");
        matrixPane.setPrefSize(55, 55);

        // Ensure the matrix is within the bounds of the mainPane pane
        double maxX = mainPaneWidth - matrixPane.getPrefWidth();
        double maxY = mainPaneHeight - matrixPane.getPrefHeight();

        // Calculate random positions within the bounds of the mainPane
        double x = (Math.random() * maxX);
        double y = (Math.random() * maxY);

        System.out.println("This is where the panes will be positioned (x,y):" + x + ", " + y);

        matrixPane.setLayoutX(x);
        matrixPane.setLayoutY(y);

        matrixPane.setOnMousePressed(event -> {
            markMatrixAsInteracted(matrixPane);
            matrixPane.setUserData(new double[]{event.getSceneX() - matrixPane.getLayoutX(),
                    event.getSceneY() - matrixPane.getLayoutY()});
        });
        matrixPane.setOnMouseDragged(event -> {
            double[] offset = (double[]) matrixPane.getUserData();
            matrixPane.setLayoutX(event.getSceneX() - offset[0]);
            matrixPane.setLayoutY(event.getSceneY() - offset[1]);
        });
        matrixPane.setOnMouseReleased(event -> {
            // Snap logic
            snapToClosestMatrix(matrixPane, allMatrices);

            // Update connections if the matrix is dragged apart
            updateConnectionsAfterDrag(matrixPane);
        });

        // Create and assign MatrixInfo
        counter++;
        String matrixLabel = "Matrix #" + counter;
        MatrixInfo matrixInfo = new MatrixInfo(matrixLabel);
        matrixInfoMap.put(matrixPane, matrixInfo); // Store MatrixInfo in the map
        connectedMatrices.put(matrixInfo, new ArrayList<>()); // Initialize connection list

        Text label = new Text(10, 20, matrixLabel);
        matrixPane.getChildren().add(label);

        // Set focus to the matrixPane to ensure key events work
        matrixPane.setFocusTraversable(true);
        matrixPane.requestFocus();

        attachClickHandler(matrixPane);

        mainPane.getChildren().add(matrixPane);
        allMatrices.add(matrixPane);
    }

    private void snapToClosestMatrix(Pane currentMatrix, List<Pane> allMatrices) {
        boolean snapped = performSnap(currentMatrix, allMatrices);

        if (!snapped) {
            // Update connections if the matrix is dragged apart
            updateConnectionsAfterDrag(currentMatrix);
        }

        // Check for further snapping opportunities
        allMatrices.stream()
                .filter(matrix -> matrix != currentMatrix)
                .forEach(adjacentMatrix -> performSnap(adjacentMatrix, allMatrices));

        // Update and print the current matrix multiplication orders
        printAllClusters();
    }


    private boolean performSnap(Pane matrix, List<Pane> allMatrices) {
        MatrixInfo matrixInfo = matrixInfoMap.get(matrix);
        if (matrixInfo != null && !matrixInfo.hasBeenInteractedWith()) {
            return false; // Skip snapping if the matrix hasn't been interacted with
        }

        Pane closestMatrix = null;
        double minDistance = Double.MAX_VALUE;

        for (Pane potentialNeighbor : allMatrices) {
            if (potentialNeighbor != matrix) {
                double distance = calculateDistance(matrix, potentialNeighbor);
                if (distance < minDistance) {
                    minDistance = distance;
                    closestMatrix = potentialNeighbor;
                }
            }
        }

        if (closestMatrix != null && minDistance <= 75.0) {
            double newX;
            boolean spaceOccupied = false;

            if (matrix.getLayoutX() > closestMatrix.getLayoutX()) {
                // Snap to the right of closestMatrix
                newX = closestMatrix.getLayoutX() + closestMatrix.getWidth();
            } else {
                // Snap to the left of closestMatrix
                newX = closestMatrix.getLayoutX() - matrix.getWidth();
            }

            // Check if the space is already occupied
            for (Pane potentialOccupant : allMatrices) {
                if (potentialOccupant != matrix && potentialOccupant.getLayoutX() == newX) {
                    spaceOccupied = true;
                    break;
                }
            }

            if (!spaceOccupied) {
                matrix.setLayoutX(newX);
                matrix.setLayoutY(closestMatrix.getLayoutY());

                // Update connections
                updateConnectedMatrices(matrix, closestMatrix);

                // Debugging statements
                MatrixInfo closestMatrixInfo = matrixInfoMap.get(closestMatrix);
                System.out.println("Matrix " + matrixInfo + " snapped to " + closestMatrixInfo);

                return true; // Indicates that a snap occurred
            }
        }
        return false; // Indicates that no snap occurred
    }


    private double calculateDistance(Pane matrix1, Pane matrix2) {
        double xDiff = matrix1.getLayoutX() - matrix2.getLayoutX();
        double yDiff = matrix1.getLayoutY() - matrix2.getLayoutY();
        return Math.sqrt(xDiff * xDiff + yDiff * yDiff);
    }

    private void attachClickHandler(Pane matrix) {
        matrix.setOnContextMenuRequested(event -> removeMatrixAndUpdateConnections(matrix));
    }


    private void updateConnectedMatrices(Pane matrix1, Pane matrix2) {
        MatrixInfo info1 = matrixInfoMap.get(matrix1);
        MatrixInfo info2 = matrixInfoMap.get(matrix2);

        connectedMatrices.computeIfAbsent(info1, k -> new ArrayList<>());
        connectedMatrices.computeIfAbsent(info2, k -> new ArrayList<>());

        if (!connectedMatrices.get(info1).contains(info2)) {
            connectedMatrices.get(info1).add(info2);
        }

        if (!connectedMatrices.get(info2).contains(info1)) {
            connectedMatrices.get(info2).add(info1);
        }
    }

    private Map<Integer, Set<Pane>> findAllConnectedGroups() {
        Set<MatrixInfo> visited = new HashSet<>();
        Map<Integer, Set<Pane>> groups = new HashMap<>();
        int groupId = 0;
        final double SNAP_THRESHOLD = 75;

        for (Pane matrix : allMatrices) {
            MatrixInfo matrixInfo = matrixInfoMap.get(matrix);
            if (!visited.contains(matrixInfo)) {
                Set<Pane> group = new HashSet<>();
                dfs(matrix, visited, group, SNAP_THRESHOLD);
                if (group.size() > 1) { // Only consider groups with more than one matrix
                    groups.put(groupId++, group);
                }
            }
        }
        return groups;
    }

    private void dfs(Pane currentMatrix, Set<MatrixInfo> visited, Set<Pane> group, double snapThreshold) {
        MatrixInfo currentInfo = matrixInfoMap.get(currentMatrix);
        visited.add(currentInfo);
        group.add(currentMatrix);

        List<MatrixInfo> neighbors = connectedMatrices.get(currentInfo);
        if (neighbors != null) {
            for (MatrixInfo neighbor : neighbors) {
                if (!visited.contains(neighbor)) {
                    Pane neighborPane = allMatrices.stream()
                            .filter(p -> matrixInfoMap.get(p).equals(neighbor))
                            .findFirst()
                            .orElse(null);
                    if (neighborPane != null && calculateDistance(currentMatrix, neighborPane) <= snapThreshold) {
                        dfs(neighborPane, visited, group, snapThreshold);
                    }
                }
            }
        }
    }


    public Map<Integer, List<String>> getMatrixMultiplicationOrders() {
        Map<Integer, Set<Pane>> groups = findAllConnectedGroups();
        Map<Integer, List<String>> multiplicationOrders = new HashMap<>();

        for (Map.Entry<Integer, Set<Pane>> entry : groups.entrySet()) {
            Integer groupId = entry.getKey();
            Set<Pane> group = entry.getValue();

            // Exclude groups with only one matrix
            if (group.size() > 1) {
                List<String> order = group.stream()
                        .sorted(Comparator.comparing(Pane::getLayoutX))
                        .map(matrixInfoMap::get)
                        .map(MatrixInfo::title)
                        .collect(Collectors.toList());

                multiplicationOrders.put(groupId, order);
            }
        }

        return multiplicationOrders;
    }

    private void removeMatrixAndUpdateConnections(Pane matrix) {
        allMatrices.remove(matrix);
        mainPane.getChildren().remove(matrix);

        // Remove the matrix from connectedMatrices and update connections
        MatrixInfo removedMatrixInfo = matrixInfoMap.get(matrix);
        if (removedMatrixInfo != null) {
            // Remove connections from other matrices to this matrix
            for (Map.Entry<MatrixInfo, List<MatrixInfo>> entry : connectedMatrices.entrySet()) {
                entry.getValue().remove(removedMatrixInfo);
            }

            // Remove this matrix from connectedMatrices
            connectedMatrices.remove(removedMatrixInfo);
        }

        matrixInfoMap.remove(matrix); // Remove the matrix from matrixInfoMap

        // Update matrix multiplication orders immediately after removal
        printAllClusters();
    }

    private void printAllClusters() {
        Map<Integer, List<String>> clusters = getMatrixMultiplicationOrders();
        if (clusters.isEmpty()) {
            System.out.println("No clusters found.");
            return;
        }

        for (Map.Entry<Integer, List<String>> entry : clusters.entrySet()) {
            Integer clusterId = entry.getKey();
            List<String> matricesInCluster = entry.getValue();
            System.out.println("Cluster " + clusterId + ": " + matricesInCluster);
        }
    }


    private void updateConnectionsAfterDrag(Pane matrix) {
        final double SNAP_THRESHOLD = 75; // Use the same threshold as for snapping
        MatrixInfo matrixInfo = matrixInfoMap.get(matrix);

        // Retrieve the connected matrices list, or initialize as empty if null
        List<MatrixInfo> connected = connectedMatrices.get(matrixInfo);
        if (connected == null) {
            connected = new ArrayList<>();
        } else {
            connected = new ArrayList<>(connected);
        }

        // Check and update connections
        for (MatrixInfo connectedMatrixInfo : connected) {
            Pane connectedMatrix = allMatrices.stream()
                    .filter(p -> matrixInfoMap.get(p).equals(connectedMatrixInfo))
                    .findFirst()
                    .orElse(null);
            if (connectedMatrix != null && calculateDistance(matrix, connectedMatrix) > SNAP_THRESHOLD) {
                // Remove the connection if matrices are too far apart
                connectedMatrices.get(matrixInfo).remove(connectedMatrixInfo);
                connectedMatrices.get(connectedMatrixInfo).remove(matrixInfo);
            }
        }
    }


    private void markMatrixAsInteracted(Pane matrix) {
        MatrixInfo currentInfo = matrixInfoMap.get(matrix);
        if (currentInfo != null && !currentInfo.hasBeenInteractedWith()) {
            matrixInfoMap.put(matrix, currentInfo.withInteraction());
        }
    }

    public double getPaneXBounds() {
        return mainPane.getHeight();
    }

    public double getPaneYBounds() {
        return mainPane.getWidth();
    }
}
