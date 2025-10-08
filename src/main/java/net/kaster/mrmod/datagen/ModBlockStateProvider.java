package net.kaster.mrmod.datagen;

import net.kaster.mrmod.MrMod;
import net.kaster.mrmod.block.ModBlocks;
import net.kaster.mrmod.block.custom.TomatoCropBlock;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.codec.Resources;

import java.util.function.Function;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, MrMod.MOD_ID, exFileHelper);
    }


    @Override
    protected void registerStatesAndModels() {
        makeTomatoCrop(ModBlocks.TOMATO_CROP.get(), TomatoCropBlock.AGE, "tomato_stage", "tomato_stage");

    }



public void makeTomatoCrop(Block pBlock, IntegerProperty pCropAgeProperty, String pModelName, String pTextureName){
    getVariantBuilder(pBlock).forAllStates(state ->
            generateCropModel(state, pCropAgeProperty, pModelName, pTextureName));
}

private ConfiguredModel[] generateCropModel(BlockState state, IntegerProperty pCropAgeProperty, String modelName, String textureName) {
    ConfiguredModel[] models = new ConfiguredModel[1];
    models[0] = new ConfiguredModel(models().crop(modelName + state.getValue(pCropAgeProperty),
            new ResourceLocation(MrMod.MOD_ID, "block/" + textureName + state.getValue(pCropAgeProperty))).renderType("cutout"));

    return models;
}

}



