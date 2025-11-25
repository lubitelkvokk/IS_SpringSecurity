package ru.minusd.security.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.minusd.security.domain.dto.FavoritePlaceDTO;
import ru.minusd.security.domain.model.User;
import ru.minusd.security.service.FavoritePlaceService;

import java.util.List;

@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
@Tag(name = "Любимые места", description = "API для управления любимыми местами пользователя")
public class FavoritePlaceController {
    private final FavoritePlaceService favoritePlaceService;

    @GetMapping
    @Operation(summary = "Получить все любимые места текущего пользователя")
    public List<FavoritePlaceDTO> getUserPlaces(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return favoritePlaceService.getUserFavoritePlaces(user.getId());
    }

    @PostMapping
    @Operation(summary = "Добавить новое любимое место")
    public FavoritePlaceDTO createPlace(
            @Valid @RequestBody FavoritePlaceDTO dto,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return favoritePlaceService.createFavoritePlace(dto, user);
    }

    @PutMapping("/{placeId}")
    @Operation(summary = "Обновить любимое место")
    public FavoritePlaceDTO updatePlace(
            @PathVariable Long placeId,
            @Valid @RequestBody FavoritePlaceDTO dto,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return favoritePlaceService.updateFavoritePlace(placeId, dto, user.getId());
    }

    @DeleteMapping("/{placeId}")
    @Operation(summary = "Удалить любимое место")
    public void deletePlace(
            @PathVariable Long placeId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        favoritePlaceService.deleteFavoritePlace(placeId, user.getId());
    }
}
