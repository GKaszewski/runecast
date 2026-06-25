package dev.gabrielkaszewski.runecast.mixin;

import dev.gabrielkaszewski.runecast.effect.EffectRegistry;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.modificationstation.stationapi.api.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public class BlockBreakMixin {
    @Inject(
        method = "getBlockBreakingSpeed(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/modificationstation/stationapi/api/block/BlockState;)F",
        at = @At("RETURN"),
        cancellable = true,
        remap = false
    )
    private void onGetBlockBreakingSpeed(BlockView world, BlockPos pos, BlockState state, CallbackInfoReturnable<Float> cir) {
        PlayerInventory self = (PlayerInventory) (Object) this;
        ItemStack held = self.getSelectedItem();
        if (held == null) return;
        float speed = cir.getReturnValue() != null ? cir.getReturnValue() : 1.0f;
        float newSpeed = EffectRegistry.applyMining(held, speed, state.getBlock(), self.player);
        if (newSpeed != speed) cir.setReturnValue(newSpeed);
    }
}
