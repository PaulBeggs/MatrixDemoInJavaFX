package matrix.gui;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.util.Duration;
import matrix.fileManaging.FileUtil;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;

public class MatrixApp extends Application {
    public static Stage primaryStage;
    public static String selectedTheme = "light";

    @Override
    public void start(Stage stage) {
        FileUtil.ensureAllFilesExist();
        primaryStage = stage;
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/matrix/gui/resources/themeScene.fxml")));
            Scene scene = new Scene(root);
            setupGlobalEscapeHandler(scene);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Select Theme");
            primaryStage.show();
            primaryStage.setOnCloseRequest(event -> closeApps());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Couldn't load the theme selection scene.");
        }
    }


    public static void applyTheme(Scene scene) {
        scene.getStylesheets().clear();
        System.out.println("(Inside applyTheme) This is the selected theme: \n" + selectedTheme);
        String cssFile = selectedTheme.equals("dark") ? "/styles/darkMode.css" : "/styles/lightMode.css";
        scene.getStylesheets().add(Objects.requireNonNull(MatrixApp.class.getResource(cssFile)).toExternalForm());
    }


    public static Stage getPrimaryStage() {
        System.out.println("This is the primary stage: \n" + primaryStage);
        return primaryStage;
    }

    public static void setSelectedTheme(String theme) {
        System.out.println("(Inside setSelectedScene) This is the selected theme: \n" + selectedTheme);
        selectedTheme = theme;
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void setupGlobalEscapeHandler(Scene scene) {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                closeApps();
            }
        });
    }

    public static void closeApps() {
        Platform.exit(); // Immediately close the UI
        System.exit(130); // Exit the JVM
    }
}
