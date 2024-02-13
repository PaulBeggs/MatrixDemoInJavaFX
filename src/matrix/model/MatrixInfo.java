package matrix.model;

public record MatrixInfo(String title, boolean hasBeenInteractedWith) {

    public MatrixInfo(String title) {
        this(title, false);
    }

    public MatrixInfo withInteraction() {
        return new MatrixInfo(this.title, true);
    }
}
