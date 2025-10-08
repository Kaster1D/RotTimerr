package net.kaster.mrmod.items;

import net.kaster.mrmod.MrMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.EventBus;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MrMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MR_TAB = CREATIVE_MODE_TABS.register("mr_mod",
        () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.TINKAN.get()))
            .title(Component.translatable("creativetab.mr_mod"))
            .displayItems((pParameters, pOutput) -> {
                pOutput.accept(ModItems.TINKAN.get());
                pOutput.accept(ModItems.TINNY.get());
                pOutput.accept(ModItems.TINKANFISH.get());
                pOutput.accept(ModItems.TINKANCHICKEN.get());
                pOutput.accept(ModItems.TINKANTOMATO.get());
                pOutput.accept(ModItems.TOMATO.get());
                pOutput.accept(ModItems.CHEESE.get());
                pOutput.accept(ModItems.FARSH.get());
                pOutput.accept(ModItems.FLOUR.get());
                pOutput.accept(ModItems.PASTA.get());
                pOutput.accept(ModItems.TOMATO_SEEDS.get());
                pOutput.accept(ModItems.CHEESE_BUCKET.get());
                pOutput.accept(ModItems.CHICKENLEG.get());
                pOutput.accept(ModItems.JAR.get());
                pOutput.accept(ModItems.JAMCHERRY.get());
                pOutput.accept(ModItems.CHERRY.get());
    })
            .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
