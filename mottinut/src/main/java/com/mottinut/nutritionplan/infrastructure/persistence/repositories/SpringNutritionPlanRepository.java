package com.mottinut.nutritionplan.infrastructure.persistence.repositories;


import com.mottinut.nutritionplan.infrastructure.persistence.entities.NutritionPlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SpringNutritionPlanRepository extends JpaRepository<NutritionPlanEntity, Long> {

    @Query("SELECT n FROM NutritionPlanEntity n WHERE n.status = 'pending_review' ORDER BY n.createdAt DESC")
    List<NutritionPlanEntity> findPendingPlans();

    // ✅ Agregar esta consulta nueva
    @Query("SELECT n FROM NutritionPlanEntity n WHERE n.nutritionistId = :nutritionistId AND n.status = 'pending_review' ORDER BY n.createdAt DESC")
    List<NutritionPlanEntity> findPendingPlansByNutritionist(@Param("nutritionistId") Long nutritionistId);

    @Query("SELECT n FROM NutritionPlanEntity n WHERE n.patientId = :patientId AND n.status = 'pending_patient_acceptance' ORDER BY n.reviewedAt DESC")
    List<NutritionPlanEntity> findPendingPatientAcceptance(@Param("patientId") Long patientId);

    @Query("SELECT n FROM NutritionPlanEntity n WHERE n.patientId = :patientId AND n.status = 'accepted_by_patient' AND n.weekStartDate BETWEEN :weekStart AND :weekEnd ORDER BY n.weekStartDate DESC")
    Optional<NutritionPlanEntity> findAcceptedByPatientAndWeekRange(@Param("patientId") Long patientId, @Param("weekStart") LocalDate weekStart, @Param("weekEnd") LocalDate weekEnd);

    @Query("SELECT n FROM NutritionPlanEntity n WHERE n.patientId = :patientId AND n.status = 'accepted_by_patient' ORDER BY n.weekStartDate DESC")
    List<NutritionPlanEntity> findAcceptedByPatient(@Param("patientId") Long patientId);

    @Query("SELECT n FROM NutritionPlanEntity n WHERE n.nutritionistId = :nutritionistId AND n.status = 'rejected_by_patient' ORDER BY n.patientResponseAt DESC")
    List<NutritionPlanEntity> findRejectedByPatient(@Param("nutritionistId") Long nutritionistId);
}
