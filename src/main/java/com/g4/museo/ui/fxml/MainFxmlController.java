package com.g4.museo.ui.fxml;

import com.g4.museo.MuseoApplication;
import com.g4.museo.event.AppReadyEvent;
import com.g4.museo.event.ArtworkRefreshedEvent;
import com.g4.museo.event.UserChangedEvent;
import com.g4.museo.persistence.dto.ArtworkFullDTO;
import com.g4.museo.persistence.dto.Collection;
import com.g4.museo.persistence.dto.ArtworkState;
import com.g4.museo.persistence.r2dbc.*;
import com.g4.museo.ui.utils.ErrorWindowFactory;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.time.format.DateTimeFormatter;
import java.util.*;

import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import com.gluonhq.charm.glisten.control.TextField;
import reactor.core.publisher.Flux;


@Component
public class MainFxmlController extends FXMLController implements Initializable {

    @Autowired
    ArtworkFullR2dbcDao artworkFullR2dbcDao;

    @Autowired
    CollectionR2dbcDao collectionR2dbcDao;

    @Autowired
    ArtworkR2dbcDao artworkR2dbcDao;

    @Autowired
    StateR2dbcDao stateR2dbcDao;

    @Autowired
    ConfigurableApplicationContext applicationContext;

    @Autowired
    ApplicationEventPublisher applicationEventPublisher;

    @FXML
    private Button loginButton;

    @FXML
    private Button managementButton;

    @FXML
    private Button logoutButton;

    @FXML
    private TableView<ArtworkFullDTO> artworkGrid;

    @FXML
    private ComboBox<String> collectionBox;

    @FXML
    private ComboBox<String> stateBox;

    @FXML
    private TextField artworkSearch;

    @FXML
    private TextField authorSearch;

    private List<ArtworkFullDTO> artworks = new ArrayList<>();

