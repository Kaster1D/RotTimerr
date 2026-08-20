package net.kaster.mrmod.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.kaster.mrmod.ModDataComponents;
import net.kaster.mrmod.config.ModConfig;
import net.kaster.mrmod.items.ModItems;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;
import java.util.function.BiConsumer;

public class FoodSpoilHandler {

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(FoodSpoilHandler::onServerTick);
    }

    private static void onServerTick(MinecraftServer server) {
        long tickCount = server.getTicks();
        int scanInterval = ModConfig.getContainerScanInterval();

        for (ServerWorld level : server.getWorlds()) {
            for (PlayerEntity player : level.getPlayers()) {
                long gameTime = level.getTime();
                float tempMultiplier = getTemperatureMultiplier(level, player.getBlockPos());

                processContainer(
                        player.getInventory().size(),
                        player.getInventory()::getStack,
                        (idx, stack) -> player.getInventory().setStack(idx, stack),
                        gameTime,
                        tempMultiplier,
                        1.0f
                );

                if (tickCount % scanInterval == 0) {
                    scanContainersNearPlayer(level, player);
                }
            }
        }
    }

    private static void scanContainersNearPlayer(ServerWorld level, PlayerEntity player) {
        BlockPos playerPos = player.getBlockPos();
        long gameTime = level.getTime();
        int scanRadius = ModConfig.getContainerScanRadius();

        for (int dx = -scanRadius; dx <= scanRadius; dx++) {
            for (int dy = -scanRadius; dy <= scanRadius; dy++) {
                for (int dz = -scanRadius; dz <= scanRadius; dz++) {
                    BlockPos pos = playerPos.add(dx, dy, dz);
                    BlockEntity blockEntity = level.getBlockEntity(pos);
                    if (blockEntity == null) continue;

                    float tempMultiplier = getTemperatureMultiplier(level, pos);

                    if (blockEntity instanceof ChestBlockEntity chest) {
                        processContainer(
                                chest.size(),
                                chest::getStack,
                                chest::setStack,
                                gameTime,
                                tempMultiplier,
                                (float) ModConfig.getChestMultiplier()
                        );
                        chest.markDirty();
                    } else if (blockEntity instanceof BarrelBlockEntity barrel) {
                        processContainer(
                                barrel.size(),
                                barrel::getStack,
                                barrel::setStack,
                                gameTime,
                                tempMultiplier,
                                (float) ModConfig.getBarrelMultiplier()
                        );
                        barrel.markDirty();
                    }
                }
            }
        }
    }

    private static void processContainer(
            int size,
            java.util.function.IntFunction<ItemStack> getter,
            BiConsumer<Integer, ItemStack> setter,
            long gameTime,
            float tempMultiplier,
            float containerMultiplier
    ) {
        normalizeContainer(size, getter, setter, gameTime);

        for (int slot = 0; slot < size; slot++) {
            ItemStack stack = getter.apply(slot);
            if (stack.isEmpty()) continue;

            Item item = stack.getItem();
            if (!isSubjectToSpoil(item)) continue;
            if (isBlacklisted(item)) continue;

            long created = getOrSetCreated(stack, gameTime);
            long spoilTime = (long) (getSpoilTime(item) * containerMultiplier / tempMultiplier);

            if (gameTime - created >= spoilTime) {
                setter.accept(slot, new ItemStack(Items.ROTTEN_FLESH, stack.getCount()));
            }
        }
    }

    private static float getTemperatureMultiplier(World world, BlockPos pos) {
        float biomeTemp = world.getBiome(pos).value().getTemperature();
        if (biomeTemp < 0.15f) return (float) ModConfig.getTemperatureCold();
        if (biomeTemp < 0.5f) return (float) ModConfig.getTemperatureCool();
        if (biomeTemp < 1.0f) return (float) ModConfig.getTemperatureNormal();
        return (float) ModConfig.getTemperatureHot();
    }

    private static void normalizeContainer(
            int size,
            java.util.function.IntFunction<ItemStack> getter,
            BiConsumer<Integer, ItemStack> setter,
            long now
    ) {
        Map<ItemKey, Long> minCreatedByType = new HashMap<>();

        for (int slot = 0; slot < size; slot++) {
            ItemStack stack = getter.apply(slot);
            if (stack.isEmpty()) continue;
            Item item = stack.getItem();
            if (!isSubjectToSpoil(item)) continue;

            ItemKey key = new ItemKey(item);
            long created = getOrSetCreated(stack, now);
            minCreatedByType.merge(key, created, Math::min);
        }

        for (int slot = 0; slot < size; slot++) {
            ItemStack stack = getter.apply(slot);
            if (stack.isEmpty()) continue;
            Item item = stack.getItem();
            if (!isSubjectToSpoil(item)) continue;

            ItemKey key = new ItemKey(item);
            Long minCreated = minCreatedByType.get(key);
            if (minCreated != null) {
                setCreated(stack, minCreated);
                setter.accept(slot, stack);
            }
        }
    }

    private record ItemKey(Item item) {}

    private static boolean isSubjectToSpoil(Item item) {
        if (item.getComponents().get(net.minecraft.component.DataComponentTypes.FOOD) == null) return false;
        if (item == Items.ROTTEN_FLESH) return false;
        return true;
    }

    private static boolean isBlacklisted(Item item) {
        Identifier id = Registries.ITEM.getId(item);
        return ModConfig.getBlacklist().contains(id.toString());
    }

    private static long getSpoilTime(Item item) {
        long baseSpoilTime = ModConfig.getBaseSpoilTicks();

        Identifier id = Registries.ITEM.getId(item);
        String idStr = id.toString();

        Float customMultiplier = ModConfig.getCustomMultipliers().get(idStr);
        if (customMultiplier != null) {
            return (long) (baseSpoilTime * customMultiplier);
        }

        if (item == ModItems.TINKAN || item == ModItems.TINKANFISH
                || item == ModItems.TINKANCHICKEN || item == ModItems.TINKANTOMATO) {
            return (long) (baseSpoilTime * ModConfig.getTinkanMultiplier());
        }

        return baseSpoilTime;
    }

    private static long getOrSetCreated(ItemStack stack, long now) {
        Long existing = stack.get(ModDataComponents.CREATED_TIME);
        if (existing != null) return existing;
        stack.set(ModDataComponents.CREATED_TIME, now);
        return now;
    }

    private static void setCreated(ItemStack stack, long value) {
        stack.set(ModDataComponents.CREATED_TIME, value);
    }
}
