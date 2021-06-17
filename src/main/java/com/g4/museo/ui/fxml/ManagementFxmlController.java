package com.g4.museo.ui.fxml;

import com.g4.museo.event.CollectionRefreshEvent;
import com.g4.museo.event.OwnerRefreshEvent;
import com.g4.museo.event.StateRefreshEvent;
import com.g4.museo.event.UserAddedEvent;
import com.g4.museo.persistence.dto.*;
import com.g4.museo.persistence.r2dbc.CollectionR2dbcDao;
import com.g4.museo.persistence.r2dbc.OwnerR2dbcDao;
import com.g4.museo.persistence.r2dbc.StateR2dbcDao;
import com.g4.museo.persistence.r2dbc.UserR2dbcDao;
import com.g4.museo.ui.utils.AlertWindowFactory;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    PasswordEncoder passwordEncoder;

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

    @FXML
    TextField stateField;
    
    @FXML
    TextField usernameField;
    
    @FXML
    PasswordField passwordField;

    @FXML
    TextField ownerField;

    @FXML
    TextField orgaField;

    @FXML
    TextField addressField;

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
            .filter(o -> !o.getOrganisation().equals("Museo"))
            .collect(Collectors.toList()));
            firstname.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFirstname()));
            lastname.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLastname()));
            orga.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getOrganisation()));
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

    @EventListener(UserAddedEvent.class)
    private void updateUser(){
        users.clear();
        Flux<User> userFlux = userR2dbcDao.findAll();
        userFlux.doOnComplete(() -> {
            userGrid.setItems(new FilteredList<>(FXCollections.observableArrayList(users)));
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

    @FXML
    private void addState(){
        if(stateField.getText().equals("")){
            AlertWindowFactory.create("Champs requis", "Veuillez renseigner un nom d'état");
        } else {
            ArtworkState state = ArtworkState.builder()
                    .stateName(stateField.getText())
                    .build();
            Mono<ArtworkState> newState = stateR2dbcDao.save(state);
            stateGrid.getItems().add(newState.block());
            applicationEventPublisher.publishEvent(new StateRefreshEvent(this));
        }
    }
    
    @FXML
    private void addUser(){
        if(usernameField.getText().equals("") || passwordField.getText().equals("")){
            AlertWindowFactory.create("Champs requis", "Veuillez renseigner un nom d'utilisateur et un mot de passe");
        } else {
            User user = User.builder()
                    .username(usernameField.getText())
                    .password(passwordEncoder.encode(passwordField.getText()))
                    .authority("ROLE_USER")
                    .enabled(true)
                    .build();
            userR2dbcDao.newUser(user.getUsername(), user.getPassword(), user.isEnabled()).block();
            userR2dbcDao.newAuthority(user.getUsername(), user.getAuthority()).block();
            applicationEventPublisher.publishEvent(new UserAddedEvent(this));
        }
    }

    @FXML
    private void addOwner(){
        if((ownerField.getText().equals("") && orgaField.getText().equals("")) || addressField.getText().equals("")){
            AlertWindowFactory.create("Champs requis", "Veuillez renseigner un nom de propriétaire ou d'organisation et une adresse");
        } else {
            Owner.OwnerBuilder ownerBuilder = Owner.builder()
                    .organisation(orgaField.getText())
                    .adress(addressField.getText());
            if(ownerField.getText().split(" ").length <2 && orgaField.getText().equals("")){
                AlertWindowFactory.create("Champs requis", "Veuillez renseigner un prénom et un nom de propriétaire");
            } else if (orgaField.getText().equals("")){
                ownerBuilder.firstname(ownerField.getText().split(" ")[0])
                        .lastname(ownerField.getText().split(" ")[1]);
            }
            Owner owner = ownerBuilder.build();
            Mono<Owner> newOwner = ownerR2dbcDao.save(owner);
            ownerGrid.getItems().add(newOwner.block());
            applicationEventPublisher.publishEvent(new OwnerRefreshEvent(this));
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