    private void populateArtworkGrid(){
        Flux<ArtworkFullDTO> flux = artworkFullR2dbcDao.findAllArtworks();
        TableColumn<ArtworkFullDTO, ImageView> picture = new TableColumn<>("Photo");
        TableColumn<ArtworkFullDTO, String> name = new TableColumn<>("Nom de l'oeuvre");
        TableColumn<ArtworkFullDTO, String> artist = new TableColumn<>("Nom de l'artiste");
        TableColumn<ArtworkFullDTO, String> date = new TableColumn<>("Date de création");
        TableColumn<ArtworkFullDTO, String> returnDate = new TableColumn<>("Date de rendu");
        TableColumn<ArtworkFullDTO, String> localisation = new TableColumn<>("Localisation");
        TableColumn<ArtworkFullDTO, String> state = new TableColumn<>("Statut");
        artworkGrid.getColumns().addAll(picture, name, artist, date, returnDate, localisation, state);
        artworkGrid.getColumns().forEach(c -> c.setStyle( "-fx-alignment: CENTER;"));
        flux.doOnComplete(() -> {
            artworkGrid.getItems().addAll(artworks);
            AtomicReference<ImageView> image = new AtomicReference<>();
            Subscriber<ByteBuffer> bytes = new Subscriber<ByteBuffer>() {
                @Override
                public void onSubscribe(Subscription subscription) {
                    subscription.request(1);
                }

                @Override
                public void onNext(ByteBuffer byteBuffer) {
                    var tempImage = new ImageView();
                    InputStream stream = new ByteArrayInputStream(byteBuffer.array());
                    tempImage.setImage(new Image(stream));
                    tempImage.setPreserveRatio(true);
                    tempImage.setSmooth(true);
                    tempImage.setCache(true);
                    tempImage.setFitHeight(100);
                    image.set(tempImage);
                }

                @Override
                public void onError(Throwable throwable) {
                    ErrorWindowFactory.create(new Exception(throwable));
                }

                @Override
                public void onComplete() {
                    //No operation necessary upon completion
                }
            };
            picture.setCellValueFactory(c -> {
                c.getValue().getArtwork().getPicture().stream().subscribe(bytes);
                return new SimpleObjectProperty<ImageView>(image.get());
            });
            name.setCellValueFactory(c-> new SimpleStringProperty(c.getValue().getArtwork().getName()));
            artist.setCellValueFactory(c-> new SimpleStringProperty(c.getValue().getArtwork().getAuthor()));
            date.setCellValueFactory(c-> new SimpleStringProperty(c.getValue().getArtwork().getDate().format(DateTimeFormatter.ISO_LOCAL_DATE)));
            returnDate.setCellValueFactory(c-> {
                if(c.getValue().getArtwork().isBorrowed()){
                    return new SimpleStringProperty(c.getValue().getArtworkBorrow().getReturnDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
                } else {
                    return new SimpleStringProperty("");
                }
            });
            localisation.setCellValueFactory(c-> new SimpleStringProperty(c.getValue().getArtwork().getStoredLocation()));
            state.setCellValueFactory(c-> new SimpleStringProperty(c.getValue().getArtworkState().getStateName()));
            applicationEventPublisher.publishEvent(new AppReadyEvent(this));
        }).log().subscribe(artworks::add);

    }

    private void populateComboBox(){
        Flux<Collection> fluxCollection = collectionR2dbcDao.findAllCollections();
        List<Collection> collectionList = new ArrayList<>();
        fluxCollection.doOnComplete(() -> {
            collectionBox.getItems().addAll(collectionList
                    .stream()
                    .map(Collection::getCollectionName)
                    .collect(Collectors.toList()));
            collectionBox.getItems().add(null);
        }).log().subscribe(collectionList::add);
        collectionBox.setOnAction(event -> {
            ComboBox<String> box = (ComboBox) event.getSource();
            if (box.getValue() != null) {
                FilteredList<ArtworkFullDTO> filteredData = new FilteredList<>(FXCollections.observableArrayList(artworks));
                filteredData.setPredicate(a -> {
                    Boolean res;
                    try{
                        res = a.getCollection().getCollectionName().equalsIgnoreCase(box.getValue());
                    } catch (NullPointerException e){
                        res = false;
                    }
                    return res;
                });
                artworkGrid.setItems(filteredData);
            } else {
                FilteredList<ArtworkFullDTO> filteredData = new FilteredList<>(FXCollections.observableArrayList(artworks));
                artworkGrid.setItems(filteredData);
            }
        });
        Flux<ArtworkState> fluxState = stateR2dbcDao.getAllStates();
        List<ArtworkState> stateList = new ArrayList<>();
        fluxState.doOnComplete(() -> stateBox.getItems().addAll(stateList
                .stream()
                .map(ArtworkState::getStateName)
                .collect(Collectors.toList()))).subscribe(stateList::add);
        stateBox.getItems().add(null);
        stateBox.setOnAction(event -> {
            ComboBox<String> box = (ComboBox) event.getSource();
            FilteredList<ArtworkFullDTO> filteredData = new FilteredList<>(FXCollections.observableArrayList(artworks));
            if(box.getValue()!=null){
                filteredData.setPredicate(a -> a.getArtworkState().getStateName()
                        .equalsIgnoreCase(box.getValue()));
            }
            artworkGrid.setItems(filteredData);
        });
    }

    private void initSearchBars(){
        artworkSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            FilteredList<ArtworkFullDTO> filteredData = new FilteredList<>(FXCollections.observableArrayList(artworks));
            if(!newValue.equals("")){
                filteredData.setPredicate(a -> a.getArtwork().getName()
                        .toLowerCase()
                        .startsWith(newValue.toLowerCase()));
            }
            artworkGrid.setItems(filteredData);
        });
        authorSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            FilteredList<ArtworkFullDTO> filteredData = new FilteredList<>(FXCollections.observableArrayList(artworks));
            if(!newValue.equals("")){
                filteredData.setPredicate(a -> a.getArtwork().getAuthor()
                        .toLowerCase()
                        .contains(newValue.toLowerCase()));
            }
            artworkGrid.setItems(filteredData);
        });

    }

    @FXML
    public void onLoginCalled(ActionEvent event){
        var loginStage = new Stage();
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

    @FXML
    public void onLogoutCalled(){
        SecurityContextHolder.clearContext();
        MuseoApplication.initAnonymous();
        applicationEventPublisher.publishEvent(new UserChangedEvent(this));
        var alertSuccessfulLogout = new Alert(Alert.AlertType.INFORMATION);
        alertSuccessfulLogout.setHeaderText("Successful Logout");
        alertSuccessfulLogout.setContentText("Successfully logged out");
        alertSuccessfulLogout.showAndWait();
    }

    @EventListener(UserChangedEvent.class)
    public void updateRoles(){
        if(SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))){
            managementButton.setVisible(true);
            loginButton.setVisible(false);
            logoutButton.setVisible(true);
        } else {
            managementButton.setVisible(false);
            loginButton.setVisible(true);
            logoutButton.setVisible(false);
        }
    }


    @EventListener(ArtworkRefreshedEvent.class)
    public void updateArtworks(){
        artworks.clear();
        Flux<ArtworkFullDTO> flux = artworkFullR2dbcDao.findAllArtworks();
        flux.doOnComplete(() ->  {
            FilteredList<ArtworkFullDTO> filteredData = new FilteredList<>(FXCollections.observableArrayList(artworks));
            artworkGrid.setItems(filteredData);
        }).subscribe(artworks::add);
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
        var managementStage = new Stage();
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
        var artworkStage = new Stage();
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

    @FXML
    public void onDisplayArtworkCalled(MouseEvent event){
        var displayartworkStage = new Stage();
        displayartworkStage.setTitle("Museo Diplay Artwork");
        displayartworkStage.setResizable(false);
        DisplayArtworkFxmlController displayartworkController = applicationContext.getBean(DisplayArtworkFxmlController.class);
        Scene displayartworkScene = null;
        try {
            if(displayartworkController.getView().getScene() == null){
                displayartworkScene = new Scene(displayartworkController.getView());
            } else if(displayartworkController.getView().getScene().getWindow().isShowing()) {
                return;
            } else {
                displayartworkScene = displayartworkController.getView().getScene();
            }
            displayartworkStage.setScene(displayartworkScene);
            displayartworkStage.show();
        } catch (IOException e) {
            ErrorWindowFactory.create(e);
        }
    }
}
