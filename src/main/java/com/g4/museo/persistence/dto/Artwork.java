package com.g4.museo.persistence.dto;

import io.r2dbc.spi.Blob;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import java.time.LocalDate;

@Builder
@Getter
@Setter
@ToString
public class Artwork {
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
}
