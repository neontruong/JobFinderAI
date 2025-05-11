package com.example.jobfinder01.CV;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Skill implements Serializable {
    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    public Skill() {}

    public Skill(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}