package com.chillycoffey.colorfulpuff.client.renderer;

import com.chillycoffey.colorfulpuff.ModInit;
import com.chillycoffey.colorfulpuff.entity.mob.PuffBaseEntity;
import com.chillycoffey.colorfulpuff.client.model.PuffEntityModel;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;

public abstract class AbstractPuffEntityRenderer<T extends PuffBaseEntity> extends BipedEntityRenderer<T, PuffEntityModel<T>> {
    public AbstractPuffEntityRenderer(EntityRendererFactory.Context ctx, PuffEntityModel<T> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Override
    protected void scale(T entity, MatrixStack matrices, float amount) {
        float f;

        if(entity.isBaby()) {
            f = 0.66F;
        } else {
            f = 0.88F;
        }

        matrices.scale(f, f, f);
    }

    @Override
    protected void setupTransforms(T entity, MatrixStack matrices, float animationProgress, float bodyYaw, float tickDelta) {
        super.setupTransforms(entity, matrices, animationProgress, bodyYaw, tickDelta);
        EntityPose entityPose = (entity).getPose();
        if (entityPose == EntityPose.SLEEPING) {
            if(entity.isBaby()) {
                matrices.translate(0, -0.34F, 0);
            } else {
                matrices.translate(0, -0.12F, 0);
            }
        }
    }

    @Override
    public final Identifier getTexture(T mobEntity) {
        return new Identifier(ModInit.MODID, this.getTextureLocation(mobEntity));
    }

    public abstract String getTextureLocation(T mobEntity);
}
