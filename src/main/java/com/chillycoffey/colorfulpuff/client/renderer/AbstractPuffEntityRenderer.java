package com.chillycoffey.colorfulpuff.client.renderer;

import com.chillycoffey.colorfulpuff.ModInit;
import com.chillycoffey.colorfulpuff.entity.mob.PuffBaseEntity;
import com.chillycoffey.colorfulpuff.client.model.PuffEntityModel;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public abstract class AbstractPuffEntityRenderer<T extends PuffBaseEntity> extends BipedEntityRenderer<T, PuffEntityModel<T>> {
    public AbstractPuffEntityRenderer(EntityRendererFactory.Context ctx, PuffEntityModel model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Override
    protected void scale(T entity, MatrixStack matrices, float amount) {
        float f = 1.0F;

        if(entity.isBaby()) {
            f *= 0.66F;
        } else {
            f *= 0.88F;
        }

        matrices.scale(f, f, f);
    }

    @Override
    public final Identifier getTexture(T mobEntity) {
        return new Identifier(ModInit.MODID, this.getTextureLocation(mobEntity));
    }

    public abstract String getTextureLocation(T mobEntity);
}
