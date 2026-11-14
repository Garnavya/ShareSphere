package com.sharesphere.service;

import com.sharesphere.dto.request.CreateItemRequest;
import com.sharesphere.entity.Item;
import com.sharesphere.entity.ItemImage;
import com.sharesphere.entity.User;
import com.sharesphere.exception.ResourceNotFoundException;
import com.sharesphere.repository.ItemRepository;
import com.sharesphere.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<Item> getItems(String category, Boolean isFree, String search) {
        if (category == null && isFree == null && (search == null || search.isBlank())) {
            return itemRepository.findByIsAvailableTrueOrderByCreatedAtDesc();
        }
        return itemRepository.searchItems(
                (category != null && !category.isBlank()) ? category : null,
                isFree,
                (search != null && !search.isBlank()) ? search.trim() : null
        );
    }

    @Transactional(readOnly = true)
    public Item getItemById(String id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));
    }

    @Transactional
    public Item createItem(CreateItemRequest request, String ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner user not found with id: " + ownerId));

        BigDecimal dailyRate = Boolean.TRUE.equals(request.getIsFree()) ? BigDecimal.ZERO : request.getDailyRate();

        Item item = Item.builder()
                .title(request.getTitle().trim())
                .description(request.getDescription())
                .category(request.getCategory().trim())
                .condition(request.getCondition())
                .dailyRate(dailyRate != null ? dailyRate : BigDecimal.ZERO)
                .securityDeposit(request.getSecurityDeposit() != null ? request.getSecurityDeposit() : BigDecimal.ZERO)
                .isFree(Boolean.TRUE.equals(request.getIsFree()))
                .isAvailable(true)
                .address(request.getAddress().trim())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .owner(owner)
                .build();

        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            List<ItemImage> images = new ArrayList<>();
            for (int i = 0; i < request.getImageUrls().size(); i++) {
                images.add(ItemImage.builder()
                        .item(item)
                        .url(request.getImageUrls().get(i))
                        .isPrimary(i == 0)
                        .build());
            }
            item.setImages(images);
        }

        return itemRepository.save(item);
    }
}
