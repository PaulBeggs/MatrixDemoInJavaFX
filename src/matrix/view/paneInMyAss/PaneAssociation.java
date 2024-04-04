package matrix.view.paneInMyAss;

import javafx.scene.layout.Pane;

import java.util.LinkedList;
import java.util.List;

public class PaneAssociation {

    //    Purpose: To manage the associations between panes in terms of UI order and to facilitate the correct sequence
    //    of matrix operations based on the spatial arrangement of panes.

    //    Structure: This could be a list or sequence that maintains the order of panes as they are intended to be
    //    multiplied. Given the requirement for order, you might consider a structure like LinkedList<Pane> or even a
    //    more complex structure that maintains pairs of associated panes and their operation sequence, depending on how
    //    complex the operation logic is.

    private List<Pane> panes = new LinkedList<>();

    // Add a pane to the association
    public void addPane(Pane pane) {
        panes.add(pane);
    }

    // Remove a pane from the association
    public void removePane(Pane pane) {
        panes.remove(pane);
    }

    public List<Pane> getPanes() {
        return panes;
    }

    // Method to check if a pane is part of this association
    public boolean contains(Pane pane) {
        return panes.contains(pane);
    }
}
