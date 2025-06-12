package com.mottinut.auth.domain.entities;

import com.mottinut.auth.domain.valueobjects.Password;
import com.mottinut.auth.domain.valueobjects.Role;
import com.mottinut.shared.domain.valueobjects.Email;
import com.mottinut.shared.domain.valueobjects.UserId;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

public abstract class User {
    @Getter
    protected final UserId userId;
    @Getter
    protected final Email email;
    @Getter
    protected final Password password;
    @Getter
    protected final Role role;
    @Getter
    protected String firstName;
    @Getter
    protected String lastName;
    @Getter
    protected LocalDate birthDate;
    @Getter
    protected String phone;
    @Getter
    protected final LocalDateTime createdAt;

    protected User(UserId userId, Email email, Password password, Role role,
                   String firstName, String lastName, LocalDate birthDate, String phone) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.phone = phone;
        this.createdAt = LocalDateTime.now();
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public void updateBasicProfile(String firstName, String lastName, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }
}
