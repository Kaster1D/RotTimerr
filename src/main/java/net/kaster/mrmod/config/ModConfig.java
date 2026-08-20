package net.kaster.mrmod.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.kaster.mrmod.RotTimerMod;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static ConfigData data;

    public static class ConfigData {
        public int base_spoil_ticks = 48000;
        public int container_scan_interval = 200;
        public int container_scan_radius = 4;
        public double barrel_multiplier = 2.0;
        public double chest_multiplier = 1.0;
        public double tinkan_multiplier = 5.0;
        public double temperature_cold = 4.0;
        public double temperature_cool = 1.33;
        public double temperature_normal = 1.0;
        public double temperature_hot = 2.0;
        public List<String> custom_multipliers = Arrays.asList(
                "farmersdelight:ham=0.8",
                "farmersdelight:dried_kelp=3.0"
        );
        public List<String> blacklist = Arrays.asList(
                "minecraft:golden_apple",
                "minecraft:enchanted_golden_apple"
        );
    }

    public static void load() {
        Path configDir = Path.of("config");
        Path configFile = configDir.resolve("rottimer.json");
        try {
            if (Files.exists(configFile)) {
                data = GSON.fromJson(Files.newBufferedReader(configFile), ConfigData.class);
            } else {
                data = new ConfigData();
                Files.createDirectories(configDir);
                Files.writeString(configFile, GSON.toJson(data));
            }
        } catch (Exception e) {
            RotTimerMod.LOGGER.error("Failed to load config", e);
            data = new ConfigData();
        }
    }

    public static int getBaseSpoilTicks() { return data.base_spoil_ticks; }
    public static int getContainerScanInterval() { return data.container_scan_interval; }
    public static int getContainerScanRadius() { return data.container_scan_radius; }
    public static double getBarrelMultiplier() { return data.barrel_multiplier; }
    public static double getChestMultiplier() { return data.chest_multiplier; }
    public static double getTinkanMultiplier() { return data.tinkan_multiplier; }
    public static double getTemperatureCold() { return data.temperature_cold; }
    public static double getTemperatureCool() { return data.temperature_cool; }
    public static double getTemperatureNormal() { return data.temperature_normal; }
    public static double getTemperatureHot() { return data.temperature_hot; }

    private static Map<String, Float> parsedMultipliers = null;
    private static Set<String> parsedBlacklist = null;

    public static Map<String, Float> getCustomMultipliers() {
        if (parsedMultipliers == null) {
            parsedMultipliers = new HashMap<>();
            for (String entry : data.custom_multipliers) {
                String[] parts = entry.split("=", 2);
                if (parts.length == 2) {
                    try {
                        parsedMultipliers.put(parts[0], Float.parseFloat(parts[1]));
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
        return parsedMultipliers;
    }

    public static Set<String> getBlacklist() {
        if (parsedBlacklist == null) {
            parsedBlacklist = new HashSet<>(data.blacklist);
        }
        return parsedBlacklist;
    }
}
