package com.g4.museo.ui.fxml;

import com.g4.museo.persistence.dto.ArtworkDTO;
import com.g4.museo.persistence.jdbc.ArtworkJdbcDao;
import com.g4.museo.ui.LoginInitializer;
import com.g4.museo.ui.ManagementInitializer;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicReference;

import com.g4.museo.events.LoginCalledEvent;

@Component
public class MainFxmlController {
    private ConfigurableApplicationContext applicationContext;

    @FXML
    private TableView artworkGrid;

    public void populateArtworkGrid(List<ArtworkDTO> artworks, ArtworkJdbcDao artworkJdbcDao){
        TimeZone tz = TimeZone.getDefault();
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        df.setTimeZone(tz);
        artworkGrid.getItems().addAll(artworks);
        TableColumn<ArtworkDTO, String> name = new TableColumn<>("Nom de l'oeuvre");
        name.setCellValueFactory(c-> new SimpleStringProperty(c.getValue().getName()));
        TableColumn<ArtworkDTO, String> artist = new TableColumn<>("Nom de l'artiste");
        artist.setCellValueFactory(c-> new SimpleStringProperty(c.getValue().getAuthorName()));
        TableColumn<ArtworkDTO, String> date = new TableColumn<>("Date de création");
        date.setCellValueFactory(c-> new SimpleStringProperty(df.format(c.getValue().getDate())));
        TableColumn<ArtworkDTO, String> returnDate = new TableColumn<>("Date de rendu");
        returnDate.setCellValueFactory(c-> {
            if(c.getValue().isBorrowed()){
                return new SimpleStringProperty(df.format(artworkJdbcDao.getReturnDateByID(c.getValue().getIdartwork())));
            } else {
                return new SimpleStringProperty("");
            }
        });
        TableColumn<ArtworkDTO, String> localisation = new TableColumn<>("Localisation");
        localisation.setCellValueFactory(c-> new SimpleStringProperty(c.getValue().getStoredLocation()));
        artworkGrid.getColumns().addAll(name, artist, date, returnDate, localisation);
    }

    @FXML
    public void onLoginCalled(ActionEvent event){
        Scene scene = (Scene) ((Node) event.getSource()).getScene();
        Stage stage = (Stage)scene.getWindow();
        LoginInitializer.onLoginEvent(stage);
    }

    @FXML
    public void onManagementCalled(ActionEvent event){
        Scene scene = (Scene) ((Node) event.getSource()).getScene();
        Stage stage = (Stage)scene.getWindow();
        ManagementInitializer.onManagementEvent(stage);
    }
}
