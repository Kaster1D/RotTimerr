package net.kaster.mrmod.items;

import com.google.common.util.concurrent.ClosingFuture;
import net.kaster.mrmod.MrMod;
import net.kaster.mrmod.block.ModBlocks;
import net.kaster.mrmod.block.custom.TomatoItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MrMod.MOD_ID);

    public static final RegistryObject<Item> TINKAN = ITEMS.register("tinkan",
            () -> new Item(new Item.Properties().food(ModFoods.TINKAN)));

    public static final RegistryObject<Item> TINNY = ITEMS.register("tinny",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> TINKANFISH = ITEMS.register("tinkanfish",
            () -> new Item(new Item.Properties().food(ModFoods.TINKANFISH)));

    public static final RegistryObject<Item> TINKANCHICKEN = ITEMS.register("tinkanchicken",
            () -> new Item(new Item.Properties().food(ModFoods.TINKANCHICKEN)));

    public static final RegistryObject<Item> TINKANTOMATO = ITEMS.register("tinkantomato",
            () -> new Item(new Item.Properties().food(ModFoods.TINKANTOMATO)));

    public static final RegistryObject<Item> CHEESE = ITEMS.register("cheese",
            () -> new Item(new Item.Properties().food(ModFoods.CHEESE)));

    public static final RegistryObject<Item> TOMATO = ITEMS.register("tomato",
            () -> new TomatoItem(new Item.Properties()
                    .stacksTo(64)
                    .food(ModFoods.TOMATO)
            )
    );

    public static final RegistryObject<Item> CHEESE_BUCKET = ITEMS.register("cheese_bucket",
            () -> new Item(new Item.Properties()
                    .stacksTo(1)
                    .craftRemainder(Items.BUCKET) // <-- вот это
            )
    );

    public static final RegistryObject<Item> CHERRY = ITEMS.register("cherry",
            () -> new Item(new Item.Properties().food(ModFoods.CHERRY)));

    public static final RegistryObject<Item> JAMCHERRY = ITEMS.register("jamcherry",
            () -> new Item(new Item.Properties().food(ModFoods.JAMCHERRY)));


    public static final RegistryObject<Item> CHICKENLEG = ITEMS.register("chickenleg",
            () -> new Item(new Item.Properties().food(ModFoods.CHICKENLEG)));

    public static final RegistryObject<Item> JAR = ITEMS.register("jar",
            () -> new Item(new Item.Properties()));


    public static final RegistryObject<Item> FARSH = ITEMS.register("farsh",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> FLOUR = ITEMS.register("flour",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> PASTA = ITEMS.register("pasta",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> TOMATO_SEEDS = ITEMS.register("tomato_seeds",
            () -> new ItemNameBlockItem(ModBlocks.TOMATO_CROP.get(), new Item.Properties()));



    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
