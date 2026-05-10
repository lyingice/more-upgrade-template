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
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

import java.util.List;
import java.util.function.Predicate;
import net.mcreator.mut.init.MutBowStats;
import net.mcreator.mut.init.MutBowDamage;

public abstract class BaseBowItem extends BowItem {

    protected static final double VANILLA_BASE_DAMAGE = 2.0;
    protected final MutBowStats.BowConfig config;

    public BaseBowItem(MutBowStats.BowConfig config) {
        super(createProperties(config));
        this.config = config;
        // 自动注册到 MutBowStats
        MutBowStats.register(this, config);
    }

    private static Properties createProperties(MutBowStats.BowConfig config) {
        Properties props = new Properties()
                .stacksTo(1)
                .durability(config.durability())
                .rarity(config.rarity());
        if (config.fireResistant()) {
            props.fireResistant();
        }
        return props;
    }

    @Override
    public int getEnchantmentValue() {
        return config.enchantmentValue();
    }

    @Override
    public boolean isValidRepairItem(ItemStack itemstack, ItemStack repairitem) {
        return config.repairIngredient().get().test(repairitem);
    }

    @Override
    protected Projectile createProjectile(Level level, LivingEntity shooter, ItemStack weaponStack,
                                          ItemStack projectileStack, boolean isCrit) {
        Projectile projectile = super.createProjectile(level, shooter, weaponStack, projectileStack, isCrit);
        if (projectile instanceof AbstractArrow arrow) {
            // 从 MutBowStats 读取伤害加成
            double bonus = MutBowStats.getDamageBonus(this);
            double totalDamage = VANILLA_BASE_DAMAGE + bonus;
            arrow.setBaseDamage(totalDamage);
        }
        return projectile;
    }

    @Override
    public void releaseUsing(ItemStack bowStack, Level world, LivingEntity shooter, int remainingUseTicks) {
        if (shooter instanceof Player player) {
            ItemStack ammoStack = player.getProjectile(bowStack);
            if (!ammoStack.isEmpty()) {
                int useTime = this.getUseDuration(bowStack, shooter) - remainingUseTicks;
                useTime = net.neoforged.neoforge.event.EventHooks.onArrowLoose(bowStack, world, player, useTime, !ammoStack.isEmpty());
                if (useTime < 0) return;

                float power = getPowerForTime(useTime);
                if (!((double) power < 0.1)) {
                    List<ItemStack> projectiles = draw(bowStack, ammoStack, player);
                    if (world instanceof net.minecraft.server.level.ServerLevel serverLevel && !projectiles.isEmpty()) {
                        this.shoot(serverLevel, player, player.getUsedItemHand(), bowStack, projectiles, power * 3.0F, 1.0F, power == 1.0F, null);
                    }

                    world.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS,
                            1.0F, 1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + power * 0.5F);

                    player.awardStat(Stats.ITEM_USED.get(this));
                    bowStack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(player.getUsedItemHand()));
                }
            }
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack bowStack = player.getItemInHand(hand);
        boolean hasAmmo = !player.getProjectile(bowStack).isEmpty();

        InteractionResultHolder<ItemStack> ret = net.neoforged.neoforge.event.EventHooks.onArrowNock(bowStack, world, player, hand, hasAmmo);
        if (ret != null) return ret;

        if (!player.hasInfiniteMaterials() && !hasAmmo) {
            return InteractionResultHolder.fail(bowStack);
        } else {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(bowStack);
        }
    }

    public static float getPowerForTime(int useTime, BowItem bow) {
        int maxDrawDuration = MutBowStats.getMaxDrawDuration(bow);
        float f = (float) useTime / maxDrawDuration;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) f = 1.0F;
        return f;
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return ARROW_ONLY;
    }

    @Override
    public int getDefaultProjectileRange() {
        return 15;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack itemstack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(itemstack, context, list, flag);

        double bonus = MutBowStats.getDamageBonus(this);
        double totalDamage = VANILLA_BASE_DAMAGE + bonus;
        int maxDrawDuration = MutBowStats.getMaxDrawDuration(this);
        float seconds = maxDrawDuration / 20.0f;

        list.add(Component.translatable("item.mut.bow_damage.description", totalDamage).withStyle(ChatFormatting.GRAY));
        list.add(Component.translatable("item.mut.max_draw_duration.description", maxDrawDuration, seconds).withStyle(ChatFormatting.GRAY));
    }

    public static class DragonBowItem extends BaseBowItem {
        public DragonBowItem() { super(MutBowStats.DRAGON); }
    }
    //子类添加实例
    //public static class XxBowItem extends BaseBowItem {
    //    public XxBowItem() { super(MutBowStats.X); }}
}