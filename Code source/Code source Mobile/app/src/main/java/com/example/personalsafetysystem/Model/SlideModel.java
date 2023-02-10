package com.example.personalsafetysystem.Model;

public class SlideModel {
    private int image;
    private String heading;
    private String description;
    private int icon;

    public SlideModel(int icon, int image, String heading, String description) {
        this.icon = icon;
        this.image = image;
        this.heading = heading;
        this.description = description;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
