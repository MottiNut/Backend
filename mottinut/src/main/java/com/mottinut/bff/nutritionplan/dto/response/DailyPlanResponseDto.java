package com.mottinut.bff.nutritionplan.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class DailyPlanResponseDto {
    private String date;
    private String dayName;
    private Object meals;
    private Integer totalCalories;
    private Map<String, Number> macronutrients;
}
