package com.g4.museo.persistence.dto;

import io.r2dbc.spi.Blob;
import javafx.scene.image.Image;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.lang.Nullable;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@ToString
public class ArtworkFull {
    @Id
    private Integer idartwork;
    private String name;
    private String author;
    private Blob picture;
    private LocalDate date;
    private boolean certified;
    private String storedLocation;
    private Integer collectionID = null;
    private int stateID;
    private boolean borrowed;
    private String desc;
    private String lastUpdatedBy;
    private Integer idborrow;
    private Integer idowner;
    private LocalDate dateBorrowed;
    private LocalDate returnDate;
    private boolean stored;
    private boolean longTerm;
    private String width;
    private String height;
    private String perimeter;
    private String insuranceNumber;
    private String material;
    private String technic;
    private String type;
    private boolean restored;
    private String collectionName;
    private String stateName;
    private String ownerFirstname;
    private String ownerLastname;
    private String ownerOrga;
    private String ownerAddress;
    private Image image;
}
