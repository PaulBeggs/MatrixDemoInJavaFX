package matrix.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

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
    private boolean isLightMode = true;
    private boolean isFraction = true;

    @FXML
    public void initialize() {
        MatrixApp.setSelectedTheme("light");
        MatrixApp.setFractionMode(isFraction);

        startApp.setOnAction(event -> {
            switchToMainScene();
        });
    }

    @FXML
    public void changeTheme() {
        isLightMode = !isLightMode;
        if (isLightMode) {
            MatrixApp.setSelectedTheme("light");
            imgMode.setImage(new Image(Objects.requireNonNull(getClass().getResource("/matrix/gui/resources/colored moon.png")).toExternalForm()));
            if (isFraction) {
                fractionMode.setImage(new Image(Objects.requireNonNull(getClass().getResource("/matrix/gui/resources/fraction.png")).toExternalForm()));
            } else {
                fractionMode.setImage(new Image(Objects.requireNonNull(getClass().getResource("/matrix/gui/resources/decimal.png")).toExternalForm()));
            }
        } else {
            MatrixApp.setSelectedTheme("dark");
            imgMode.setImage(new Image(Objects.requireNonNull(getClass().getResource("/matrix/gui/resources/colored sun.png")).toExternalForm()));
            if (isFraction) {
                fractionMode.setImage(new Image(Objects.requireNonNull(getClass().getResource("/matrix/gui/resources/fraction white.png")).toExternalForm()));
            } else {
                fractionMode.setImage(new Image(Objects.requireNonNull(getClass().getResource("/matrix/gui/resources/decimal white.png")).toExternalForm()));
            }
        }
        MatrixApp.applyTheme(imgMode.getScene());
    }

    @FXML
    public void changeFraction() {
        isFraction = !isFraction;
        if (isFraction) {
            MatrixApp.setFractionMode(true);
            if (isLightMode) {
                fractionMode.setImage(new Image(Objects.requireNonNull(getClass().getResource("/matrix/gui/resources/fraction.png")).toExternalForm()));
            } else {
                fractionMode.setImage(new Image(Objects.requireNonNull(getClass().getResource("/matrix/gui/resources/fraction white.png")).toExternalForm()));
            }
        } else {
            MatrixApp.setFractionMode(true);
            if (isLightMode) {
                fractionMode.setImage(new Image(Objects.requireNonNull(getClass().getResource("/matrix/gui/resources/decimal.png")).toExternalForm()));
            } else {
                fractionMode.setImage(new Image(Objects.requireNonNull(getClass().getResource("/matrix/gui/resources/decimal white.png")).toExternalForm()));
            }
        }
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
