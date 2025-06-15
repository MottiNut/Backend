package com.mottinut.bff.patient.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientWithHistoryDto {
    private PatientProfileDto patient;
    private List<MedicalHistoryDto> medicalHistories;
    private MedicalHistoryDto latestHistory;
    private Integer totalHistories;
}

