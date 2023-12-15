package matrix.gui;

public enum FilePath {
    TRIANGULAR_PATH("SavedMatrices/triangular/triangular_data.txt"),
    DETERMINANT_PATH("SavedMatrices/determinants/determinant_result.txt"),
    MATRIX_PATH("SavedMatrices/matrices/matrix_data.txt");

    private final String path;

    FilePath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}

