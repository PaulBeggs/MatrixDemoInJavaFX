module MatrixGUI_2 {
    requires javafx.fxml;
    requires javafx.controls;

    exports matrix.gui;
    exports matrix.model;
    opens matrix.gui;
    opens matrix.model;
    exports matrix.operators;
    opens matrix.operators;
    exports matrix.fileManaging;
    opens matrix.fileManaging;
}