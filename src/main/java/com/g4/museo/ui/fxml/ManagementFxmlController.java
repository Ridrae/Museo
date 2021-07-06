package com.g4.museo.ui.fxml;

import com.g4.museo.event.CollectionRefreshEvent;
import com.g4.museo.event.OwnerRefreshEvent;
import com.g4.museo.event.StateRefreshEvent;
import com.g4.museo.event.UserRefreshEvent;
import com.g4.museo.persistence.dto.*;
import com.g4.museo.persistence.dto.Collection;
import com.g4.museo.persistence.r2dbc.CollectionR2dbcDao;
import com.g4.museo.persistence.r2dbc.OwnerR2dbcDao;
import com.g4.museo.persistence.r2dbc.StateR2dbcDao;
import com.g4.museo.persistence.r2dbc.UserR2dbcDao;
import com.g4.museo.ui.utils.*;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URL;
import java.util.*;
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

    @EventListener(UserRefreshEvent.class)
    private void updateUser(){
        if (userGrid!=null){
            users.clear();
            Flux<User> userFlux = userR2dbcDao.findAll();
            userFlux.doOnComplete(() -> {
                userGrid.setItems(new FilteredList<>(FXCollections.observableArrayList(users)));
            }).subscribe(users::add);
        }
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
    private void deleteCollection(){
        try {
            Collection collection = collectionGrid.getSelectionModel().getSelectedItem();
            if (ConfirmWindowFactory.create("Confirmer la suppression", "Voulez-vous supprimer la collection " + collection.getCollectionName() + " ?").get().getText().equals("Confirmer")){
                collectionR2dbcDao.delete(collection).block();
                collectionGrid.getItems().remove(collection);
                applicationEventPublisher.publishEvent(new CollectionRefreshEvent(this));
            }
        } catch (NullPointerException e) {
            AlertWindowFactory.create("Selectionnez une collection", "Veuillez selectionner une collection à supprimer");
        }
    }

    @FXML
    private void editCollection(){
        try{
            Collection collection = collectionGrid.getSelectionModel().getSelectedItem();
            Optional<String> result =  EditWindowFactory.create("Edition de la collection: " + collection.getCollectionName(), "Entrez un nouveau nom: ");
            if(result.isPresent()){
                collectionGrid.getItems().remove(collection);
                collection.setCollectionName(result.get());
                collectionR2dbcDao.save(collection).block();
                collectionGrid.getItems().add(collection);
                applicationEventPublisher.publishEvent(new CollectionRefreshEvent(this));
            }
        } catch (NullPointerException e){
            AlertWindowFactory.create("Selectionnez une collection", "Veuillez selectionner une collection à modifier");
        }
    }

    @FXML
    private void deleteUser(){
        try {
            User user = userGrid.getSelectionModel().getSelectedItem();
            if (!user.getAuthority().equals("USER_ADMIN")) {
                if (ConfirmWindowFactory.create("Confirmer la suppression", "Voulez-vous supprimer l'utilisateur " + user.getUsername() + " ?").get().getText().equals("Confirmer")){
                    userGrid.getItems().remove(user);
                    userR2dbcDao.deleteAuthorities(user.getUsername()).block();
                    userR2dbcDao.deleteUser(user.getUsername()).block();
                    applicationEventPublisher.publishEvent(new UserRefreshEvent(this));
                }
            }
        } catch (NullPointerException e) {
            AlertWindowFactory.create("Selectionnez un utilisateur", "Veuillez selectionner un utilisateur à supprimer");
        }
    }

    @FXML
    private void editUser(){
        try{
            User user = userGrid.getSelectionModel().getSelectedItem();
            if (SecurityContextHolder.getContext().getAuthentication().getName().equals(user.getUsername())){
                AlertWindowFactory.create("Utilisateur incorrect", "Vous ne pouvez pas modifier l'utilisateur actuellement connecté");
            } else {
                Optional<Map<String, String>> result = UserEditWindowFactory.create("Edition de l'utilisateur: " + user.getUsername());
                if (result.isPresent()){
                    if (!result.get().get("password").equals("")){
                        user.setPassword(passwordEncoder.encode(result.get().get("password")));
                    }
                    userR2dbcDao.updateUser(user.getUsername(), result.get().get("username"), user.getPassword()).block();
                    applicationEventPublisher.publishEvent(new UserRefreshEvent(this));
                }
            }
        } catch(NullPointerException e){
            AlertWindowFactory.create("Selectionnez un utilisateur", "Veuillez selectionner un utilisateur à modifier");
        }
    }

    @FXML
    private void deleteOwner(){
        try {
            Owner owner = ownerGrid.getSelectionModel().getSelectedItem();
            if(ConfirmWindowFactory.create("Confirmer la suppression", "Voulez-vous supprimer le propriétaire ?").get().getText().equals("Confirmer")){
                ownerR2dbcDao.delete(owner).block();
                ownerGrid.getItems().remove(owner);
                applicationEventPublisher.publishEvent(new OwnerRefreshEvent(this));
            }
        } catch (NullPointerException e){
            AlertWindowFactory.create("Selectionnez un propriétaire", "Veuillez selectionner un propriétaire à modifier");
        }
    }

    @FXML
    private void editOwner(){
        try{
            Owner owner = ownerGrid.getSelectionModel().getSelectedItem();
            Optional<Map<String, String>> result = OwnerEditWindowFactory.create("Edition du propriétaire");
            if(result.isPresent()){
                ownerGrid.getItems().remove(owner);
                if(!result.get().get("firstname").equals("")){
                    owner.setFirstname(result.get().get("firstname"));
                }
                if(!result.get().get("lastname").equals("")){
                    owner.setLastname(result.get().get("lastname"));
                }
                if(!result.get().get("orga").equals("")){
                    owner.setOrganisation(result.get().get("orga"));
                }
                if(!result.get().get("address").equals("")){
                    owner.setAdress(result.get().get("address"));
                }
                ownerR2dbcDao.save(owner).block();
                ownerGrid.getItems().add(owner);
                applicationEventPublisher.publishEvent(new OwnerRefreshEvent(this));
            }
        } catch (NullPointerException e){
            AlertWindowFactory.create("Selectionnez un propriétaire", "Veuillez selectionner un propriétaire à modifier");
        }
    }

    @FXML
    private void deleteState(){
        try{
            ArtworkState state = stateGrid.getSelectionModel().getSelectedItem();
            if(ConfirmWindowFactory.create("Confirmer la suppression", "Voulez-vous supprimer l'état " + state.getStateName() + " ?").get().getText().equals("Confirmer")){
                stateR2dbcDao.delete(state).block();
                stateGrid.getItems().remove(state);
                applicationEventPublisher.publishEvent(new StateRefreshEvent(this));
            }
        } catch(NullPointerException e){
            AlertWindowFactory.create("Selectionnez un état", "Veuillez selectionner un état à supprimer");
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
    private void editState(){
        try{
            ArtworkState state = stateGrid.getSelectionModel().getSelectedItem();
            Optional<String> result =  EditWindowFactory.create("Edition de l'état: " + state.getStateName(), "Entrez un nouveau nom: ");
            if(result.isPresent()){
                stateGrid.getItems().remove(state);
                state.setStateName(result.get());
                stateR2dbcDao.save(state).block();
                stateGrid.getItems().add(state);
                applicationEventPublisher.publishEvent(new StateRefreshEvent(this));
            }
        } catch (NullPointerException e){
            AlertWindowFactory.create("Selectionnez un état", "Veuillez selectionner un état à modifier");
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
            applicationEventPublisher.publishEvent(new UserRefreshEvent(this));
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
