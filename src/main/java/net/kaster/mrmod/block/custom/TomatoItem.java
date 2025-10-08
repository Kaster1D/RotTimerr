package net.kaster.mrmod.block.custom;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

public class TomatoItem extends Item {
    public TomatoItem(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable CompoundTag getShareTag(ItemStack stack) {
        // Убираем CreatedTime перед отправкой клиенту
        CompoundTag tag = super.getShareTag(stack);
        if (tag != null) {
            tag.remove("CreatedTime");
            if (tag.isEmpty()) return null;
        }
        return tag;
    }
}
