package com.mottinut.patient.infrastructure.persistence.repositories;

import com.mottinut.patient.domain.entity.MedicalHistory;
import com.mottinut.patient.domain.repositories.MedicalHistoryRepository;
import com.mottinut.patient.domain.valueobjects.MedicalHistoryId;
import com.mottinut.patient.domain.valueobjects.PatientId;
import com.mottinut.patient.infrastructure.persistence.entities.MedicalHistoryEntity;
import com.mottinut.patient.infrastructure.persistence.mappers.PatientMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JpaMedicalHistoryRepository implements MedicalHistoryRepository {
    private final MedicalHistoryJpaRepository jpaRepository;
    private final PatientMapper patientMapper;

    @Override
    public MedicalHistory save(MedicalHistory medicalHistory) {
        MedicalHistoryEntity entity = patientMapper.toEntity(medicalHistory);
        MedicalHistoryEntity savedEntity = jpaRepository.save(entity);
        return patientMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<MedicalHistory> findById(MedicalHistoryId historyId) {
        return jpaRepository.findById(historyId.getValue())
                .map(patientMapper::toDomain);
    }

    @Override
    public List<MedicalHistory> findByPatientId(PatientId patientId) {
        return jpaRepository.findByPatientIdOrderByConsultationDateDesc(patientId.getValue())
                .stream()
                .map(patientMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<MedicalHistory> findByPatientIdAndDateRange(PatientId patientId, LocalDate startDate, LocalDate endDate) {
        return jpaRepository.findByPatientIdAndDateRange(patientId.getValue(), startDate, endDate)
                .stream()
                .map(patientMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(MedicalHistoryId historyId) {
        jpaRepository.deleteById(historyId.getValue());
    }
}
