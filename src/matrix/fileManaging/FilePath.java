package matrix.fileManaging;

public enum FilePath {
    REF_PATH("SavedMatrices/ref/ref_data.txt"),
    CALCULATOR_PATH("SavedMatrices/calculator/calculator_data.txt"),
    DETERMINANT_PATH("SavedMatrices/determinants/determinant_result.txt"),
//    EIGEN_PATH("SavedMatrices/eigen/eigen_data.txt"),
    IDENTITY_PATH("SavedMatrices/matrices/identity.txt"),
    INVERSE_PATH("SavedMatrices/inverse/inverse_data.txt"),
    MATRIX_PATH("SavedMatrices/matrices/matrix_data.txt"),
    RREF_PATH("SavedMatrices/rref/rref_data.txt"),
    TRIANGULAR_PATH("SavedMatrices/triangular/triangular_data.txt");

    private final String path;

    FilePath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}


