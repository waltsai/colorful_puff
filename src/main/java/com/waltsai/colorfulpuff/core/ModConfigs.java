package com.waltsai.colorfulpuff.core;

import com.waltsai.colorfulpuff.SimpleConfig;

import java.util.HashMap;

public class ModConfigs {
    public static SimpleConfig CONFIG;
    private static final HashMap<String, Object> CONFIG_DATAS = new HashMap<>();

    public static void createConfigData() {
        CONFIG = SimpleConfig.of("config").provider(ModConfigs::configInitialData).request();

        register("PuffBlinking", true);
        register("PuffSize", 0.88);
        register("MiniPuffSize", 0.66);
    }

    public static void register(String valueKey, String initialValue) {
        CONFIG_DATAS.put(valueKey, CONFIG.getOrDefault(valueKey, initialValue));
    }

    public static void register(String valueKey, int initialValue) {
        CONFIG_DATAS.put(valueKey, CONFIG.getOrDefault(valueKey, initialValue));
    }

    public static void register(String valueKey, boolean initialValue) {
        CONFIG_DATAS.put(valueKey, CONFIG.getOrDefault(valueKey, initialValue));
    }

    public static void register(String valueKey, double initialValue) {
        CONFIG_DATAS.put(valueKey, CONFIG.getOrDefault(valueKey, initialValue));
    }

    public static double getDouble(String key) {
        return (double) CONFIG_DATAS.get(key);
    }

    public static int getInt(String key) {
        return (int) CONFIG_DATAS.get(key);
    }

    public static boolean getBoolean(String key) {
        return (boolean) CONFIG_DATAS.get(key);
    }

    public static String getString(String key) {
        return (String) CONFIG_DATAS.get(key);
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
                "## Puff entities' size(Compared to Humans' size).\n" +
                "PuffSize=0.88\n" +
                "## Mini Puff entities' size(Compared to Humans' size).\n" +
                "MiniPuffSize=0.66\n";

        return str;
    }
}
