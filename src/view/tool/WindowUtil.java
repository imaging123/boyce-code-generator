package view.tool;

import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

/**
 * @author boyce - 02/18/2018
 */
public class WindowUtil {

    public static void showInfoDialog(String info) {
        Dialog dialog = new Dialog();
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
        closeButton.managedProperty().bind(closeButton.visibleProperty());
        closeButton.setVisible(false);
        dialog.setTitle("Information");
        dialog.setContentText(info);
        dialog.showAndWait();
    }
}
