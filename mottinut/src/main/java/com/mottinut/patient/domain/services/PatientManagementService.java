package com.mottinut.patient.domain.services;

import com.mottinut.auth.domain.services.UserService;
import com.mottinut.patient.domain.entity.MedicalHistory;
import com.mottinut.patient.domain.entity.PatientProfile;
import com.mottinut.patient.domain.entity.PatientWithHistory;
import com.mottinut.patient.domain.enums.ChronicDiseaseFilter;
import com.mottinut.patient.domain.repositories.MedicalHistoryRepository;
import com.mottinut.patient.domain.repositories.PatientProfileRepository;
import com.mottinut.patient.domain.valueobjects.MedicalHistoryId;
import com.mottinut.patient.domain.valueobjects.PatientId;
import com.mottinut.shared.domain.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientManagementService {
    private final PatientProfileRepository patientRepository;
    private final MedicalHistoryRepository medicalHistoryRepository;
    private final UserService userService;

    public List<PatientProfile> getAllPatients() {
        return patientRepository.findAll();
    }

    public PatientProfile getPatientById(PatientId patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new NotFoundException("Paciente no encontrado"));
    }

    public PatientWithHistory getPatientWithHistory(PatientId patientId) {
        PatientProfile patient = getPatientById(patientId);
        List<MedicalHistory> histories = medicalHistoryRepository.findByPatientId(patientId);
        return new PatientWithHistory(patient, histories);
    }

    public List<PatientProfile> filterPatientsByChronicDisease(ChronicDiseaseFilter filter) {
        if (filter == ChronicDiseaseFilter.ALL) {
            return getAllPatients();
        }
        if (filter == ChronicDiseaseFilter.NONE) {
            return patientRepository.findAll().stream()
                    .filter(patient -> !patient.isHasMedicalCondition() ||
                            patient.getChronicDisease() == null ||
                            patient.getChronicDisease().trim().isEmpty())
                    .collect(Collectors.toList());
        }
        return patientRepository.findByChronicDisease(filter.getDescription().toLowerCase());
    }

    public MedicalHistory createMedicalHistory(PatientId patientId,
                                               LocalDate consultationDate,
                                               Double height,
                                               Double weight,
                                               Double waistCircumference,
                                               Double hipCircumference,
                                               Double bodyFatPercentage,
                                               String bloodPressure,
                                               Integer heartRate,
                                               Double bloodGlucose,
                                               String lipidProfile,
                                               String eatingHabits,
                                               Double waterConsumption,
                                               String supplementation,
                                               Double caloricIntake,
                                               String macronutrients,
                                               String foodPreferences,
                                               String foodRelationship,
                                               Integer stressLevel,
                                               Integer sleepQuality,
                                               String nutritionalObjectives,
                                               String patientEvolution,
                                               String professionalNotes) {

        // Verificar que el paciente existe
        getPatientById(patientId);

        // Actualizar peso y altura si se proporcionan
        if (height != null || weight != null) {
            patientRepository.updateWeightAndHeight(patientId, weight, height);
        }

        // Crear el historial médico
        MedicalHistory medicalHistory = MedicalHistory.create(
                patientId, consultationDate, waistCircumference, hipCircumference,
                bodyFatPercentage, bloodPressure, heartRate, bloodGlucose,
                lipidProfile, eatingHabits, waterConsumption, supplementation,
                caloricIntake, macronutrients, foodPreferences, foodRelationship,
                stressLevel, sleepQuality, nutritionalObjectives, patientEvolution,
                professionalNotes
        );

        return medicalHistoryRepository.save(medicalHistory);
    }

    public MedicalHistory updateMedicalHistory(MedicalHistoryId historyId,
                                               Double height,
                                               Double weight,
                                               Double waistCircumference,
                                               Double hipCircumference,
                                               Double bodyFatPercentage,
                                               String bloodPressure,
                                               Integer heartRate,
                                               Double bloodGlucose,
                                               String lipidProfile,
                                               String eatingHabits,
                                               Double waterConsumption,
                                               String supplementation,
                                               Double caloricIntake,
                                               String macronutrients,
                                               String foodPreferences,
                                               String foodRelationship,
                                               Integer stressLevel,
                                               Integer sleepQuality,
                                               String nutritionalObjectives,
                                               String patientEvolution,
                                               String professionalNotes) {

        MedicalHistory existingHistory = medicalHistoryRepository.findById(historyId)
                .orElseThrow(() -> new NotFoundException("Historial médico no encontrado"));

        // Actualizar peso y altura del paciente si se proporcionan
        // Usar el PatientId del historial existente
        if (height != null || weight != null) {
            patientRepository.updateWeightAndHeight(existingHistory.getPatientId(), weight, height);
        }

        MedicalHistory updatedHistory = existingHistory.update(
                waistCircumference, hipCircumference, bodyFatPercentage,
                bloodPressure, heartRate, bloodGlucose, lipidProfile,
                eatingHabits, waterConsumption, supplementation, caloricIntake,
                macronutrients, foodPreferences, foodRelationship, stressLevel,
                sleepQuality, nutritionalObjectives, patientEvolution, professionalNotes
        );

        return medicalHistoryRepository.save(updatedHistory);
    }


    public List<MedicalHistory> getPatientHistoriesInDateRange(PatientId patientId, LocalDate startDate, LocalDate endDate) {
        return medicalHistoryRepository.findByPatientIdAndDateRange(patientId, startDate, endDate);
    }
}
