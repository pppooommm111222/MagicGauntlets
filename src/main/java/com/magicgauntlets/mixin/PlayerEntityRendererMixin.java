package com.magicgauntlets.mixin;

import com.magicgauntlets.MagicGauntlets;
import com.magicgauntlets.client.RingRenderState;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {
    
    @Inject(method = "createRenderState", at = @At("RETURN"), cancellable = true)
    private void onCreateRenderState(CallbackInfoReturnable<PlayerEntityRenderState> cir) {
        cir.setReturnValue(new RingRenderState());
    }

    @Inject(method = "updateRenderState(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;F)V", at = @At("TAIL"))
    private void onUpdateRenderState(AbstractClientPlayerEntity player, PlayerEntityRenderState state, float tickDelta, CallbackInfo ci) {
        if (state instanceof RingRenderState ringState) {
            for (int i = 0; i < player.getInventory().size(); i++) {
                ItemStack stack = player.getInventory().getStack(i);
                if (stack.isOf(MagicGauntlets.GRAVITY_GAUNTLET) || stack.isOf(MagicGauntlets.CRYO_GAUNTLET) || 
                    stack.isOf(MagicGauntlets.STARBURST_GAUNTLET) || stack.isOf(MagicGauntlets.THUNDER_GAUNTLET)) {
                    ringState.hasRing = true;
                    ringState.ringStack = stack;
                    return;
                }
            }
            ringState.hasRing = false;
            ringState.ringStack = ItemStack.EMPTY;
        }
    }
}
