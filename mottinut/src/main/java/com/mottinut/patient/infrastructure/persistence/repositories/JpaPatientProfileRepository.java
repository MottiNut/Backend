package com.mottinut.patient.infrastructure.persistence.repositories;

import com.mottinut.auth.domain.valueobjects.Role;
import com.mottinut.auth.infrastructure.persistence.entities.PatientEntity;
import com.mottinut.auth.infrastructure.persistence.repositories.UserJpaRepository;
import com.mottinut.patient.domain.entity.PatientProfile;
import com.mottinut.patient.domain.repositories.PatientProfileRepository;
import com.mottinut.patient.domain.valueobjects.PatientId;
import com.mottinut.patient.infrastructure.persistence.mappers.PatientMapper;
import com.mottinut.shared.domain.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JpaPatientProfileRepository implements PatientProfileRepository {
    private final UserJpaRepository userJpaRepository;
    private final PatientMapper patientMapper;

    @Override
    public List<PatientProfile> findAll() {
        return userJpaRepository.findByUserType(Role.PATIENT)
                .stream()
                .map(patientMapper::toPatientProfile)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<PatientProfile> findById(PatientId patientId) {
        return userJpaRepository.findById(patientId.getValue())
                .filter(entity -> entity instanceof PatientEntity)
                .map(patientMapper::toPatientProfile);
    }

    @Override
    public List<PatientProfile> findByChronicDisease(String chronicDisease) {
        return userJpaRepository.findByUserType(Role.PATIENT)
                .stream()
                .filter(entity -> entity instanceof PatientEntity)
                .map(entity -> (PatientEntity) entity)
                .filter(patient -> patient.getChronicDisease() != null &&
                        patient.getChronicDisease().toLowerCase().contains(chronicDisease.toLowerCase()))
                .map(entity -> patientMapper.toPatientProfile(entity))
                .collect(Collectors.toList());
    }

    @Override
    public PatientProfile save(PatientProfile patient) {
        // Este método se usaría solo para actualizaciones de datos básicos del paciente
        // La creación de pacientes se mantiene en el módulo auth
        throw new UnsupportedOperationException("La creación de pacientes se maneja en el módulo auth");
    }

    @Override
    public PatientProfile updateWeightAndHeight(PatientId patientId, Double weight, Double height) {
        PatientEntity patientEntity = (PatientEntity) userJpaRepository.findById(patientId.getValue())
                .orElseThrow(() -> new NotFoundException("Paciente no encontrado"));

        if (weight != null) {
            patientEntity.setWeight(weight);
        }
        if (height != null) {
            patientEntity.setHeight(height);
        }

        PatientEntity savedEntity = userJpaRepository.save(patientEntity);
        return patientMapper.toPatientProfile(savedEntity);
    }
}