package net.kaster.mrmod.items;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.kaster.mrmod.RotTimerMod;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModCreativeTab {
    public static void register() {
        RegistryKey<ItemGroup> key = RegistryKey.of(RegistryKeys.ITEM_GROUP, Identifier.of(RotTimerMod.MOD_ID, "mr_mod"));

        ItemGroup group = FabricItemGroup.builder()
                .icon(() -> new ItemStack(ModItems.TINKAN))
                .displayName(Text.translatable("creativetab.mr_mod"))
                .build();

        Registry.register(Registries.ITEM_GROUP, key, group);

        ItemGroupEvents.modifyEntriesEvent(key).register(entries -> {
            entries.add(ModItems.TINKAN);
            entries.add(ModItems.TINNY);
            entries.add(ModItems.TINKANFISH);
            entries.add(ModItems.TINKANCHICKEN);
            entries.add(ModItems.TINKANTOMATO);
            entries.add(ModItems.CHEESE);
            entries.add(ModItems.TOMATO);
            entries.add(ModItems.CHEESE_BUCKET);
            entries.add(ModItems.CHERRY);
            entries.add(ModItems.JAMCHERRY);
            entries.add(ModItems.CHICKENLEG);
            entries.add(ModItems.JAR);
            entries.add(ModItems.FARSH);
            entries.add(ModItems.FLOUR);
            entries.add(ModItems.PASTA);
            entries.add(ModItems.TOMATO_SEEDS);
        });
    }
}
