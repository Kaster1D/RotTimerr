package net.kaster.mrmod.items;

import net.kaster.mrmod.RotTimerMod;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;

public class ChickenLegItem extends Item {
    public ChickenLegItem(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity entity) {
        if (!world.isClient) {
            entity.dropItem(Items.BONE);
        }
        return super.finishUsing(stack, world, entity);
    }
}
