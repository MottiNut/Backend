package com.mottinut.bff.auth.dto.request;

public class UpdatePatientProfileRequest {
    private String firstName;
    private String lastName;
    private String phone;
    private Double height;
    private Double weight;
    private Boolean hasMedicalCondition;
    private String chronicDisease;
    private String allergies;
    private String dietaryPreferences;
    private String emergencyContact;
    private String gender;

    // Getters y setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public Boolean getHasMedicalCondition() { return hasMedicalCondition; }
    public void setHasMedicalCondition(Boolean hasMedicalCondition) { this.hasMedicalCondition = hasMedicalCondition; }

    public String getChronicDisease() { return chronicDisease; }
    public void setChronicDisease(String chronicDisease) { this.chronicDisease = chronicDisease; }

    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) { this.allergies = allergies; }

    public String getDietaryPreferences() { return dietaryPreferences; }
    public void setDietaryPreferences(String dietaryPreferences) { this.dietaryPreferences = dietaryPreferences; }

    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
}