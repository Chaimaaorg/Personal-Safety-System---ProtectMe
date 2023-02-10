package com.example.personalsafetysystem.UserDashboard;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Police implements Serializable {
    String name, distance, contact, frq_id, image;
    private double latitude;
    private double longitude;

    public Police() {
    }

    public Police(JSONObject jsonObject) throws JSONException {
        this.name = jsonObject.getString("name");
        this.distance = jsonObject.getString("distance");
        this.contact = jsonObject.getString("timezone");
        this.frq_id = jsonObject.getString("fsq_id");
        JSONObject icon = jsonObject.getJSONArray("categories")
                .getJSONObject(0)
                .getJSONObject("icon");
        String prefix = icon.getString("prefix");
        String suffix = icon.getString("suffix");
        this.image = prefix + "64" + suffix; // Choose the desired size (e.g., 64) for the image
        JSONObject geocodes = jsonObject.getJSONObject("geocodes").getJSONObject("main");
        this.latitude = geocodes.getDouble("latitude");
        this.longitude = geocodes.getDouble("longitude");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getFrq_id() {
        return frq_id;
    }

    public void setFrq_id(String frq_id) {
        this.frq_id = frq_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


}


