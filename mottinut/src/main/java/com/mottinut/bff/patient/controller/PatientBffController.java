package com.mottinut.bff.patient.controller;

import com.mottinut.bff.patient.dto.request.*;
import com.mottinut.bff.patient.service.PatientBffService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/bff/patients")
@PreAuthorize("hasRole('NUTRITIONIST')")
@RequiredArgsConstructor
@Validated
public class PatientBffController {

    private final PatientBffService patientBffService;

    @GetMapping
    public ResponseEntity<List<PatientProfileDto>> getAllPatients(
            @RequestParam(required = false, defaultValue = "all") String chronicDisease,
            @RequestParam(required = false, defaultValue = "name") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String order,
            Authentication authentication) {
        List<PatientProfileDto> patients = patientBffService.getAllPatients(chronicDisease, sortBy, order, authentication);
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/{patientId}")
    public ResponseEntity<PatientProfileDto> getPatientById(@PathVariable Long patientId, Authentication authentication) {
        PatientProfileDto patient = patientBffService.getPatientById(patientId, authentication);
        return ResponseEntity.ok(patient);
    }

    @GetMapping("/{patientId}/with-history")
    public ResponseEntity<PatientWithHistoryDto> getPatientWithHistory(@PathVariable Long patientId, Authentication authentication) {
        PatientWithHistoryDto patientWithHistory = patientBffService.getPatientWithHistory(patientId, authentication);
        return ResponseEntity.ok(patientWithHistory);
    }

    @GetMapping("/{patientId}/history")
    public ResponseEntity<List<MedicalHistoryDto>> getPatientHistory(
            @PathVariable Long patientId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Authentication authentication) {
        List<MedicalHistoryDto> histories = patientBffService.getPatientHistory(patientId, startDate, endDate, authentication);
        return ResponseEntity.ok(histories);
    }

    @PostMapping("/{patientId}/history")
    public ResponseEntity<MedicalHistoryDto> createMedicalHistory(
            @PathVariable Long patientId,
            @Valid @RequestBody CreateMedicalHistoryRequest request,
            Authentication authentication) {
        MedicalHistoryDto createdHistory = patientBffService.createMedicalHistory(patientId, request, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdHistory);
    }

    @PutMapping("/history/{historyId}")
    public ResponseEntity<MedicalHistoryDto> updateMedicalHistory(
            @PathVariable Long historyId,
            @Valid @RequestBody UpdateMedicalHistoryRequest request,
            Authentication authentication) {
        MedicalHistoryDto updatedHistory = patientBffService.updateMedicalHistory(historyId, request, authentication);
        return ResponseEntity.ok(updatedHistory);
    }

    @GetMapping("/chronic-diseases")
    public ResponseEntity<List<ChronicDiseaseFilterDto>> getChronicDiseaseFilters(Authentication authentication) {
        List<ChronicDiseaseFilterDto> filters = patientBffService.getChronicDiseaseFilters(authentication);
        return ResponseEntity.ok(filters);
    }

    @GetMapping("/sort-options")
    public ResponseEntity<PatientSortOptionsDto> getSortOptions(Authentication authentication) {
        PatientSortOptionsDto sortOptions = patientBffService.getSortOptions(authentication);
        return ResponseEntity.ok(sortOptions);
    }

    @GetMapping("/{patientId}/health-summary")
    public ResponseEntity<PatientHealthSummaryDto> getPatientHealthSummary(@PathVariable Long patientId, Authentication authentication) {
        PatientHealthSummaryDto summary = patientBffService.getPatientHealthSummary(patientId, authentication);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/{patientId}/progress")
    public ResponseEntity<PatientProgressDto> getPatientProgress(
            @PathVariable Long patientId,
            @RequestParam(required = false, defaultValue = "30") int days,
            Authentication authentication) {
        PatientProgressDto progress = patientBffService.getPatientProgress(patientId, days, authentication);
        return ResponseEntity.ok(progress);
    }
}

