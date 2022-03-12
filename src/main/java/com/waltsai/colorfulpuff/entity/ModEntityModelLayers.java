package com.waltsai.colorfulpuff.entity;

import com.waltsai.colorfulpuff.ModInit;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class ModEntityModelLayers {
    public static final EntityModelLayer PUFF = register("puff", "main");

    public static EntityModelLayer register(String id, String layer) {
        return new EntityModelLayer(new Identifier(ModInit.MODID, id), layer);
    }
}
