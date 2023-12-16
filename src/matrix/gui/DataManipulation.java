package matrix.gui;

public interface DataManipulation {
    void handleSaveButton();
    void update();
    void setInitMatrixData(String fileName);
    void saveToFile();
    void setupAutoSave();
}
