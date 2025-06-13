package com.mottinut.bff.nutritionplan.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class WeeklyPlanResponseDto {
    private Long planId;
    private String weekStartDate;
    private String weekEndDate;
    private String goal;
    private Integer energyRequirement;
    private List<DailyPlanResponseDto> dailyPlans;
    private String reviewNotes;
}
