package net.kaster.mrmod;

import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import com.mojang.serialization.Codec;

public class ModDataComponents {
    public static ComponentType<Long> CREATED_TIME;

    public static void register() {
        CREATED_TIME = Registry.register(
                Registries.DATA_COMPONENT_TYPE,
                Identifier.of(RotTimerMod.MOD_ID, "created_time"),
                ComponentType.<Long>builder()
                        .codec(Codec.LONG)
                        .build()
        );
    }
}
