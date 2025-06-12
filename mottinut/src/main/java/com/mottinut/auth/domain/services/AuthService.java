package com.mottinut.auth.domain.services;

import com.mottinut.auth.domain.entities.Nutritionist;
import com.mottinut.auth.domain.entities.Patient;
import com.mottinut.auth.domain.entities.User;
import com.mottinut.auth.domain.factory.UserFactory;
import com.mottinut.auth.domain.repositories.UserRepository;
import com.mottinut.auth.domain.valueobjects.Password;
import com.mottinut.auth.domain.valueobjects.Role;
import com.mottinut.shared.domain.exceptions.BusinessException;
import com.mottinut.shared.domain.valueobjects.Email;
import com.mottinut.shared.domain.valueobjects.UserId;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Transactional
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final UserFactory userFactory;

    public AuthService(UserRepository userRepository, UserFactory userFactory) {
        this.userRepository = userRepository;
        this.userFactory = userFactory;
    }

    public Patient registerPatient(Email email, String plainPassword, String firstName,
                                   String lastName, LocalDate birthDate, String phone,
                                   Double height, Double weight, boolean hasMedicalCondition,
                                   String chronicDisease, String allergies, String dietaryPreferences) {

        if (userRepository.existsByEmail(email)) {
            throw new ValidationException("El email ya existe");
        }

        Password password = Password.fromPlainText(plainPassword);

        Patient patient = userFactory.createPatient(
                null, email, password, firstName, lastName, birthDate, phone,
                height, weight, hasMedicalCondition, chronicDisease, allergies, dietaryPreferences
        );

        return (Patient) userRepository.save(patient);
    }

    public Nutritionist registerNutritionist(Email email, String plainPassword, String firstName,
                                             String lastName, LocalDate birthDate, String phone,
                                             String licenseNumber, String specialization, String workplace) {

        if (userRepository.existsByEmail(email)) {
            throw new ValidationException("El email ya existe");
        }

        Password password = Password.fromPlainText(plainPassword);

        Nutritionist nutritionist = userFactory.createNutritionist(
                null, email, password, firstName, lastName, birthDate, phone,
                licenseNumber, specialization, workplace
        );

        return (Nutritionist) userRepository.save(nutritionist);
    }

    public User authenticate(Email email, String plainPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            throw new ValidationException("Credenciales incorrectas");
        }

        User user = userOpt.get();
        if (!user.getPassword().matches(plainPassword)) {
            throw new ValidationException("Credenciales incorrectas");
        }

        return user;
    }

    public User findById(UserId userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));
    }
}
