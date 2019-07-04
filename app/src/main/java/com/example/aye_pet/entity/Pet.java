package com.example.aye_pet.entity;

import java.io.Serializable;

public class Pet implements Serializable {
    String ownerId;
    String name;
    String type;
    String gender;
    String size;
    String age;
    String vaccinated;
    String dewormed;
    String neutered;
    String location;
    String postedDate;
    String description;
    String imageURL;

    public Pet() {
    }

    public Pet(String ownerId, String name, String type, String gender, String size, String age, String vaccinated, String dewormed, String neutered, String location, String postedDate, String description, String imageURL) {
        this.ownerId = ownerId;
        this.name = name;
        this.type = type;
        this.gender = gender;
        this.size = size;
        this.age = age;
        this.vaccinated = vaccinated;
        this.dewormed = dewormed;
        this.neutered = neutered;
        this.location = location;
        this.postedDate = postedDate;
        this.description = description;
        this.imageURL = imageURL;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getGender() {
        return gender;
    }

    public String getSize() {
        return size;
    }

    public String getAge() {
        return age;
    }

    public String getVaccinated() {
        return vaccinated;
    }

    public String getDewormed() {
        return dewormed;
    }

    public String getNeutered() {
        return neutered;
    }

    public String getLocation() {
        return location;
    }

    public String getPostedDate() {
        return postedDate;
    }

    public String getDescription() {
        return description;
    }

    public String getImageURL() {
        return imageURL;
    }

}
