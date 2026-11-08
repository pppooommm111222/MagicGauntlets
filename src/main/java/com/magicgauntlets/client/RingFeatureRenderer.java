package com.magicgauntlets.client;
import com.magicgauntlets.MagicGauntlets;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;

public class RingFeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    private final ItemRenderer itemRenderer;
    public RingFeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context) {
        super(context);
        this.itemRenderer = MinecraftClient.getInstance().getItemRenderer();
    }
    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        // 只要玩家背包里有任何一个手环，就显示（这里用引力手环测试）
        ItemStack ringStack = new ItemStack(MagicGauntlets.GRAVITY_GAUNTLET);
        boolean hasRing = entity.getInventory().main.stream().anyMatch(s -> s.isOf(MagicGauntlets.GRAVITY_GAUNTLET) || s.isOf(MagicGauntlets.CRYO_GAUNTLET) || s.isOf(MagicGauntlets.STARBURST_GAUNTLET) || s.isOf(MagicGauntlets.THUNDER_GAUNTLET));
        
        if (hasRing) {
             this.getContextModel().rightArm.rotate(matrices);
             matrices.translate(-0.08, 0.55, 0.08); // 调整在手腕的位置
             matrices.scale(0.4f, 0.4f, 0.4f); // 大小
             matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
             this.itemRenderer.renderItem(ringStack, ModelTransformationMode.FIXED, false, matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV, MinecraftClient.getInstance().getItemRenderer().getModels().getModel(ringStack));
        }
    }
}
