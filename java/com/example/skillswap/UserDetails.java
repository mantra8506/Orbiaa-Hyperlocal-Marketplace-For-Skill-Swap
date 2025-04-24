package com.example.skillswap;

public class UserDetails {
    public String userId, occupation, skill, experience, location, workLink, description, achievements;

    public UserDetails() {
        // Default constructor required for calls to DataSnapshot.getValue(UserDetails.class)
    }

    public UserDetails(String userId, String occupation, String skill, String experience,
                       String location, String workLink, String description, String achievements) {
        this.userId = userId;
        this.occupation = occupation;
        this.skill = skill;
        this.experience = experience;
        this.location = location;
        this.workLink = workLink;
        this.description = description;
        this.achievements = achievements;
    }
}
