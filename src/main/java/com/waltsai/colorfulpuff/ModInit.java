package com.waltsai.colorfulpuff;

import com.waltsai.colorfulpuff.config.ModConfig;
import com.waltsai.colorfulpuff.config.SimpleConfig;
import com.waltsai.colorfulpuff.core.ModEntities;
import com.waltsai.colorfulpuff.core.ModItems;
import com.waltsai.colorfulpuff.entity.ModEntityInitalizer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.FabricLoaderImpl;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;

public class ModInit implements ModInitializer {
    public static final String MODID = "colorfulpuff";

    @Override
    public void onInitialize() {
        ModEntities.registerEntities();
        ModEntities.registerSchedules();
        ModEntities.registerBrain();
        ModEntityInitalizer.setupAttributes();
        ModItems.registerItems();
        ModConfig.createConfigData();
        /*
        try {
            path = path.resolve(ModInit.MODID).resolve( "config.properties" );
            if(!path.toFile().exists()) {
                path.toFile().createNewFile();
                PrintWriter writer = new PrintWriter(path.toFile(), "UTF-8");
                writer.write(configInitialData());
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

         */
    }

    public static String configInitialData(String filename) {
        String str = "### Configuration for Colorful Puff Mod\n" +
                "# ============================\n" +
                "# By editing the variables below,\n" +
                "# you can easily modify the mod settings.\n" +
                "# \n" +
                "# If you need help, you can find support in our Discord.\n" +
                "\n" +
                "\n" +
                "## If puff entities are able to blink.\n" +
                "PuffBlinking=true\n" +
                "\n" +
                "## Puff entities' height(Human is 2m tall).\n" +
                "PuffHeight=1.76\n" +
                "## Mini Puff entities' height(Human is 2m tall).\n" +
                "MiniPuffHeight=1.32\n";

        return str;
    }
}
