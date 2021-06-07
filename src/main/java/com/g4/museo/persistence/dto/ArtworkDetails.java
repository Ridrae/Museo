package com.g4.museo.persistence.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

@Builder
@Getter
@Setter
@ToString
public class ArtworkDetails {
    @Id
    private Integer iddetails;
    private Integer idartwork;
    private String width;
    private String height;
    private String perimeter;
    private String insuranceNumber;
    private String material;
    private String technic;
    private String type;
    private boolean restored;
}
