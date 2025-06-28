package com.mottinut.bff.auth.dto.request;

public class UpdateNutritionistProfileRequest {
    private String firstName;
    private String lastName;
    private String phone;
    private Integer yearsOfExperience;
    private String biography;

    // Getters y setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Integer getYearsOfExperience() { return yearsOfExperience; }
    public void setYearsOfExperience(Integer yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }

    public String getBiography() { return biography; }
    public void setBiography(String biography) { this.biography = biography; }
}
