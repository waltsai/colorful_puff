package com.chillycoffey.colorfulpuff.entity;

import com.chillycoffey.colorfulpuff.core.ModEntities;
import com.chillycoffey.colorfulpuff.entity.mob.PuffEntity;
import com.chillycoffey.colorfulpuff.client.model.PuffEntityModel;
import com.chillycoffey.colorfulpuff.client.renderer.PuffEntityRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.TexturedModelData;

public class ModEntityInitalizer {

    public static void setupAttributes() {
        FabricDefaultAttributeRegistry.register(ModEntities.PUFF, PuffEntity.createPuffAttributes());
    }

    public static void setupRenderers() {
        EntityRendererRegistry.register(ModEntities.PUFF, PuffEntityRenderer::new);
    }

    public static void registerModels() {
        EntityModelLayerRegistry.registerModelLayer(ModEntityModelLayers.PUFF, () -> TexturedModelData.of(PuffEntityModel.getTexturedModelData(Dilation.NONE), 64, 64));
    }
}
