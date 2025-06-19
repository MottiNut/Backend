package com.mottinut.bff.nutritionplan.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class DailyPlanResponseDto {
    private String date;
    private String dayName;
    private Object meals; // Cambiar de Map<String, Object> a Object para manejar tanto listas como objetos
    private Integer totalCalories;
    private Map<String, Number> macronutrients;
}
