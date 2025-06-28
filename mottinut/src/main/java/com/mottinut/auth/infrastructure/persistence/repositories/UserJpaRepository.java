package com.mottinut.auth.infrastructure.persistence.repositories;

import com.mottinut.auth.domain.valueobjects.Role;
import com.mottinut.auth.infrastructure.persistence.entities.NutritionistEntity;
import com.mottinut.auth.infrastructure.persistence.entities.PatientEntity;
import com.mottinut.auth.infrastructure.persistence.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    // Query simplificada ya que tenemos tablas separadas
    @Query("SELECT p FROM PatientEntity p WHERE p.userId = :userId")
    Optional<PatientEntity> findPatientByUserId(@Param("userId") Long userId);

    @Query("SELECT n FROM NutritionistEntity n WHERE n.userId = :userId")
    Optional<NutritionistEntity> findNutritionistByUserId(@Param("userId") Long userId);

    // Consultas adicionales por tipo
    @Query("SELECT u FROM UserEntity u WHERE u.userType = :userType")
    List<UserEntity> findByUserType(@Param("userType") Role userType);
}
