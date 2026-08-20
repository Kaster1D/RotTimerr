package net.kaster.mrmod.block;

import net.kaster.mrmod.RotTimerMod;
import net.kaster.mrmod.block.custom.TomatoCropBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static Block TOMATO_CROP;

    public static void register() {
        TOMATO_CROP = Registry.register(Registries.BLOCK,
                Identifier.of(RotTimerMod.MOD_ID, "tomato_crop"),
                new TomatoCropBlock(AbstractBlock.Settings.create()
                        .noCollision()
                        .ticksRandomly()
                        .strength(0f)
                        .sounds(BlockSoundGroup.CROP)));
    }
}
