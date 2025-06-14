package com.mottinut.patient.domain.repositories;

import com.mottinut.patient.domain.entity.PatientProfile;
import com.mottinut.patient.domain.valueobjects.PatientId;

import java.util.List;
import java.util.Optional;

public interface PatientProfileRepository {
    List<PatientProfile> findAll();
    Optional<PatientProfile> findById(PatientId patientId);
    List<PatientProfile> findByChronicDisease(String chronicDisease);
    PatientProfile save(PatientProfile patient);
}
