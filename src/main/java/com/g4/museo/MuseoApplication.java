package com.g4.museo;

import com.g4.museo.ui.GuiApplication;
import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MuseoApplication {

    public static void main(String[] args) {

        Application.launch(GuiApplication.class, args);
    }

}
