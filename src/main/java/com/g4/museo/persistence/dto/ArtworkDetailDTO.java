package com.g4.museo.persistence.dto;

public class ArtworkDetailDTO {
    private int idartwork;
    private String width;
    private String height;
    private String perimeter;
    private String insuranceNumber;
    private String material;
    private String technic;
    private String type;
    private boolean restored;

    public int getIdartwork() {
        return idartwork;
    }

    public void setIdartwork(int idartwork) {
        this.idartwork = idartwork;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getPerimeter() {
        return perimeter;
    }

    public void setPerimeter(String perimeter) {
        this.perimeter = perimeter;
    }

    public String getInsuranceNumber() {
        return insuranceNumber;
    }

    public void setInsuranceNumber(String insuranceNumber) {
        this.insuranceNumber = insuranceNumber;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getTechnic() {
        return technic;
    }

    public void setTechnic(String technic) {
        this.technic = technic;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRestored() {
        return restored;
    }

    public void setRestored(boolean restored) {
        this.restored = restored;
    }
}
