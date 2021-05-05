package com.g4.museo.persistence.dto;

import java.sql.Timestamp;

public class ArtworkBorrowDTO {
    private int idartwork;
    private int idowner;
    private Timestamp dateBorrowed;
    private Timestamp returnDate;
    private boolean stored;
    private boolean longTerm;

    public int getIdartwork() {
        return idartwork;
    }

    public void setIdartwork(int idartwork) {
        this.idartwork = idartwork;
    }

    public int getIdowner() {
        return idowner;
    }

    public void setIdowner(int idowner) {
        this.idowner = idowner;
    }

    public Timestamp getDateBorrowed() {
        return dateBorrowed;
    }

    public void setDateBorrowed(Timestamp dateBorrowed) {
        this.dateBorrowed = dateBorrowed;
    }

    public Timestamp getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Timestamp returnDate) {
        this.returnDate = returnDate;
    }

    public boolean isStored() {
        return stored;
    }

    public void setStored(boolean stored) {
        this.stored = stored;
    }

    public boolean isLongTerm() {
        return longTerm;
    }

    public void setLongTerm(boolean longTerm) {
        this.longTerm = longTerm;
    }
}
