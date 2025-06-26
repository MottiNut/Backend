package com.mottinut.patient.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class PatientWithHistory {
    private final PatientProfile patient;
    private final List<MedicalHistory> medicalHistories;

    public MedicalHistory getLatestHistory() {
        return medicalHistories.stream()
                .max(Comparator.comparing(MedicalHistory::getConsultationDate))
                .orElse(null);
    }

    public List<MedicalHistory> getHistoriesInDateRange(LocalDate startDate, LocalDate endDate) {
        return medicalHistories.stream()
                .filter(history -> !history.getConsultationDate().isBefore(startDate) &&
                        !history.getConsultationDate().isAfter(endDate))
                .sorted(Comparator.comparing(MedicalHistory::getConsultationDate).reversed())
                .collect(Collectors.toList());
    }
}
