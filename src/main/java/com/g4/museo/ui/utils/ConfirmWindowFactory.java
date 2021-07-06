package com.g4.museo.ui.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class ConfirmWindowFactory {
    public static Optional<ButtonType> create(String header, String message){
        var alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(header);
        alert.setContentText(message);

        ButtonType buttonTypeConfirm= new ButtonType("Confirmer");
        ButtonType buttonTypeCancel = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeConfirm, buttonTypeCancel);

        return alert.showAndWait();
    }
}
