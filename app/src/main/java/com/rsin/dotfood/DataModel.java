package com.rsin.dotfood;

import java.io.Serializable;
import java.util.List;

public class DataModel implements Serializable {
    private String title;
    private String description;
    private String state;
    private String city;
    private String pinCode;
    private String address;
    private String latitude;
    private String longitude;
    private String name;
    private String uuid;
    private String phone;
    private List<String > all_photos;
    private String document_photo;

    public DataModel() {
    }

    public DataModel(String title, String description, String state, String city, String pinCode, String address, String latitude, String longitude, String name, String uuid, String phone, List<String> all_photos, String document_photo) {
        this.title = title;
        this.description = description;
        this.state = state;
        this.city = city;
        this.pinCode = pinCode;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.uuid = uuid;
        this.phone = phone;
        this.all_photos = all_photos;
        this.document_photo = document_photo;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAll_photos(List<String> all_photos) {
        this.all_photos = all_photos;
    }

    public void setDocument_photo(String document_photo) {
        this.document_photo = document_photo;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getState() {
        return state;
    }

    public String getCity() {
        return city;
    }

    public String getPinCode() {
        return pinCode;
    }

    public String getAddress() {
        return address;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getName() {
        return name;
    }

    public String getUuid() {
        return uuid;
    }

    public String getPhone() {
        return phone;
    }

    public List<String> getAll_photos() {
        return all_photos;
    }

    public String getDocument_photo() {
        return document_photo;
    }
}
