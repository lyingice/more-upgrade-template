package net.mcreator.mut.item;

import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ChatDecorator;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Items;
import net.mcreator.mut.init.MutBowDamage;

public class IronBowItem extends BowItem {
    // 公开常量，供其他类使用
    public static final int MAX_DRAW_DURATION = 20;
    public static final int DEFAULT_RANGE = 15;

    // 原版基础伤害
    private static final double VANILLA_BASE_DAMAGE = 2.0;

    public IronBowItem() {
        super(new Item.Properties()
                .stacksTo(1)
                .durability(484)
                .rarity(Rarity.COMMON));
    }

    @Override
    protected Projectile createProjectile(Level level, LivingEntity shooter, ItemStack weaponStack,
                                          ItemStack projectileStack, boolean isCrit) {
        Projectile projectile = super.createProjectile(level, shooter, weaponStack, projectileStack, isCrit);

        if (projectile instanceof AbstractArrow arrow) {
            // 从 MutBowDamage 获取这把弓的额外伤害加成
            double bonus = MutBowDamage.get(this);
            // 总伤害 = 原版基础 + 额外加成
            double totalDamage = VANILLA_BASE_DAMAGE + bonus;
            arrow.setBaseDamage(totalDamage);
        }

        return projectile;
    }

    @Override
    public int getEnchantmentValue() {
        return 14;
    }

    @Override
    public void releaseUsing(ItemStack bowStack, Level world, LivingEntity shooter, int remainingUseTicks) {
        if (shooter instanceof Player player) {
            // 获取弹药
            ItemStack ammoStack = player.getProjectile(bowStack);
            if (!ammoStack.isEmpty()) {
                // 计算使用时间
                int useTime = this.getUseDuration(bowStack, shooter) - remainingUseTicks;
                // 触发 Forge 事件
                useTime = net.neoforged.neoforge.event.EventHooks.onArrowLoose(bowStack, world, player, useTime, !ammoStack.isEmpty());
                if (useTime < 0) return;

                // 计算拉弓力度
                float power = getPowerForTime(useTime);
                if (!((double) power < 0.1)) {
                    // 获取弹药列表
                    List<ItemStack> projectiles = draw(bowStack, ammoStack, player);
                    if (world instanceof net.minecraft.server.level.ServerLevel serverLevel && !projectiles.isEmpty()) {
                        // 发射箭矢
                        this.shoot(serverLevel, player, player.getUsedItemHand(), bowStack, projectiles, power * 3.0F, 1.0F, power == 1.0F, null);
                    }

                    // 播放声音
                    world.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.ARROW_SHOOT,
                            SoundSource.PLAYERS,
                            1.0F,
                            1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + power * 0.5F);

                    // 增加统计
                    player.awardStat(Stats.ITEM_USED.get(this));

                    // 消耗耐久
                    bowStack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(player.getUsedItemHand()));
                }
            }
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack bowStack = player.getItemInHand(hand);
        boolean hasAmmo = !player.getProjectile(bowStack).isEmpty();

        // 触发 Forge 事件
        InteractionResultHolder<ItemStack> ret = net.neoforged.neoforge.event.EventHooks.onArrowNock(bowStack, world, player, hand, hasAmmo);
        if (ret != null) return ret;

        if (!player.hasInfiniteMaterials() && !hasAmmo) {
            return InteractionResultHolder.fail(bowStack);
        } else {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(bowStack);
        }
    }

    // 公开静态方法，供 PullProcedure 使用
    public static float getPowerForTime(int useTime) {
        float f = (float) useTime / MAX_DRAW_DURATION;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }
        return f;
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return ARROW_ONLY;
    }

    @Override
    public int getDefaultProjectileRange() {
        return DEFAULT_RANGE;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack itemstack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(itemstack, context, list, flag);

        // 显示弹射物伤害（原版2.0 + 材料加成）
        double bonus = MutBowDamage.get(this);
        double totalDamage = VANILLA_BASE_DAMAGE + bonus;
        list.add(Component.translatable("item.mut.bow_damage.description", totalDamage)
                .withStyle(ChatFormatting.GRAY));

        // 显示最大拉弓时间
        float seconds = MAX_DRAW_DURATION / 20.0f;
        list.add(Component.translatable("item.mut.max_draw_duration.description", MAX_DRAW_DURATION, seconds)
                .withStyle(ChatFormatting.GRAY));
    }

    @Override
    public boolean isValidRepairItem(ItemStack itemstack, ItemStack repairitem) {
        return Ingredient.of(new ItemStack(Items.IRON_INGOT)).test(repairitem);
    }
}