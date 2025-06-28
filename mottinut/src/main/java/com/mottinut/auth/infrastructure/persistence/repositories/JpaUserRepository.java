package com.mottinut.auth.infrastructure.persistence.repositories;

import com.mottinut.auth.domain.entities.Nutritionist;
import com.mottinut.auth.domain.entities.Patient;
import com.mottinut.auth.domain.entities.User;
import com.mottinut.auth.domain.repositories.UserRepository;
import com.mottinut.auth.infrastructure.persistence.entities.NutritionistEntity;
import com.mottinut.auth.infrastructure.persistence.entities.PatientEntity;
import com.mottinut.auth.infrastructure.persistence.entities.UserEntity;
import com.mottinut.auth.infrastructure.persistence.mappers.UserMapper;
import com.mottinut.shared.domain.valueobjects.Email;
import com.mottinut.shared.domain.valueobjects.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaUserRepository implements UserRepository {

    private final UserJpaRepository userJpaRepository;
    private final UserMapper userMapper;

    public JpaUserRepository(UserJpaRepository userJpaRepository, UserMapper userMapper) {
        this.userJpaRepository = userJpaRepository;
        this.userMapper = userMapper;
    }

    @Override
    public User save(User user) {
        UserEntity entity = userMapper.toEntity(user);
        UserEntity savedEntity = userJpaRepository.save(entity);
        return userMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<User> findById(UserId userId) {
        return userJpaRepository.findById(userId.getValue())
                .map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return userJpaRepository.findByEmail(email.getValue())
                .map(userMapper::toDomain);
    }

    @Override
    public Optional<Patient> findPatientById(UserId userId) {
        return userJpaRepository.findById(userId.getValue())
                .filter(entity -> entity instanceof PatientEntity)
                .map(entity -> (Patient) userMapper.toDomain(entity));
    }

    @Override
    public Optional<Nutritionist> findNutritionistById(UserId userId) {
        return userJpaRepository.findById(userId.getValue())
                .filter(entity -> entity instanceof NutritionistEntity)
                .map(entity -> (Nutritionist) userMapper.toDomain(entity));
    }

    @Override
    public boolean existsByEmail(Email email) {
        return userJpaRepository.existsByEmail(email.getValue());
    }

    @Override
    public void deleteById(UserId userId) {
        userJpaRepository.deleteById(userId.getValue());
    }
}