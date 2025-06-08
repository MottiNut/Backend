package com.mottinut.auth.domain.services;

import com.mottinut.auth.domain.entities.User;
import com.mottinut.auth.domain.repositories.UserRepository;
import com.mottinut.shared.domain.exceptions.NotFoundException;
import com.mottinut.shared.domain.valueobjects.UserId;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserById(UserId userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
    }

    public User updateUserProfile(UserId userId, String firstName, String lastName,
                                  String phone, Double height, Double weight,
                                  boolean hasMedicalCondition, String chronicDisease,
                                  String allergies, String dietaryPreferences) {
        User user = getUserById(userId);
        user.updateProfile(firstName, lastName, phone, height, weight,
                hasMedicalCondition, chronicDisease, allergies, dietaryPreferences);
        return userRepository.save(user);
    }
}
