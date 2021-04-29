package com.g4.museo.ui.fxml;

import com.g4.museo.persistence.dto.ArtworkDTO;
import com.g4.museo.persistence.jdbc.ArtworkJdbcDao;
import com.g4.museo.ui.utils.ErrorWindowFactory;
import com.g4.museo.ui.LoginInitializer;
import com.g4.museo.ui.ManagementInitializer;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TimeZone;

@Component
public class MainFxmlController extends FXMLController implements Initializable {

    @Autowired
    ArtworkJdbcDao artworkJdbcDao;

    @Autowired
    ConfigurableApplicationContext applicationContext;

    @FXML
    private TableView artworkGrid;

    public void populateArtworkGrid(){
        List<ArtworkDTO> artworks = artworkJdbcDao.getAllArtwork();
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        df.setTimeZone(TimeZone.getDefault());
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
        TableColumn<ArtworkDTO, String> state = new TableColumn<>("Statut");
        state.setCellValueFactory(c-> new SimpleStringProperty(c.getValue().getState()));
        artworkGrid.getColumns().addAll(name, artist, date, returnDate, localisation, state);
    }

    @FXML
    public void onLoginCalled(ActionEvent event){
        Stage loginStage = new Stage();
        loginStage.setTitle("Museo Login");
        loginStage.setResizable(false);
        LoginFxmlController loginController = applicationContext.getBean(LoginFxmlController.class);
        Scene loginScene = null;
        try {
                if(loginController.getView().getScene() == null){
                    loginScene = new Scene(loginController.getView());
                } else if(loginController.getView().getScene().getWindow().isShowing()) {
                    return;
                } else {
                    loginScene = loginController.getView().getScene();
                }
                loginStage.setScene(loginScene);
                loginStage.show();
        } catch (IOException e) {
            ErrorWindowFactory.create(e);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        populateArtworkGrid();
    }
    @FXML
    public void onManagementCalled(ActionEvent event){
        Scene scene = (Scene) ((Node) event.getSource()).getScene();
        Stage stage = (Stage)scene.getWindow();
        ManagementInitializer.onManagementEvent(stage);
    }
}
