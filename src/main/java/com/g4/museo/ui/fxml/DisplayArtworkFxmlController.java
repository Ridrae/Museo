package com.g4.museo.ui.fxml;

import com.g4.museo.event.*;
import com.g4.museo.persistence.dto.ArtworkFull;
import com.g4.museo.persistence.dto.ArtworkState;
import com.g4.museo.persistence.dto.Collection;
import com.g4.museo.persistence.dto.Owner;
import com.g4.museo.persistence.r2dbc.ArtworkFullR2dbcDao;
import com.g4.museo.persistence.r2dbc.CollectionR2dbcDao;
import com.g4.museo.persistence.r2dbc.OwnerR2dbcDao;
import com.g4.museo.persistence.r2dbc.StateR2dbcDao;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

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
public class DisplayArtworkFxmlController extends FXMLController implements Initializable {

    @Autowired
    OwnerR2dbcDao ownerR2dbcDao;

    @Autowired
    CollectionR2dbcDao collectionR2dbcDao;

    @Autowired
    StateR2dbcDao stateR2dbcDao;

    @Autowired
    ArtworkFullR2dbcDao artworkFullR2dbcDao;

    @Autowired
    ApplicationEventPublisher applicationEventPublisher;

    @FXML
    ComboBox<String> ownerBox;

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
    Button imageButton;

    private ArtworkFull artworkFull;

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

