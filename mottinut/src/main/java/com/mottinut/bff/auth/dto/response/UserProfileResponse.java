package com.mottinut.bff.auth.dto.response;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PatientProfileResponse.class, name = "patient"),
        @JsonSubTypes.Type(value = NutritionistProfileResponse.class, name = "nutritionist")
})
public abstract class UserProfileResponse {
    protected Long userId;
    protected String email;
    protected String firstName;
    protected String lastName;
    protected String fullName;
    protected LocalDate birthDate;
    protected String phone;
    protected String role;
    protected LocalDateTime createdAt;
}
