package dev.gabrielkaszewski.runecast.mixin;

import dev.gabrielkaszewski.runecast.effect.EffectRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public class ItemStackDamageMixin {
    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void onDamage(int amount, Entity entity, CallbackInfo ci) {
        ItemStack self = (ItemStack) (Object) this;
        if (!EffectRegistry.shouldApplyDurability(self)) ci.cancel();
    }
}
