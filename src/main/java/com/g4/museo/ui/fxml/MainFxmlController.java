package com.g4.museo.ui.fxml;

import com.g4.museo.persistence.dto.ArtworkDTO;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

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
}
