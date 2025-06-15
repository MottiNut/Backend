package com.mottinut.bff.patient.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientProgressDto {
    private Integer periodDays;
    private Integer totalConsultations;
    private Double bodyFatChange;
    private Double waistCircumferenceChange;
    private Double averageSleepQuality;
    private Double averageStressLevel;
}
