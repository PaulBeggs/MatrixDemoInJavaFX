package matrix.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
    CheckBox showInfo, forceFractions;
    private static boolean isFraction = true, isLightMode = false;

    @FXML
    public void initialize() {
        updateTheme();
        setToolTips();
        startApp.setOnAction(event -> {
            MatrixApp.setMatrixApp(true);
            MatrixApp.setIsForceFractions(forceFractions.isSelected());
            MatrixApp.setShowInfo(showInfo.isSelected());
            try {
                switchToMainScene();
                if (showInfo.isSelected()) {
                    showInformationScene();
                }
            } catch (IOException e) {
                ErrorsAndSyntax.showErrorPopup("Cannot open the Main Scene.");
                throw new RuntimeException(e);
            }
        });
        openCalculator.setOnAction(event -> {
            MatrixApp.setMatrixApp(false);
            MatrixApp.setIsForceFractions(forceFractions.isSelected());
            MatrixApp.setShowInfo(showInfo.isSelected());
            try {
                switchToCalculatorScene();
                if (showInfo.isSelected()) {
                    showInformationScene();
                }
            } catch (IOException e) {
                ErrorsAndSyntax.showErrorPopup("Cannot open the Calculator Scene.");
                throw new RuntimeException(e);
            }
        });
    }

    @FXML
    public void changeTheme() {
        setLightMode(!isLightMode);
        updateTheme();
        MatrixApp.applyTheme(imgMode.getScene());
    }

    @FXML
    public void changeFraction() {
        setFraction(!isFraction);
        MatrixApp.setFractionMode(isFraction);
        updateFractionModeImage(isLightMode, isFraction);
        if (!isFraction) {
            forceFractions.setSelected(false);
            forceFractions.setDisable(true);
        } else {
            forceFractions.setDisable(false);
        }
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
        String imagePath = isLightMode ? "/matrix/gui/resources/coloredMoon.png" : "/matrix/gui/resources/coloredSun.png";
        imgMode.setImage(loadImage(imagePath));
    }

    private void updateFractionModeImage(boolean isLightMode, boolean isFraction) {
        String fractionImagePath = isLightMode
                ? (isFraction ? "/matrix/gui/resources/fraction.png" : "/matrix/gui/resources/decimal.png")
                : (isFraction ? "/matrix/gui/resources/fractionWhite.png" : "/matrix/gui/resources/decimalWhite.png");
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

    public void showInformationScene() throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/matrix/gui/resources/mainInformation.fxml")));
        Scene scene = new Scene(root);

        Stage infoStage = new Stage();
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                infoStage.close();
            }
        });

        MatrixApp.applyTheme(scene);
        infoStage.setTitle("Information");
        infoStage.setScene(scene);
        infoStage.show();
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
        openCalculator.setTooltip(new Tooltip("Launches side Calculator App"));
        startApp.setTooltip(new Tooltip("Launches main Matrix App"));
        showInfo.setTooltip(new Tooltip("Launches a scene with information"));
        forceFractions.setTooltip(new Tooltip("All decimals are presented as fractions"));
    }

    public static void setFraction(boolean fraction) {isFraction = fraction;}
    public void setLightMode(boolean lightMode) {isLightMode = lightMode;}
}
