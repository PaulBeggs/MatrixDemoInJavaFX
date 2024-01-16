package matrix.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import matrix.fileManaging.FilePath;
import matrix.utility.FileUtil;
import matrix.utility.UniversalListeners;

import java.io.IOException;
import java.util.Objects;

public class ThemeController {

    @FXML
    ImageView imgMode;
    @FXML
    Button startApp;
    @FXML
    Button themeBtnMode;
    @FXML
    ImageView fractionMode;
    @FXML
    Button fractionBtnMode;
    private boolean isLightMode = false;
    private boolean isFraction = true;

    @FXML
    public void initialize() {
        updateTheme();
        startApp.setOnAction(event -> {
            switchToMainScene();
        });
    }

    @FXML
    public void changeTheme() {
        isLightMode = !isLightMode;
        updateTheme();
        MatrixApp.applyTheme(imgMode.getScene());
    }

    @FXML
    public void changeFraction() {
        isFraction = !isFraction;
        MatrixApp.setFractionMode(isFraction);
        updateFractionModeImage(isLightMode, isFraction);
    }

    private void updateTheme() {
        String theme = isLightMode ? "light" : "dark";
        setTheme(theme);
        updateModeImage(isLightMode);
        updateFractionModeImage(isLightMode, isFraction);
    }

    private void setTheme(String theme) {
        MatrixApp.setSelectedTheme(theme);
    }

    private void updateModeImage(boolean isLightMode) {
        String imagePath = isLightMode ? "/matrix/gui/resources/colored moon.png" : "/matrix/gui/resources/colored sun.png";
        imgMode.setImage(loadImage(imagePath));
    }

    private void updateFractionModeImage(boolean isLightMode, boolean isFraction) {
        String fractionImagePath = isLightMode
                ? (isFraction ? "/matrix/gui/resources/fraction.png" : "/matrix/gui/resources/decimal.png")
                : (isFraction ? "/matrix/gui/resources/fraction white.png" : "/matrix/gui/resources/decimal white.png");
        fractionMode.setImage(loadImage(fractionImagePath));
    }

    private Image loadImage(String path) {
        return new Image(Objects.requireNonNull(getClass().getResource(path)).toExternalForm());
    }

    private void switchToMainScene() {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/matrix/gui/resources/matrixGUI.fxml")));
            Scene scene = new Scene(root);

            UniversalListeners.addEnterKeyHandlerToChildren(root);
            UniversalListeners.setupGlobalEscapeHandler(scene);

            MatrixApp.applyTheme(scene);
            MatrixApp.setFractionMode(isFraction);
            FileUtil.ensureNumbersAreUpdated(FilePath.MATRIX_PATH.getPath());
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
