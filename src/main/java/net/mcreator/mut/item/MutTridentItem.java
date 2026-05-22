package net.mcreator.mut.item;

import net.mcreator.mut.entity.MutThrownTrident;
import net.mcreator.mut.init.MutModItems;
import net.mcreator.mut.init.MutTridentStats;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class MutTridentItem extends TridentItem {

    public static final int THROW_THRESHOLD_TIME = 10;
    public static final float SHOOT_POWER = 2.5F;

    public MutTridentItem(MutTridentStats.Stats stats) {
        this(stats, stats.fireResistant()
                ? new Properties().fireResistant()
                : new Properties());
    }

    public MutTridentItem(MutTridentStats.Stats stats, Properties customProps) {
        super((stats.fireResistant() ? customProps.fireResistant() : customProps)
                .stacksTo(1)
                .durability(stats.durability())
                .rarity(stats.rarity())
                .attributes(buildAttributes(stats.attackDamage(), stats.attackSpeed())));
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
                                thrownTrident.setBaseDamage(MutTridentStats.get(stack.getItem()).throwDamage());
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

    @Override
    public int getEnchantmentValue() {
        return MutTridentStats.get(this).enchantmentValue();
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairItem) {
        return MutTridentStats.get(this).repairItem().test(repairItem);
    }

    private static boolean isTooDamagedToUse(ItemStack stack) {
        return stack.getDamageValue() >= stack.getMaxDamage() - 1;
    }

    // ========== 子类 ==========

    public static class WoodenTridentItem extends MutTridentItem {
        public WoodenTridentItem() { super(MutTridentStats.WOODEN); }
    }
    public static class CopperTridentItem extends MutTridentItem {
        public CopperTridentItem() { super(MutTridentStats.COPPER); }
    }
    public static class IronTridentItem extends MutTridentItem {
        public IronTridentItem() { super(MutTridentStats.IRON); }
    }
    public static class GoldenTridentItem extends MutTridentItem {
        public GoldenTridentItem() { super(MutTridentStats.GOLDEN); }
    }
    public static class DiamondTridentItem extends MutTridentItem {
        public DiamondTridentItem() { super(MutTridentStats.DIAMOND); }
    }
    public static class NetheriteTridentItem extends MutTridentItem {
        public NetheriteTridentItem() { super(MutTridentStats.NETHERITE); }
    }
    public static class SteelTridentItem extends MutTridentItem {
        public SteelTridentItem() { super(MutTridentStats.STEEL); }
    }
    public static class GildingTridentItem extends MutTridentItem {
        public GildingTridentItem() { super(MutTridentStats.GILDING); }
    }
    public static class BlueDiamondTridentItem extends MutTridentItem {
        public BlueDiamondTridentItem() { super(MutTridentStats.BLUE_DIAMOND); }
    }
    public static class AdvancedSteelTridentItem extends MutTridentItem {
        public AdvancedSteelTridentItem() { super(MutTridentStats.ADVANCED_STEEL); }
    }
    public static class ObsidianTridentItem extends MutTridentItem {
        public ObsidianTridentItem() { super(MutTridentStats.OBSIDIAN); }
    }
    public static class NetheriteObsidianTridentItem extends MutTridentItem {
        public NetheriteObsidianTridentItem() { super(MutTridentStats.NETHERITE_OBSIDIAN); }
    }
    public static class CryingObsidianTridentItem extends MutTridentItem {
        public CryingObsidianTridentItem() { super(MutTridentStats.CRYING_OBSIDIAN); }
    }
    public static class NetherStarTridentItem extends MutTridentItem {
        public NetherStarTridentItem() { super(MutTridentStats.NETHER_STAR); }
        @Override
        @OnlyIn(Dist.CLIENT)
        public boolean isFoil(ItemStack itemstack) {
            return true;
        }

    }
    public static class DragonTridentItem extends MutTridentItem {
        public DragonTridentItem() { super(MutTridentStats.DRAGON); }
    }
    public static class WitherTridentItem extends MutTridentItem {
        public WitherTridentItem() {
            super(MutTridentStats.WITHER,
                    new Properties()
                            .fireResistant()
                            .component(DataComponents.CUSTOM_DATA, createWitherMarkData()));
        }

        private static CustomData createWitherMarkData() {
            CompoundTag tag = new CompoundTag();
            tag.putString("Affix", "wither_mark");
            return CustomData.of(tag);
        }
    }
    // 子类
    public static class NetheriteCopperTridentItem extends MutTridentItem {
        public NetheriteCopperTridentItem() { super(MutTridentStats.NETHERITE_COPPER); }
    }
    public static class NetheriteEmeraldTridentItem extends MutTridentItem {
        public NetheriteEmeraldTridentItem() { super(MutTridentStats.NETHERITE_EMERALD); }
    }
    public static class NetheriteRedstoneTridentItem extends MutTridentItem {
        public NetheriteRedstoneTridentItem() { super(MutTridentStats.NETHERITE_REDSTONE); }
    }
    public static class NetheriteAmethystTridentItem extends MutTridentItem {
        public NetheriteAmethystTridentItem() { super(MutTridentStats.NETHERITE_AMETHYST); }
    }
    public static class AmethystTridentItem extends MutTridentItem {
        public AmethystTridentItem() { super(MutTridentStats.AMETHYST); }
    }
    public static class EmeraldTridentItem extends MutTridentItem {
        public EmeraldTridentItem() { super(MutTridentStats.EMERALD); }
    }
}