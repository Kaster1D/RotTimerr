package net.kaster.mrmod.items;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

@Mod.EventBusSubscriber
public class FoodSpoilHandler {

    private static final String TAG_CREATED = "CreatedTime";
    private static final long SPOIL_TIME_TICKS = 48000; // 2 игровых дня
    private static final float BARREL_MULTIPLIER = 2.0F;
    private static final float TINKAN_MULTIPLIER = 5.0F;
    private static final float TINKANFISH_MULTIPLIER = 5.0F;
    private static final float TINKANCHICKEN_MULTIPLIER = 5.0F;
    private static final float TINKANTOMATO_MULTIPLIER = 5.0F;

    // Порча еды в инвентаре игрока
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        Level level = player.level();
        if (level.isClientSide) return;

        long gameTime = level.getGameTime();

        // 1) Нормализуем CreatedTime по типам предметов внутри инвентаря игрока
        normalizeContainer(
                player.getInventory().getContainerSize(),
                idx -> player.getInventory().getItem(idx),
                (idx, stack) -> player.getInventory().setItem(idx, stack),
                gameTime
        );

        // 2) Применяем порчу (после нормализации)
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            if (stack.isEmpty()) continue;

            Item item = stack.getItem();
            if (!isSubjectToSpoil(item)) continue;

            long created = getOrSetCreated(stack, gameTime);
            long spoilTime = getSpoilTime(item);

            if (gameTime - created >= spoilTime) {
                player.getInventory().setItem(slot, new ItemStack(Items.ROTTEN_FLESH, stack.getCount()));
            }
        }
        player.getInventory().setChanged();
    }

    // Обработка содержимого бочки
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide()) return;
        if (event.getHand() != InteractionHand.MAIN_HAND) return;

        Level level = event.getLevel();
        BlockPos pos = event.getPos();

        if (level.getBlockState(pos).getBlock() != Blocks.BARREL) return;
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof BarrelBlockEntity barrel)) return;

        processBarrel(level, barrel);
    }

    private static void processBarrel(Level world, BarrelBlockEntity barrel) {
        long gameTime = world.getGameTime();

        // 1) Нормализуем CreatedTime по типам предметов внутри бочки
        normalizeContainer(
                barrel.getContainerSize(),
                barrel::getItem,
                barrel::setItem,
                gameTime
        );

        // 2) Применяем порчу (после нормализации)
        for (int slot = 0; slot < barrel.getContainerSize(); slot++) {
            ItemStack stack = barrel.getItem(slot);
            if (stack.isEmpty()) continue;

            Item item = stack.getItem();
            if (!isSubjectToSpoil(item)) continue;

            long created = getOrSetCreated(stack, gameTime);
            long spoilTime = (long) (getSpoilTime(item) * BARREL_MULTIPLIER);

            if (gameTime - created >= spoilTime) {
                barrel.setItem(slot, new ItemStack(Items.ROTTEN_FLESH, stack.getCount()));
            }
        }
        barrel.setChanged();
    }

    // Нормализация: находим минимальное CreatedTime по каждому типу предмета и проставляем всем стак‑ам этого типа
    private static void normalizeContainer(
            int size,
            java.util.function.IntFunction<ItemStack> getter,
            BiConsumer<Integer, ItemStack> setter,
            long now
    ) {
        // Сначала собираем минимальные CreatedTime по типам
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

        if (minCreatedByType.isEmpty()) return;

        // Затем проставляем минимальные значения всем стак‑ам соответствующего типа
        for (int slot = 0; slot < size; slot++) {
            ItemStack stack = getter.apply(slot);
            if (stack.isEmpty()) continue;

            Item item = stack.getItem();
            if (!isSubjectToSpoil(item)) continue;

            ItemKey key = new ItemKey(item, stripCreatedForCompare(stack));
            Long minCreated = minCreatedByType.get(key);
            if (minCreated == null) continue;

            setCreated(stack, minCreated);
            setter.accept(slot, stack);
        }
    }

    // Ключ “типа” предмета: сам Item + все теги, КРОМЕ CreatedTime
    private record ItemKey(Item item, CompoundTag otherTags) {}

    // Возвращает копию тега без поля CreatedTime (для сравнения типов)
    private static CompoundTag stripCreatedForCompare(ItemStack stack) {
        CompoundTag src = stack.getTag();
        if (src == null) return null;
        CompoundTag copy = src.copy();
        copy.remove(TAG_CREATED);
        return copy.isEmpty() ? null : copy;
    }

    // Условия попадания под систему порчи
    private static boolean isSubjectToSpoil(Item item) {
        if (!item.isEdible()) return false;
        if (item == Items.ROTTEN_FLESH) return false;
        return true;
    }

    // Время порчи
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

    // Чтение/установка CreatedTime
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
