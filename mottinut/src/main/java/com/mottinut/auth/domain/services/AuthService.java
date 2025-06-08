package com.mottinut.auth.domain.services;

import com.mottinut.auth.domain.entities.User;
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

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(Email email, String plainPassword, Role role, String firstName,
                         String lastName, LocalDate birthDate, String phone, Double height,
                         Double weight, boolean hasMedicalCondition, String chronicDisease,
                         String allergies, String dietaryPreferences) {

        if (userRepository.existsByEmail(email)) {
            throw new ValidationException("El email ya existe");
        }

        Password password = Password.fromPlainText(plainPassword);

        // No generar ID aquí, dejar que JPA lo haga automáticamente
        User user = new User(
                null, // UserId será generado por la base de datos
                email,
                password,
                role,
                firstName,
                lastName,
                birthDate,
                phone,
                height,
                weight,
                hasMedicalCondition,
                chronicDisease,
                allergies,
                dietaryPreferences
        );

        return userRepository.save(user);
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
