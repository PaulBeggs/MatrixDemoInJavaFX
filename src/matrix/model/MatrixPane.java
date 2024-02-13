package matrix.model;

public class MatrixPane {
    private double x;
    private double y;
    private double dx;
    private double dy;
    private double length;
    private boolean canMove;

    public MatrixPane(double length) {
        this.length = length;

        canMove = true;
    }

    public void update() {
        x += dx;
        y += dy;
//        checkBounds();
    }

    // Method to reverse direction if the block hits the edge
//    private void checkBounds() {
//        if (x reaches left/right edge) {
//            dx *= -1; // Reverse X direction
//        }
//        if (y = ) {
//            dy *= -1; // Reverse Y direction
//        }
//    }
}
