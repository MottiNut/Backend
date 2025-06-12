package com.mottinut.nutritionplan.domain.services;

import com.mottinut.auth.domain.entities.User;
import com.mottinut.auth.domain.services.UserService;
import com.mottinut.nutritionplan.domain.entities.NutritionPlan;
import com.mottinut.nutritionplan.domain.enums.ReviewAction;
import com.mottinut.nutritionplan.domain.repositories.NutritionPlanRepository;
import com.mottinut.nutritionplan.domain.valueobjects.NutritionPlanId;
import com.mottinut.shared.domain.exceptions.NotFoundException;
import com.mottinut.shared.domain.exceptions.UnauthorizedException;
import com.mottinut.shared.domain.exceptions.ValidationException;
import com.mottinut.shared.domain.valueobjects.UserId;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

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

        // Generar plan con IA
        String planContent = aiPlanGeneratorService.generatePlan(
                patientId, weekStartDate, energyRequirement, goal, specialRequirements);

        // Crear y guardar el plan
        NutritionPlan plan = new NutritionPlan(
                null, patientId, nutritionistId, weekStartDate,
                energyRequirement, goal, specialRequirements, planContent);

        return nutritionPlanRepository.save(plan);
    }

    public List<NutritionPlan> getPendingPlans() {
        return nutritionPlanRepository.findPendingPlans();
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

    public Optional<NutritionPlan> getPatientPlanForWeek(UserId patientId, LocalDate weekStartDate) {
        return nutritionPlanRepository.findApprovedPlanByPatientAndWeek(patientId, weekStartDate);
    }

    public List<NutritionPlan> getPatientPlans(UserId patientId) {
        return nutritionPlanRepository.findApprovedPlansByPatient(patientId);
    }
}

