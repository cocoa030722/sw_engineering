package com.group.sw_engineering.domain;

import java.time.Instant;

/**
 * Plain domain model. Deliberately framework-agnostic (no JPA annotations) so the
 * same class can be persisted by an in-memory store today and later mapped to a
 * real table (e.g. by adding {@code @Entity} here, or by introducing a separate
 * JPA entity + mapper) without changing the service/controller layer.
 */
public class Item {

    private Long id;
    private String name;
    private String description;
    private boolean done;
    private Instant createdAt;

    public Item() {
    }

    public Item(Long id, String name, String description, boolean done, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.done = done;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
