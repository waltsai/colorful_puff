package com.chillycoffey.colorfulpuff;

import com.chillycoffey.colorfulpuff.core.ModEntities;
import com.chillycoffey.colorfulpuff.core.ModItems;
import com.chillycoffey.colorfulpuff.entity.ModEntityInitalizer;
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
    }
}
