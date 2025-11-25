package ru.minusd.security.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.minusd.security.domain.dto.UserDTO;
import ru.minusd.security.domain.model.User;
import ru.minusd.security.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Пользователи", description = "API для работы с пользователями")
public class ExampleController {
    private final UserService userService;

    @GetMapping
    @Operation(summary = "Получить список всех пользователей")
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/profile")
    @Operation(summary = "Получить профиль текущего пользователя")
    public UserDTO getProfile(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}