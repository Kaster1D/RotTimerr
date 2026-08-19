package net.kaster.mrmod.items;

import net.kaster.mrmod.config.ModConfig;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FoodSpoilHandler {

    private static final String TAG_CREATED = "CreatedTime";

    // =========================
    // ПОРЧА В ИНВЕНТАРЕ ИГРОКА
    // =========================
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Player player = event.player;
        Level level = player.level();
        if (level.isClientSide) return;

        long gameTime = level.getGameTime();
        float tempMultiplier = getTemperatureMultiplier(level, player.blockPosition());

        processContainer(
                player.getInventory().getContainerSize(),
                idx -> player.getInventory().getItem(idx),
                (idx, stack) -> player.getInventory().setItem(idx, stack),
                gameTime,
                tempMultiplier,
                1.0f
        );
    }

    // =========================
    // ПОРЧА В СУНДУКАХ И БОЧКАХ
    // =========================
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        long tickCount = event.getServer().getTickCount();
        int scanInterval = ModConfig.COMMON.containerScanInterval.get();
        if (tickCount % scanInterval != 0) return;

        for (ServerLevel level : event.getServer().getAllLevels()) {
            for (Player player : level.players()) {
                scanContainersNearPlayer(level, player);
            }
        }
    }

    private static void scanContainersNearPlayer(ServerLevel level, Player player) {
        BlockPos playerPos = player.blockPosition();
        long gameTime = level.getGameTime();
        int scanRadius = ModConfig.COMMON.containerScanRadius.get();

        for (int dx = -scanRadius; dx <= scanRadius; dx++) {
            for (int dy = -scanRadius; dy <= scanRadius; dy++) {
                for (int dz = -scanRadius; dz <= scanRadius; dz++) {
                    BlockPos pos = playerPos.offset(dx, dy, dz);
                    BlockEntity blockEntity = level.getBlockEntity(pos);
                    if (blockEntity == null) continue;

                    float tempMultiplier = getTemperatureMultiplier(level, pos);

                    if (blockEntity instanceof ChestBlockEntity chest) {
                        processContainer(
                                chest.getContainerSize(),
                                chest::getItem,
                                chest::setItem,
                                gameTime,
                                tempMultiplier,
                                (float) ModConfig.COMMON.chestMultiplier.get().doubleValue()
                        );
                        chest.setChanged();
                    } else if (blockEntity instanceof BarrelBlockEntity barrel) {
                        processContainer(
                                barrel.getContainerSize(),
                                barrel::getItem,
                                barrel::setItem,
                                gameTime,
                                tempMultiplier,
                                (float) ModConfig.COMMON.barrelMultiplier.get().doubleValue()
                        );
                        barrel.setChanged();
                    }
                }
            }
        }
    }

    // =========================
    // ОБРАБОТКА КОНТЕЙНЕРОВ
    // =========================
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

    // =========================
    // ПОЛУЧЕНИЕ МУЛЬТИПЛИКАТОРА ТЕМПЕРАТУРЫ
    // =========================
    private static float getTemperatureMultiplier(Level level, BlockPos pos) {
        float biomeTemp = level.getBiome(pos).value().getBaseTemperature();
        if (biomeTemp < 0.15f) return (float) ModConfig.COMMON.temperatureCold.get().doubleValue();
        if (biomeTemp < 0.5f) return (float) ModConfig.COMMON.temperatureCool.get().doubleValue();
        if (biomeTemp < 1.0f) return (float) ModConfig.COMMON.temperatureNormal.get().doubleValue();
        return (float) ModConfig.COMMON.temperatureHot.get().doubleValue();
    }

    // =========================
    // ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ
    // =========================
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

            ItemKey key = new ItemKey(item, stripCreatedForCompare(stack));
            long created = getOrSetCreated(stack, now);
            minCreatedByType.merge(key, created, Math::min);
        }

        for (int slot = 0; slot < size; slot++) {
            ItemStack stack = getter.apply(slot);
            if (stack.isEmpty()) continue;
            Item item = stack.getItem();
            if (!isSubjectToSpoil(item)) continue;

            ItemKey key = new ItemKey(item, stripCreatedForCompare(stack));
            Long minCreated = minCreatedByType.get(key);
            if (minCreated != null) {
                setCreated(stack, minCreated);
                setter.accept(slot, stack);
            }
        }
    }

    private record ItemKey(Item item, CompoundTag otherTags) {}

    private static CompoundTag stripCreatedForCompare(ItemStack stack) {
        CompoundTag src = stack.getTag();
        if (src == null) return null;
        CompoundTag copy = src.copy();
        copy.remove(TAG_CREATED);
        return copy.isEmpty() ? null : copy;
    }

    private static boolean isSubjectToSpoil(Item item) {
        if (!item.isEdible()) return false;
        if (item == Items.ROTTEN_FLESH) return false;
        return true;
    }

    private static boolean isBlacklisted(Item item) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
        if (id == null) return false;
        return ModConfig.getBlacklist().contains(id.toString());
    }

    private static long getSpoilTime(Item item) {
        long baseSpoilTime = ModConfig.COMMON.baseSpoilTicks.get();

        ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
        if (id != null) {
            String idStr = id.toString();

            Float customMultiplier = ModConfig.getCustomMultipliers().get(idStr);
            if (customMultiplier != null) {
                return (long) (baseSpoilTime * customMultiplier);
            }
        }

        if (item == ModItems.TINKAN.get() || item == ModItems.TINKANFISH.get()
                || item == ModItems.TINKANCHICKEN.get() || item == ModItems.TINKANTOMATO.get()) {
            return (long) (baseSpoilTime * ModConfig.COMMON.tinkanMultiplier.get().doubleValue());
        }

        return baseSpoilTime;
    }

    private static long getOrSetCreated(ItemStack stack, long now) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains(TAG_CREATED)) {
            tag.putLong(TAG_CREATED, now);
            return now;
        }
        return tag.getLong(TAG_CREATED);
    }

    private static void setCreated(ItemStack stack, long value) {
        stack.getOrCreateTag().putLong(TAG_CREATED, value);
    }
}
