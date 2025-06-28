package com.mottinut.bff.patient.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientSortOptionsDto {
    private List<SortFieldDto> sortFields;
    private List<SortOrderDto> sortOrders;
}