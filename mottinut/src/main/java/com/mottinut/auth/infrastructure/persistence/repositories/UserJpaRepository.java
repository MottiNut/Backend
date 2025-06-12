package com.mottinut.auth.infrastructure.persistence.repositories;

import com.mottinut.auth.infrastructure.persistence.entities.NutritionistEntity;
import com.mottinut.auth.infrastructure.persistence.entities.PatientEntity;
import com.mottinut.auth.infrastructure.persistence.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM UserEntity u WHERE u.userId = :userId AND TYPE(u) = PatientEntity")
    Optional<PatientEntity> findPatientByUserId(@Param("userId") Long userId);

    @Query("SELECT u FROM UserEntity u WHERE u.userId = :userId AND TYPE(u) = NutritionistEntity")
    Optional<NutritionistEntity> findNutritionistByUserId(@Param("userId") Long userId);
}
