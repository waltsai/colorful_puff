package com.waltsai.colorfulpuff.core;

import com.waltsai.colorfulpuff.ModInit;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class ModEntityModelLayers {
    public static final EntityModelLayer PUFF = register("puff", "main");
    public static final EntityModelLayer PUFF_BASE_INNER_ARMOR = register("puff", "inner_armor");
    public static final EntityModelLayer PUFF_BASE_OUTER_ARMOR = register("puff", "outer_armor");

    public static EntityModelLayer register(String id, String layer) {
        return new EntityModelLayer(new Identifier(ModInit.MODID, id), layer);
    }
}
