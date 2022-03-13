package com.waltsai.colorfulpuff;

import com.waltsai.colorfulpuff.core.ModBlocks;
import com.waltsai.colorfulpuff.core.ModConfigs;
import com.waltsai.colorfulpuff.core.ModEntities;
import com.waltsai.colorfulpuff.core.ModItems;
import com.waltsai.colorfulpuff.entity.ModEntityInitalizer;
import net.fabricmc.api.ModInitializer;

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
    }
}
