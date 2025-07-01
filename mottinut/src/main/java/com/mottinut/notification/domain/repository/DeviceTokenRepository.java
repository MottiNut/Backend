package com.mottinut.notification.domain.repository;

import com.mottinut.notification.domain.entities.UserDeviceToken;
import com.mottinut.notification.domain.valueobjects.DeviceToken;
import com.mottinut.shared.domain.valueobjects.UserId;

import java.util.List;
import java.util.Optional;

public interface DeviceTokenRepository {
    Optional<DeviceToken> findByUserId(UserId userId);
    List<DeviceToken> findAllActiveByUserId(UserId userId);
    void save(UserId userId, DeviceToken deviceToken);
    void remove(UserId userId);
    void markAsInvalid(UserId userId, DeviceToken deviceToken);
    boolean existsForUser(UserId userId);
    Optional<DeviceToken> findByValue(String value);
    Optional<UserDeviceToken> findFullByValue(String value); // nuevo método
    void deactivateAllByUser(UserId userId);
    void reactivate(UserId userId, DeviceToken token);


}
