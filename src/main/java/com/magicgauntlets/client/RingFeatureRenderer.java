package com.magicgauntlets.client;

import com.magicgauntlets.MagicGauntlets;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.client.network.AbstractClientPlayerEntity;

public class RingFeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    private final ItemRenderer itemRenderer;

    public RingFeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context) {
        super(context);
        this.itemRenderer = MinecraftClient.getInstance().getItemRenderer();
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        ItemStack ringStack = new ItemStack(MagicGauntlets.GRAVITY_GAUNTLET);
        
        boolean hasRing = entity.getInventory().main.stream().anyMatch(s -> 
            s.isOf(MagicGauntlets.GRAVITY_GAUNTLET) || 
            s.isOf(MagicGauntlets.CRYO_GAUNTLET) || 
            s.isOf(MagicGauntlets.STARBURST_GAUNTLET) || 
            s.isOf(MagicGauntlets.THUNDER_GAUNTLET)
        );
        
        if (!hasRing) return;

        // 绑定到右手腕并调整位置大小
        this.getContextModel().rightArm.rotate(matrices);
        matrices.translate(-0.08, 0.55, 0.08); 
        matrices.scale(0.4f, 0.4f, 0.4f); 
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));

        // 1.21.5 正确的获取 BakedModel 方法
        BakedModel model = MinecraftClient.getInstance().getItemRenderer().getModel(ringStack, entity.getWorld(), entity, 0);
        
        // 1.21.5 正确的渲染方法
        this.itemRenderer.renderItem(
            ringStack, 
            ModelTransformationMode.FIXED, 
            false, 
            matrices, 
            vertexConsumers, 
            light, 
            OverlayTexture.DEFAULT_UV, 
            model
        );
    }
}
