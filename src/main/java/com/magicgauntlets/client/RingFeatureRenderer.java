package com.magicgauntlets.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;

public class RingFeatureRenderer extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {
    private final ItemRenderer itemRenderer;

    public RingFeatureRenderer(FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> context) {
        super(context);
        this.itemRenderer = MinecraftClient.getInstance().getItemRenderer();
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, PlayerEntityRenderState state, float limbAngle, float limbDistance) {
        if (!(state instanceof RingRenderState ringState) || !ringState.hasRing) {
            return;
        }

        matrices.push();
        this.getContextModel().rightArm.rotate(matrices);
        matrices.translate(-0.08, 0.55, 0.08); 
        matrices.scale(0.4f, 0.4f, 0.4f); 
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));

        ItemStack stack = ringState.ringStack;
        BakedModel model = MinecraftClient.getInstance().getItemRenderer().getModel(stack, MinecraftClient.getInstance().world, null, 0);
        
        this.itemRenderer.renderItem(
            stack, 
            ModelTransformationMode.FIXED, 
            false, 
            matrices, 
            vertexConsumers, 
            light, 
            OverlayTexture.DEFAULT_UV, 
            model
        );
        matrices.pop();
    }
}
