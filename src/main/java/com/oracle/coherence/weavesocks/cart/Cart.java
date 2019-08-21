package com.oracle.coherence.weavesocks.cart;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Cart implements Serializable {
    public String customerId; // Public instead of getters/setters.
    private Map<String, Item> items = new LinkedHashMap<>();

    public Cart() {
        this(null);
    }

    public Cart(String customerId) {
        this.customerId = customerId;
    }

    public List<Item> items() {
        return new ArrayList<>(items.values());
    }

    public Item getItem(String itemId) {
        return items.get(itemId);
    }

    public Cart add(Item item) {
        Item existing = items.putIfAbsent(item.itemId(), item);
        if (existing != null) {
            existing.setQuantity(existing.quantity() + item.quantity());
        }
        return this;
    }

    public Cart remove(Item item) {
        return remove(item.itemId());
    }

    public Cart remove(String itemId) {
        items.remove(itemId);
        return this;
    }

    public Cart update(Item item) {
        Item updated = items.computeIfPresent(item.itemId(), (id, it) -> {
            it.setQuantity(item.quantity());
            return it;
        });
        if (updated == null) {
            add(item);
        }
        return this;
    }

    public Cart merge(Cart other) {
        other.items().forEach(this::add);
        return this;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "customerId='" + customerId + '\'' +
                ", items=" + items +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cart cart = (Cart) o;

        return Objects.equals(customerId, cart.customerId);
    }

    @Override
    public int hashCode() {
        return customerId != null ? customerId.hashCode() : 0;
    }
}
