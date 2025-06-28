package com.mottinut.patient.domain.repositories;

import com.mottinut.patient.domain.entity.MedicalHistory;
import com.mottinut.patient.domain.valueobjects.MedicalHistoryId;
import com.mottinut.patient.domain.valueobjects.PatientId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MedicalHistoryRepository {
    MedicalHistory save(MedicalHistory medicalHistory);
    Optional<MedicalHistory> findById(MedicalHistoryId historyId);
    List<MedicalHistory> findByPatientId(PatientId patientId);
    List<MedicalHistory> findByPatientIdAndDateRange(PatientId patientId, LocalDate startDate, LocalDate endDate);
    void deleteById(MedicalHistoryId historyId);
}
