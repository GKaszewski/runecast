package dev.gabrielkaszewski.runecast.mixin;

import dev.gabrielkaszewski.runecast.effect.EffectRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public class LivingEntityFallMixin {
    @ModifyVariable(method = "onLanding", at = @At("HEAD"), argsOnly = true, index = 1)
    private float onFall(float distance) {
        if (!((Object) this instanceof PlayerEntity player)) return distance;
        ItemStack boots = player.inventory.getArmorStack(0);
        if (boots == null) return distance;
        return EffectRegistry.applyFall(boots, distance);
    }
}
