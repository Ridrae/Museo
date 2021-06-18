package com.g4.museo.ui.fxml;

import com.g4.museo.event.CollectionRefreshEvent;
import com.g4.museo.event.OwnerRefreshEvent;
import com.g4.museo.event.StateRefreshEvent;
import com.g4.museo.event.UserLoginEvent;
import com.g4.museo.persistence.dto.*;
import com.g4.museo.persistence.r2dbc.*;
import com.g4.museo.ui.utils.AlertWindowFactory;
import com.g4.museo.ui.utils.ErrorWindowFactory;
import io.r2dbc.spi.Blob;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Component
public class ArtworkFxmlController extends FXMLController implements Initializable {

    @Autowired
    OwnerR2dbcDao ownerR2dbcDao;

    @Autowired
    CollectionR2dbcDao collectionR2dbcDao;

    @Autowired
    StateR2dbcDao stateR2dbcDao;

    @Autowired
    ArtworkFullR2dbcDao artworkFullR2dbcDao;

    @FXML
    ComboBox<String>  ownerBox;

    @FXML
    ComboBox<String> collectionBox;

    @FXML
    TextField nameField;

    @FXML
    TextField authorField;

    @FXML
    DatePicker dateField;

    @FXML
    DatePicker returnField;

    @FXML
    TextField stateField;

    @FXML
    CheckBox certificateBox;

    @FXML
    ImageView pictureView;

    @FXML
    ComboBox<String> stateBox;

    @FXML
    TextArea descField;

    @FXML
    ComboBox<String> restoredBox;

    @FXML
    TextField heigthField;

    @FXML
    TextField widthField;

    @FXML
    TextField perimeterField;

    @FXML
    TextField insuranceField;

    @FXML
    TextField materialField;

    @FXML
    TextField technicField;

    @FXML
    TextField typeField;

    @FXML
    DatePicker borrowField;

    @FXML
    CheckBox storedBox;

    @FXML
    Label descLabel;

     @FXML
    AnchorPane detailPane;

     @FXML
     Label detailLabel;

    List<Node> toUpdate;

    private File imageFile;
    List<Collection> collectionList = new ArrayList<>();
    List<ArtworkState> stateList = new ArrayList<>();
    List<Owner> ownersList = new ArrayList<>();

    @FXML
    public void onReturn(ActionEvent event){
        var scene = ((Node) event.getSource()).getScene();
        var stage = (Stage)scene.getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources){
        toUpdate = Arrays.asList(detailPane, detailLabel, descField, descLabel);
        initComboBox();
        initPermissions();
        descField.setWrapText(true);
    }

