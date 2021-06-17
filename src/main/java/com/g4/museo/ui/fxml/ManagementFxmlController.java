package com.g4.museo.ui.fxml;

import com.g4.museo.event.CollectionRefreshEvent;
import com.g4.museo.persistence.dto.*;
import com.g4.museo.persistence.r2dbc.CollectionR2dbcDao;
import com.g4.museo.persistence.r2dbc.OwnerR2dbcDao;
import com.g4.museo.persistence.r2dbc.StateR2dbcDao;
import com.g4.museo.persistence.r2dbc.UserR2dbcDao;
import com.g4.museo.ui.utils.AlertWindowFactory;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Component
public class ManagementFxmlController extends FXMLController implements Initializable {

    @Autowired
    CollectionR2dbcDao collectionR2dbcDao;

    @Autowired
    OwnerR2dbcDao ownerR2dbcDao;

    @Autowired
    StateR2dbcDao stateR2dbcDao;

    @Autowired
    UserR2dbcDao userR2dbcDao;

    @Autowired
    ApplicationEventPublisher applicationEventPublisher;

    @FXML
    TableView<Collection> collectionGrid;

    @FXML
    TableView<Owner> ownerGrid;

    @FXML
    TableView<ArtworkState> stateGrid;

    @FXML
    TableView<User> userGrid;

    @FXML
    TextField collectionField;

    private List<Collection> collections = new ArrayList<>();
    private List<Owner> owners = new ArrayList<>();
    private List<ArtworkState> states = new ArrayList<>();
    private List<User> users = new ArrayList<>();

    @FXML
    public void onReturn(ActionEvent event){
        var scene = ((Node) event.getSource()).getScene();
        var stage = (Stage)scene.getWindow();
        stage.close();
    }

    private void populateCollectionGrid(){
        Flux<Collection> collectionFlux = collectionR2dbcDao.findAll();
        TableColumn<Collection, String> collection = new TableColumn<>("Collection");
        collectionGrid.getColumns().add(collection);
        collectionFlux.doOnComplete(() -> {
            collectionGrid.getItems().addAll(collections);
            collection.setCellValueFactory(c-> new SimpleStringProperty(c.getValue().getCollectionName()));
        }).subscribe(collections::add);
    }

    private void populateOwnerGrid(){
        Flux<Owner> ownerFlux = ownerR2dbcDao.findAll();
        TableColumn<Owner, String> firstname = new TableColumn<>("Prénom");
        TableColumn<Owner, String> lastname = new TableColumn<>("Nom");
        TableColumn<Owner, String> orga = new TableColumn<>("Organisation");
        TableColumn<Owner, String> address = new TableColumn<>("Adresse");
        ownerGrid.getColumns().addAll(firstname, lastname, orga, address);
        ownerFlux.doOnComplete(() -> {
            ownerGrid.getItems().addAll(owners.stream()
            .filter(o -> !o.getOrga().equals("Museo"))
            .collect(Collectors.toList()));
            firstname.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFirstname()));
            lastname.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLastname()));
            orga.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getOrga()));
            address.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAdress()));
        }).subscribe(owners::add);
    }

    private void populateStateGrid(){
        Flux<ArtworkState> stateFlux = stateR2dbcDao.findAll();
        TableColumn<ArtworkState, String> state = new TableColumn<>("Etat");
        stateGrid.getColumns().add(state);
        stateFlux.doOnComplete(() -> {
            stateGrid.getItems().addAll(states);
            state.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStateName()));
        }).subscribe(states::add);
    }

    private void populateUserGrid(){
        Flux<User> userFlux = userR2dbcDao.findAll();
        TableColumn<User, String> username = new TableColumn<>("Nom d'utilisateur");
        TableColumn<User, Boolean> enabled = new TableColumn<>("Actif");
        TableColumn<User, String> authority = new TableColumn<>("Rôle");
        userGrid.getColumns().addAll(username, enabled, authority);
        userFlux.doOnComplete(() -> {
            userGrid.getItems().addAll(users);
            username.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsername()));
            enabled.setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().isEnabled()));
            authority.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAuthority()));
        }).subscribe(users::add);
    }

    @FXML
    private void addCollection(){
        if(collectionField.getText().equals("")){
            AlertWindowFactory.create("Champs requis", "Veuillez renseigner un nom de collection");
        } else {
            Collection collection = Collection.builder()
                    .collectionName(collectionField.getText())
                    .build();
            Mono<Collection> newCollection = collectionR2dbcDao.save(collection);
            collectionGrid.getItems().add(newCollection.block());
            applicationEventPublisher.publishEvent(new CollectionRefreshEvent(this));
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateCollectionGrid();
        populateOwnerGrid();
        populateStateGrid();
        populateUserGrid();
    }
}
