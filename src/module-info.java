module MatrixDemoInJavaFX {
    requires javafx.fxml;
    requires javafx.controls;

    exports matrix.gui;
    exports matrix.model;
    exports matrix.util;
    exports matrix.fileManaging;
    exports matrix.view;
    opens matrix.gui;
    opens matrix.model;
    opens matrix.util;
    opens matrix.fileManaging;
    opens matrix.view;
}