package matrix.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import matrix.gui.MatrixApp;
import matrix.utility.UniversalListeners;

import java.io.IOException;
import java.util.Objects;

public enum Scenes {
    /*
     This enum is important because it allows brevity within the other classes.
     The "toString" method is simply there to accompany the choices within the box.
     */
    INVERSE {
        @Override
        public String toString() {
            return "Inverse";
        }

        @Override
        public String getTitle() {
            return "Inversion Operation";
        }

        @Override
        public void switchScene(ActionEvent event) throws IOException {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("resources/inverseScene.fxml")));
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
    RREF {
        @Override
        public String toString() {
            return "RREF";
        }

        @Override
        public String getTitle() {
            return "RREF Operation";
        }

        @Override
        public void switchScene(ActionEvent event) throws IOException {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("resources/RREFScene.fxml")));
            switchToScene(event, root, getTitle());
        }
    },
    MATRIX {
        @Override
        public String toString() {
            return "Updated Matrix";
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
    };

    public abstract void switchScene(ActionEvent event) throws IOException;
    public abstract String getTitle();

    protected void switchToScene(ActionEvent event, Parent root, String title) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);

        UniversalListeners.setupGlobalEscapeHandler(scene);
        UniversalListeners.addEnterKeyHandlerToChildren(root);

        MatrixApp.applyTheme(scene);

        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }
}
