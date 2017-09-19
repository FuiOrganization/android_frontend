package br.com.fui.fuiapplication;

import java.io.Serializable;

/**
 * Created by guilherme on 19/09/17.
 */

public class Experience implements Serializable {
    private String title;
    private String description;
    private Integer imageId;

    public Experience(String title, String description, Integer imageId) {
        this.title = title;
        this.description = description;
        this.imageId = imageId;
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

    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }
}
