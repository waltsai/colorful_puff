package com.chillycoffey.colorfulpuff;

import com.chillycoffey.colorfulpuff.core.ModParticles;
import com.chillycoffey.colorfulpuff.entity.ModEntityInitalizer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ClientModInit implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModEntityInitalizer.setupRenderers();
        ModEntityInitalizer.registerModels();
        ModParticles.registerParticles();
    }
}
