package br.com.fui.fuiapplication.models;

import java.util.Date;

/**
 * Created by guilherme on 21/11/17.
 */

public class Checkin {
    private int experienceId;
    private String experienceName;
    private Date createdAt;

    public Checkin(int experienceId, String experienceName, Date createdAt) {
        this.experienceId = experienceId;
        this.experienceName = experienceName;
        this.createdAt = createdAt;
    }

    public int getExperienceId() {
        return experienceId;
    }

    public void setExperienceId(int experienceId) {
        this.experienceId = experienceId;
    }

    public String getExperienceName() {
        return experienceName;
    }

    public void setExperienceName(String experienceName) {
        this.experienceName = experienceName;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
