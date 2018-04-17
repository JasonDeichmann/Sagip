package com.example.leebet_pc.saggip;

public class EmergencyContactModel {
    private String name;
    private String number;
    private String photo_uri;
    private boolean isSelected = false;

    public EmergencyContactModel(String name, String number, String photo_uri){
        this.name = name;
        this.number = number;
        this.photo_uri = photo_uri;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPhoto_uri() {
        return photo_uri;
    }

    public void setPhoto_uri(String photo_uri) {
        this.photo_uri = photo_uri;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
