package net.kaster.mrmod.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class ModConfig {

    public static final ForgeConfigSpec COMMON_SPEC;
    public static final Common COMMON;

    static {
        Pair<Common, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder()
                .configure(Common::new);
        COMMON = pair.getLeft();
        COMMON_SPEC = pair.getRight();
    }

    public static class Common {
        public final ForgeConfigSpec.IntValue baseSpoilTicks;
        public final ForgeConfigSpec.IntValue containerScanInterval;
        public final ForgeConfigSpec.IntValue containerScanRadius;

        public final ForgeConfigSpec.DoubleValue barrelMultiplier;
        public final ForgeConfigSpec.DoubleValue chestMultiplier;
        public final ForgeConfigSpec.DoubleValue tinkanMultiplier;

        public final ForgeConfigSpec.DoubleValue temperatureCold;
        public final ForgeConfigSpec.DoubleValue temperatureCool;
        public final ForgeConfigSpec.DoubleValue temperatureNormal;
        public final ForgeConfigSpec.DoubleValue temperatureHot;

        public final ForgeConfigSpec.ConfigValue<List<? extends String>> customMultipliers;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> blacklist;

        public Common(ForgeConfigSpec.Builder builder) {
            builder.push("general");

            baseSpoilTicks = builder
                    .comment("Base ticks before food spoils (48000 = 2 in-game days)")
                    .defineInRange("base_spoil_ticks", 48000, 1, 10000000);

            containerScanInterval = builder
                    .comment("How often (in ticks) to scan nearby containers for spoilage")
                    .defineInRange("container_scan_interval", 200, 1, 1200);

            containerScanRadius = builder
                    .comment("Radius (in blocks) around player to scan containers")
                    .defineInRange("container_scan_radius", 4, 1, 16);

            builder.pop();

            builder.push("multipliers");

            barrelMultiplier = builder
                    .comment("Barrel multiplier (food lasts X times longer in barrels)")
                    .defineInRange("barrel", 2.0, 1.0, 100.0);

            chestMultiplier = builder
                    .comment("Chest multiplier")
                    .defineInRange("chest", 1.0, 1.0, 100.0);

            tinkanMultiplier = builder
                    .comment("Tinkan (canned food) multiplier")
                    .defineInRange("tinkan", 5.0, 1.0, 100.0);

            builder.pop();

            builder.push("temperature");

            temperatureCold = builder
                    .comment("Cold biome multiplier (< 0.15 temp). Higher = food lasts longer")
                    .defineInRange("cold", 4.0, 1.0, 100.0);

            temperatureCool = builder
                    .comment("Cool biome multiplier (< 0.5 temp)")
                    .defineInRange("cool", 1.33, 1.0, 100.0);

            temperatureNormal = builder
                    .comment("Normal biome multiplier (< 1.0 temp)")
                    .defineInRange("normal", 1.0, 1.0, 100.0);

            temperatureHot = builder
                    .comment("Hot biome multiplier (>= 1.0 temp). Higher = food spoils faster")
                    .defineInRange("hot", 2.0, 1.0, 100.0);

            builder.pop();

            builder.push("custom_items");

            customMultipliers = builder
                    .comment(
                            "Custom spoil multipliers for specific items.",
                            "Format: \"modid:itemid=multiplier\"",
                            "Example: \"farmersdelight:ham=0.8\" (spoils 20% faster)",
                            "Example: \"farmersdelight:dried_kelp=3.0\" (lasts 3x longer)",
                            "If an item is not listed here, the default multiplier (1.0) is used."
                    )
                    .defineListAllowEmpty("custom_multipliers",
                            Arrays.asList(
                                    "farmersdelight:ham=0.8",
                                    "farmersdelight:dried_kelp=3.0"
                            ),
                            obj -> obj instanceof String && ((String) obj).contains("=")
                    );

            blacklist = builder
                    .comment(
                            "Items that should NOT spoil at all.",
                            "Format: \"modid:itemid\"",
                            "Example: \"minecraft:golden_apple\""
                    )
                    .defineListAllowEmpty("blacklist",
                            Arrays.asList(
                                    "minecraft:golden_apple",
                                    "minecraft:enchanted_golden_apple"
                            ),
                            obj -> obj instanceof String && ((String) obj).contains(":")
                    );

            builder.pop();
        }
    }

    // =========================
    // Парсинг кастомных множителей
    // =========================
    private static Map<String, Float> parsedMultipliers = null;
    private static Set<String> parsedBlacklist = null;

    public static Map<String, Float> getCustomMultipliers() {
        if (parsedMultipliers == null) {
            parsedMultipliers = new HashMap<>();
            for (String entry : COMMON.customMultipliers.get()) {
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
            parsedBlacklist = new HashSet<>(COMMON.blacklist.get());
        }
        return parsedBlacklist;
    }

    public static void resetCache() {
        parsedMultipliers = null;
        parsedBlacklist = null;
    }
}
