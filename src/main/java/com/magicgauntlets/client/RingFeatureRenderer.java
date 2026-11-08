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
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
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
        
        boolean hasRing = false;
        // 修复 private 报错：遍历玩家整个背包格子
        for (int i = 0; i < entity.getInventory().size(); i++) {
            ItemStack stack = entity.getInventory().getStack(i);
            if (stack.isOf(MagicGauntlets.GRAVITY_GAUNTLET) || stack.isOf(MagicGauntlets.CRYO_GAUNTLET) || 
                stack.isOf(MagicGauntlets.STARBURST_GAUNTLET) || stack.isOf(MagicGauntlets.THUNDER_GAUNTLET)) {
                hasRing = true;
                break;
            }
        }
        
        if (!hasRing) return;

        // 绑定到右手腕并调整位置大小
        this.getContextModel().rightArm.rotate(matrices);
        matrices.translate(-0.08, 0.55, 0.08); 
        matrices.scale(0.4f, 0.4f, 0.4f); 
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));

        // 修复 API 报错：使用 ModelIdentifier 获取 1.21.5 的模型
        ModelIdentifier modelId = new ModelIdentifier(Identifier.of("magicgauntlets", "gravity_gauntlet"), "inventory");
        BakedModel model = MinecraftClient.getInstance().getBakedModelManager().getModel(modelId);
        
        // 渲染
        this.itemRenderer.renderItem(
            new ItemStack(MagicGauntlets.GRAVITY_GAUNTLET), 
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