    @EventListener(UserLoginEvent.class)
    private void updatePermissions(){
        if (descField!=null) {
            if(SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))){
                descField.setEditable(true);
                imageButton.setDisable(false);
                heigthField.setEditable(true);
                widthField.setEditable(true);
                perimeterField.setEditable(true);
                insuranceField.setEditable(true);
                materialField.setEditable(true);
                technicField.setEditable(true);
                typeField.setEditable(true);
                restoredBox.setDisable(false);

            } else {
                descField.setEditable(false);
                imageButton.setDisable(true);
                heigthField.setEditable(false);
                widthField.setEditable(false);
                perimeterField.setEditable(false);
                perimeterField.setEditable(false);
                insuranceField.setEditable(false);
                materialField.setEditable(false);
                technicField.setEditable(false);
                typeField.setEditable(false);
                restoredBox.setDisable(true);
            }
        }
    }

    public void setArtwork(ArtworkFull artwork){
        artworkFull = artwork;
        populateArtwork();
    }

    private void populateArtwork(){
        pictureView.setImage(artworkFull.getImage());
        pictureView.setPreserveRatio(true);
        pictureView.setSmooth(true);
        pictureView.setCache(true);

        descField.setText(artworkFull.getDesc());
        nameField.setText(artworkFull.getName());
        authorField.setText(artworkFull.getAuthor());
        ownerBox.getSelectionModel().select(artworkFull.getOwnerFirstname() + " " + artworkFull.getOwnerLastname());
        collectionBox.getSelectionModel().select(artworkFull.getCollectionName());
        stateBox.getSelectionModel().select(artworkFull.getStateName());
        dateField.setValue(artworkFull.getDate());
        returnField.setValue(artworkFull.getReturnDate());
        borrowField.setValue(artworkFull.getDateBorrowed());
        stateField.setText(artworkFull.getStoredLocation());
        certificateBox.setSelected(artworkFull.isCertified());
        storedBox.setSelected(artworkFull.isStored());

        heigthField.setText(artworkFull.getHeight());
        widthField.setText(artworkFull.getWidth());
        perimeterField.setText(artworkFull.getPerimeter());
        insuranceField.setText(artworkFull.getInsuranceNumber());
        materialField.setText(artworkFull.getMaterial());
        technicField.setText(artworkFull.getTechnic());
        typeField.setText(artworkFull.getType());
        restoredBox.getSelectionModel().select(artworkFull.isRestored() ? "Restauré" : "Neuf");
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

    public void updateArtwork(){
        artworkFull.setAuthor(authorField.getText());
        artworkFull.setName(nameField.getText());
        artworkFull.setDate(dateField.getValue());
        artworkFull.setDateBorrowed(borrowField.getValue());
        artworkFull.setReturnDate(returnField.getValue());
        artworkFull.setStoredLocation(stateField.getText());
        artworkFull.setCertified(certificateBox.isSelected());
        artworkFull.setStored(storedBox.isSelected());
        if(ownerBox.getValue().equals("Museo") && artworkFull.isBorrowed()){
            artworkFull.setBorrowed(false);
            artworkFull.setIdowner(ownerR2dbcDao.findOwnerIdByOrga("Museo").block());
        } else if (!ownerBox.getValue().equals("Museo")){
            String firstname = ownerBox.getValue().split(" ")[0];
            String lastname = ownerBox.getValue().split(" ")[1];
            if(!(firstname.equals(artworkFull.getOwnerFirstname()) && lastname.equals(artworkFull.getOwnerLastname()))){
                artworkFull.setBorrowed(true);
                artworkFull.setIdowner(ownerR2dbcDao.findOwnerIdByName(firstname, lastname).block());
            }
        }
        if(!collectionBox.getValue().equals(artworkFull.getCollectionName())){
            artworkFull.setCollectionID(collectionR2dbcDao.findCollectionIdByName(collectionBox.getValue()).block());
        }
        if(!stateBox.getValue().equals(artworkFull.getStateName())){
            artworkFull.setStateID(stateR2dbcDao.findStateIdByName(stateBox.getValue()).block());
        }
        if (SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            if (!pictureView.getImage().equals(artworkFull.getImage())) {
                ByteBuffer bytes = null;
                try {
                    bytes = ByteBuffer.wrap(Files.readAllBytes(imageFile.toPath()));
                    Publisher<ByteBuffer> flux = Flux.just(bytes);
                    artworkFull.setPicture(Blob.from(flux));
                    artworkFull.setImage(pictureView.getImage());
                } catch (IOException e) {
                    ErrorWindowFactory.create(e);
                }
            }
            artworkFull.setDesc(descField.getText());
            artworkFull.setHeight(heigthField.getText());
            artworkFull.setWidth(widthField.getText());
            artworkFull.setPerimeter(perimeterField.getText());
            artworkFull.setInsuranceNumber(insuranceField.getText());
            artworkFull.setMaterial(materialField.getText());
            artworkFull.setTechnic(technicField.getText());
            artworkFull.setType(typeField.getText());
            artworkFull.setRestored(!restoredBox.getValue().equals("Neuf"));
            artworkFullR2dbcDao.updateArtwork(artworkFull.getIdartwork(), artworkFull.getName(), artworkFull.getAuthor(), artworkFull.getDate(),
                    artworkFull.isCertified(), artworkFull.getStoredLocation(), artworkFull.getCollectionID(), artworkFull.getStateID(), artworkFull.isBorrowed(),
                    artworkFull.getDesc(), SecurityContextHolder.getContext().getAuthentication().getName()).block();
            if(imageFile!=null){
                try {
                    byte[] picture  = Files.readAllBytes(imageFile.toPath());
                    artworkFullR2dbcDao.updatePicture(artworkFull.getIdartwork(), picture).subscribe();
                } catch (IOException e) {
                    ErrorWindowFactory.create(e);
                }
            }
            artworkFullR2dbcDao.updateDetails(artworkFull.getIdartwork(), artworkFull.getWidth(), artworkFull.getHeight(), artworkFull.getPerimeter(),
                    artworkFull.getInsuranceNumber(), artworkFull.getMaterial(), artworkFull.getTechnic(), artworkFull.getType(), artworkFull.isRestored()).block();
            artworkFullR2dbcDao.updateBorrow(artworkFull.getIdartwork(), artworkFull.getIdowner(), artworkFull.getDateBorrowed(), artworkFull.getReturnDate(), artworkFull.isStored(), artworkFull.isLongTerm()).block();
            applicationEventPublisher.publishEvent(new ArtworkRefreshedEvent(this));
        }
    }

    @FXML
    public void onAddPicture(){
        Stage stage;
        try {
            stage = (Stage) this.getView().getScene().getWindow();
            var fileChooser = new FileChooser();
            fileChooser.setTitle("Selectionnez une image");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Tousz", "*.*"),
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

    @Override
    public void initialize(URL location, ResourceBundle resources){
        updatePermissions();
        initComboBox();
    }
}
