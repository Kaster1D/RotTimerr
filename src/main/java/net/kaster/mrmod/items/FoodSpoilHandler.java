package net.kaster.mrmod.items;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

@Mod.EventBusSubscriber
public class FoodSpoilHandler {

    private static final String TAG_CREATED = "CreatedTime";
    private static final long SPOIL_TIME_TICKS = 48000; // ~2 игровых дня
    private static final float BARREL_MULTIPLIER = 2.0F;
    private static final float CHEST_MULTIPLIER = 1.0F;
    private static final float TINKAN_MULTIPLIER = 5.0F;
    private static final float TINKANFISH_MULTIPLIER = 5.0F;
    private static final float TINKANCHICKEN_MULTIPLIER = 5.0F;
    private static final float TINKANTOMATO_MULTIPLIER = 5.0F;

    // =========================
    // 🌡️ ПОРЧА В ИНВЕНТАРЕ ИГРОКА
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
    // 🌡️ ПОРЧА В СУНДУКАХ И БОЧКАХ
    // =========================
    @SubscribeEvent
    public static void onBlockTick(BlockEvent.NeighborNotifyEvent event) {
        Level level = (Level) event.getLevel();
        if (level.isClientSide()) return;

        BlockPos pos = event.getPos();
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity == null) return;

        long gameTime = level.getGameTime();
        float tempMultiplier = getTemperatureMultiplier(level, pos);

        if (blockEntity instanceof ChestBlockEntity chest) {
            processContainer(
                    chest.getContainerSize(),
                    chest::getItem,
                    chest::setItem,
                    gameTime,
                    tempMultiplier,
                    CHEST_MULTIPLIER
            );
            chest.setChanged();
        } else if (blockEntity instanceof BarrelBlockEntity barrel) {
            processContainer(
                    barrel.getContainerSize(),
                    barrel::getItem,
                    barrel::setItem,
                    gameTime,
                    tempMultiplier,
                    BARREL_MULTIPLIER
            );
            barrel.setChanged();
        }
    }

    // =========================
    // 🧊 ОБРАБОТКА КОНТЕЙНЕРОВ
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

            long created = getOrSetCreated(stack, gameTime);
            long spoilTime = (long) (getSpoilTime(item) * containerMultiplier / tempMultiplier);

            if (gameTime - created >= spoilTime) {
                setter.accept(slot, new ItemStack(Items.ROTTEN_FLESH, stack.getCount()));
            }
        }
    }

    // =========================
    // 🌡️ ПОЛУЧЕНИЕ МУЛЬТИПЛИКАТОРА ТЕМПЕРАТУРЫ
    // =========================
    private static float getTemperatureMultiplier(Level level, BlockPos pos) {
        float biomeTemp = level.getBiome(pos).value().getBaseTemperature();
        if (biomeTemp < 0.15f) return 0.25f; // холод — замедляем
        if (biomeTemp < 0.5f) return 0.75f; // прохладно
        if (biomeTemp < 1.0f) return 1.0f; // норма
        return 2.0f; // жара — ускоряем
    }

    // =========================
    // 🔧 ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ
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

    private static long getSpoilTime(Item item) {
        long spoilTime = SPOIL_TIME_TICKS;
        if (item == ModItems.TINKAN.get() || item == ModItems.TINKANFISH.get()
                || item == ModItems.TINKANCHICKEN.get() || item == ModItems.TINKANTOMATO.get()) {
            spoilTime *= TINKAN_MULTIPLIER;
            if (item == ModItems.TINKANFISH.get()) spoilTime *= TINKANFISH_MULTIPLIER;
            if (item == ModItems.TINKANCHICKEN.get()) spoilTime *= TINKANCHICKEN_MULTIPLIER;
            if (item == ModItems.TINKANTOMATO.get()) spoilTime *= TINKANTOMATO_MULTIPLIER;
        }
        return spoilTime;
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
