package ru.minusd.security.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoritePlaceDTO {
    private Long id;

    @NotBlank(message = "Place name cannot be blank")
    @Size(min = 2, max = 100, message = "Place name must be between 2 and 100 characters")
    private String placeName;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
}
