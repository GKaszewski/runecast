package dev.gabrielkaszewski.runecast.mixin;

import dev.gabrielkaszewski.runecast.effect.EffectRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class BlockAfterBreakMixin {
    @Inject(method = "afterBreak", at = @At("RETURN"))
    private void onAfterBreak(World world, PlayerEntity player, int x, int y, int z, int meta, CallbackInfo ci) {
        if (world.isRemote) return;
        ItemStack held = player.inventory.getSelectedItem();
        if (held == null) return;
        int extraRolls = Math.round(EffectRegistry.computeLootBonus(held));
        Block self = (Block) (Object) this;
        for (int i = 0; i < extraRolls; i++) {
            self.dropStacks(world, x, y, z, meta);
        }
    }
}
