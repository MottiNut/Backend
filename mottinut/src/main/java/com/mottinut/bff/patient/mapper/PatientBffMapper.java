package com.mottinut.bff.patient.mapper;

import com.mottinut.bff.patient.dto.request.*;
import com.mottinut.patient.domain.entity.MedicalHistory;
import com.mottinut.patient.domain.entity.PatientProfile;
import com.mottinut.patient.domain.entity.PatientWithHistory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PatientBffMapper {

    public PatientProfileDto toPatientProfileDto(PatientProfile patient) {
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
            dto.setBmiCategory(calculateBmiCategory(patient.calculateBMI()));
        }

        dto.setHasMedicalCondition(patient.isHasMedicalCondition());
        dto.setChronicDisease(patient.getChronicDisease());
        dto.setAllergies(patient.getAllergies());
        dto.setDietaryPreferences(patient.getDietaryPreferences());
        dto.setEmergencyContact(patient.getEmergencyContact());
        dto.setGender(patient.getGender());
        dto.setCreatedAt(patient.getCreatedAt());

        return dto;
    }

    public MedicalHistoryDto toMedicalHistoryDto(MedicalHistory history) {
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

        // Agregar campos calculados para el BFF
        if (history.getWaistCircumference() != null && history.getHipCircumference() != null
                && history.getHipCircumference() > 0) {
            dto.setWaistHipRatio(history.getWaistCircumference() / history.getHipCircumference());
        }

        return dto;
    }

    public PatientWithHistoryDto toPatientWithHistoryDto(PatientWithHistory patientWithHistory) {
        PatientWithHistoryDto dto = new PatientWithHistoryDto();
        dto.setPatient(toPatientProfileDto(patientWithHistory.getPatient()));
        dto.setMedicalHistories(patientWithHistory.getMedicalHistories().stream()
                .map(this::toMedicalHistoryDto)
                .collect(Collectors.toList()));

        MedicalHistory latestHistory = patientWithHistory.getLatestHistory();
        if (latestHistory != null) {
            dto.setLatestHistory(toMedicalHistoryDto(latestHistory));
        }

        dto.setTotalHistories(patientWithHistory.getMedicalHistories().size());

        return dto;
    }

    public PatientHealthSummaryDto toPatientHealthSummaryDto(PatientWithHistory patientWithHistory) {
        PatientHealthSummaryDto dto = new PatientHealthSummaryDto();
        PatientProfile patient = patientWithHistory.getPatient();
        MedicalHistory latestHistory = patientWithHistory.getLatestHistory();

        dto.setPatientId(patient.getPatientId().getValue());
        dto.setFullName(patient.getFullName());
        dto.setAge(patient.getAge());

        if (patient.getHeight() != null && patient.getWeight() != null && patient.getHeight() > 0) {
            dto.setBmi(patient.calculateBMI());
            dto.setBmiCategory(calculateBmiCategory(patient.calculateBMI()));
        }

        dto.setHasMedicalCondition(patient.isHasMedicalCondition());
        dto.setChronicDisease(patient.getChronicDisease());
        dto.setGender(patient.getGender());

        if (latestHistory != null) {
            dto.setLastConsultationDate(latestHistory.getConsultationDate());
            dto.setBloodPressure(latestHistory.getBloodPressure());
            dto.setBloodGlucose(latestHistory.getBloodGlucose());
            dto.setStressLevel(latestHistory.getStressLevel());
            dto.setSleepQuality(latestHistory.getSleepQuality());

            if (latestHistory.getWaistCircumference() != null && latestHistory.getHipCircumference() != null
                    && latestHistory.getHipCircumference() > 0) {
                dto.setWaistHipRatio(latestHistory.getWaistCircumference() / latestHistory.getHipCircumference());
            }
        }

        dto.setTotalConsultations(patientWithHistory.getMedicalHistories().size());

        return dto;
    }

    public PatientProgressDto toPatientProgressDto(List<MedicalHistory> histories, int days) {
        PatientProgressDto dto = new PatientProgressDto();
        dto.setPeriodDays(days);
        dto.setTotalConsultations(histories.size());

        if (!histories.isEmpty()) {
            MedicalHistory latest = histories.get(0);
            MedicalHistory oldest = histories.get(histories.size() - 1);

            // Calcular progreso de peso (asumiendo que está en algún campo relacionado)
            if (latest.getBodyFatPercentage() != null && oldest.getBodyFatPercentage() != null) {
                dto.setBodyFatChange(latest.getBodyFatPercentage() - oldest.getBodyFatPercentage());
            }

            // Calcular progreso de circunferencias
            if (latest.getWaistCircumference() != null && oldest.getWaistCircumference() != null) {
                dto.setWaistCircumferenceChange(latest.getWaistCircumference() - oldest.getWaistCircumference());
            }

            // Promedio de calidad de sueño
            double avgSleepQuality = histories.stream()
                    .filter(h -> h.getSleepQuality() != null)
                    .mapToInt(MedicalHistory::getSleepQuality)
                    .average()
                    .orElse(0.0);
            dto.setAverageSleepQuality(avgSleepQuality);

            // Promedio de nivel de estrés
            double avgStressLevel = histories.stream()
                    .filter(h -> h.getStressLevel() != null)
                    .mapToInt(MedicalHistory::getStressLevel)
                    .average()
                    .orElse(0.0);
            dto.setAverageStressLevel(avgStressLevel);
        }

        return dto;
    }

    private String calculateBmiCategory(double bmi) {
        if (bmi < 18.5) return "Bajo peso";
        else if (bmi < 25) return "Normal";
        else if (bmi < 30) return "Sobrepeso";
        else return "Obesidad";
    }
}
