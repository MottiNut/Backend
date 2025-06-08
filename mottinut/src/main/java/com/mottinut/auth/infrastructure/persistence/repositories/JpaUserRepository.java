package com.mottinut.auth.infrastructure.persistence.repositories;

import com.mottinut.auth.domain.entities.User;
import com.mottinut.auth.domain.repositories.UserRepository;
import com.mottinut.auth.infrastructure.persistence.entities.UserEntity;
import com.mottinut.auth.infrastructure.persistence.mappers.UserMapper;
import com.mottinut.shared.domain.valueobjects.Email;
import com.mottinut.shared.domain.valueobjects.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

interface SpringUserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);
}
@Repository
public class JpaUserRepository implements UserRepository {
    private final SpringUserRepository springRepository;
    private final UserMapper userMapper;

    public JpaUserRepository(SpringUserRepository springRepository, UserMapper userMapper) {
        this.springRepository = springRepository;
        this.userMapper = userMapper;
    }

    @Override
    public User save(User user) {
        UserEntity entity = userMapper.toEntity(user);
        UserEntity saved = springRepository.save(entity);
        return userMapper.toDomain(saved);
    }

    @Override
    public Optional<User> findById(UserId userId) {
        return springRepository.findById(userId.getValue())
                .map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return springRepository.findByEmail(email.getValue())
                .map(userMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return springRepository.existsByEmail(email.getValue());
    }

    @Override
    public void deleteById(UserId userId) {
        springRepository.deleteById(userId.getValue());
    }
}