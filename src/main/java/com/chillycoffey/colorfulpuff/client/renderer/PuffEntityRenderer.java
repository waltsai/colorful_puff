package com.chillycoffey.colorfulpuff.client.renderer;

import com.chillycoffey.colorfulpuff.ModInit;
import com.chillycoffey.colorfulpuff.entity.ModEntityModelLayers;
import com.chillycoffey.colorfulpuff.entity.mob.PuffEntity;
import com.chillycoffey.colorfulpuff.client.model.PuffEntityModel;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.*;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class PuffEntityRenderer extends AbstractPuffEntityRenderer<PuffEntity> {
    public PuffEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new PuffEntityModel<>(ctx.getPart(ModEntityModelLayers.PUFF)), 0.5F);
        this.addFeature(new PuffEyeFeatureRenderer<>(this) {
            @Override
            public Identifier getEyeTexture(PuffEntity entity) {
                return new Identifier(ModInit.MODID, "textures/entity/original/eyes/" + entity.getEyeType().name().toLowerCase() + ".png");
            }
        });
        this.addFeature(new ArmorFeatureRenderer<>(this, new BipedEntityModel<>(ctx.getPart(EntityModelLayers.PLAYER_INNER_ARMOR)), new BipedEntityModel<>(ctx.getPart(EntityModelLayers.PLAYER_OUTER_ARMOR))));
        this.addFeature(new HeldItemFeatureRenderer<>(this));
        this.addFeature(new HeadFeatureRenderer<>(this, ctx.getModelLoader()));
        this.addFeature(new ElytraFeatureRenderer<>(this, ctx.getModelLoader()));
    }

    @Override
    protected void scale(PuffEntity entity, MatrixStack matrices, float amount) {
        super.scale(entity, matrices, amount);

        if(entity.getClothType() == PuffEntity.ClothType.HONEY) {
            matrices.scale(0.909F, 0.909F, 0.909F);
        }
    }

    @Override
    public String getTextureLocation(PuffEntity entity) {
        return "textures/entity/original/cloth/" + entity.getClothType().name().toLowerCase() + ".png";
    }

    @Override
    public void render(PuffEntity mobEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        super.render(mobEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}
