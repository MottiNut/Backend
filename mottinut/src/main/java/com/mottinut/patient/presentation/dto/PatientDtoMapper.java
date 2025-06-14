package com.mottinut.patient.presentation.dto;
import com.mottinut.patient.domain.entity.MedicalHistory;
import com.mottinut.patient.domain.entity.PatientProfile;
import com.mottinut.patient.domain.entity.PatientWithHistory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Component
public class PatientDtoMapper {

    public PatientProfileDto toDto(PatientProfile patient) {
        PatientProfileDto dto = new PatientProfileDto();
        dto.setPatientId(patient.getPatientId().getValue());
        dto.setFirstName(patient.getFirstName());
        dto.setLastName(patient.getLastName());
        dto.setFullName(patient.getFullName());
        dto.setEmail(patient.getEmail());
        dto.setBirthDate(patient.getBirthDate());
        dto.setAge(patient.getAge());
        dto.setPhone(patient.getPhone());
        dto.setHeight(patient.getHeight());
        dto.setWeight(patient.getWeight());

        if (patient.getHeight() != null && patient.getWeight() != null && patient.getHeight() > 0) {
            dto.setBmi(patient.calculateBMI());
        }

        dto.setHasMedicalCondition(patient.isHasMedicalCondition());
        dto.setChronicDisease(patient.getChronicDisease());
        dto.setAllergies(patient.getAllergies());
        dto.setDietaryPreferences(patient.getDietaryPreferences());
        dto.setEmergencyContact(patient.getEmergencyContact());
        dto.setCreatedAt(patient.getCreatedAt());

        return dto;
    }

    public MedicalHistoryDto toDto(MedicalHistory history) {
        MedicalHistoryDto dto = new MedicalHistoryDto();
        dto.setHistoryId(history.getHistoryId() != null ? history.getHistoryId().getValue() : null);
        dto.setPatientId(history.getPatientId().getValue());
        dto.setConsultationDate(history.getConsultationDate());
        dto.setWaistCircumference(history.getWaistCircumference());
        dto.setHipCircumference(history.getHipCircumference());
        dto.setBodyFatPercentage(history.getBodyFatPercentage());
        dto.setBloodPressure(history.getBloodPressure());
        dto.setHeartRate(history.getHeartRate());
        dto.setBloodGlucose(history.getBloodGlucose());
        dto.setLipidProfile(history.getLipidProfile());
        dto.setEatingHabits(history.getEatingHabits());
        dto.setWaterConsumption(history.getWaterConsumption());
        dto.setSupplementation(history.getSupplementation());
        dto.setCaloricIntake(history.getCaloricIntake());
        dto.setMacronutrients(history.getMacronutrients());
        dto.setFoodPreferences(history.getFoodPreferences());
        dto.setFoodRelationship(history.getFoodRelationship());
        dto.setStressLevel(history.getStressLevel());
        dto.setSleepQuality(history.getSleepQuality());
        dto.setNutritionalObjectives(history.getNutritionalObjectives());
        dto.setPatientEvolution(history.getPatientEvolution());
        dto.setProfessionalNotes(history.getProfessionalNotes());
        dto.setCreatedAt(history.getCreatedAt());
        dto.setUpdatedAt(history.getUpdatedAt());
        return dto;
    }

    public PatientWithHistoryDto toDto(PatientWithHistory patientWithHistory) {
        PatientWithHistoryDto dto = new PatientWithHistoryDto();
        dto.setPatient(toDto(patientWithHistory.getPatient()));
        dto.setMedicalHistories(patientWithHistory.getMedicalHistories().stream()
                .map(this::toDto)
                .collect(Collectors.toList()));

        MedicalHistory latestHistory = patientWithHistory.getLatestHistory();
        if (latestHistory != null) {
            dto.setLatestHistory(toDto(latestHistory));
        }

        dto.setTotalHistories(patientWithHistory.getMedicalHistories().size());
        return dto;
    }
}