    @EventListener(UserLoginEvent.class)
    private void initPermissions(){
        if (descField!=null) {
            if(SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))){
                toUpdate.forEach(c -> c.setVisible(true));
            } else {
                toUpdate.forEach(c -> c.setVisible(false));
            }
        }
    }

    @EventListener(CollectionRefreshEvent.class)
    private void updateCollections(){
        if(collectionBox!=null){
            collectionList.clear();
            Flux<Collection> fluxCollection = collectionR2dbcDao.findAll();
            fluxCollection.doOnComplete(() -> {
                List<String> newList = collectionList
                        .stream()
                        .map(Collection::getCollectionName)
                        .collect(Collectors.toList());
                newList.add(null);
                collectionBox.setItems(new FilteredList<>(FXCollections.observableArrayList(newList)));
            }).subscribe(collectionList::add);
        }
    }

    @EventListener(StateRefreshEvent.class)
    private void updateState(){
        if(stateBox!=null){
            stateList.clear();
            Flux<ArtworkState> fluxCollection = stateR2dbcDao.findAll();
            fluxCollection.doOnComplete(() -> {
                List<String> newList = stateList
                        .stream()
                        .map(ArtworkState::getStateName)
                        .collect(Collectors.toList());
                newList.add(null);
                stateBox.setItems(new FilteredList<>(FXCollections.observableArrayList(newList)));
            }).subscribe(stateList::add);
        }
    }

    @EventListener(OwnerRefreshEvent.class)
    private void updateOwner(){
        if(ownerBox!=null){
            ownersList.clear();
            ownerBox.getItems().clear();
            Flux<Owner> flux = ownerR2dbcDao.findAll();
            flux.doOnComplete(() -> ownersList.forEach(owner -> {
                if(owner.getFirstname() != null && owner.getLastname() != null){
                    var fullname = new StringBuilder(owner.getFirstname());
                    fullname.append(" ");
                    fullname.append(owner.getLastname());
                    ownerBox.getItems().add(fullname.toString());
                } else if(owner.getOrganisation() != null) {
                    ownerBox.getItems().add(owner.getOrganisation());
                }
            })).subscribe(ownersList::add);
        }
    }

    private void initComboBox(){
        updateOwner();
        updateCollections();
        updateState();

        restoredBox.getItems().addAll(Arrays.asList("Neuf", "Restauré"));
    }

    @FXML
    public void onAddPicture(){
        Stage stage = null;
        try {
            stage = (Stage) this.getView().getScene().getWindow();
            var fileChooser = new FileChooser();
            fileChooser.setTitle("Selectionnez une image");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("All Images", "*.*"),
                    new FileChooser.ExtensionFilter("JPG, JPEG", "*.jpg", "*.jpeg"),
                    new FileChooser.ExtensionFilter("PNG", "*.png")
            );
            imageFile = fileChooser.showOpenDialog(stage);
            if (imageFile != null){
                pictureView.setImage(new Image(new FileInputStream(imageFile)));
                pictureView.setPreserveRatio(true);
                pictureView.setSmooth(true);
                pictureView.setCache(true);
            }
        } catch (IOException e) {
            ErrorWindowFactory.create(e);
        }
    }

    @SuppressWarnings("java:S3776")
    @FXML
    public void addArtwork(){
        var artworkFullDTO = ArtworkFull.builder().build();
        if(nameField.getText().equals("")){
            AlertWindowFactory.create("Champs Requis", "Entrez un nom");
            return;
        } else {
            artworkFullDTO.setName(nameField.getText());
        }
        if(authorField.getText().equals("")){
            AlertWindowFactory.create("Champs Requis", "Entrez un auteur");
            return;
        } else {
            artworkFullDTO.setAuthor(authorField.getText());
        }
        if (imageFile != null){
            try {
                var bytes = ByteBuffer.wrap(Files.readAllBytes(imageFile.toPath()));
                Publisher<ByteBuffer> flux = Flux.just(bytes);
                artworkFullDTO.setPicture(Blob.from(flux));
            } catch (IOException e) {
                ErrorWindowFactory.create(e);
            }
        } else {
            AlertWindowFactory.create("Champs Requis", "Ajoutez une image");
            return;
        }
        if(dateField.getValue()!=null){
            artworkFullDTO.setDate(dateField.getValue());
        } else {
            AlertWindowFactory.create("Champs Requis", "Selectionnez une date de création");
            return;
        }
        artworkFullDTO.setCertified(certificateBox.isSelected());
        if(stateBox.getValue()!=null){
            artworkFullDTO.setStateID(stateR2dbcDao.findStateIdByName(stateBox.getValue()).block());
        } else {
            AlertWindowFactory.create("Champs Requis", "Selectionnez un état");
            return;
        }
        if(stateField.getText().equals("") && stateBox.getValue().equals("Musée")){
            AlertWindowFactory.create("Champs Requis", "Entrez un emplacement");
            return;
        } else {
            artworkFullDTO.setStoredLocation(stateField.getText());
        }
        if(collectionBox.getValue()!=null){
            artworkFullDTO.setCollectionID(collectionR2dbcDao.findCollectionIdByName(collectionBox.getValue()).block());
        }
        if(ownerBox.getValue() == null){
            AlertWindowFactory.create("Champs Requis", "Selectionnez un propriétaire");
            return;
        } else artworkFullDTO.setBorrowed(!ownerBox.getValue().equals("Museo"));
        if(descField.getText().equals("")){
            AlertWindowFactory.create("Champs Requis", "Entrez une description");
            return;
        } else {
            artworkFullDTO.setDesc(descField.getText());
        }
        if (widthField.getText().equals("")){
            AlertWindowFactory.create("Champs Requis", "Entrez une largeur");
            return;
        }else{
            artworkFullDTO.setWidth(widthField.getText());
        }
        if(heigthField.getText().equals("")){
            AlertWindowFactory.create("Champs Requis", "Entrez une hauteur");
            return;
        }else{
            artworkFullDTO.setHeight(heigthField.getText());
        }
        if(perimeterField.getText().equals("")){
            AlertWindowFactory.create("Champs Requis", "Entrez un périmètre");
            return;
        }else{
            artworkFullDTO.setPerimeter(perimeterField.getText());
        }

        artworkFullDTO.setInsuranceNumber(insuranceField.getText());
        artworkFullDTO.setMaterial(materialField.getText());
        artworkFullDTO.setTechnic(technicField.getText());

        if(typeField.getText().equals("")){
            AlertWindowFactory.create("Champs Requis", "Entrez un type");
            return;
        } else {
            artworkFullDTO.setType(typeField.getText());
        }
        if(restoredBox.getValue() == null){
            AlertWindowFactory.create("Champs Requis", "Entrez un état");
            return;
        } else artworkFullDTO.setRestored(!restoredBox.getValue().equals("Neuf"));

        if(artworkFullDTO.isBorrowed()) {
            if (ownerBox.getValue() == null) {
                AlertWindowFactory.create("Champs Requis", "Selectionnez un propriétaire");
                return;
            } else {
                String firstname = ownerBox.getValue().split(" ")[0];
                String lastname = ownerBox.getValue().split(" ")[1];
                artworkFullDTO.setIdowner(ownerR2dbcDao.findOwnerIdByName(firstname, lastname).block());
            }
            if(borrowField.getValue() == null) {
                AlertWindowFactory.create("Champs Requis", "Entrez une date d'emprunt");
                return;
            } else {
                artworkFullDTO.setDateBorrowed(borrowField.getValue());
            }
            if(returnField.getValue() == null){
                artworkFullDTO.setLongTerm(true);
            } else {
                artworkFullDTO.setReturnDate(returnField.getValue());
                artworkFullDTO.setLongTerm(false);
            }
            artworkFullDTO.setStored(storedBox.isSelected());
        } else {
            artworkFullDTO.setIdowner(ownerR2dbcDao.findOwnerIdByOrga("Museo").block());
            artworkFullDTO.setLongTerm(false);
            artworkFullDTO.setStored(false);
        }

        Mono<ArtworkFull> artwork = artworkFullR2dbcDao.save(artworkFullDTO);
    }
}
