package dev.gabrielkaszewski.runecast.mixin;

import dev.gabrielkaszewski.runecast.detection.RitualApplicator;
import dev.gabrielkaszewski.runecast.detection.RitualCluster;
import dev.gabrielkaszewski.runecast.detection.RitualScanner;
import dev.gabrielkaszewski.runecast.detection.RitualTimer;
import dev.gabrielkaszewski.runecast.nbt.EnchantmentNbt;
import dev.gabrielkaszewski.runecast.recipe.EnchantRecipe;
import dev.gabrielkaszewski.runecast.util.ItemIds;
import net.minecraft.entity.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {
    private static final int SETTLE_TICKS = 20;

    @Unique
    private RitualTimer runecast$timer = new RitualTimer(SETTLE_TICKS);

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        ItemEntity self = (ItemEntity) (Object) this;
        if (self.world.isRemote) return;

        if (runecast$timer.tick()) {
            // Timer fired — re-validate and apply
            EnchantRecipe match = detectMatch(self);
            if (match != null) RitualApplicator.apply(self.world, self, match);
        } else if (!runecast$timer.isArmed()) {
            // Idle — check for a new ritual
            if (detectMatch(self) != null) runecast$timer.arm();
        }
    }

    private EnchantRecipe detectMatch(ItemEntity self) {
        String targetId = ItemIds.resolve(self.stack);
        if (targetId == null) return null;

        RitualCluster cluster = RitualCluster.collect(self.world, self);
        Map<String, Integer> counts = cluster.counts();

        return RitualScanner.scan(
            targetId,
            EnchantmentNbt.getEnchantments(self.stack),
            counts,
            counts.keySet()
        );
    }
}
