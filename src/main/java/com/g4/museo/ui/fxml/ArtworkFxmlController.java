package com.g4.museo.ui.fxml;

import com.g4.museo.persistence.dto.CollectionDTO;
import com.g4.museo.persistence.dto.OwnerDTO;
import com.g4.museo.persistence.jdbc.CollectionJdbcDao;
import com.g4.museo.persistence.jdbc.OwnerJdbcDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Component
public class ArtworkFxmlController extends FXMLController implements Initializable {

    @Autowired
    OwnerJdbcDao ownerJdbcDao;

    @Autowired
    CollectionJdbcDao collectionJdbcDao;

    @FXML
    ComboBox ownerBox;

    @FXML
    ComboBox collectionBox;

    @FXML
    public void onReturn(ActionEvent event){
        Scene scene = (Scene) ((Node) event.getSource()).getScene();
        Stage stage = (Stage)scene.getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources){
        initComboBox();
    }

    private void initComboBox(){
        List<OwnerDTO> owners = ownerJdbcDao.getOwners();
        owners.forEach(owner -> {
            if(owner.getFirstname() != null && owner.getLastname() != null){
                StringBuilder fullname = new StringBuilder(owner.getFirstname());
                fullname.append(" ");
                fullname.append(owner.getLastname());
                ownerBox.getItems().add(fullname.toString());
            } else if(owner.getOrga() != null) {
                ownerBox.getItems().add(owner.getOrga());
            }
        });
        collectionBox.getItems().addAll(collectionJdbcDao.getCollections()
                .stream()
                .map(CollectionDTO::getCollectionName)
                .collect(Collectors.toList()));
    }

    @FXML
    public void addArtwork(){

     }
}
