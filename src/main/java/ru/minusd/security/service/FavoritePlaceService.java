package ru.minusd.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.minusd.security.domain.dto.FavoritePlaceDTO;
import ru.minusd.security.domain.model.FavoritePlace;
import ru.minusd.security.domain.model.User;
import ru.minusd.security.repository.FavoritePlaceRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoritePlaceService {
    private final FavoritePlaceRepository favoritePlaceRepository;

    /**
     * Получить все любимые места пользователя
     *
     * @param userId ID пользователя
     * @return список любимых мест
     */
    public List<FavoritePlaceDTO> getUserFavoritePlaces(Long userId) {
        return favoritePlaceRepository.findByUserId(userId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Создать новое любимое место
     *
     * @param dto данные места
     * @param user пользователь
     * @return созданное место
     */
    public FavoritePlaceDTO createFavoritePlace(FavoritePlaceDTO dto, User user) {
        FavoritePlace place = FavoritePlace.builder()
                .placeName(dto.getPlaceName())
                .description(dto.getDescription())
                .user(user)
                .build();

        FavoritePlace saved = favoritePlaceRepository.save(place);
        return mapToDTO(saved);
    }

    /**
     * Обновить любимое место
     *
     * @param placeId ID места
     * @param dto новые данные
     * @param userId ID пользователя (для проверки прав)
     * @return обновленное место
     */
    public FavoritePlaceDTO updateFavoritePlace(Long placeId, FavoritePlaceDTO dto, Long userId) {
        FavoritePlace place = favoritePlaceRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("Place not found"));

        // Проверка, что место принадлежит пользователю
        if (!place.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("You don't have permission to update this place");
        }

        place.setPlaceName(dto.getPlaceName());
        place.setDescription(dto.getDescription());

        FavoritePlace updated = favoritePlaceRepository.save(place);
        return mapToDTO(updated);
    }

    /**
     * Удалить любимое место
     *
     * @param placeId ID места
     * @param userId ID пользователя (для проверки прав)
     */
    public void deleteFavoritePlace(Long placeId, Long userId) {
        FavoritePlace place = favoritePlaceRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("Place not found"));

        // Проверка, что место принадлежит пользователю
        if (!place.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("You don't have permission to delete this place");
        }

        favoritePlaceRepository.deleteById(placeId);
    }

    private FavoritePlaceDTO mapToDTO(FavoritePlace place) {
        return FavoritePlaceDTO.builder()
                .id(place.getId())
                .placeName(place.getPlaceName())
                .description(place.getDescription())
                .build();
    }
}
