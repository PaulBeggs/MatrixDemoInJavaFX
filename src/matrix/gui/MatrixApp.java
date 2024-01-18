package matrix.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import matrix.fileManaging.FileUtil;

import java.io.IOException;
import java.util.Objects;

public class MatrixApp extends Application {
    public static Stage primaryStage;
    public static String selectedTheme = "light";
    public static boolean fractionMode = true;

    @Override
    public void start(Stage stage) {
        FileUtil.ensureAllFilesExist();
        primaryStage = stage;
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/matrix/gui/resources/themeScene.fxml")));
            Scene scene = new Scene(root);
            setupGlobalEscapeHandler(scene);
            applyTheme(scene);
            setFractionMode(fractionMode);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Select Theme");
            primaryStage.show();
            primaryStage.setOnCloseRequest(event -> closeApps());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Couldn't load the theme selection scene.");
        }
    }

    public static void setupGlobalEscapeHandler(Scene scene) {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                closeApps();
            }
        });
    }
    public static void applyTheme(Scene scene) {
        scene.getStylesheets().clear();
//        System.out.println("(Inside applyTheme) This is the selected theme: \n" + selectedTheme);
        String cssFile = selectedTheme.equals("dark") ? "/styles/darkMode.css" : "/styles/lightMode.css";
        scene.getStylesheets().add(Objects.requireNonNull(MatrixApp.class.getResource(cssFile)).toExternalForm());
    }
    public static Stage getPrimaryStage() {return primaryStage;}
    public static void setSelectedTheme(String theme) {selectedTheme = theme;}
    public static void setFractionMode(boolean modeBoolean) {fractionMode = modeBoolean;}
    public static boolean isFractionMode() {return fractionMode;}
    public static void closeApps() {Platform.exit(); System.exit(130);}
    public static void main(String[] args) {launch(args);}
}
