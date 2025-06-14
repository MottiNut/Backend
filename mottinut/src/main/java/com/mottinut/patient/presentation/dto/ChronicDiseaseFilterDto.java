package com.mottinut.patient.presentation.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChronicDiseaseFilterDto {
    private String code;
    private String description;
}
