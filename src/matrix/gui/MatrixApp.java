package matrix.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import matrix.fileManaging.FileUtil;

import java.util.Objects;

public class MatrixApp extends Application {
    private static Stage primaryStage;



    @Override
    public void start(Stage stage) {
        FileUtil.ensureAllFilesExist();
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("resources/matrixGUI.fxml"), "FXML resource is null"));

            Scene scene = new Scene(root);
            primaryStage = stage;
            stage.setTitle("Matrix GUI");

            stage.setScene(scene);
            stage.show();
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("Couldn't load the scene.");
        }
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }


        public static void main(String[] args) {
        launch(args);
    }
}
