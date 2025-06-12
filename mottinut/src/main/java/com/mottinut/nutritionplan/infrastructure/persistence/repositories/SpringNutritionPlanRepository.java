package com.mottinut.nutritionplan.infrastructure.persistence.repositories;

import com.mottinut.nutritionplan.infrastructure.persistence.entities.NutritionPlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

interface SpringNutritionPlanRepository extends JpaRepository<NutritionPlanEntity, Long> {
    @Query("SELECT p FROM NutritionPlanEntity p WHERE p.status = 'pending_review' ORDER BY p.createdAt ASC")
    List<NutritionPlanEntity> findPendingPlans();

    @Query("SELECT p FROM NutritionPlanEntity p WHERE p.patientId = :patientId AND p.weekStartDate = :weekStartDate AND p.status = 'approved'")
    Optional<NutritionPlanEntity> findApprovedByPatientAndWeek(@Param("patientId") Long patientId,
                                                               @Param("weekStartDate") LocalDate weekStartDate);

    @Query("SELECT p FROM NutritionPlanEntity p WHERE p.patientId = :patientId AND p.status = 'approved' ORDER BY p.weekStartDate DESC")
    List<NutritionPlanEntity> findApprovedByPatient(@Param("patientId") Long patientId);
}
