package com.g4.museo.persistence.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
public class ArtworkFullDTO {
    private Artwork artwork;
    private ArtworkBorrow artworkBorrow;
    private ArtworkDetails artworkDetails;
    private Collection collection;
    private Owner owner;
    private ArtworkState artworkState;

}
