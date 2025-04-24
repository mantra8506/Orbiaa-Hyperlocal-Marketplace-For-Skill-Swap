package com.example.skillswap;

public class UserDetails2 {
    public String email, occupation, skill, location, workLink, description, achievements, imageUrl;
    public String experience; // Change this from int to String

    public UserDetails2(String email, String occupation, String skill, String experience,
                        String location, String workLink, String description,
                        String achievements, String imageUrl) {
        this.email = email;
        this.occupation = occupation;
        this.skill = skill;
        this.experience = experience;
        this.location = location;
        this.workLink = workLink;
        this.description = description;
        this.achievements = achievements;
        this.imageUrl = imageUrl;
    }
}

