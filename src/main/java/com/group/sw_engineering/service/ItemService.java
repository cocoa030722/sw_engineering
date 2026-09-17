package com.group.sw_engineering.service;

import com.group.sw_engineering.domain.Item;
import com.group.sw_engineering.dto.ItemRequest;
import com.group.sw_engineering.exception.ResourceNotFoundException;
import com.group.sw_engineering.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * Application logic sits here, between the HTTP layer (controller) and the
 * storage layer (repository). The controller never talks to the repository
 * directly — that keeps request/response concerns and persistence concerns
 * from leaking into each other.
 */
@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    public Item findById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item " + id + " not found"));
    }

    public Item create(ItemRequest request) {
        Item item = new Item(null, request.getName(), request.getDescription(), request.isDone(), Instant.now());
        return itemRepository.save(item);
    }

    public Item update(Long id, ItemRequest request) {
        Item existing = findById(id);
        existing.setName(request.getName());
        existing.setDescription(request.getDescription());
        existing.setDone(request.isDone());
        return itemRepository.save(existing);
    }

    public void delete(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Item " + id + " not found");
        }
        itemRepository.deleteById(id);
    }
}
