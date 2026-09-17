package com.group.sw_engineering.controller;

import com.group.sw_engineering.domain.Item;
import com.group.sw_engineering.dto.ItemRequest;
import com.group.sw_engineering.dto.ItemResponse;
import com.group.sw_engineering.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API consumed by the React frontend (see frontend/src/api/items.js).
 * Routes:
 *   GET    /api/items      -> list
 *   GET    /api/items/{id} -> detail
 *   POST   /api/items      -> create
 *   PUT    /api/items/{id} -> update
 *   DELETE /api/items/{id} -> delete
 */
@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<ItemResponse> findAll() {
        return itemService.findAll().stream().map(ItemResponse::from).toList();
    }

    @GetMapping("/{id}")
    public ItemResponse findById(@PathVariable Long id) {
        return ItemResponse.from(itemService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponse create(@Valid @RequestBody ItemRequest request) {
        Item created = itemService.create(request);
        return ItemResponse.from(created);
    }

    @PutMapping("/{id}")
    public ItemResponse update(@PathVariable Long id, @Valid @RequestBody ItemRequest request) {
        Item updated = itemService.update(id, request);
        return ItemResponse.from(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        itemService.delete(id);
    }
}
