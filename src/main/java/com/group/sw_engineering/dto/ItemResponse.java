package com.group.sw_engineering.dto;

import com.group.sw_engineering.domain.Item;

import java.time.Instant;

/**
 * Shape of the JSON returned to the client. A dedicated response type avoids
 * leaking internal domain fields and gives the frontend a stable contract even
 * if {@link Item} changes shape later (e.g. once a real DB entity is introduced).
 */
public record ItemResponse(Long id, String name, String description, boolean done, Instant createdAt) {

    public static ItemResponse from(Item item) {
        return new ItemResponse(item.getId(), item.getName(), item.getDescription(), item.isDone(), item.getCreatedAt());
    }
}
