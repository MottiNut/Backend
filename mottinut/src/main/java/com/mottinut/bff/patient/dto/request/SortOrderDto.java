package com.mottinut.bff.patient.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SortOrderDto {
    private String code;
    private String description;
}
