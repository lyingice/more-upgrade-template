package net.mcreator.mut.item;

import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.mcreator.mut.config.MutCrossbowLoadCountConfig;
import net.mcreator.mut.init.MutCrossbowStats;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class NewCrossbowItem extends CrossbowItem {

    private final Supplier<Item> selfItemSupplier;

    protected NewCrossbowItem(Supplier<Item> crossbowItemSupplier, MutCrossbowStats.Stats stats) {
        super(stats.fireResistant()
                ? new Item.Properties().stacksTo(1).durability(stats.durability()).fireResistant().rarity(stats.rarity())
                : new Item.Properties().stacksTo(1).durability(stats.durability()).rarity(stats.rarity()));
        this.selfItemSupplier = crossbowItemSupplier;
        MutCrossbowStats.register(this, stats);
    }
    // 新构造：有词条的调用这个
    protected NewCrossbowItem(Supplier<Item> crossbowItemSupplier, MutCrossbowStats.Stats stats, Properties customProps) {
        super(stats.fireResistant()
                ? customProps.stacksTo(1).durability(stats.durability()).fireResistant().rarity(stats.rarity())
                : customProps.stacksTo(1).durability(stats.durability()).rarity(stats.rarity()));
        this.selfItemSupplier = crossbowItemSupplier;
        MutCrossbowStats.register(this, stats);
    }

    private Item getSelfItem() {
        return selfItemSupplier.get();
    }

    // ==================== 蓄力时间 ====================

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        float chargeTime = (float) MutCrossbowStats.chargeTime(getSelfItem());
        float modified = net.minecraft.world.item.enchantment.EnchantmentHelper
                .modifyCrossbowChargingTime(stack, entity, chargeTime);
        return net.minecraft.util.Mth.floor(modified * 20.0F) + 3;
    }

    // ==================== 附魔能力 ====================

    @Override
    public int getEnchantmentValue() {
        return MutCrossbowStats.enchantmentValue(getSelfItem());
    }

    // ==================== 装填逻辑 ====================

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        int useTime = this.getUseDuration(stack, entity) - timeLeft;
        float pullProgress = getPowerForTime(useTime, stack, entity);

        if (pullProgress >= 1.0F && !CrossbowItem.isCharged(stack)) {
            if (tryLoadProjectilesWithCount(entity, stack)) {
                level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                        net.minecraft.sounds.SoundEvents.CROSSBOW_LOADING_END,
                        entity.getSoundSource(), 1.0F,
                        1.0F / (level.getRandom().nextFloat() * 0.5F + 1.0F) + 0.2F);
            }
        }
    }

    private static float getPowerForTime(int useTime, ItemStack stack, LivingEntity entity) {
        float f = (float) useTime / (float) CrossbowItem.getChargeDuration(stack, entity);
        if (f > 1.0F) {
            f = 1.0F;
        }
        return f;
    }

    private boolean tryLoadProjectilesWithCount(LivingEntity entity, ItemStack crossbowStack) {
        int loadCount = getEffectiveLoadCount(crossbowStack);

        List<ItemStack> loadedProjectiles = new ArrayList<>();
        ItemStack ammoStack = entity.getProjectile(crossbowStack);

        if (ammoStack.isEmpty()) {
            return false;
        }

        for (int i = 0; i < loadCount; i++) {
            List<ItemStack> drawn = draw(crossbowStack, ammoStack, entity);
            if (!drawn.isEmpty()) {
                loadedProjectiles.addAll(drawn);
            } else {
                break;
            }
        }

        if (!loadedProjectiles.isEmpty()) {
            crossbowStack.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.of(loadedProjectiles));
            return true;
        }
        return false;
    }

    // ==================== 射击逻辑 ====================

    @Override
    public void performShooting(Level level, LivingEntity shooter, InteractionHand hand,
                                ItemStack weapon, float originalSpeed, float inaccuracy,
                                @Nullable LivingEntity target) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        ChargedProjectiles charged = weapon.get(DataComponents.CHARGED_PROJECTILES);
        if (charged == null || charged.isEmpty()) {
            return;
        }

        List<ItemStack> allProjectiles = new ArrayList<>(charged.getItems());
        if (allProjectiles.isEmpty()) {
            return;
        }

        ItemStack projectileToShoot = allProjectiles.get(0).copy();

        if (allProjectiles.size() > 1) {
            allProjectiles.remove(0);
            weapon.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.of(allProjectiles));
        } else {
            weapon.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
        }

        float speed = MutCrossbowStats.projectileSpeed(getSelfItem());

        // 弹射物速度 > 4.5 时完全移除散射，否则按速度比例缩放
        float adjustedInaccuracy;
        if (speed > 4.5F) {
            adjustedInaccuracy = 0.0F;
        } else {
            adjustedInaccuracy = inaccuracy * (3.15F / speed);
        }

        this.shoot(serverLevel, shooter, hand, weapon,
                List.of(projectileToShoot), speed, adjustedInaccuracy,
                shooter instanceof Player, target);

        level.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(),
                net.minecraft.sounds.SoundEvents.CROSSBOW_SHOOT,
                SoundSource.PLAYERS, 1.0F,
                1.0F / (level.getRandom().nextFloat() * 0.5F + 1.0F) + 0.2F);

        if (shooter instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.SHOT_CROSSBOW.trigger(serverPlayer, weapon);
            serverPlayer.awardStat(Stats.ITEM_USED.get(weapon.getItem()));
        }
    }

    // ==================== 装填数量计算 ====================

    private int getEffectiveLoadCount(ItemStack crossbowStack) {
        int base = MutCrossbowStats.defaultLoadCount(getSelfItem());
        int configExtra = MutCrossbowLoadCountConfig.getTotalExtraLoadCount(getSelfItem());

        ItemEnchantments enchantments = crossbowStack
                .getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);

        int multiLoadLevel = 0;
        for (var entry : enchantments.entrySet()) {
            String enchantId = entry.getKey().getRegisteredName();
            if ("mut:multiload".equals(enchantId)) {
                multiLoadLevel = entry.getIntValue();
                break;
            }
        }

        int perLevel = MutCrossbowLoadCountConfig.getLoadCountPerLevel();
        return base + configExtra + (multiLoadLevel * perLevel);
    }

    // ==================== Tooltip ====================

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context,
                                List<Component> list, TooltipFlag flag) {
        ChargedProjectiles charged = stack.get(DataComponents.CHARGED_PROJECTILES);
        if (charged != null && !charged.isEmpty()) {
            List<ItemStack> projectiles = charged.getItems();
            ItemStack first = projectiles.get(0);

            MutableComponent projectileText = Component.translatable("item.minecraft.crossbow.projectile")
                    .append(CommonComponents.SPACE)
                    .append(first.getDisplayName());

            if (projectiles.size() > 1) {
                projectileText = projectileText.append(
                        Component.literal(" ×" + projectiles.size())
                                .withStyle(ChatFormatting.GRAY));
            }

            list.add(projectileText.withStyle(ChatFormatting.GRAY));
        }

        MutCrossbowStats.Stats stats = MutCrossbowStats.get(getSelfItem());

        list.add(Component.translatable("item.mut.crossbow.charge_time",
                        String.format("%.2f", stats.maxChargeTime()))
                .withStyle(ChatFormatting.GRAY));

        list.add(Component.translatable("item.mut.crossbow.speed",
                        String.format("%.1f", stats.projectileSpeed()))
                .withStyle(ChatFormatting.GRAY));

        int totalLoad = getEffectiveLoadCount(stack);
        if (totalLoad > 1) {
            list.add(Component.translatable("item.mut.crossbow.load_count", totalLoad)
                    .withStyle(ChatFormatting.GRAY));
        }
    }

    // ==================== 修复物品 ====================

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairItem) {
        return MutCrossbowStats.repairItem(getSelfItem()).test(repairItem);
    }

    // ==================== 弹射物类型 ====================

    @Override
    public java.util.function.Predicate<ItemStack> getAllSupportedProjectiles() {
        return ARROW_ONLY;
    }

    @Override
    public int getDefaultProjectileRange() {
        return 8;
    }
}