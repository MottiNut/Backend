package com.mottinut.nutritionplan.domain.services;

import com.mottinut.auth.domain.entities.User;
import com.mottinut.auth.domain.services.UserService;
import com.mottinut.nutritionplan.domain.entities.NutritionPlan;
import com.mottinut.nutritionplan.domain.enums.PatientAction;
import com.mottinut.nutritionplan.domain.enums.ReviewAction;
import com.mottinut.nutritionplan.domain.repositories.NutritionPlanRepository;
import com.mottinut.nutritionplan.domain.valueobjects.NutritionPlanId;
import com.mottinut.shared.domain.exceptions.NotFoundException;
import com.mottinut.shared.domain.exceptions.UnauthorizedException;
import com.mottinut.shared.domain.exceptions.ValidationException;
import com.mottinut.shared.domain.valueobjects.UserId;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
@Service
@Transactional
public class NutritionPlanService {
    private final NutritionPlanRepository nutritionPlanRepository;
    private final AiPlanGeneratorService aiPlanGeneratorService;
    private final UserService userService;

    public NutritionPlanService(NutritionPlanRepository nutritionPlanRepository,
                                AiPlanGeneratorService aiPlanGeneratorService,
                                UserService userService) {
        this.nutritionPlanRepository = nutritionPlanRepository;
        this.aiPlanGeneratorService = aiPlanGeneratorService;
        this.userService = userService;
    }

    public NutritionPlan generatePlan(UserId nutritionistId, UserId patientId,
                                      LocalDate weekStartDate, Integer energyRequirement,
                                      String goal, String specialRequirements) {
        // Verificar que el nutricionista existe
        User nutritionist = userService.getUserById(nutritionistId);
        if (!nutritionist.getRole().isNutritionist()) {
            throw new UnauthorizedException("Solo nutricionistas pueden generar planes");
        }

        // Verificar que el paciente existe
        User patient = userService.getUserById(patientId);
        if (!patient.getRole().isPatient()) {
            throw new ValidationException("El usuario debe ser un paciente");
        }

        try {
            // Generar plan con IA (ahora directamente desde Spring Boot)
            String planContent = aiPlanGeneratorService.generatePlan(
                    patientId, weekStartDate, energyRequirement, goal, specialRequirements);

            // Crear y guardar el plan
            NutritionPlan plan = new NutritionPlan(
                    null, patientId, nutritionistId, weekStartDate,
                    energyRequirement, goal, specialRequirements, planContent);

            return nutritionPlanRepository.save(plan);

        } catch (RuntimeException e) {
            // Log y re-throw para mantener el comportamiento esperado
            throw new RuntimeException("Error al generar el plan nutricional: " + e.getMessage(), e);
        }
    }

    // Método actualizado para buscar planes en cualquier día de la semana
    public Optional<NutritionPlan> getPatientActivePlan(UserId patientId, LocalDate date) {
        // Buscar plan que contenga la fecha especificada
        LocalDate weekStart = date.minusDays(6); // Buscar desde 6 días antes
        LocalDate weekEnd = date.plusDays(6);   // Hasta 6 días después

        return nutritionPlanRepository.findAcceptedPlanByPatientAndWeekRange(patientId, weekStart, weekEnd);
    }

    // Método para buscar plan por semana específica
    public Optional<NutritionPlan> getPatientPlanForWeek(UserId patientId, LocalDate weekStartDate) {
        LocalDate weekEnd = weekStartDate.plusDays(6);
        return nutritionPlanRepository.findAcceptedPlanByPatientAndWeekRange(patientId, weekStartDate, weekEnd);
    }

    public List<NutritionPlan> getPendingPlans() {
        return nutritionPlanRepository.findPendingPlans();
    }

    public NutritionPlan getPlanById(NutritionPlanId planId) {
        return nutritionPlanRepository.findById(planId)
                .orElseThrow(() -> new NotFoundException("Plan nutricional no encontrado"));
    }

    public NutritionPlan reviewPlan(UserId nutritionistId, NutritionPlanId planId,
                                    ReviewAction action, String reviewNotes) {
        // Verificar que el nutricionista existe
        User nutritionist = userService.getUserById(nutritionistId);
        if (!nutritionist.getRole().isNutritionist()) {
            throw new UnauthorizedException("Solo nutricionistas pueden revisar planes");
        }

        NutritionPlan plan = nutritionPlanRepository.findById(planId)
                .orElseThrow(() -> new NotFoundException("Plan nutricional no encontrado"));

        if (!plan.isPending()) {
            throw new ValidationException("El plan ya ha sido revisado");
        }

        if (action == ReviewAction.APPROVE) {
            plan.approve(reviewNotes);
        } else {
            plan.reject(reviewNotes);
        }

        return nutritionPlanRepository.save(plan);
    }

    public NutritionPlan editPlan(UserId nutritionistId, NutritionPlanId planId,
                                  String newPlanContent, String reviewNotes) {
        // Verificar que el nutricionista existe
        User nutritionist = userService.getUserById(nutritionistId);
        if (!nutritionist.getRole().isNutritionist()) {
            throw new UnauthorizedException("Solo nutricionistas pueden editar planes");
        }

        NutritionPlan plan = nutritionPlanRepository.findById(planId)
                .orElseThrow(() -> new NotFoundException("Plan nutricional no encontrado"));

        if (!plan.canBeEditedByNutritionist()) {
            throw new ValidationException("El plan no puede ser editado en su estado actual");
        }

        plan.editPlan(newPlanContent, reviewNotes);
        return nutritionPlanRepository.save(plan);
    }

    public List<NutritionPlan> getPendingPatientAcceptancePlans(UserId patientId) {
        return nutritionPlanRepository.findPendingPatientAcceptancePlans(patientId);
    }

    public NutritionPlan patientRespondToPlan(UserId patientId, NutritionPlanId planId,
                                              PatientAction action, String feedback) {
        // Verificar que el paciente existe
        User patient = userService.getUserById(patientId);
        if (!patient.getRole().isPatient()) {
            throw new UnauthorizedException("Solo pacientes pueden responder a planes");
        }

        NutritionPlan plan = nutritionPlanRepository.findById(planId)
                .orElseThrow(() -> new NotFoundException("Plan nutricional no encontrado"));

        if (!plan.isPendingPatientAcceptance()) {
            throw new ValidationException("El plan no está pendiente de aceptación del paciente");
        }

        if (!plan.getPatientId().equals(patientId)) {
            throw new UnauthorizedException("Solo el paciente asignado puede responder al plan");
        }

        if (action == PatientAction.ACCEPT) {
            plan.acceptByPatient(feedback);
        } else {
            plan.rejectByPatient(feedback);
        }

        return nutritionPlanRepository.save(plan);
    }

    public List<NutritionPlan> getRejectedByPatientPlans(UserId nutritionistId) {
        return nutritionPlanRepository.findRejectedByPatientPlans(nutritionistId);
    }

    public List<NutritionPlan> getPatientPlans(UserId patientId) {
        return nutritionPlanRepository.findAcceptedPlansByPatient(patientId);
    }

    // Método helper para obtener el inicio de semana de un plan
    private LocalDate getWeekStart(LocalDate date) {
        return date.with(DayOfWeek.MONDAY);
    }
}