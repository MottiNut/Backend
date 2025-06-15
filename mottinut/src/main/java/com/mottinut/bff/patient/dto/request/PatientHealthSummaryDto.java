package com.mottinut.bff.patient.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientHealthSummaryDto {
    private Long patientId;
    private String fullName;
    private Integer age;
    private Double bmi;
    private String bmiCategory;
    private Boolean hasMedicalCondition;
    private String chronicDisease;
    private String gender;
    private LocalDate lastConsultationDate;
    private String bloodPressure;
    private Double bloodGlucose;
    private Integer stressLevel;
    private Integer sleepQuality;
    private Double waistHipRatio;
    private Integer totalConsultations;
}