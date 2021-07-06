package com.g4.museo.ui.utils;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class OwnerEditWindowFactory {
    public static Optional<Map<String, String>> create(String header){
        Dialog<Map<String, String>> dialog = new Dialog<>();
        Map<String, String> map = new HashMap<>();
        dialog.setHeaderText(header);

        ButtonType confirmButtonType = new ButtonType("Valider", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, buttonTypeCancel);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField firstname = new TextField();
        firstname.setPromptText("Prénom");
        TextField lastname = new TextField();
        lastname.setPromptText("Nom");
        TextField orga = new TextField();
        orga.setPromptText("Organisation");
        TextField address = new TextField();
        address.setPromptText("Adresse");


        grid.add(new Label("Prénom:"), 0, 0);
        grid.add(firstname, 1, 0);
        grid.add(new Label("Nom:"), 0, 1);
        grid.add(lastname, 1, 1);
        grid.add(new Label("Organisation:"), 0, 2);
        grid.add(orga, 1, 2);
        grid.add(new Label("Adresse:"), 0, 3);
        grid.add(address, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType) {
                map.put("firstname", firstname.getText());
                map.put("lastname", lastname.getText());
                map.put("orga", orga.getText());
                map.put("address", address.getText());
                return map;
            }
            return null;
        });
        return dialog.showAndWait();
    }
}
