package dev.gabrielkaszewski.runecast.detection;

import dev.gabrielkaszewski.runecast.util.ItemIds;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.*;

public final class RitualCluster {
    public static final double RADIUS = 1.0;

    private final List<ItemEntity> ingredients;

    private RitualCluster(List<ItemEntity> ingredients) {
        this.ingredients = ingredients;
    }

    public static RitualCluster collect(World world, ItemEntity target) {
        Box aabb = Box.createCached(
            target.x - RADIUS, target.y - RADIUS, target.z - RADIUS,
            target.x + RADIUS, target.y + RADIUS, target.z + RADIUS
        );
        @SuppressWarnings("unchecked")
        List<ItemEntity> nearby = (List<ItemEntity>) world.collectEntitiesByClass(ItemEntity.class, aabb);
        List<ItemEntity> ingredients = new ArrayList<>();
        for (ItemEntity e : nearby) {
            if (e != target) ingredients.add(e);
        }
        return new RitualCluster(ingredients);
    }

    public Map<String, Integer> counts() {
        Map<String, Integer> result = new HashMap<>();
        for (ItemEntity e : ingredients) {
            String id = ItemIds.resolve(e.stack);
            if (id != null) result.merge(id, e.stack.count, Integer::sum);
        }
        return result;
    }

    public List<ItemEntity> entities() {
        return Collections.unmodifiableList(ingredients);
    }
}
