package matrix.utility;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import matrix.gui.MatrixApp;

// Thanks, ChatGPT.
public class UniversalListeners {
    public static void addEnterKeyHandlerToChildren(Parent parent) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            if (node instanceof TextField) {
                node.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
                    if (event.getCode() == KeyCode.ENTER) {
                        // Move to the next TextField
                        Node nextNode = getNextFocusableNode(node, parent);
                        if (nextNode != null) {
                            nextNode.requestFocus();
                        }
                    }
                });
            } else if (node instanceof Parent) {
                // Recursively add handler to children of this parent
                addEnterKeyHandlerToChildren((Parent) node);
            }
        }
    }

    private static Node getNextFocusableNode(Node current, Parent root) {
        boolean next = false;
        for (Node node : root.getChildrenUnmodifiable()) {
            if (next && node.isFocusTraversable()) {
                return node;
            }
            if (node.equals(current)) {
                next = true;
            } else if (node instanceof Parent) {
                Node found = getNextFocusableNode(current, (Parent) node);
                if (found != null) {
                    return found;
                }
            }
        }
        return null; // If no next node is found, return null
    }

    public static void setupGlobalEscapeHandler(Scene scene) {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                MatrixApp.closeApps();
            }
        });
    }
}
