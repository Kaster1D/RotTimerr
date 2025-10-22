package net.kaster.mrmod.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Items;

public class ChickenLegItem extends Item {

    public ChickenLegItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);

        // Только на сервере, и если это игрок
        if (!level.isClientSide && entity instanceof Player player) {
            // Добавить кость
            player.getInventory().add(new ItemStack(Items.BONE));
        }

        return result;
    }
}
