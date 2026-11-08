package com.magicgauntlets.client;

import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.item.ItemStack;

public class RingRenderState extends PlayerEntityRenderState {
    public boolean hasRing = false;
    public ItemStack ringStack = ItemStack.EMPTY;
}
