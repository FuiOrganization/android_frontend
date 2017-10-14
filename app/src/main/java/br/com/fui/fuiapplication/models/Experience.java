package br.com.fui.fuiapplication.models;

import java.io.Serializable;

/**
 * Created by guilherme on 19/09/17.
 */

public class Experience implements Serializable {
    private String title;
    private String description;
    private String image;
    private boolean sponsored;

    public Experience(String title, String description, String image, boolean sponsored) {
        this.title = title;
        this.description = description;
        this.image = image;
        this.sponsored = sponsored;
    }

    public boolean isSponsored() {
        return sponsored;
    }

    public void setSponsored(boolean sponsored) {
        this.sponsored = sponsored;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
