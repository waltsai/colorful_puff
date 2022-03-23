package com.waltsai.colorfulpuff;

import com.waltsai.colorfulpuff.core.ModBlocks;
import com.waltsai.colorfulpuff.core.ModConfigs;
import com.waltsai.colorfulpuff.core.ModEntities;
import com.waltsai.colorfulpuff.core.ModItems;
import com.waltsai.colorfulpuff.entity.ModEntityInitalizer;
import com.waltsai.colorfulpuff.entity.mob.PuffBaseEntity;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.attribute.EntityAttributeModifier;

import java.util.UUID;

import static com.waltsai.colorfulpuff.core.ModConfigs.CONFIG;

public class ModInit implements ModInitializer {
    public static final String MODID = "colorfulpuff";

    @Override
    public void onInitialize() {
        ModEntities.registerEntities();
        ModEntities.registerSchedules();
        ModEntities.registerBrain();
        ModEntityInitalizer.setupAttributes();
        ModItems.registerItems();
        ModBlocks.registerBlocks();
        ModBlocks.registerBlockItems();
        ModConfigs.createConfigData();
        PuffBaseEntity.ATTACKING_DAMAGE_BOOST = new EntityAttributeModifier(PuffBaseEntity.ATTACKING_DAMAGE_BOOST_ID, "Attacking damage boost", (ModConfigs.getDouble("PuffDamageMultiplier") - 1), EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
    }
}
