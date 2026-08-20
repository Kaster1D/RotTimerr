package net.kaster.mrmod.items;

import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class ModFoods {
    public static final FoodComponent TINKAN = new FoodComponent.Builder().nutrition(10)
            .saturationModifier(1f).alwaysEdible().build();

    public static final FoodComponent TINKANFISH = new FoodComponent.Builder().nutrition(10)
            .saturationModifier(1f).alwaysEdible().build();

    public static final FoodComponent TINKANCHICKEN = new FoodComponent.Builder().nutrition(10)
            .saturationModifier(1f).alwaysEdible().build();

    public static final FoodComponent TINKANTOMATO = new FoodComponent.Builder().nutrition(5)
            .saturationModifier(1f).alwaysEdible().build();

    public static final FoodComponent TOMATO = new FoodComponent.Builder()
            .nutrition(2).saturationModifier(0.2f).snack().build();

    public static final FoodComponent CHICKENLEG = new FoodComponent.Builder()
            .nutrition(6).saturationModifier(0.5f).build();

    public static final FoodComponent CHERRY = new FoodComponent.Builder()
            .nutrition(2).saturationModifier(0.2f).snack().build();

    public static final FoodComponent JAMCHERRY = new FoodComponent.Builder()
            .nutrition(4).saturationModifier(0.4f).snack().build();

    public static final FoodComponent CHEESE = new FoodComponent.Builder().nutrition(2)
            .saturationModifier(0.2f).snack().build();
}
