package com.chillycoffey.colorfulpuff.client.renderer;

import com.chillycoffey.colorfulpuff.ModInit;
import com.chillycoffey.colorfulpuff.entity.mob.PuffBaseEntity;
import com.chillycoffey.colorfulpuff.client.model.PuffEntityModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;


@Environment(EnvType.CLIENT)
public abstract class PuffEyeFeatureRenderer<T extends PuffBaseEntity, M extends PuffEntityModel<T>> extends FeatureRenderer<T,M> {
    private static final float BLINKING_TICK = 1.668f * 2;

    public PuffEyeFeatureRenderer(FeatureRendererContext<T, M> context) {
        super(context);
    }

    public final Identifier getTexture(T entity) {
        if(entity.isSleeping()) {
            return getEyebrowTexture();
        } else {
            return getEyeTexture(entity);
        }
    }

    public abstract Identifier getEyeTexture(T entity);

    public Identifier getEyebrowTexture() {
        return new Identifier(ModInit.MODID, "textures/entity/original/eye/eyebrow.png");
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(getTexture(entity)));
        if(entity.isSleeping()) {
            this.getContextModel().eyebrows.render(matrices, vertexConsumer, light, LivingEntityRenderer.getOverlay(entity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
        } else {
            if(!entity.isEntityBlinking && entity.isInBlinkAnimation()) {
                entity.isEntityBlinking = true;
                entity.blinkingAge = entity.age + tickDelta;
            }

            float f = entity.age + tickDelta - entity.blinkingAge;

            if(entity.isEntityBlinking && f <= BLINKING_TICK) {
                matrices.push();
                setEyelidCoverRate(matrices, Math.min(Math.max(Math.abs(f - BLINKING_TICK / 2F) / (BLINKING_TICK / 2F), 0), 1), headPitch, entity.isInSittingPose(), entity.hasVehicle());
                this.getContextModel().eyes.render(matrices, vertexConsumer, light, LivingEntityRenderer.getOverlay(entity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
                matrices.pop();
            } else {
                if(entity.isEntityBlinking) {
                    entity.isEntityBlinking = false;
                }
                matrices.translate(0, (entity.isInSittingPose() ? 0.65625F : 0.0F), 0);
                matrices.translate(0, (entity.hasVehicle() ? 0.53125F : 0.0F), 0);
                this.getContextModel().eyes.render(matrices, vertexConsumer, light, LivingEntityRenderer.getOverlay(entity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }



    private void setEyelidCoverRate(MatrixStack matrices, float f, float headPitch, boolean isSitting, boolean isRiding) {
        float offset = (1 - f) * -0.0625F;
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(headPitch));

        matrices.translate(0, offset, 0);

        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-headPitch));
        matrices.translate(0, (isSitting ? 0.65625F : 0.0F), 0);
        matrices.translate(0, (isRiding ? 0.53125F : 0.0F), 0);
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(headPitch));

        matrices.scale(1, f, 1);

        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-headPitch));
    }
}