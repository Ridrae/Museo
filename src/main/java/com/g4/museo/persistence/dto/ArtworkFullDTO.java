package com.g4.museo.persistence.dto;

public class ArtworkFullDTO {
    private ArtworkDTO artworkDTO = new ArtworkDTO();
    private ArtworkBorrowDTO artworkBorrowDTO = new ArtworkBorrowDTO();
    private ArtworkDetailDTO artworkDetailDTO = new ArtworkDetailDTO();
    private AuthorDTO authorDTO = new AuthorDTO();
    private CollectionDTO collectionDTO = new CollectionDTO();
    private OwnerDTO ownerDTO = new OwnerDTO();
    private StateDTO stateDTO = new StateDTO();

    public ArtworkDTO getArtworkDTO() {
        return artworkDTO;
    }

    public void setArtworkDTO(ArtworkDTO artworkDTO) {
        this.artworkDTO = artworkDTO;
    }

    public ArtworkBorrowDTO getArtworkBorrowDTO() {
        return artworkBorrowDTO;
    }

    public void setArtworkBorrowDTO(ArtworkBorrowDTO artworkBorrowDTO) {
        this.artworkBorrowDTO = artworkBorrowDTO;
    }

    public ArtworkDetailDTO getArtworkDetailDTO() {
        return artworkDetailDTO;
    }

    public void setArtworkDetailDTO(ArtworkDetailDTO artworkDetailDTO) {
        this.artworkDetailDTO = artworkDetailDTO;
    }

    public AuthorDTO getAuthorDTO() {
        return authorDTO;
    }

    public void setAuthorDTO(AuthorDTO authorDTO) {
        this.authorDTO = authorDTO;
    }

    public CollectionDTO getCollectionDTO() {
        return collectionDTO;
    }

    public void setCollectionDTO(CollectionDTO collectionDTO) {
        this.collectionDTO = collectionDTO;
    }

    public OwnerDTO getOwnerDTO() {
        return ownerDTO;
    }

    public void setOwnerDTO(OwnerDTO ownerDTO) {
        this.ownerDTO = ownerDTO;
    }

    public StateDTO getStateDTO() {
        return stateDTO;
    }

    public void setStateDTO(StateDTO stateDTO) {
        this.stateDTO = stateDTO;
    }
}
