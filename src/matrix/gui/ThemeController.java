package matrix.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
import matrix.model.Matrix;

import java.io.IOException;
import java.util.Objects;

public class ThemeController {

    @FXML
    private Button lightBtnMode;
    @FXML
    private Button darkBtnMode;

    @FXML
    public void initialize() {
        lightBtnMode.setOnAction(event -> {
            MatrixApp.setSelectedTheme("light");
            switchToMainScene();
        });

        darkBtnMode.setOnAction(event -> {
            MatrixApp.setSelectedTheme("dark");
            switchToMainScene();
        });
    }

    private void switchToMainScene() {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/matrix/gui/resources/matrixGUI.fxml")));
            Scene scene = new Scene(root);

            MatrixApp.setupGlobalEscapeHandler(scene);
            MatrixApp.applyTheme(scene);
            Stage primaryStage = MatrixApp.getPrimaryStage();

            primaryStage.setTitle("Matrix GUI");

            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Couldn't load the main scene.");
        }
    }
}
