package com.mottinut.nutritionplan.presentation.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class DailyPlanResponseDto {
    private String date;
    private String dayName;
    private Map<String, Object> meals; // breakfast, lunch, dinner, snacks
    private Integer totalCalories;
    private Map<String, Double> macronutrients; // proteins, carbs, fats
}
