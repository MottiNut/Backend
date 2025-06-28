package com.mottinut.bff.patient.service;

import com.mottinut.auth.domain.entities.User;
import com.mottinut.auth.domain.services.UserService;
import com.mottinut.bff.patient.dto.enums.PatientSortField;
import com.mottinut.bff.patient.dto.enums.SortOrder;
import com.mottinut.bff.patient.dto.request.*;
import com.mottinut.bff.patient.mapper.PatientBffMapper;
import com.mottinut.crosscutting.security.CustomUserPrincipal;
import com.mottinut.patient.domain.entity.MedicalHistory;
import com.mottinut.patient.domain.entity.PatientProfile;
import com.mottinut.patient.domain.entity.PatientWithHistory;
import com.mottinut.patient.domain.enums.ChronicDiseaseFilter;
import com.mottinut.patient.domain.services.PatientManagementService;
import com.mottinut.patient.domain.valueobjects.MedicalHistoryId;
import com.mottinut.patient.domain.valueobjects.PatientId;
import com.mottinut.shared.domain.exceptions.UnauthorizedException;
import com.mottinut.shared.domain.valueobjects.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole('NUTRITIONIST')")
public class PatientBffService {

    private final PatientManagementService patientManagementService;
    private final PatientBffMapper patientBffMapper;
    private final UserService userService;

    public List<PatientProfileDto> getAllPatients(String chronicDisease, String sortBy, String order, Authentication authentication) {
        // Verificar que es nutricionista
        validateNutritionist(authentication);

        ChronicDiseaseFilter filter = ChronicDiseaseFilter.fromCode(chronicDisease);
        List<PatientProfile> patients = patientManagementService.filterPatientsByChronicDisease(filter);

        List<PatientProfileDto> patientDtos = patients.stream()
                .map(patientBffMapper::toPatientProfileDto)
                .collect(Collectors.toList());

        // Aplicar ordenamiento
        return sortPatients(patientDtos, sortBy, order);
    }

    private List<PatientProfileDto> sortPatients(List<PatientProfileDto> patients, String sortBy, String order) {
        PatientSortField sortField = PatientSortField.fromCode(sortBy);
        SortOrder sortOrder = SortOrder.fromCode(order);

        Comparator<PatientProfileDto> comparator = switch (sortField) {
            case AGE -> Comparator.comparing(PatientProfileDto::getAge,
                    Comparator.nullsLast(Integer::compareTo));
            case CREATED -> Comparator.comparing(PatientProfileDto::getCreatedAt,
                    Comparator.nullsLast(LocalDateTime::compareTo));
            default -> Comparator.comparing(PatientProfileDto::getFullName,
                    Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
        };

        if (sortOrder == SortOrder.DESC) {
            comparator = comparator.reversed();
        }

        return patients.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    public PatientProfileDto getPatientById(Long patientId, Authentication authentication) {
        // Verificar que es nutricionista
        validateNutritionist(authentication);

        PatientProfile patient = patientManagementService.getPatientById(PatientId.of(patientId));
        return patientBffMapper.toPatientProfileDto(patient);
    }

    public PatientWithHistoryDto getPatientWithHistory(Long patientId, Authentication authentication) {
        // Verificar que es nutricionista
        validateNutritionist(authentication);

        PatientWithHistory patientWithHistory = patientManagementService.getPatientWithHistory(PatientId.of(patientId));
        return patientBffMapper.toPatientWithHistoryDto(patientWithHistory);
    }

    public List<MedicalHistoryDto> getPatientHistory(Long patientId, LocalDate startDate, LocalDate endDate, Authentication authentication) {
        // Verificar que es nutricionista
        validateNutritionist(authentication);

        List<MedicalHistory> histories;
        if (startDate != null && endDate != null) {
            histories = patientManagementService.getPatientHistoriesInDateRange(
                    PatientId.of(patientId), startDate, endDate);
        } else {
            PatientWithHistory patientWithHistory = patientManagementService.getPatientWithHistory(PatientId.of(patientId));
            histories = patientWithHistory.getMedicalHistories();
        }

        return histories.stream()
                .map(patientBffMapper::toMedicalHistoryDto)
                .collect(Collectors.toList());
    }

    public MedicalHistoryDto createMedicalHistory(Long patientId,
                                                  CreateMedicalHistoryRequest request,
                                                  Authentication authentication) {
        validateNutritionist(authentication);

        MedicalHistory createdHistory = patientManagementService.createMedicalHistory(
                PatientId.of(patientId),
                request.getConsultationDate(),
                request.getHeight(),        // Nuevo parámetro
                request.getWeight(),        // Nuevo parámetro
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

        return patientBffMapper.toMedicalHistoryDto(createdHistory);
    }

    public MedicalHistoryDto updateMedicalHistory(Long historyId,
                                                  UpdateMedicalHistoryRequest request,
                                                  Authentication authentication) {
        validateNutritionist(authentication);

        MedicalHistory updatedHistory = patientManagementService.updateMedicalHistory(
                MedicalHistoryId.of(historyId),
                request.getHeight(),
                request.getWeight(),
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

        return patientBffMapper.toMedicalHistoryDto(updatedHistory);
    }

    public List<ChronicDiseaseFilterDto> getChronicDiseaseFilters(Authentication authentication) {
        // Verificar que es nutricionista
        validateNutritionist(authentication);

        return Arrays.stream(ChronicDiseaseFilter.values())
                .map(filter -> new ChronicDiseaseFilterDto(filter.getCode(), filter.getDescription()))
                .collect(Collectors.toList());
    }

    public PatientSortOptionsDto getSortOptions(Authentication authentication) {
        // Verificar que es nutricionista
        validateNutritionist(authentication);

        List<SortFieldDto> sortFields = Arrays.stream(PatientSortField.values())
                .map(field -> new SortFieldDto(field.getCode(), field.getDescription()))
                .collect(Collectors.toList());

        List<SortOrderDto> sortOrders = Arrays.stream(SortOrder.values())
                .map(order -> new SortOrderDto(order.getCode(), order.getDescription()))
                .collect(Collectors.toList());

        return new PatientSortOptionsDto(sortFields, sortOrders);
    }

    public PatientHealthSummaryDto getPatientHealthSummary(Long patientId, Authentication authentication) {
        // Verificar que es nutricionista
        validateNutritionist(authentication);

        PatientWithHistory patientWithHistory = patientManagementService.getPatientWithHistory(PatientId.of(patientId));
        return patientBffMapper.toPatientHealthSummaryDto(patientWithHistory);
    }

    public PatientProgressDto getPatientProgress(Long patientId, int days, Authentication authentication) {
        // Verificar que es nutricionista
        validateNutritionist(authentication);

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days);

        List<MedicalHistory> histories = patientManagementService.getPatientHistoriesInDateRange(
                PatientId.of(patientId), startDate, endDate);

        return patientBffMapper.toPatientProgressDto(histories, days);
    }

    // Método helper para validar nutricionista (igual que en NutritionistNutritionPlanService)
    private void validateNutritionist(Authentication authentication) {
        UserId nutritionistId = getCurrentUserId(authentication);
        User nutritionist = userService.getUserById(nutritionistId);
        if (!nutritionist.getRole().isNutritionist()) {
            throw new UnauthorizedException("Solo nutricionistas pueden acceder a la información de pacientes");
        }
    }

    // Método helper para obtener el ID del usuario actual (igual que en NutritionistNutritionPlanService)
    private UserId getCurrentUserId(Authentication authentication) {
        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();
        return principal.getUser().getUserId();
    }
}