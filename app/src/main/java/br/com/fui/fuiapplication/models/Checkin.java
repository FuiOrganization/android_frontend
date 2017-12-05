package br.com.fui.fuiapplication.models;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by guilherme on 21/11/17.
 */

public class Checkin {
    private Experience experience;
    private Date createdAt;
    private static final SimpleDateFormat defaultDateFormat = new SimpleDateFormat("dd/MM/yy HH:mm");

    public Checkin(int experienceId, String experienceName, String experienceDescription, String experienceImage, Date createdAt) {
        this.experience = new Experience(experienceId, experienceName, experienceDescription, experienceImage, false, true);
        this.createdAt = createdAt;
    }

    public Experience getExperience() { return experience; }

    public void setExperience(Experience experience) { this.experience = experience; }

    public Date getCreatedAt() { return createdAt; }

    public String getFormattedDate(){ return defaultDateFormat.format(this.getCreatedAt()); }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
