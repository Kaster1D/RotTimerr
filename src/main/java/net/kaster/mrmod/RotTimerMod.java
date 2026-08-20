package net.kaster.mrmod;

import net.kaster.mrmod.block.ModBlocks;
import net.kaster.mrmod.config.ModConfig;
import net.kaster.mrmod.event.FoodSpoilHandler;
import net.kaster.mrmod.items.ModCreativeTab;
import net.kaster.mrmod.items.ModItems;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RotTimerMod implements ModInitializer {
    public static final String MOD_ID = "mrmod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModConfig.load();
        ModDataComponents.register();
        ModBlocks.register();
        ModItems.register();
        ModCreativeTab.register();
        FoodSpoilHandler.register();
        LOGGER.info("Rot Timer initialized!");
    }
}
