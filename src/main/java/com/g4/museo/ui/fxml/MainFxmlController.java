package com.g4.museo.ui.fxml;

import com.g4.museo.event.UserChangedEvent;
import com.g4.museo.persistence.dto.ArtworkDTO;
import com.g4.museo.persistence.jdbc.ArtworkJdbcDao;
import com.g4.museo.ui.utils.ErrorWindowFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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
    Button loginButton;

    @FXML
    Button managementButton;

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

    @EventListener(UserChangedEvent.class)
    public void updateRoles(){
        if(SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))){
            managementButton.setVisible(true);
            loginButton.setVisible(false);
        } else {
            managementButton.setVisible(false);
            loginButton.setVisible(true);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        updateRoles();
        populateArtworkGrid();
    }

    @FXML
    public void onManagementCalled(ActionEvent event){
        Stage managementStage = new Stage();
        managementStage.setTitle("Museo Management");
        managementStage.setResizable(false);
        ManagementFxmlController managementController = applicationContext.getBean(ManagementFxmlController.class);
        Scene managementScene = null;
        try {
            if(managementController.getView().getScene() == null){
                managementScene = new Scene(managementController.getView());
            } else if(managementController.getView().getScene().getWindow().isShowing()) {
                return;
            } else {
                managementScene = managementController.getView().getScene();
            }
            managementStage.setScene(managementScene);
            managementStage.show();
        } catch (IOException e) {
            ErrorWindowFactory.create(e);
        }
    }


    @FXML
    public void onArtworkCalled(ActionEvent event){
        Stage artworkStage = new Stage();
        artworkStage.setTitle("Museo Artwork");
        artworkStage.setResizable(false);
        ArtworkFxmlController artworkController = applicationContext.getBean(ArtworkFxmlController.class);
        Scene artworkScene = null;
        try {
            if(artworkController.getView().getScene() == null){
                artworkScene = new Scene(artworkController.getView());
            } else if(artworkController.getView().getScene().getWindow().isShowing()) {
                return;
            } else {
                artworkScene = artworkController.getView().getScene();
            }
            artworkStage.setScene(artworkScene);
            artworkStage.show();
        } catch (IOException e) {
            ErrorWindowFactory.create(e);
        }
    }
}
