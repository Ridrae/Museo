package com.g4.museo.ui.fxml;

import com.g4.museo.event.UserLoginEvent;
import com.g4.museo.persistence.dto.ArtworkFull;
import com.g4.museo.persistence.dto.ArtworkState;
import com.g4.museo.persistence.dto.Collection;
import com.g4.museo.persistence.dto.Owner;
import com.g4.museo.persistence.r2dbc.CollectionR2dbcDao;
import com.g4.museo.persistence.r2dbc.OwnerR2dbcDao;
import com.g4.museo.persistence.r2dbc.StateR2dbcDao;
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
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.io.ByteArrayInputStream;
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
    Label descLabel;

    @FXML
    AnchorPane detailPane;

    @FXML
    Label detailLabel;

    @FXML
    Button imageButton;

    private ArtworkFull artworkFull;

    private File imageFile;

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
                restoredBox.setDisable(true);

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

    private void initComboBox(){
        Flux<Owner> flux = ownerR2dbcDao.findAll();
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
        Flux<Collection> fluxCollection = collectionR2dbcDao.findAll();
        List<Collection> collectionList = new ArrayList<>();
        fluxCollection.doOnComplete(() -> {
            collectionBox.getItems().addAll(collectionList
                    .stream()
                    .map(Collection::getCollectionName)
                    .collect(Collectors.toList()));
            collectionBox.getItems().add(null);
        }).subscribe(collectionList::add);

        Flux<ArtworkState> fluxState = stateR2dbcDao.findAll();
        List<ArtworkState> stateList = new ArrayList<>();
        fluxState.doOnComplete(() -> stateBox.getItems().addAll(stateList
                .stream()
                .map(ArtworkState::getStateName)
                .collect(Collectors.toList()))).subscribe(stateList::add);

        restoredBox.getItems().addAll(Arrays.asList("Neuf", "Restauré"));
    }

    public void updateArtwork(){
        if(!authorField.getText().equals(artworkFull.getAuthor())){
            artworkFull.setAuthor(authorField.getText());
        }
        if(!nameField.getText().equals(artworkFull.getName())){
            artworkFull.setName(nameField.getText());
        }
        if(!dateField.getValue().isEqual(artworkFull.getDate())){
            artworkFull.setDate(dateField.getValue());
        }
        if(!borrowField.getValue().equals(artworkFull.getDateBorrowed())){
            artworkFull.setDateBorrowed(borrowField.getValue());
        }
        if(!returnField.getValue().equals(artworkFull.getReturnDate())){
            artworkFull.setReturnDate(returnField.getValue());
        }
        if(!stateField.getText().equals(artworkFull.getStoredLocation())){
            artworkFull.setStoredLocation(stateField.getText());
        }
        if(certificateBox.isSelected() != artworkFull.isCertified()){
            artworkFull.setCertified(certificateBox.isSelected());
        }
        if(storedBox.isSelected() != artworkFull.isStored()){
            artworkFull.setStored(storedBox.isSelected());
        }
        String firstname = ownerBox.getValue().split(" ")[0];
        String lastname = ownerBox.getValue().split(" ")[1];
        if(!(firstname.equals(artworkFull.getOwnerFirstname()) && lastname.equals(artworkFull.getOwnerLastname()))){

        }
        if (SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            if(!pictureView.getImage().equals(artworkFull.getImage())){
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
            if(!descField.getText().equals(artworkFull.getDesc())){
                artworkFull.setDesc(descField.getText());
            }
            if(!heigthField.getText().equals(artworkFull.getHeight())){
                artworkFull.setHeight(heigthField.getText());
            }
            if(!widthField.getText().equals(artworkFull.getWidth())){
                artworkFull.setWidth(widthField.getText());
            }
            if(!perimeterField.getText().equals(artworkFull.getPerimeter())){
                artworkFull.setPerimeter(perimeterField.getText());
            }
            if(!insuranceField.getText().equals(artworkFull.getInsuranceNumber())){
                artworkFull.setInsuranceNumber(insuranceField.getText());
            }
        }
    }

    @FXML
    public void onAddPicture(){
        Stage stage;
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

    @Override
    public void initialize(URL location, ResourceBundle resources){
        updatePermissions();
        initComboBox();
    }
}
