package net.kaster.mrmod.items;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.entity.living.MobEffectEvent;

public class ModFoods {
    public static final FoodProperties TINKAN = new FoodProperties.Builder().nutrition(10).meat()
            .saturationMod(1f).effect(() -> new MobEffectInstance(MobEffects.HEAL, 60), 0.1f).build();

    public static final FoodProperties TINKANFISH = new FoodProperties.Builder().nutrition(10).meat()
            .saturationMod(1f).effect(() -> new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 60), 0.1f).build();

    public static final FoodProperties TINKANCHICKEN = new FoodProperties.Builder().nutrition(10).meat()
            .saturationMod(1f).effect(() -> new MobEffectInstance(MobEffects.LEVITATION, 60), 0.1f).build();

    public static final FoodProperties TINKANTOMATO = new FoodProperties.Builder().nutrition(5).meat()
            .saturationMod(1f).effect(() -> new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 20), 0.1f).build();

    public static final FoodProperties TOMATO = new FoodProperties.Builder()
            .nutrition(2)        // сколько восстанавливает голода
            .fast()              // еда съедается быстро
            .saturationMod(0.2f) // коэффициент насыщения (0.8f = 80% от базового)
            .build();

    public static final FoodProperties CHICKENLEG = new FoodProperties.Builder()
            .nutrition(6)
            .meat()
            .saturationMod(0.5f) // коэффициент насыщения (0.8f = 80% от базового)
            .build();

    public static final FoodProperties CHERRY = new FoodProperties.Builder()
            .nutrition(2)        // сколько восстанавливает голода
            .fast()              // еда съедается быстро
            .saturationMod(0.2f) // коэффициент насыщения (0.8f = 80% от базового)
            .build();

    public static final FoodProperties JAMCHERRY = new FoodProperties.Builder()
            .nutrition(4)        // сколько восстанавливает голода
            .meat()              // еда съедается быстро
            .saturationMod(0.4f) // коэффициент насыщения (0.8f = 80% от базового)
            .build();

    public static final FoodProperties CHEESE = new FoodProperties.Builder().nutrition(2).fast()
            .saturationMod(0.2f).effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10), 0.1f).build();
}
