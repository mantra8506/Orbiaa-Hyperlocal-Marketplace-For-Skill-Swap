package com.example.skillswap;

public class User {
    private String firstName, lastName, email, skill, experience, location;

    public User() {
        // Default constructor required for Firebase
    }

    public User(String firstName, String lastName, String email, String skill, String experience, String location) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.skill = skill;
        this.experience = experience;
        this.location = location;
    }

    // Getters and Setters
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getSkill() { return skill; }
    public String getExperience() { return experience; }
    public String getLocation() { return location; }
}
