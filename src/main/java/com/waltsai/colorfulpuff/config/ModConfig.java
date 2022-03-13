package com.waltsai.colorfulpuff.config;

import com.google.common.collect.ImmutableMap;
import com.waltsai.colorfulpuff.ModInit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModConfig {
    public static SimpleConfig CONFIG;
    private static final HashMap<String, Object> CONFIG_DATAS = new HashMap<>();

    public static void createConfigData() {
        CONFIG = SimpleConfig.of("config").provider(ModInit::configInitialData).request();

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
}
