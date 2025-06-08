package com.mottinut.auth.presentation.controllers;

import com.mottinut.auth.domain.entities.User;
import com.mottinut.auth.domain.services.AuthService;
import com.mottinut.auth.domain.services.UserService;
import com.mottinut.auth.domain.valueobjects.Role;
import com.mottinut.auth.infrastructure.security.CustomUserPrincipal;
import com.mottinut.auth.infrastructure.security.JwtTokenProvider;
import com.mottinut.auth.presentation.dto.LoginRequest;
import com.mottinut.auth.presentation.dto.LoginResponse;
import com.mottinut.auth.presentation.dto.RegisterRequest;
import com.mottinut.auth.presentation.dto.UserResponse;
import com.mottinut.shared.domain.valueobjects.Email;
import com.mottinut.shared.presentation.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final JwtTokenProvider tokenProvider;

    public AuthController(AuthService authService, UserService userService, JwtTokenProvider tokenProvider) {
        this.authService = authService;
        this.userService = userService;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<LoginResponse>> register(@Valid @RequestBody RegisterRequest request) {
        User user = authService.register(
                new Email(request.getEmail()),
                request.getPassword(),
                Role.fromString(request.getRole()),
                request.getFirstName(),
                request.getLastName(),
                request.getBirthDate(),
                request.getPhone(),
                request.getHeight(),
                request.getWeight(),
                request.isHasMedicalCondition(),
                request.getChronicDisease(),
                request.getAllergies(),
                request.getDietaryPreferences()
        );

        var token = tokenProvider.generateToken(user.getUserId(), user.getRole());

        LoginResponse response = new LoginResponse(
                token.getValue(),
                user.getUserId().getValue(),
                user.getRole().getValue(),
                user.getFullName()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Usuario registrado exitosamente", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        User user = authService.authenticate(
                new Email(request.getEmail()),
                request.getPassword()
        );

        var token = tokenProvider.generateToken(user.getUserId(), user.getRole());

        LoginResponse response = new LoginResponse(
                token.getValue(),
                user.getUserId().getValue(),
                user.getRole().getValue(),
                user.getFullName()
        );

        return ResponseEntity.ok(new ApiResponse<>("Login exitoso", response));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile(@AuthenticationPrincipal CustomUserPrincipal principal) {
        User user = principal.getUser();
        UserResponse response = UserResponse.fromUser(user);
        return ResponseEntity.ok(new ApiResponse<>("Perfil obtenido exitosamente", response));
    }

    @GetMapping("/verify-token")
    public ResponseEntity<ApiResponse<Map<String, Object>>> verifyToken(@AuthenticationPrincipal CustomUserPrincipal principal) {
        User user = principal.getUser();

        Map<String, Object> response = new HashMap<>();
        response.put("valid", true);
        response.put("user_id", user.getUserId().getValue());
        response.put("role", user.getRole().getValue());
        response.put("email", user.getEmail().getValue());

        return ResponseEntity.ok(new ApiResponse<>("Token válido", response));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "ok");
        response.put("message", "API Auth funcionando correctamente");
        response.put("version", "1.0.0");
        response.put("timestamp", LocalDateTime.now().toString());

        return ResponseEntity.ok(response);
    }
}
