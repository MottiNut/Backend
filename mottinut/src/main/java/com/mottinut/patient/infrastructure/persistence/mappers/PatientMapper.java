package com.mottinut.patient.infrastructure.persistence.mappers;

import com.mottinut.auth.infrastructure.persistence.entities.PatientEntity;
import com.mottinut.auth.infrastructure.persistence.entities.UserEntity;
import com.mottinut.patient.domain.entity.MedicalHistory;
import com.mottinut.patient.domain.entity.PatientProfile;
import com.mottinut.patient.domain.valueobjects.MedicalHistoryId;
import com.mottinut.patient.domain.valueobjects.PatientId;
import com.mottinut.patient.infrastructure.persistence.entities.MedicalHistoryEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PatientMapper {

    public PatientProfile toPatientProfile(UserEntity userEntity) {
        if (!(userEntity instanceof PatientEntity)) {
            throw new IllegalArgumentException("La entidad debe ser de tipo PatientEntity");
        }

        PatientEntity patientEntity = (PatientEntity) userEntity;

        return new PatientProfile(
                PatientId.of(patientEntity.getUserId()),
                patientEntity.getFirstName(),
                patientEntity.getLastName(),
                patientEntity.getEmail(),
                patientEntity.getBirthDate(),
                patientEntity.getPhone(),
                patientEntity.getHeight(),
                patientEntity.getWeight(),
                Boolean.TRUE.equals(patientEntity.getHasMedicalCondition()),
                patientEntity.getChronicDisease(),
                patientEntity.getAllergies(),
                patientEntity.getDietaryPreferences(),
                patientEntity.getEmergencyContact(),
                patientEntity.getGender(),
                patientEntity.getCreatedAt()
        );
    }

    public MedicalHistory toDomain(MedicalHistoryEntity entity) {
        return new MedicalHistory(
                entity.getHistoryId() != null ? MedicalHistoryId.of(entity.getHistoryId()) : null,
                PatientId.of(entity.getPatientId()),
                entity.getConsultationDate(),
                entity.getWaistCircumference(),
                entity.getHipCircumference(),
                entity.getBodyFatPercentage(),
                entity.getBloodPressure(),
                entity.getHeartRate(),
                entity.getBloodGlucose(),
                entity.getLipidProfile(),
                entity.getEatingHabits(),
                entity.getWaterConsumption(),
                entity.getSupplementation(),
                entity.getCaloricIntake(),
                entity.getMacronutrients(),
                entity.getFoodPreferences(),
                entity.getFoodRelationship(),
                entity.getStressLevel(),
                entity.getSleepQuality(),
                entity.getNutritionalObjectives(),
                entity.getPatientEvolution(),
                entity.getProfessionalNotes(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public MedicalHistoryEntity toEntity(MedicalHistory domain) {
        return MedicalHistoryEntity.builder()
                .historyId(domain.getHistoryId() != null ? domain.getHistoryId().getValue() : null)
                .patientId(domain.getPatientId().getValue())
                .consultationDate(domain.getConsultationDate())
                .waistCircumference(domain.getWaistCircumference())
                .hipCircumference(domain.getHipCircumference())
                .bodyFatPercentage(domain.getBodyFatPercentage())
                .bloodPressure(domain.getBloodPressure())
                .heartRate(domain.getHeartRate())
                .bloodGlucose(domain.getBloodGlucose())
                .lipidProfile(domain.getLipidProfile())
                .eatingHabits(domain.getEatingHabits())
                .waterConsumption(domain.getWaterConsumption())
                .supplementation(domain.getSupplementation())
                .caloricIntake(domain.getCaloricIntake())
                .macronutrients(domain.getMacronutrients())
                .foodPreferences(domain.getFoodPreferences())
                .foodRelationship(domain.getFoodRelationship())
                .stressLevel(domain.getStressLevel())
                .sleepQuality(domain.getSleepQuality())
                .nutritionalObjectives(domain.getNutritionalObjectives())
                .patientEvolution(domain.getPatientEvolution())
                .professionalNotes(domain.getProfessionalNotes())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
