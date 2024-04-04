module MatrixDemoInJavaFX {
    requires javafx.fxml;
    requires javafx.controls;
    requires junit;

    exports matrix.util to junit;

    exports matrix.model;
//    exports matrix.util;
    exports matrix.fileManaging;
    exports matrix.view;
    opens matrix.gui;
    opens matrix.model;
    opens matrix.util;
    opens matrix.fileManaging;
    opens matrix.view;
    exports matrix.view.paneInMyAss;
    opens matrix.view.paneInMyAss;
}