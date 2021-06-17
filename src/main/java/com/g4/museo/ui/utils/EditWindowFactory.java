package com.g4.museo.ui.utils;

import javafx.scene.control.TextInputDialog;

import java.util.Optional;

public class EditWindowFactory {
    public static Optional<String> create(String header, String message){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText(header);
        dialog.setContentText(message);
        return dialog.showAndWait();
    }
}
