package com.mottinut.patient.infrastructure.persistence.repositories;

import com.mottinut.patient.infrastructure.persistence.entities.MedicalHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MedicalHistoryJpaRepository extends JpaRepository<MedicalHistoryEntity, Long> {
    List<MedicalHistoryEntity> findByPatientIdOrderByConsultationDateDesc(Long patientId);

    @Query("SELECT mh FROM MedicalHistoryEntity mh WHERE mh.patientId = :patientId " +
            "AND mh.consultationDate BETWEEN :startDate AND :endDate " +
            "ORDER BY mh.consultationDate DESC")
    List<MedicalHistoryEntity> findByPatientIdAndDateRange(
            @Param("patientId") Long patientId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}