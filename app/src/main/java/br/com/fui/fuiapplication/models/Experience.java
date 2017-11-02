package br.com.fui.fuiapplication.models;

import java.io.Serializable;

/**
 * Created by guilherme on 19/09/17.
 */

public class Experience implements Serializable {
    private int id;
    private String title;
    private String description;
    private String image;
    private boolean sponsored;
    private boolean hasUserVisited;

    public Experience(int id, String title, String description, String image, boolean sponsored, boolean hasUserVisited) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.image = image;
        this.sponsored = sponsored;
        this.hasUserVisited = hasUserVisited;
    }

    public boolean hasUserVisited() { return hasUserVisited; }

    public void setUserVisited(boolean hasUserVisited) { this.hasUserVisited = hasUserVisited; }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

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
