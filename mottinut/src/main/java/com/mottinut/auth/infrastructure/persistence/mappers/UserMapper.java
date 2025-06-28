package com.mottinut.auth.infrastructure.persistence.mappers;

import com.mottinut.auth.domain.entities.Nutritionist;
import com.mottinut.auth.domain.entities.Patient;
import com.mottinut.auth.domain.entities.User;
import com.mottinut.auth.domain.valueobjects.Password;
import com.mottinut.auth.domain.valueobjects.Role;
import com.mottinut.auth.infrastructure.persistence.entities.NutritionistEntity;
import com.mottinut.auth.infrastructure.persistence.entities.PatientEntity;
import com.mottinut.auth.infrastructure.persistence.entities.UserEntity;
import com.mottinut.shared.domain.valueobjects.Email;
import com.mottinut.shared.domain.valueobjects.UserId;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toDomain(UserEntity entity) {
        if (entity == null) return null;

        if (entity instanceof PatientEntity) {
            return toPatientDomain((PatientEntity) entity);
        } else if (entity instanceof NutritionistEntity) {
            return toNutritionistDomain((NutritionistEntity) entity);
        }

        throw new IllegalArgumentException("Tipo de entidad no soportado: " + entity.getClass());
    }

    private Patient toPatientDomain(PatientEntity entity) {
        return new Patient(
                entity.getUserId() != null ? new UserId(entity.getUserId()) : null,
                new Email(entity.getEmail()),
                Password.fromHash(entity.getPassword()),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getBirthDate(),
                entity.getPhone(),
                entity.getHeight(),
                entity.getWeight(),
                Boolean.TRUE.equals(entity.getHasMedicalCondition()),
                entity.getChronicDisease(),
                entity.getAllergies(),
                entity.getDietaryPreferences(),
                entity.getEmergencyContact(),
                entity.getGender(),
                entity.getProfileImage(),
                entity.getImageContentType()
        );
    }

    private Nutritionist toNutritionistDomain(NutritionistEntity entity) {
        // Constructor principal con campos requeridos
        Nutritionist nutritionist = new Nutritionist(
                entity.getUserId() != null ? new UserId(entity.getUserId()) : null,
                new Email(entity.getEmail()),
                Password.fromHash(entity.getPassword()),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getBirthDate(),
                entity.getPhone(),
                entity.getProfileImage(),        // Corregido
                entity.getImageContentType(),   // Corregido
                entity.getCnpCode(),            // Corregido
                entity.getLicenseFrontImage(),
                entity.getLicenseBackImage(),
                entity.getSpecialty(),
                entity.getMasterDegree(),
                entity.getOtherSpecialty(),
                entity.getLocation(),
                entity.getAddress(),
                entity.getAcceptTerms() != null ? entity.getAcceptTerms() : false, // acceptTerms
                entity.getYearsOfExperience(),
                entity.getBiography()
        );

        // Actualizar estados de verificación
        if (Boolean.TRUE.equals(entity.getEmailVerified())) {
            nutritionist.verifyEmail();
        }
        if (Boolean.TRUE.equals(entity.getPhoneVerified())) {
            nutritionist.verifyPhone();
        }

        return nutritionist;
    }

    public UserEntity toEntity(User user) {
        if (user == null) return null;

        if (user instanceof Patient) {
            return toPatientEntity((Patient) user);
        } else if (user instanceof Nutritionist) {
            return toNutritionistEntity((Nutritionist) user);
        }

        throw new IllegalArgumentException("Tipo de dominio no soportado: " + user.getClass());
    }

    private PatientEntity toPatientEntity(Patient patient) {
        PatientEntity entity = new PatientEntity();
        setCommonFields(entity, patient);

        // Campos específicos del paciente
        entity.setHeight(patient.getHeight());
        entity.setWeight(patient.getWeight());
        entity.setHasMedicalCondition(patient.hasMedicalCondition());
        entity.setChronicDisease(patient.getChronicDisease());
        entity.setAllergies(patient.getAllergies());
        entity.setDietaryPreferences(patient.getDietaryPreferences());
        entity.setEmergencyContact(patient.getEmergencyContact());
        entity.setGender(patient.getGender());

        return entity;
    }

    private NutritionistEntity toNutritionistEntity(Nutritionist nutritionist) {
        NutritionistEntity entity = new NutritionistEntity();
        setCommonFields(entity, nutritionist);

        // Campos específicos del nutricionista
        entity.setCnpCode(nutritionist.getCnpCode());
        entity.setSpecialty(nutritionist.getSpecialty());
        entity.setLocation(nutritionist.getLocation());
        entity.setAddress(nutritionist.getAddress());
        entity.setMasterDegree(nutritionist.getMasterDegree());
        entity.setOtherSpecialty(nutritionist.getOtherSpecialty());
        entity.setAcceptTerms(nutritionist.isAcceptTerms());
        entity.setYearsOfExperience(nutritionist.getYearsOfExperience());
        entity.setBiography(nutritionist.getBiography());

        // Imágenes de licencia
        entity.setLicenseFrontImage(nutritionist.getLicenseFrontImage());
        entity.setLicenseBackImage(nutritionist.getLicenseBackImage());

        return entity;
    }

    private void setCommonFields(UserEntity entity, User user) {
        // Solo establecer ID si es válido y mayor que 0
        if (user.getUserId() != null && user.getUserId().getValue() != null && user.getUserId().getValue() > 0) {
            entity.setUserId(user.getUserId().getValue());
        }

        entity.setEmail(user.getEmail().getValue());
        entity.setPassword(user.getPassword().getHashedValue());
        entity.setFirstName(user.getFirstName());
        entity.setLastName(user.getLastName());
        entity.setBirthDate(user.getBirthDate());
        entity.setPhone(user.getPhone());

        // Imagen de perfil
        entity.setProfileImage(user.getProfileImage());
        entity.setImageContentType(user.getImageContentType());

        // Estados de verificación
        entity.setEmailVerified(user.isEmailVerified());
        entity.setPhoneVerified(user.isPhoneVerified());
        entity.setEmailVerifiedAt(user.getEmailVerifiedAt());
        entity.setPhoneVerifiedAt(user.getPhoneVerifiedAt());

        // Establecer el tipo de usuario basado en la instancia
        if (user instanceof Patient) {
            entity.setUserType(Role.PATIENT);
        } else if (user instanceof Nutritionist) {
            entity.setUserType(Role.NUTRITIONIST);
        }
    }
}