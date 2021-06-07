package com.g4.museo.ui.utils;

import javafx.scene.control.Alert;

public class AlertWindowFactory {
    public static void create(String header, String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
