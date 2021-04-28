package com.g4.museo.ui.fxml;

import com.g4.museo.persistence.dto.ArtworkDTO;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class MainFxmlController {
    @FXML
    private TableView artworkGrid;

    public void populateArtworkGrid(List<ArtworkDTO> artworks){
        artworkGrid.getItems().addAll(artworks);
        TableColumn<ArtworkDTO, String> name = new TableColumn<>("Nom de l'oeuvre");
        name.setCellValueFactory(c-> new SimpleStringProperty(c.getValue().getName()));
        TableColumn<ArtworkDTO, String> artist = new TableColumn<>("Nom de l'artiste");
        artist.setCellValueFactory(c-> new SimpleStringProperty("Test"));
        artworkGrid.getColumns().addAll(name, artist);
    }

    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException {
        Parent login_page_parent = FXMLLoader.load(getClass().getClassLoader().getResource("login.fxml"));
        Scene login_page_scene = new Scene (login_page_parent);
        Stage app_stage = (Stage) ((Node)  event.getSource()).getScene().getWindow();
        app_stage.setScene(login_page_scene);
        app_stage.show();
    }

}
