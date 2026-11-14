package com.sharesphere.controller;

import com.sharesphere.dto.request.CreateItemRequest;
import com.sharesphere.entity.Item;
import com.sharesphere.security.CustomUserDetails;
import com.sharesphere.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getItems(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean isFree,
            @RequestParam(required = false) String search
    ) {
        List<Item> items = itemService.getItems(category, isFree, search);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", items.size());
        response.put("items", items);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getItemById(@PathVariable String id) {
        Item item = itemService.getItemById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("item", item);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createItem(
            @Valid @RequestBody CreateItemRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Item item = itemService.createItem(request, userDetails.getId());
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Item listing created successfully.");
        response.put("item", item);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
