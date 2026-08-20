package net.kaster.mrmod.block.custom;

import net.kaster.mrmod.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.CropBlock;

public class TomatoCropBlock extends CropBlock {
    public static final int MAX_AGE = 5;

    public TomatoCropBlock(Settings settings) {
        super(settings);
    }

    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }

    @Override
    protected Block getSeedsItem() {
        return ModBlocks.TOMATO_CROP;
    }
}
