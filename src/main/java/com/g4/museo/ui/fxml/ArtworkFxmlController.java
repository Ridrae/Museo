package com.g4.museo.ui.fxml;

import com.g4.museo.event.UserChangedEvent;
import com.g4.museo.persistence.dto.*;
import com.g4.museo.persistence.r2dbc.*;
import com.g4.museo.ui.utils.AlertWindowFactory;
import com.g4.museo.ui.utils.ErrorWindowFactory;
import io.r2dbc.spi.Blob;
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
import org.aspectj.util.FileUtil;
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
    ArtworkR2dbcDao artworkR2dbcDao;

    @Autowired
    ArtworkBorrowR2dbcDao artworkBorrowR2dbcDao;

    @Autowired
    ArtworkDetailsR2dbcDao artworkDetailsR2dbcDao;

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

    @EventListener(UserChangedEvent.class)
    private void initPermissions(){
        if (descField!=null) {
            if(SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))){
                toUpdate.forEach(c -> c.setVisible(true));
            } else {
                toUpdate.forEach(c -> c.setVisible(false));
            }
        }
    }

    private void initComboBox(){
        Flux<Owner> flux = ownerR2dbcDao.findAllOwners();
        List<Owner> ownersList = new ArrayList<>();
        flux.doOnComplete(() -> ownersList.forEach(owner -> {
            if(owner.getFirstname() != null && owner.getLastname() != null){
                var fullname = new StringBuilder(owner.getFirstname());
                fullname.append(" ");
                fullname.append(owner.getLastname());
                ownerBox.getItems().add(fullname.toString());
            } else if(owner.getOrga() != null) {
                ownerBox.getItems().add(owner.getOrga());
            }
        })).subscribe(ownersList::add);
        Flux<Collection> fluxCollection = collectionR2dbcDao.findAllCollections();
        List<Collection> collectionList = new ArrayList<>();
        fluxCollection.doOnComplete(() -> {
            collectionBox.getItems().addAll(collectionList
                    .stream()
                    .map(Collection::getCollectionName)
                    .collect(Collectors.toList()));
            collectionBox.getItems().add(null);
        }).subscribe(collectionList::add);

        Flux<ArtworkState> fluxState = stateR2dbcDao.getAllStates();
        List<ArtworkState> stateList = new ArrayList<>();
        fluxState.doOnComplete(() -> stateBox.getItems().addAll(stateList
                .stream()
                .map(ArtworkState::getStateName)
                .collect(Collectors.toList()))).subscribe(stateList::add);

        restoredBox.getItems().addAll(Arrays.asList("Neuf", "Restauré"));
    }

    @FXML
    public void onAddPicture(){

        Stage stage = null;
        try {
            stage = (Stage) this.getView().getScene().getWindow();
            var fileChooser = new FileChooser();
            fileChooser.setTitle("Select an image");
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
        var artworkFullDTO = ArtworkFullDTO.builder()
                .artwork(Artwork.builder().build())
                .artworkBorrow(ArtworkBorrow.builder().build())
                .artworkDetails(ArtworkDetails.builder().build())
                .build();
        if(nameField.getText().equals("")){
            AlertWindowFactory.create("Missing infos", "Please select a name");
            return;
        } else {
            artworkFullDTO.getArtwork().setName(nameField.getText());
        }
        if(authorField.getText().equals("")){
            AlertWindowFactory.create("Missing infos", "Please select an author");
            return;
        } else {
            artworkFullDTO.getArtwork().setAuthor(authorField.getText());
        }
        if (imageFile != null){
            try {
                var bytes = ByteBuffer.wrap(FileUtil.readAsByteArray(imageFile));
                Publisher<ByteBuffer> flux = Flux.just(bytes);
                artworkFullDTO.getArtwork().setPicture(Blob.from(flux));
            } catch (IOException e) {
                ErrorWindowFactory.create(e);
            }
        } else {
            AlertWindowFactory.create("Missing infos", "Please select a picture");
            return;
        }
        if(dateField.getValue()!=null){
            artworkFullDTO.getArtwork().setDate(dateField.getValue());
        } else {
            AlertWindowFactory.create("Missing infos", "Please select a creation date");
            return;
        }
        artworkFullDTO.getArtwork().setCertified(certificateBox.isSelected());
        if(stateBox.getValue()!=null){
            artworkFullDTO.getArtwork().setStateID(stateR2dbcDao.findStateIdByName(stateBox.getValue()).block());
        } else {
            AlertWindowFactory.create("Missing infos", "Please select a status");
            return;
        }
        if(stateField.getText().equals("") && stateBox.getValue().equals("museum")){
            AlertWindowFactory.create("Missing infos", "Please select a location");
            return;
        } else {
            artworkFullDTO.getArtwork().setStoredLocation(stateField.getText());
        }
        if(collectionBox.getValue()!=null){
            artworkFullDTO.getArtwork().setCollectionID(collectionR2dbcDao.findCollectionIdByName(collectionBox.getValue()).block());
        }
        if(ownerBox.getValue() == null){
            AlertWindowFactory.create("Missing infos", "Please select an owner");
            return;
        } else artworkFullDTO.getArtwork().setBorrowed(!ownerBox.getValue().equals("Museo"));
        if(descField.getText().equals("")){
            AlertWindowFactory.create("Missing infos", "Please write a description");
            return;
        } else {
            artworkFullDTO.getArtwork().setDesc(descField.getText());
        }
        if (widthField.getText().equals("")){
            AlertWindowFactory.create("Missing infos", "Please specify a width");
            return;
        }else{
            artworkFullDTO.getArtworkDetails().setWidth(widthField.getText());
        }
        if(heigthField.getText().equals("")){
            AlertWindowFactory.create("Missing infos", "Please specify a heigth");
            return;
        }else{
            artworkFullDTO.getArtworkDetails().setHeight(heigthField.getText());
        }
        if(perimeterField.getText().equals("")){
            AlertWindowFactory.create("Missing infos", "Please specify a perimter");
            return;
        }else{
            artworkFullDTO.getArtworkDetails().setPerimeter(perimeterField.getText());
        }

        artworkFullDTO.getArtworkDetails().setInsuranceNumber(insuranceField.getText());
        artworkFullDTO.getArtworkDetails().setMaterial(materialField.getText());
        artworkFullDTO.getArtworkDetails().setTechnic(technicField.getText());

        if(typeField.getText().equals("")){
            AlertWindowFactory.create("Missing infos", "Please specify a type");
            return;
        } else {
            artworkFullDTO.getArtworkDetails().setType(typeField.getText());
        }
        if(restoredBox.getValue() == null){
            AlertWindowFactory.create("Missing infos", "Please select a status");
            return;
        } else artworkFullDTO.getArtworkDetails().setRestored(!restoredBox.getValue().equals("Neuf"));

        if(artworkFullDTO.getArtwork().isBorrowed()) {
            if (ownerBox.getValue() == null) {
                AlertWindowFactory.create("Missing infos", "Please select an owner");
                return;
            } else {
                String firstname = ownerBox.getValue().split(" ")[0];
                String lastname = ownerBox.getValue().split(" ")[1];
                artworkFullDTO.getArtworkBorrow().setIdowner(ownerR2dbcDao.findOwnerIdByName(firstname, lastname).block());
            }
            if(borrowField.getValue() == null) {
                AlertWindowFactory.create("Missing infos", "Please enter a borrow date");
                return;
            } else {
                artworkFullDTO.getArtworkBorrow().setDateBorrowed(borrowField.getValue());
            }
            if(returnField.getValue() == null){
                artworkFullDTO.getArtworkBorrow().setLongTerm(true);
            } else {
                artworkFullDTO.getArtworkBorrow().setReturnDate(returnField.getValue());
                artworkFullDTO.getArtworkBorrow().setLongTerm(false);
            }
            artworkFullDTO.getArtworkBorrow().setStored(storedBox.isSelected());
        } else {
            artworkFullDTO.getArtworkBorrow().setIdowner(ownerR2dbcDao.findOwnerIdByOrga("Museo").block());
            artworkFullDTO.getArtworkBorrow().setLongTerm(false);
            artworkFullDTO.getArtworkBorrow().setStored(false);
        }

        Mono<Artwork> artwork = artworkR2dbcDao.save(artworkFullDTO.getArtwork());
        Integer id = artwork.block().getIdartwork();
        artworkFullDTO.getArtworkBorrow().setIdartwork(id);
        artworkFullDTO.getArtworkDetails().setIdartwork(id);
        artworkBorrowR2dbcDao.save(artworkFullDTO.getArtworkBorrow()).block();
        artworkDetailsR2dbcDao.save(artworkFullDTO.getArtworkDetails()).block();
     }
}
