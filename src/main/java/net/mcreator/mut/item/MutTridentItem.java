package net.mcreator.mut.item;

import net.mcreator.mut.entity.MutThrownTrident;
import net.mcreator.mut.init.MutModItems;
import net.mcreator.mut.init.MutTridentStats;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public class MutTridentItem extends TridentItem {

    public static final int THROW_THRESHOLD_TIME = 10;
    public static final float SHOOT_POWER = 2.5F;

    private final Supplier<Item> selfSupplier;

    public MutTridentItem(Supplier<Item> selfSupplier, int durability, boolean fireResistant, MutTridentStats.Stats stats) {
        super(fireResistant
                ? new Properties().stacksTo(1).durability(durability).fireResistant().rarity(stats.rarity())
                .attributes(buildAttributes(stats.attackDamage(), stats.attackSpeed()))
                : new Properties().stacksTo(1).durability(durability).rarity(stats.rarity())
                .attributes(buildAttributes(stats.attackDamage(), stats.attackSpeed())));
        this.selfSupplier = selfSupplier;
    }

    private Item self() {
        return selfSupplier.get();
    }
    private static ItemAttributeModifiers buildAttributes(double attackDamage, float attackSpeed) {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(BASE_ATTACK_DAMAGE_ID, attackDamage, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED,
                        new AttributeModifier(BASE_ATTACK_SPEED_ID, attackSpeed, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .build();
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.SPEAR;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (isTooDamagedToUse(itemstack)) {
            return InteractionResultHolder.fail(itemstack);
        } else if (EnchantmentHelper.getTridentSpinAttackStrength(itemstack, player) > 0.0F && !player.isInWaterOrRain()) {
            return InteractionResultHolder.fail(itemstack);
        } else {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(itemstack);
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (entity instanceof Player player) {
            int i = this.getUseDuration(stack, entity) - timeLeft;
            if (i >= THROW_THRESHOLD_TIME) {
                float riptideStrength = EnchantmentHelper.getTridentSpinAttackStrength(stack, player);
                if (!(riptideStrength > 0.0F) || player.isInWaterOrRain()) {
                    if (!isTooDamagedToUse(stack)) {
                        Holder<SoundEvent> soundHolder = EnchantmentHelper.pickHighestLevel(stack, EnchantmentEffectComponents.TRIDENT_SOUND)
                                .orElse(SoundEvents.TRIDENT_THROW);
                        if (!level.isClientSide) {
                            stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(entity.getUsedItemHand()));
                            if (riptideStrength == 0.0F) {
                                MutThrownTrident thrownTrident = new MutThrownTrident(level, player, stack);
                                MutTridentStats.Stats stats = MutTridentStats.get(self());
                                thrownTrident.setBaseDamage(stats.throwDamage());
                                thrownTrident.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, SHOOT_POWER, 1.0F);
                                if (player.hasInfiniteMaterials()) {
                                    thrownTrident.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                                }
                                level.addFreshEntity(thrownTrident);
                                level.playSound(null, thrownTrident, soundHolder.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
                                if (!player.hasInfiniteMaterials()) {
                                    player.getInventory().removeItem(stack);
                                }
                            }
                        }
                        player.awardStat(Stats.ITEM_USED.get(this));
                        if (riptideStrength > 0.0F) {
                            float f7 = player.getYRot();
                            float f1 = player.getXRot();
                            float f2 = -Mth.sin(f7 * (float) (Math.PI / 180.0)) * Mth.cos(f1 * (float) (Math.PI / 180.0));
                            float f3 = -Mth.sin(f1 * (float) (Math.PI / 180.0));
                            float f4 = Mth.cos(f7 * (float) (Math.PI / 180.0)) * Mth.cos(f1 * (float) (Math.PI / 180.0));
                            float f5 = Mth.sqrt(f2 * f2 + f3 * f3 + f4 * f4);
                            f2 *= riptideStrength / f5;
                            f3 *= riptideStrength / f5;
                            f4 *= riptideStrength / f5;
                            player.push(f2, f3, f4);
                            player.startAutoSpinAttack(20, 8.0F, stack);
                            if (player.onGround()) {
                                player.move(MoverType.SELF, new Vec3(0.0, 1.1999999F, 0.0));
                            }
                            level.playSound(null, player, soundHolder.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return true;
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
    }

    private static boolean isTooDamagedToUse(ItemStack stack) {
        return stack.getDamageValue() >= stack.getMaxDamage() - 1;
    }
    @Override
    public int getEnchantmentValue() {
        return MutTridentStats.get(self()).enchantmentValue();
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairItem) {
        return MutTridentStats.get(self()).repairItem().test(repairItem);
    }

    // ========== 子类 ==========
    public static class WoodenTridentItem extends MutTridentItem {
        public WoodenTridentItem() {
            super(MutModItems.WOODEN_TRIDENT::get, 125, false, MutTridentStats.WOODEN);
        }
    }
    public static class CopperTridentItem extends MutTridentItem {
        public CopperTridentItem() {
            super(MutModItems.COPPER_TRIDENT::get, 215, false, MutTridentStats.COPPER);
        }
    }
    public static class IronTridentItem extends MutTridentItem {
        public IronTridentItem() {
            super(MutModItems.IRON_TRIDENT::get, 250, false, MutTridentStats.IRON);
        }
    }
    public static class GoldenTridentItem extends MutTridentItem {
        public GoldenTridentItem() {
            super(MutModItems.GOLDEN_TRIDENT::get, 375, false, MutTridentStats.GOLDEN);
        }
    }
    public static class DiamondTridentItem extends MutTridentItem {
        public DiamondTridentItem() {
            super(MutModItems.DIAMOND_TRIDENT::get, 1031, false, MutTridentStats.DIAMOND);
        }
    }
    public static class NetheriteTridentItem extends MutTridentItem {
        public NetheriteTridentItem() {
            super(MutModItems.NETHERITE_TRIDENT::get, 1266, true, MutTridentStats.NETHERITE);
        }
    }
    public static class SteelTridentItem extends MutTridentItem {
        public SteelTridentItem() {
            super(MutModItems.STEEL_TRIDENT::get, 625, true, MutTridentStats.STEEL);
        }
    }
    public static class GildingTridentItem extends MutTridentItem {
        public GildingTridentItem() {
            super(MutModItems.GILDING_TRIDENT::get, 1031, true, MutTridentStats.GILDING);
        }
    }
    public static class BlueDiamondTridentItem extends MutTridentItem {
        public BlueDiamondTridentItem() {
            super(MutModItems.BLUE_DIAMOND_TRIDENT::get, 1266, true, MutTridentStats.BLUE_DIAMOND);
        }
    }
    public static class AdvancedSteelTridentItem extends MutTridentItem {
        public AdvancedSteelTridentItem() {
            super(MutModItems.ADVANCED_STEEL_TRIDENT::get, 1375, true, MutTridentStats.ADVANCED_STEEL);
        }
    }
}