package matrix.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import matrix.util.ErrorsAndSyntax;

import java.io.IOException;
import java.util.Objects;

public class ThemeController {

    @FXML
    ImageView imgMode, fractionMode;
    @FXML
    Button startApp, openCalculator, themeBtnMode, fractionBtnMode;
    @FXML
    CheckBox showInfo, convertSquareRoots;
    private boolean isLightMode = false, isFraction = true;

    @FXML
    public void initialize() {
        updateTheme();
        setToolTips();
        startApp.setOnAction(event -> {
            try {
                switchToMainScene();
            } catch (IOException e) {
                ErrorsAndSyntax.showErrorPopup("Cannot open the Main Scene.");
                throw new RuntimeException(e);
            }
        });
        openCalculator.setOnAction(event -> {
            try {
                switchToCalculatorScene();
            } catch (IOException e) {
                ErrorsAndSyntax.showErrorPopup("Cannot open the Calculator Scene.");
                throw new RuntimeException(e);
            }
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

    private void switchToMainScene() throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/matrix/gui/resources/matrixGUI.fxml")));
        Scene scene = new Scene(root);

        Stage primaryStage = getStage(scene);

        primaryStage.setTitle("Matrix GUI");

        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private void switchToCalculatorScene() throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/matrix/gui/resources/calculatorScene.fxml")));
        Scene scene = new Scene(root);

        Stage primaryStage = getStage(scene);

        primaryStage.setTitle("Calculator");

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Stage getStage(Scene scene) {
        MatrixApp.setupGlobalEscapeHandler(scene);
        MatrixApp.applyTheme(scene);
        MatrixApp.setFractionMode(isFraction);
        return MatrixApp.getPrimaryStage();
    }

    private void setToolTips() {
        themeBtnMode.setTooltip(new Tooltip("Select Light or Dark theme"));
        fractionBtnMode.setTooltip(new Tooltip("Decimals or fractions"));
    }
}
