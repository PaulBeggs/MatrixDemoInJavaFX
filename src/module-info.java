module MatrixGUI_2 {
    requires javafx.fxml;
    requires javafx.controls;

    exports matrix.gui;
    exports matrix.model;
    exports matrix.operators;
    exports matrix.fileManaging;
    opens matrix.gui;
    opens matrix.model;
    opens matrix.operators;
    opens matrix.fileManaging;
}