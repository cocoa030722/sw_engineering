package com.group.sw_engineering.repository;

import com.group.sw_engineering.domain.Item;

import java.util.List;
import java.util.Optional;

/**
 * Persistence contract for {@link Item}. The service layer only depends on this
 * interface, never on a concrete storage technology.
 *
 * <p>Today {@link InMemoryItemRepository} is the only implementation. To move to a
 * real database later:
 * <ol>
 *   <li>Add {@code spring-boot-starter-data-jpa} (+ a DB driver) to build.gradle.</li>
 *   <li>Create a JPA entity (either annotate {@link Item} with {@code @Entity} or
 *       add a dedicated entity class) and a Spring Data interface, e.g.
 *       {@code interface JpaItemRepository extends JpaRepository<Item, Long>, ItemRepository { }}</li>
 *   <li>Delete/disable {@link InMemoryItemRepository} (or leave it behind a
 *       profile) — because the service layer only talks to this interface, no
 *       other code needs to change.</li>
 * </ol>
 */
public interface ItemRepository {

    List<Item> findAll();

    Optional<Item> findById(Long id);

    Item save(Item item);

    void deleteById(Long id);

    boolean existsById(Long id);
}
