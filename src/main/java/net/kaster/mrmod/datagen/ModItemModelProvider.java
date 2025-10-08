package net.kaster.mrmod.datagen;

import net.kaster.mrmod.MrMod;
import net.kaster.mrmod.items.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MrMod.MOD_ID, existingFileHelper);
    }

    @Override
    public void registerModels() {
        simpleItem(ModItems.TOMATO);
        simpleItem(ModItems.TOMATO_SEEDS);

    }

    private ItemModelBuilder simpleItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(MrMod.MOD_ID,"item/" + item.getId().getPath()));
    }
}