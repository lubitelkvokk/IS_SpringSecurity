package ru.minusd.security.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.minusd.security.domain.model.Role;

@AllArgsConstructor
@Data
public class UserDTO {
    private String username;
    private String email;
    private Role role;
}
