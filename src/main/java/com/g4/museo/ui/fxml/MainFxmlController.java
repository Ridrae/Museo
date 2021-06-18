package com.g4.museo.ui.fxml;

import com.g4.museo.MuseoApplication;
import com.g4.museo.event.*;
import com.g4.museo.persistence.dto.ArtworkFull;
import com.g4.museo.persistence.dto.Collection;
import com.g4.museo.persistence.dto.ArtworkState;
import com.g4.museo.persistence.r2dbc.*;
import com.g4.museo.ui.utils.AlertWindowFactory;
import com.g4.museo.ui.utils.ConfirmWindowFactory;
import com.g4.museo.ui.utils.ErrorWindowFactory;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;
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
    StateR2dbcDao stateR2dbcDao;

    @Autowired
    ConfigurableApplicationContext applicationContext;

    @Autowired
    ApplicationEventPublisher applicationEventPublisher;

    @FXML
    private Button managementButton;

    @FXML
    private TableView<ArtworkFull> artworkGrid;

    @FXML
    private ComboBox<String> collectionBox;

    @FXML
    private ComboBox<String> stateBox;

    @FXML
    private TextField artworkSearch;

    @FXML
    private TextField authorSearch;

    @FXML
    private RadioButton returnRadio;

    @FXML
    private RadioButton urgentReturnRadio;

    @FXML
    private ImageView refreshButton;

    @FXML
    private Button deleteButton;

    private List<ArtworkFull> artworks = new ArrayList<>();
    List<Collection> collectionList = new ArrayList<>();
    List<ArtworkState> stateList = new ArrayList<>();

    private void populateArtworkGrid(){
        Flux<ArtworkFull> flux = artworkFullR2dbcDao.findAll();
        TableColumn<ArtworkFull, ImageView> picture = new TableColumn<>("Photo");
        TableColumn<ArtworkFull, String> name = new TableColumn<>("Nom de l'oeuvre");
        TableColumn<ArtworkFull, String> artist = new TableColumn<>("Nom de l'artiste");
        TableColumn<ArtworkFull, String> date = new TableColumn<>("Date de création");
        TableColumn<ArtworkFull, String> returnDate = new TableColumn<>("Date de rendu");
        TableColumn<ArtworkFull, String> localisation = new TableColumn<>("Localisation");
        TableColumn<ArtworkFull, String> state = new TableColumn<>("Statut");
        artworkGrid.getColumns().addAll(picture, name, artist, date, returnDate, localisation, state);
        artworkGrid.getColumns().forEach(c -> c.setStyle( "-fx-alignment: CENTER;"));
        flux.doOnComplete(() -> {
            artworkGrid.getItems().addAll(artworks);
            artworkGrid.setRowFactory(new Callback<>() {
                @Override
                public TableRow<ArtworkFull> call(TableView<ArtworkFull> personTableView) {
                    TableRow<ArtworkFull> row = new TableRow<>() {
                        @Override
                        protected void updateItem(ArtworkFull artworkFull, boolean b) {
                            super.updateItem(artworkFull, b);
                            ObservableList<String> styles = getStyleClass();
                            if (artworkFull!=null) {
                                if(artworkFull.getReturnDate().isEqual(LocalDate.now()) || artworkFull.getReturnDate().isBefore(LocalDate.now())){
                                    if (!styles.contains("toReturnUrgent")) {
                                        getStyleClass().add("toReturnUrgent");
                                    }
                                } else if (artworkFull.getReturnDate().isBefore(LocalDate.now().plusMonths(1)) || artworkFull.getReturnDate().equals(LocalDate.now().plusMonths(1))) {
                                    if (!styles.contains("toReturn")) {
                                        getStyleClass().add("toReturn");
                                    }
                                }else{
                                    styles.removeAll(Collections.singleton("toReturn"));
                                    styles.removeAll(Collections.singleton("toReturnUrgent"));
                                }
                            } else {return;}
                        }
                    };
                    row.setOnMouseClicked(event -> {
                        if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                            ArtworkFull rowData = row.getItem();
                            onDisplayArtworkCalled(rowData);
                        }
                    });
                    return row;
                }
            });
            picture.setCellValueFactory(c -> {
                var tempImage = new ImageView();
                tempImage.setImage(c.getValue().getImage());
                tempImage.setPreserveRatio(true);
                tempImage.setSmooth(true);
                tempImage.setCache(true);
                tempImage.setFitHeight(100);
                return new SimpleObjectProperty<>(tempImage);
            });
            name.setCellValueFactory(c-> new SimpleStringProperty(c.getValue().getName()));
            artist.setCellValueFactory(c-> new SimpleStringProperty(c.getValue().getAuthor()));
            date.setCellValueFactory(c-> new SimpleStringProperty(c.getValue().getDate().format(DateTimeFormatter.ISO_LOCAL_DATE)));
            returnDate.setCellValueFactory(c-> {
                if(c.getValue().isBorrowed()){
                    return new SimpleStringProperty(c.getValue().getReturnDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
                } else {
                    return new SimpleStringProperty("");
                }
            });
            localisation.setCellValueFactory(c-> new SimpleStringProperty(c.getValue().getStoredLocation()));
            state.setCellValueFactory(c-> new SimpleStringProperty(c.getValue().getStateName()));
            applicationEventPublisher.publishEvent(new AlertReturnEvent(artworks));
        }).subscribe(artworks::add);
    }

    private void populateComboBox(){
        updateCollections();
        collectionBox.setOnAction(event -> {
            FilteredList<ArtworkFull> filteredData = new FilteredList<>(FXCollections.observableArrayList(artworks));
            filteredData.setPredicate(filterPredicate());
            artworkGrid.setItems(filteredData);
        });
        updateState();
        stateBox.setOnAction(event -> {
            FilteredList<ArtworkFull> filteredData = new FilteredList<>(FXCollections.observableArrayList(artworks));
            filteredData.setPredicate(filterPredicate());
            artworkGrid.setItems(filteredData);
        });
    }

    private void initSearchBars(){
        artworkSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            FilteredList<ArtworkFull> filteredData = new FilteredList<>(FXCollections.observableArrayList(artworks));
            filteredData.setPredicate(filterPredicate());
            artworkGrid.setItems(filteredData);
        });
        authorSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            FilteredList<ArtworkFull> filteredData = new FilteredList<>(FXCollections.observableArrayList(artworks));
            filteredData.setPredicate(filterPredicate());
            artworkGrid.setItems(filteredData);
        });

    }

    @FXML
    public void onLogoutCalled(){
        SecurityContextHolder.clearContext();
        MuseoApplication.initAnonymous();
        MuseoApplication.stage.hide();
        var alertSuccessfulLogout = new Alert(Alert.AlertType.INFORMATION);
        alertSuccessfulLogout.setHeaderText("Déconnexion réussie");
        alertSuccessfulLogout.setContentText("Déconnexion réussie");
        alertSuccessfulLogout.showAndWait();
        MuseoApplication.loginStage.show();
    }

    @EventListener(UserLoginEvent.class)
    public void updateRoles(){
        if(SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))){
            managementButton.setVisible(true);
            deleteButton.setVisible(true);
        } else {
            managementButton.setVisible(false);
            deleteButton.setVisible(false);
        }
        if (!SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))){
            Platform.runLater(()->{
                MuseoApplication.stage.show();
            });
        }
    }


    @EventListener(ArtworkRefreshedEvent.class)
    public void updateArtworks(){
        artworks.clear();
        Flux<ArtworkFull> flux = artworkFullR2dbcDao.findAll();
        flux.doOnComplete(() ->  {
            FilteredList<ArtworkFull> filteredData = new FilteredList<>(FXCollections.observableArrayList(artworks));
            filteredData.setPredicate(filterPredicate());
            artworkGrid.setItems(filteredData);
            applicationEventPublisher.publishEvent(new AlertReturnEvent(artworks));
        }).subscribe(artworks::add);
    }

    @EventListener(CollectionRefreshEvent.class)
    public void updateCollections(){
        collectionList.clear();
        Flux<Collection> fluxCollection = collectionR2dbcDao.findAll();
        fluxCollection.doOnComplete(() -> {
            List<String> newList = collectionList
                    .stream()
                    .map(Collection::getCollectionName)
                    .collect(Collectors.toList());
            newList.add(null);
            Platform.runLater(() -> collectionBox.setItems(new FilteredList<>(FXCollections.observableArrayList(newList))));
        }).subscribe(collectionList::add);
    }

    @EventListener(StateRefreshEvent.class)
    public void updateState(){
        stateList.clear();
        Flux<ArtworkState> fluxState = stateR2dbcDao.findAll();
        fluxState.doOnComplete(() -> {
            List<String> newList = stateList
                    .stream()
                    .map(ArtworkState::getStateName)
                    .collect(Collectors.toList());
            newList.add(null);
            Platform.runLater(() -> stateBox.setItems(new FilteredList<>(FXCollections.observableArrayList(newList))));
        }).subscribe(stateList::add);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        refreshButton.setOnMouseClicked((event)->{
            applicationEventPublisher.publishEvent(new ArtworkRefreshedEvent(this));
            applicationEventPublisher.publishEvent(new CollectionRefreshEvent(this));
            applicationEventPublisher.publishEvent(new OwnerRefreshEvent(this));
            applicationEventPublisher.publishEvent(new StateRefreshEvent(this));
            applicationEventPublisher.publishEvent(new UserRefreshEvent(this));
        });
        updateRoles();
        populateArtworkGrid();
        populateComboBox();
        initSearchBars();
        initReturnFilters();
    }

    private void initReturnFilters(){
        returnRadio.setOnAction(event -> {
            FilteredList<ArtworkFull> filteredData = new FilteredList<>(FXCollections.observableArrayList(artworks));
            filteredData.setPredicate(filterPredicate());
            artworkGrid.setItems(filteredData);
        });
        urgentReturnRadio.setOnAction(event -> {
            FilteredList<ArtworkFull> filteredData = new FilteredList<>(FXCollections.observableArrayList(artworks));
            filteredData.setPredicate(filterPredicate());
            artworkGrid.setItems(filteredData);
        });
    }

    private Predicate<ArtworkFull> filterPredicate(){
        return o -> {
            var returnValue = true;
            if (returnRadio.isSelected()){
                returnValue = returnValue && (o.getReturnDate().isBefore(LocalDate.now().plusMonths(1)) || o.getReturnDate().isEqual(LocalDate.now().plusMonths(1)));
            }
            if(urgentReturnRadio.isSelected()){
                returnValue = returnValue && (o.getReturnDate().isBefore(LocalDate.now()) || o.getReturnDate().isEqual(LocalDate.now()));
            }
            if (collectionBox.getValue()!=null){
                Boolean res;
                try{
                    res = o.getCollectionName().equalsIgnoreCase(collectionBox.getValue());
                } catch (NullPointerException e){
                    res = false;
                }
                returnValue = returnValue && res;
            }
            if(stateBox.getValue()!=null){
                returnValue = returnValue && o.getStateName().equalsIgnoreCase(stateBox.getValue());
            }
            if(!artworkSearch.textProperty().getValue().equals("")){
                returnValue = returnValue && o.getName()
                        .toLowerCase()
                        .startsWith(artworkSearch.textProperty().getValue().toLowerCase());
            }
            if(!authorSearch.textProperty().getValue().equals("")){
                returnValue = returnValue && o.getAuthor()
                        .toLowerCase()
                        .startsWith(authorSearch.textProperty().getValue().toLowerCase());
            }
            return returnValue;
        };
    }

    @FXML
    public void onManagementCalled(ActionEvent event){
        var managementStage = new Stage();
        managementStage.setTitle("Museo Management");
        managementStage.setResizable(false);
        ManagementFxmlController managementController = applicationContext.getBean(ManagementFxmlController.class);
        Scene managementScene;
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
        artworkStage.setTitle("Oeuvre");
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
    private void onDelete(){
        try{
            ArtworkFull artworkFull = artworkGrid.getSelectionModel().getSelectedItem();
            if(ConfirmWindowFactory.create("Suppression de l'oeuvre: " + artworkFull.getName(), "Voulez-vous supprimer l'oeuvre ?").get().getText().equals("Confirmer")){
                artworkFullR2dbcDao.delete(artworkFull);
                artworkGrid.getItems().remove(artworkFull);
                applicationEventPublisher.publishEvent(new ArtworkRefreshedEvent(this));
            }

        } catch (NullPointerException e){
            AlertWindowFactory.create("Selectionnez une oeuvre:", "Selectionnez une oeuvre à supprimer");
        }
    }

    @FXML
    public void onDisplayArtworkCalled(ArtworkFull artwork){
        var displayartworkStage = new Stage();
        displayartworkStage.setTitle("Oeuvre");
        displayartworkStage.setResizable(false);
        DisplayArtworkFxmlController displayArtworkFxmlController = applicationContext.getBean(DisplayArtworkFxmlController.class);
        Scene displayartworkScene = null;
        try {
            if(displayArtworkFxmlController.getView().getScene() == null){
                displayartworkScene = new Scene(displayArtworkFxmlController.getView());
            } else if(displayArtworkFxmlController.getView().getScene().getWindow().isShowing()) {
                return;
            } else {
                displayartworkScene = displayArtworkFxmlController.getView().getScene();
            }
            displayartworkStage.setScene(displayartworkScene);
            displayArtworkFxmlController.setArtwork(artwork);
            displayartworkStage.show();
        } catch (IOException e) {
            ErrorWindowFactory.create(e);
        }
    }
}
