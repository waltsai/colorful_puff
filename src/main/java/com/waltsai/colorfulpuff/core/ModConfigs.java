package com.waltsai.colorfulpuff.core;

import com.waltsai.colorfulpuff.SimpleConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ModConfigs {
    public static SimpleConfig CONFIG;
    private static final HashMap<String, Object> CONFIG_DATAS = new HashMap<>();

    public static void createConfigData() {
        CONFIG = SimpleConfig.of("config").request();

        register("PuffBlinking", true);
        register("PuffDamageMultiplier", 1.0);
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

    public static List<String> configInitialData() {
        List<String> list = new ArrayList<>();
        list.add("# 泡芙模組配置文件");
        list.add("# ============================");
        list.add("# 透過修改文件中每個變數的值，你能夠更改遊戲模組的各數值。");
        list.add("# (true=是 false=否)");
        list.add("");
        list.add("");
        list.add("## 泡芙是否能夠眨眼?");
        list.add("PuffBlinking=true");
        list.add("");
        list.add("");
        list.add("## 設定泡芙攻擊傷害乘數:(原傷害*乘數=真實傷害)");
        list.add("PuffDamageMultiplier=1.0");

        return list;
    }
}
