package com.group.sw_engineering.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Shape of the JSON body accepted by the create/update endpoints. Kept separate
 * from {@link com.group.sw_engineering.domain.Item} so the API contract can
 * evolve independently of the persistence model.
 */
public class ItemRequest {

    @NotBlank(message = "name is required")
    @Size(max = 100, message = "name must be at most 100 characters")
    private String name;

    @Size(max = 500, message = "description must be at most 500 characters")
    private String description;

    private boolean done;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
