package matrix.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public enum Scenes {
    /*
     This enum is important because it allows brevity within the other classes.
     The "toString" method is simply there to accompany the choices within the box.
     */

    MATRIX {
        @Override
        public String toString() {
            return "Main Matrix";
        }

        @Override
        public String getTitle() {
            return "Matrix GUI";
        }

        @Override
        public void switchScene(ActionEvent event) throws IOException {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("resources/matrixGUI.fxml")));
            switchToScene(event, root, getTitle());
        }
    },

    MULTIPLICATION {
        @Override
        public String toString() {
            return "Multiply Matrix";
        }
        @Override
        public String getTitle() {
            return "Matrix Multiplication";
        }
        @Override
        public void switchScene(ActionEvent event) throws IOException {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("resources/multiplicationScene.fxml")));
            switchToScene(event, root, getTitle());
        }
    },

    SOLVE {
        @Override
        public String toString() {
            return "Matrix Solver";
        }
        @Override
        public String getTitle() {
            return "Matrix Solver";
        }
        @Override
        public void switchScene(ActionEvent event) throws IOException {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("resources/matrixSolver.fxml")));
            switchToScene(event, root, getTitle());
        }
    },

    DETERMINANT {
        @Override
        public String toString() {
            return "Determinant";
        }

        @Override
        public String getTitle() {
            return "Determinant Operation";
        }

        @Override
        public void switchScene(ActionEvent event) throws IOException {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("resources/determinantScene.fxml")));
            switchToScene(event, root, getTitle());
        }
    },

    INVERSE {
        @Override
        public String toString() {
            return "REF/Invert";
        }

        @Override
        public String getTitle() {
            return "REF, RREF, & Inverting Operations";
        }

        @Override
        public void switchScene(ActionEvent event) throws IOException {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("resources/inverseScene.fxml")));
            switchToScene(event, root, getTitle());


        }
    },

    BASES {
        @Override
        public String toString() {
            return "Bases";
        }

        @Override
        public String getTitle() {
            return "Bases Operations";
        }

        @Override
        public void switchScene(ActionEvent event) throws IOException {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("resources/basesScene.fxml")));
            switchToScene(event, root, getTitle());
        }
    },

    EIGEN {
        @Override
        public String toString() {
            return "Eigen";
        }
        @Override
        public String getTitle() {
            return "Eigenvalues and Eigenvectors";
        }
        @Override
        public void switchScene(ActionEvent event) throws IOException {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("resources/eigenScene.fxml")));
            switchToScene(event, root, getTitle());
        }
    };

    public abstract void switchScene(ActionEvent event) throws IOException;
    public abstract String getTitle();

    protected void switchToScene(ActionEvent event, Parent root, String title) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        MatrixApp.setupGlobalEscapeHandler(scene);

        stage.setTitle(title);
        MatrixApp.applyTheme(scene);

        stage.setScene(scene);
        stage.show();
    }
}
