package com.g4.museo.ui.fxml;

import com.g4.museo.event.ArtworkRefreshedEvent;
import com.g4.museo.event.UserChangedEvent;
import com.g4.museo.persistence.dto.ArtworkDTO;
import com.g4.museo.persistence.dto.CollectionDTO;
import com.g4.museo.persistence.dto.StateDTO;
import com.g4.museo.persistence.jdbc.ArtworkJdbcDao;
import com.g4.museo.persistence.jdbc.CollectionJdbcDao;
import com.g4.museo.persistence.jdbc.StateJdbcDao;
import com.g4.museo.ui.utils.ErrorWindowFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;
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

import java.util.stream.Collectors;

import com.gluonhq.charm.glisten.control.TextField;

@Component
public class MainFxmlController extends FXMLController implements Initializable {

    @Autowired
    ArtworkJdbcDao artworkJdbcDao;

    @Autowired
    CollectionJdbcDao collectionJdbcDao;

    @Autowired
    StateJdbcDao stateJdbcDao;

    @Autowired
    ConfigurableApplicationContext applicationContext;

    @FXML
    private Button loginButton;

    @FXML
    private Button managementButton;

    @FXML
    private TableView artworkGrid;

    @FXML
    private ComboBox collectionBox;

    @FXML
    private ComboBox stateBox;

    @FXML
    private TextField artworkSearch;

    @FXML
    private TextField authorSearch;

    private void populateArtworkGrid(){
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

    private void populateComboBox(){
        collectionBox.getItems().addAll(collectionJdbcDao.getCollections()
                .stream()
                .map(CollectionDTO::getColectionName)
                .collect(Collectors.toList()));
        collectionBox.getItems().add(null);
        collectionBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ComboBox box = (ComboBox) event.getSource();
                FilteredList<ArtworkDTO> filteredData = new FilteredList(FXCollections.observableArrayList(artworkJdbcDao.getAllArtwork()));
                if(box.getValue()!=null){
                    filteredData.setPredicate(a -> a.getCollectionName()
                            .equalsIgnoreCase(box.getValue().toString()));
                }
                artworkGrid.setItems(filteredData);
            }
        });
        stateBox.getItems().addAll(stateJdbcDao.getStates()
                .stream()
                .map(StateDTO::getStateName)
                .collect(Collectors.toList()));
        stateBox.getItems().add(null);
        stateBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ComboBox box = (ComboBox) event.getSource();
                FilteredList<ArtworkDTO> filteredData = new FilteredList(FXCollections.observableArrayList(artworkJdbcDao.getAllArtwork()));
                if(box.getValue()!=null){
                    filteredData.setPredicate(a -> a.getState()
                            .equalsIgnoreCase(box.getValue().toString()));
                }
                artworkGrid.setItems(filteredData);
            }
        });
    }

    private void initSearchBars(){
        artworkSearch.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                FilteredList<ArtworkDTO> filteredData = new FilteredList(FXCollections.observableArrayList(artworkJdbcDao.getAllArtwork()));
                if(!newValue.equals("")){
                    filteredData.setPredicate(a -> a.getName()
                            .toLowerCase()
                            .contains(newValue.toLowerCase()));
                }
                artworkGrid.setItems(filteredData);
            }
        });
        authorSearch.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                FilteredList<ArtworkDTO> filteredData = new FilteredList(FXCollections.observableArrayList(artworkJdbcDao.getAllArtwork()));
                if(!newValue.equals("")){
                    filteredData.setPredicate(a -> a.getAuthorName()
                            .toLowerCase()
                            .contains(newValue.toLowerCase()));
                }
                artworkGrid.setItems(filteredData);
            }
        });

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


    @EventListener(ArtworkRefreshedEvent.class)
    public void updateArtworks(){
        artworkGrid.getItems().removeAll();
        artworkGrid.getItems().addAll(artworkJdbcDao.getAllArtwork());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        updateRoles();
        populateArtworkGrid();
        populateComboBox();
        initSearchBars();
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
