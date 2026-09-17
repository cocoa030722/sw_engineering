package com.group.sw_engineering.repository;

import com.group.sw_engineering.domain.Item;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Storage stand-in for a real database. Data lives only for the lifetime of the
 * JVM process — restarting the app clears it.
 *
 * <p>Gated behind {@code app.repository.type=memory} (the default) so a future
 * JPA-backed implementation can be switched in purely via configuration, e.g.
 * {@code app.repository.type=jpa} in application.properties, with no code in
 * the service/controller layer changing.
 */
@Repository
@ConditionalOnProperty(name = "app.repository.type", havingValue = "memory", matchIfMissing = true)
public class InMemoryItemRepository implements ItemRepository {

    private final Map<Long, Item> store = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(0);

    public InMemoryItemRepository() {
        seed("Learn Spring Boot", "Read the routing/controller layer first", false);
        seed("Learn React Router", "Wire up nested routes in the frontend", false);
        seed("Connect a real database", "Swap InMemoryItemRepository for a JPA one", false);
    }

    private void seed(String name, String description, boolean done) {
        save(new Item(null, name, description, done, Instant.now()));
    }

    @Override
    public List<Item> findAll() {
        return store.values().stream()
                .sorted((a, b) -> a.getId().compareTo(b.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Item save(Item item) {
        if (item.getId() == null) {
            item.setId(idSequence.incrementAndGet());
        }
        if (item.getCreatedAt() == null) {
            item.setCreatedAt(Instant.now());
        }
        store.put(item.getId(), item);
        return item;
    }

    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }

    @Override
    public boolean existsById(Long id) {
        return store.containsKey(id);
    }
}
