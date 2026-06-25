package dev.gabrielkaszewski.runecast.mixin;

import dev.gabrielkaszewski.runecast.effect.EffectRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class LivingEntityAttackMixin {
    @Inject(method = "attack", at = @At("RETURN"))
    private void onAttack(Entity target, CallbackInfo ci) {
        if (!(target instanceof LivingEntity living)) return;
        PlayerEntity self = (PlayerEntity) (Object) this;
        ItemStack held = self.inventory.getSelectedItem();
        if (held == null) return;
        float bonus = EffectRegistry.computeAttackBonus(held);
        int extraInt = Math.round(bonus);
        if (extraInt > 0) {
            if (living.hurtTime > 0) return;
            ((LivingEntityAccessor) living).invokeApplyDamage(extraInt);
        }

        int knockback = Math.round(EffectRegistry.computeKnockbackBonus(held));
        if (knockback > 0) {
            living.applyKnockback(self, knockback, self.x - target.x, self.z - target.z);
        }
    }
}
