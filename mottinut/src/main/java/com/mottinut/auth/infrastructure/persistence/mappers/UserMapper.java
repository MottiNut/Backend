package com.mottinut.auth.infrastructure.persistence.mappers;

import com.mottinut.auth.domain.entities.User;
import com.mottinut.auth.domain.valueobjects.Password;
import com.mottinut.auth.domain.valueobjects.Role;
import com.mottinut.auth.infrastructure.persistence.entities.UserEntity;
import com.mottinut.shared.domain.valueobjects.Email;
import com.mottinut.shared.domain.valueobjects.UserId;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toDomain(UserEntity entity) {
        if (entity == null) return null;

        return new User(
                entity.getUserId() != null ? new UserId(entity.getUserId()) : null,
                new Email(entity.getEmail()),
                Password.fromHash(entity.getPassword()),
                Role.fromString(entity.getRole()),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getBirthDate(),
                entity.getPhone(),
                entity.getHeight(),
                entity.getWeight(),
                Boolean.TRUE.equals(entity.getHasMedicalCondition()),
                entity.getChronicDisease(),
                entity.getAllergies(),
                entity.getDietaryPreferences()
        );
    }

    public UserEntity toEntity(User user) {
        if (user == null) return null;

        UserEntity entity = new UserEntity();

        // Solo setear el ID si no es null y no es 0
        if (user.getUserId() != null && user.getUserId().getValue() != null && user.getUserId().getValue() > 0) {
            entity.setUserId(user.getUserId().getValue());
        }

        entity.setEmail(user.getEmail().getValue());
        entity.setPassword(user.getPassword().getHashedValue());
        entity.setRole(user.getRole().getValue());
        entity.setFirstName(user.getFirstName());
        entity.setLastName(user.getLastName());
        entity.setBirthDate(user.getBirthDate());
        entity.setPhone(user.getPhone());
        entity.setHeight(user.getHeight());
        entity.setWeight(user.getWeight());
        entity.setHasMedicalCondition(user.hasMedicalCondition());
        entity.setChronicDisease(user.getChronicDisease());
        entity.setAllergies(user.getAllergies());
        entity.setDietaryPreferences(user.getDietaryPreferences());
        entity.setCreatedAt(user.getCreatedAt());

        return entity;
    }
}