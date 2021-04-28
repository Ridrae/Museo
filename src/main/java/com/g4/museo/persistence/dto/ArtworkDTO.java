package com.g4.museo.persistence.dto;

import java.sql.Timestamp;

public class ArtworkDTO {
    private int idartwork;
    private String name;
    private String authorName;
    byte[] picture;
    private Timestamp date;
    private boolean certified;
    private String storedLocation;
    private String collectionName;
    private String state;

    public int getIdartwork() {
        return idartwork;
    }

    public void setIdartwork(int idartwork) {
        this.idartwork = idartwork;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor_id() {
        return authorName;
    }

    public void setAuthor_id(String authorName) {
        this.authorName = authorName;
    }

    public byte[] getPicture() {
        return picture;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public boolean isCertified() {
        return certified;
    }

    public void setCertified(boolean certified) {
        this.certified = certified;
    }

    public String getStoredLocation() {
        return storedLocation;
    }

    public void setStoredLocation(String storedLocation) {
        this.storedLocation = storedLocation;
    }

    public String getCollectionId() {
        return collectionName;
    }

    public void setCollectionId(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getStateId() {
        return state;
    }

    public void setStateId(String stateId) {
        this.state = state;
    }
}
