package com.waltsai.colorfulpuff.entity;

import com.waltsai.colorfulpuff.core.ModEntities;
import com.waltsai.colorfulpuff.core.ModEntityModelLayers;
import com.waltsai.colorfulpuff.entity.mob.PuffEntity;
import com.waltsai.colorfulpuff.client.model.PuffEntityModel;
import com.waltsai.colorfulpuff.client.renderer.PuffEntityRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.BipedEntityModel;

public class ModEntityInitalizer {

    public static void setupAttributes() {
        FabricDefaultAttributeRegistry.register(ModEntities.PUFF, PuffEntity.createPuffAttributes());
    }

    public static void setupRenderers() {
        EntityRendererRegistry.register(ModEntities.PUFF, PuffEntityRenderer::new);
    }

    public static void registerModels() {
        EntityModelLayerRegistry.registerModelLayer(ModEntityModelLayers.PUFF, () -> TexturedModelData.of(PuffEntityModel.getTexturedModelData(Dilation.NONE), 64, 64));
        EntityModelLayerRegistry.registerModelLayer(ModEntityModelLayers.PUFF_BASE_OUTER_ARMOR, () -> TexturedModelData.of(BipedEntityModel.getModelData(new Dilation(1.0f), 0.0f), 64, 32));
        EntityModelLayerRegistry.registerModelLayer(ModEntityModelLayers.PUFF_BASE_INNER_ARMOR, () -> TexturedModelData.of(BipedEntityModel.getModelData(new Dilation(0.5f), 0.0f), 64, 32));
    }
}
