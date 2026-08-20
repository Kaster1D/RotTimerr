package net.kaster.mrmod.items;

import net.kaster.mrmod.RotTimerMod;
import net.kaster.mrmod.block.ModBlocks;
import net.kaster.mrmod.block.custom.TomatoCropBlock;
import net.kaster.mrmod.block.custom.TomatoItem;
import net.kaster.mrmod.block.custom.SeedsBlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static Item TINKAN;
    public static Item TINNY;
    public static Item TINKANFISH;
    public static Item TINKANCHICKEN;
    public static Item TINKANTOMATO;
    public static Item CHEESE;
    public static Item TOMATO;
    public static Item CHEESE_BUCKET;
    public static Item CHERRY;
    public static Item JAMCHERRY;
    public static Item CHICKENLEG;
    public static Item JAR;
    public static Item FARSH;
    public static Item FLOUR;
    public static Item PASTA;
    public static Item TOMATO_SEEDS;

    public static void register() {
        TINKAN = register("tinkan", new Item(new Item.Settings().food(ModFoods.TINKAN)));
        TINNY = register("tinny", new Item(new Item.Settings()));
        TINKANFISH = register("tinkanfish", new Item(new Item.Settings().food(ModFoods.TINKANFISH)));
        TINKANCHICKEN = register("tinkanchicken", new Item(new Item.Settings().food(ModFoods.TINKANCHICKEN)));
        TINKANTOMATO = register("tinkantomato", new Item(new Item.Settings().food(ModFoods.TINKANTOMATO)));
        CHEESE = register("cheese", new Item(new Item.Settings().food(ModFoods.CHEESE)));
        TOMATO = register("tomato", new TomatoItem(new Item.Settings().maxCount(64).food(ModFoods.TOMATO)));
        CHEESE_BUCKET = register("cheese_bucket", new Item(new Item.Settings().maxCount(1)));
        CHERRY = register("cherry", new Item(new Item.Settings().food(ModFoods.CHERRY)));
        JAMCHERRY = register("jamcherry", new Item(new Item.Settings().food(ModFoods.JAMCHERRY)));
        CHICKENLEG = register("chickenleg", new ChickenLegItem(new Item.Settings().food(ModFoods.CHICKENLEG)));
        JAR = register("jar", new Item(new Item.Settings()));
        FARSH = register("farsh", new Item(new Item.Settings()));
        FLOUR = register("flour", new Item(new Item.Settings()));
        PASTA = register("pasta", new Item(new Item.Settings()));
        TOMATO_SEEDS = register("tomato_seeds", new SeedsBlockItem(ModBlocks.TOMATO_CROP, new Item.Settings(), "item.mrmod.tomato_seeds"));
    }

    private static Item register(String id, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(RotTimerMod.MOD_ID, id), item);
    }
}
