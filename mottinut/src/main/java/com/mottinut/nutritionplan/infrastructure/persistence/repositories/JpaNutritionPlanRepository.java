package com.mottinut.nutritionplan.infrastructure.persistence.repositories;

import com.mottinut.nutritionplan.domain.entities.NutritionPlan;
import com.mottinut.nutritionplan.domain.repositories.NutritionPlanRepository;
import com.mottinut.nutritionplan.domain.valueobjects.NutritionPlanId;
import com.mottinut.nutritionplan.infrastructure.persistence.entities.NutritionPlanEntity;
import com.mottinut.nutritionplan.infrastructure.persistence.mappers.NutritionPlanMapper;
import com.mottinut.shared.domain.valueobjects.UserId;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class JpaNutritionPlanRepository implements NutritionPlanRepository {
    private final SpringNutritionPlanRepository springRepository;
    private final NutritionPlanMapper mapper;

    public JpaNutritionPlanRepository(SpringNutritionPlanRepository springRepository,
                                      NutritionPlanMapper mapper) {
        this.springRepository = springRepository;
        this.mapper = mapper;
    }

    @Override
    public NutritionPlan save(NutritionPlan plan) {
        NutritionPlanEntity entity = mapper.toEntity(plan);
        NutritionPlanEntity saved = springRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<NutritionPlan> findById(NutritionPlanId planId) {
        return springRepository.findById(planId.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public List<NutritionPlan> findPendingPlans() {
        return springRepository.findPendingPlans().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<NutritionPlan> findApprovedPlanByPatientAndWeek(UserId patientId, LocalDate weekStartDate) {
        return springRepository.findApprovedByPatientAndWeek(patientId.getValue(), weekStartDate)
                .map(mapper::toDomain);
    }

    @Override
    public List<NutritionPlan> findApprovedPlansByPatient(UserId patientId) {
        return springRepository.findApprovedByPatient(patientId.getValue()).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
