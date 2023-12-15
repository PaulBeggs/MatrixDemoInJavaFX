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
    INVERSE {
        @Override
        public String toString() {
            return "Inverse";
        }

        @Override
        public void switchScene(ActionEvent event) throws IOException {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("resources/inverseScene.fxml")));
            switchToScene(event, root);


        }
    },
    DETERMINANT {
        @Override
        public String toString() {
            return "Determinant";
        }

        @Override
        public void switchScene(ActionEvent event) throws IOException {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("resources/determinantScene.fxml")));
            switchToScene(event, root);
        }
    },
    RREF {
        @Override
        public String toString() {
            return "RREF";
        }

        @Override
        public void switchScene(ActionEvent event) throws IOException {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("resources/RREFScene.fxml")));
            switchToScene(event, root);
        }
    },
    MATRIX {
        @Override
        public String toString() {
            return "Updated Matrix";
        }

        @Override
        public void switchScene(ActionEvent event) throws IOException {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("resources/matrixGUI.fxml")));
            switchToScene(event, root);
        }
    };

    private Stage stage;
    private Scene scene;

    public abstract void switchScene(ActionEvent event) throws IOException;

    protected void switchToScene(ActionEvent event, Parent root) {
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
