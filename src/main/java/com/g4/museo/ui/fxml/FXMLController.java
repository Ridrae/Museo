package com.g4.museo.ui.fxml;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class FXMLController {

    protected Logger log = LoggerFactory.getLogger(FXMLController.class);

    private static String prefix = "Controller";

    private Parent root;

    public Parent load() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        String name = getFxmlName();
        loader.setLocation(getClass().getClassLoader().getResource(name));
        loader.setControllerFactory(clz -> this);
        return loader.load();
    }

    private String getFxmlName(){
        String name = this.getClass().getSimpleName();
        name = name.replaceAll("\\.", "/");
        if(name.endsWith(prefix)){
            name = name.substring(0, name.lastIndexOf(prefix));
        }
        return String.format("fxml/%s.fxml", name).toLowerCase();
    }

    public Parent getView() throws IOException{
        if(root == null){
            root = load();
        }
        return root;
    }
}
