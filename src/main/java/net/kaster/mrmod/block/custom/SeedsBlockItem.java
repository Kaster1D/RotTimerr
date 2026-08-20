package net.kaster.mrmod.block.custom;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;

public class SeedsBlockItem extends BlockItem {
    private final String seedTranslationKey;

    public SeedsBlockItem(Block block, Settings settings, String seedTranslationKey) {
        super(block, settings);
        this.seedTranslationKey = seedTranslationKey;
    }

    @Override
    public String getTranslationKey() {
        return this.seedTranslationKey;
    }
}
