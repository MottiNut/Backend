/*package com.mottinut.auth.infrastructure.persistence.mappersCode;
import com.mottinut.auth.domain.emalServices.entity.UserVerification;
import com.mottinut.auth.domain.emalServices.entity.UserVerificationJpaEntity;
import com.mottinut.shared.domain.valueobjects.UserId;
import org.springframework.stereotype.Component;

@Component
public class UserVerificationMapper {

    public UserVerificationJpaEntity toJpaEntity(UserVerification domain) {
        if (domain == null) {
            return null;
        }

        UserVerificationJpaEntity entity = new UserVerificationJpaEntity();
        entity.setId(domain.getId());
        entity.setUserId(domain.getUserId().getValue());
        entity.setCode(domain.getCode());
        entity.setType(domain.getType());
        entity.setContact(domain.getContact());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setExpiresAt(domain.getExpiresAt());
        entity.setVerified(domain.isVerified());
        entity.setVerifiedAt(domain.getVerifiedAt());

        return entity;
    }

    public UserVerification toDomain(UserVerificationJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return UserVerification.builder()
                .id(entity.getId())
                .userId(new UserId(entity.getUserId()))
                .code(entity.getCode())
                .type(entity.getType())
                .contact(entity.getContact())
                .createdAt(entity.getCreatedAt())
                .expiresAt(entity.getExpiresAt())
                .isVerified(entity.isVerified())
                .verifiedAt(entity.getVerifiedAt())
                .build();
    }
}*/