package com.mottinut.patient.presentation.controller;

import com.mottinut.patient.domain.entity.MedicalHistory;
import com.mottinut.patient.domain.entity.PatientProfile;
import com.mottinut.patient.domain.entity.PatientWithHistory;
import com.mottinut.patient.domain.enums.ChronicDiseaseFilter;
import com.mottinut.patient.domain.services.PatientManagementService;
import com.mottinut.patient.domain.valueobjects.MedicalHistoryId;
import com.mottinut.patient.domain.valueobjects.PatientId;
import com.mottinut.patient.presentation.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Validated
public class PatientController {
    private final PatientManagementService patientService;
    private final PatientDtoMapper dtoMapper;

    @GetMapping
    public ResponseEntity<List<PatientProfileDto>> getAllPatients(
            @RequestParam(required = false, defaultValue = "all") String chronicDisease) {

        ChronicDiseaseFilter filter = ChronicDiseaseFilter.fromCode(chronicDisease);
        List<PatientProfile> patients = patientService.filterPatientsByChronicDisease(filter);

        List<PatientProfileDto> patientDtos = patients.stream()
                .map(dtoMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(patientDtos);
    }

    @GetMapping("/{patientId}")
    public ResponseEntity<PatientProfileDto> getPatientById(@PathVariable Long patientId) {
        PatientProfile patient = patientService.getPatientById(PatientId.of(patientId));
        return ResponseEntity.ok(dtoMapper.toDto(patient));
    }

    @GetMapping("/{patientId}/with-history")
    public ResponseEntity<PatientWithHistoryDto> getPatientWithHistory(@PathVariable Long patientId) {
        PatientWithHistory patientWithHistory = patientService.getPatientWithHistory(PatientId.of(patientId));
        return ResponseEntity.ok(dtoMapper.toDto(patientWithHistory));
    }

    @GetMapping("/{patientId}/history")
    public ResponseEntity<List<MedicalHistoryDto>> getPatientHistory(
            @PathVariable Long patientId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<MedicalHistory> histories;
        if (startDate != null && endDate != null) {
            histories = patientService.getPatientHistoriesInDateRange(PatientId.of(patientId), startDate, endDate);
        } else {
            PatientWithHistory patientWithHistory = patientService.getPatientWithHistory(PatientId.of(patientId));
            histories = patientWithHistory.getMedicalHistories();
        }

        List<MedicalHistoryDto> historyDtos = histories.stream()
                .map(dtoMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(historyDtos);
    }

    @PostMapping("/{patientId}/history")
    public ResponseEntity<MedicalHistoryDto> createMedicalHistory(
            @PathVariable Long patientId,
            @Valid @RequestBody CreateMedicalHistoryRequest request) {

        MedicalHistory createdHistory = patientService.createMedicalHistory(
                PatientId.of(patientId),
                request.getConsultationDate(),
                request.getWaistCircumference(),
                request.getHipCircumference(),
                request.getBodyFatPercentage(),
                request.getBloodPressure(),
                request.getHeartRate(),
                request.getBloodGlucose(),
                request.getLipidProfile(),
                request.getEatingHabits(),
                request.getWaterConsumption(),
                request.getSupplementation(),
                request.getCaloricIntake(),
                request.getMacronutrients(),
                request.getFoodPreferences(),
                request.getFoodRelationship(),
                request.getStressLevel(),
                request.getSleepQuality(),
                request.getNutritionalObjectives(),
                request.getPatientEvolution(),
                request.getProfessionalNotes()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(dtoMapper.toDto(createdHistory));
    }

    @PutMapping("/history/{historyId}")
    public ResponseEntity<MedicalHistoryDto> updateMedicalHistory(
            @PathVariable Long historyId,
            @Valid @RequestBody UpdateMedicalHistoryRequest request) {

        MedicalHistory updatedHistory = patientService.updateMedicalHistory(
                MedicalHistoryId.of(historyId),
                request.getWaistCircumference(),
                request.getHipCircumference(),
                request.getBodyFatPercentage(),
                request.getBloodPressure(),
                request.getHeartRate(),
                request.getBloodGlucose(),
                request.getLipidProfile(),
                request.getEatingHabits(),
                request.getWaterConsumption(),
                request.getSupplementation(),
                request.getCaloricIntake(),
                request.getMacronutrients(),
                request.getFoodPreferences(),
                request.getFoodRelationship(),
                request.getStressLevel(),
                request.getSleepQuality(),
                request.getNutritionalObjectives(),
                request.getPatientEvolution(),
                request.getProfessionalNotes()
        );

        return ResponseEntity.ok(dtoMapper.toDto(updatedHistory));
    }

    @GetMapping("/chronic-diseases")
    public ResponseEntity<List<ChronicDiseaseFilterDto>> getChronicDiseaseFilters() {
        List<ChronicDiseaseFilterDto> filters = Arrays.stream(ChronicDiseaseFilter.values())
                .map(filter -> new ChronicDiseaseFilterDto(filter.getCode(), filter.getDescription()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(filters);
    }
}