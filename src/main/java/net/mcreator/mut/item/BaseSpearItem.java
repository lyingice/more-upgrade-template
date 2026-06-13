package net.mcreator.mut.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.mcreator.mut.util.SpearCondition;
import net.mcreator.mut.MutMod;
import net.mcreator.mut.init.MutSpearStats;
import net.mcreator.mut.init.MutMoreAttributeMaterials;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public abstract class BaseSpearItem extends SpearItem {

    private final Supplier<Item> selfItemSupplier;

    // ========== 阶段配置字段 ==========
    private final float swingTimes;
    private final float hitboxMargin;
    private final int contactCooldownTicks;
    private final int delayTicks;
    private final Optional<SpearCondition> dismountConditions;
    private final Optional<SpearCondition> knockbackConditions;
    private final Optional<SpearCondition> damageConditions;
    private final float forwardMovement;
    private final float damageMultiplier;
    private final float minRange;
    private final float maxRange;
    private final float minCreativeRange;
    private final float maxCreativeRange;
    private final float hitboxMargin2;
    private final float mobFactor;
    private final boolean dealsKnockback;
    private final boolean dismounts;

    protected BaseSpearItem(MutSpearStats.Stats stats) {
        super(buildProperties(stats));
        this.selfItemSupplier = () -> this;
        MutSpearStats.register(this, stats);

        this.swingTimes = stats.swingTimes();
        this.hitboxMargin = stats.hitboxMargin();
        this.contactCooldownTicks = stats.contactCooldownTicks();
        this.delayTicks = stats.delayTicks();
        this.dismountConditions = stats.dismountConditions();
        this.knockbackConditions = stats.knockbackConditions();
        this.damageConditions = stats.damageConditions();
        this.forwardMovement = stats.forwardMovement();
        this.damageMultiplier = stats.damageMultiplier();
        this.minRange = stats.minRange();
        this.maxRange = stats.maxRange();
        this.minCreativeRange = stats.minCreativeRange();
        this.maxCreativeRange = stats.maxCreativeRange();
        this.hitboxMargin2 = stats.hitboxMargin2();
        this.mobFactor = stats.mobFactor();
        this.dealsKnockback = stats.dealsKnockback();
        this.dismounts = stats.dismounts();
    }

    protected BaseSpearItem(MutSpearStats.Stats stats, Properties customProps) {
        super(customProps);
        this.selfItemSupplier = () -> this;
        MutSpearStats.register(this, stats);

        this.swingTimes = stats.swingTimes();
        this.hitboxMargin = stats.hitboxMargin();
        this.contactCooldownTicks = stats.contactCooldownTicks();
        this.delayTicks = stats.delayTicks();
        this.dismountConditions = stats.dismountConditions();
        this.knockbackConditions = stats.knockbackConditions();
        this.damageConditions = stats.damageConditions();
        this.forwardMovement = stats.forwardMovement();
        this.damageMultiplier = stats.damageMultiplier();
        this.minRange = stats.minRange();
        this.maxRange = stats.maxRange();
        this.minCreativeRange = stats.minCreativeRange();
        this.maxCreativeRange = stats.maxCreativeRange();
        this.hitboxMargin2 = stats.hitboxMargin2();
        this.mobFactor = stats.mobFactor();
        this.dealsKnockback = stats.dealsKnockback();
        this.dismounts = stats.dismounts();
    }

    // ========== Properties 构建 ==========

    private static Properties buildProperties(MutSpearStats.Stats stats) {
        Properties props = new Properties()
                .stacksTo(1)
                .durability(stats.durability())
                .rarity(stats.rarity());
        if (stats.fireResistant()) {
            props.fireResistant();
        }
        return props;
    }

    protected static Properties buildPropertiesWithAffix(MutSpearStats.Stats stats, CustomData affixData) {
        Properties props = buildProperties(stats);
        props.component(DataComponents.CUSTOM_DATA, affixData);
        return props;
    }

    private Item getSelfItem() {
        return selfItemSupplier.get();
    }

    // ========== 属性修饰符 ==========

    private static ItemAttributeModifiers buildAttributeModifiers(MutSpearStats.Stats stats) {
        var builder = ItemAttributeModifiers.builder();

        builder.add(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(BASE_ATTACK_DAMAGE_ID,
                        stats.attackDamageBonus(),
                        AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND);

        builder.add(Attributes.ATTACK_SPEED,
                new AttributeModifier(BASE_ATTACK_SPEED_ID,
                        stats.getAttackSpeedModifier(),
                        AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND);

        builder.add(Attributes.ENTITY_INTERACTION_RANGE,
                new AttributeModifier(ResourceLocation.fromNamespaceAndPath(MutMod.MODID, "spear_range"),
                        1.5, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND);

        List<MutMoreAttributeMaterials.AttributeEntry> extraAttributes =
                MutMoreAttributeMaterials.getToolAttributes(stats.materialName());

        for (MutMoreAttributeMaterials.AttributeEntry entry : extraAttributes) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(
                    MutMod.MODID,
                    stats.materialName() + "_spear_" + entry.suffixId()
            );
            builder.add(entry.attribute(),
                    new AttributeModifier(id, entry.amount(), entry.operation()),
                    entry.slotGroup());
        }

        return builder.build();
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        return buildAttributeModifiers(MutSpearStats.get(getSelfItem()));
    }

    // ========== SpearItem 抽象方法实现 ==========

    @Override
    public float getAttackDuration() {
        return MutSpearStats.attackDuration(getSelfItem());
    }

    @Override
    public  float getDamageMultiplier() {
        return MutSpearStats.damageMultiplier(getSelfItem());
    }

    @Override
    public  SoundEvent getUseSound() {
        return MutSpearStats.useSound(getSelfItem());
    }

    @Override
    public SoundEvent getHitSound() {
        return MutSpearStats.hitSound(getSelfItem());
    }

    @Override
    public SoundEvent getAttackSound() {
        return MutSpearStats.attackSound(getSelfItem());
    }

    @Override
    public int getEnchantmentValue() {
        return MutSpearStats.enchantmentValue(getSelfItem());
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return MutSpearStats.repairIngredient(getSelfItem()).test(repair);
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
    }

    // ========== 蓄力阶段 Getter ==========

    @Override
    public int getDelayTicks() { return delayTicks; }

    @Override
    public int getDismountEndTick() {
        return delayTicks + dismountConditions.map(SpearCondition::maxDurationTicks).orElse(0);
    }

    @Override
    public int getKnockbackEndTick() {
        return delayTicks + knockbackConditions.map(SpearCondition::maxDurationTicks).orElse(0);
    }

    @Override
    public int getDamageEndTick() {
        return delayTicks + damageConditions.map(SpearCondition::maxDurationTicks).orElse(0);
    }

    @Override
    public Optional<SpearCondition> getDismountConditions() { return dismountConditions; }

    @Override
    public Optional<SpearCondition> getKnockbackConditions() { return knockbackConditions; }

    @Override
    public Optional<SpearCondition> getDamageConditions() { return damageConditions; }

    @Override
    public float getForwardMovement() { return forwardMovement; }

    @Override
    public float getMinRange() { return minRange; }

    @Override
    public float getMaxRange() { return maxRange; }

    @Override
    public float getHitboxMargin() { return hitboxMargin; }

    @Override
    public float getHitboxMargin2() { return hitboxMargin2; }

    @Override
    public int getContactCooldownTicks() { return contactCooldownTicks; }

    @Override
    public float getSwingTimes() { return swingTimes; }
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        MutSpearStats.Stats stats = MutSpearStats.get(getSelfItem());
        list.add(Component.translatable("item.mut.spear.damage_multiplier",
                        String.format("%.2f", stats.damageMultiplier()))
                .withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, context, list, flag);
    }

    // ========== 内置子类 ==========

    public static class WoodenSpearItem extends BaseSpearItem {
        public WoodenSpearItem() { super(MutSpearStats.WOOD); }
    }
    public static class StoneSpearItem extends BaseSpearItem {
        public StoneSpearItem() { super(MutSpearStats.STONE); }
    }
    public static class CopperSpearItem extends BaseSpearItem {
        public CopperSpearItem() { super(MutSpearStats.COPPER); }
    }
    public static class IronSpearItem extends BaseSpearItem {
        public IronSpearItem() { super(MutSpearStats.IRON); }
    }
    public static class GoldenSpearItem extends BaseSpearItem {
        public GoldenSpearItem() { super(MutSpearStats.GOLD); }
    }
    public static class DiamondSpearItem extends BaseSpearItem {
        public DiamondSpearItem() { super(MutSpearStats.DIAMOND); }
    }
    public static class NetheriteSpearItem extends BaseSpearItem {
        public NetheriteSpearItem() { super(MutSpearStats.NETHERITE); }
    }
    public static class SteelSpearItem extends BaseSpearItem {
        public SteelSpearItem() { super(MutSpearStats.STEEL); }
    }
    public static class AdvancedSteelSpearItem extends BaseSpearItem {
        public AdvancedSteelSpearItem() { super(MutSpearStats.ADVANCED_STEEL); }
    }
    public static class GildingSpearItem extends BaseSpearItem {
        public GildingSpearItem() { super(MutSpearStats.GILDING); }
    }
    public static class BlueDiamondSpearItem extends BaseSpearItem {
        public BlueDiamondSpearItem() { super(MutSpearStats.BLUE_DIAMOND); }
    }
    public static class ObsidianSpearItem extends BaseSpearItem {
        public ObsidianSpearItem() { super(MutSpearStats.OBSIDIAN); }
    }
    public static class NetheriteObsidianSpearItem extends BaseSpearItem {
        public NetheriteObsidianSpearItem() { super(MutSpearStats.NETHERITE_OBSIDIAN); }
    }
    public static class CryingObsidianSpearItem extends BaseSpearItem {
        public CryingObsidianSpearItem() { super(MutSpearStats.CRYING_OBSIDIAN); }
    }
    public static class EmeraldSpearItem extends BaseSpearItem {
        public EmeraldSpearItem() { super(MutSpearStats.EMERALD); }
    }
    public static class NetheriteEmeraldSpearItem extends BaseSpearItem {
        public NetheriteEmeraldSpearItem() { super(MutSpearStats.NETHERITE_EMERALD); }
    }
    public static class NetheriteRedstoneSpearItem extends BaseSpearItem {
        public NetheriteRedstoneSpearItem() { super(MutSpearStats.NETHERITE_REDSTONE); }
    }
    public static class NetherStarSpearItem extends BaseSpearItem {
        public NetherStarSpearItem() { super(MutSpearStats.NETHER_STAR); }
        @Override
        @OnlyIn(Dist.CLIENT)
        public boolean isFoil(ItemStack itemstack) {
            return true;
        }
    }
    public static class WitherSpearItem extends BaseSpearItem {
        public WitherSpearItem() { super(MutSpearStats.WITHER,buildPropertiesWithAffix(MutSpearStats.WITHER, createWitherMarkData())); }
        private static CustomData createWitherMarkData() {
            CompoundTag tag = new CompoundTag();
            tag.putString("Affix", "wither_mark");
            tag.putInt("AffixLevel", 3);
            return CustomData.of(tag);
        }
    }
    public static class DragonSpearItem extends BaseSpearItem {
        public DragonSpearItem() { super(MutSpearStats.DRAGON); }
    }
    public static class AmethystSpearItem extends BaseSpearItem {
        public AmethystSpearItem() { super(MutSpearStats.AMETHYST); }
    }
    public static class NetheriteAmethystSpearItem extends BaseSpearItem {
        public NetheriteAmethystSpearItem() { super(MutSpearStats.NETHERITE_AMETHYST); }
    }
    public static class NetheriteCopperSpearItem extends BaseSpearItem {
        public NetheriteCopperSpearItem() { super(MutSpearStats.NETHERITE_COPPER); }
    }
    public static class LapisLazuliSpearItem extends BaseSpearItem { public LapisLazuliSpearItem() { super(MutSpearStats.LAPIS_LAZULI); }}
    public static class NetheriteLapisLazuliSpearItem extends BaseSpearItem { public NetheriteLapisLazuliSpearItem() { super(MutSpearStats.NETHERITE_LAPIS_LAZULI); }}
    public static class EchoiteSpearItem extends BaseSpearItem { public EchoiteSpearItem() { super(MutSpearStats.ECHOITE); }}
    public static class PoisonSteelSpearItem extends BaseSpearItem { public PoisonSteelSpearItem() { super(MutSpearStats.POISON_STEEL,buildPropertiesWithAffix(MutSpearStats.POISON_STEEL, createAffixData())); }private static CustomData createAffixData() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Affix", "poison_mark"
        );
        tag.putInt("AffixLevel", 3);
        return CustomData.of(tag);}
    }
    public static class FlameGoldSpearItem extends BaseSpearItem { public FlameGoldSpearItem() { super(MutSpearStats.FLAME_GOLD,buildPropertiesWithAffix(MutSpearStats.FLAME_GOLD, createAffixData())); }private static CustomData createAffixData() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Affix", "fire_mark");
        tag.putInt("AffixLevel", 3);
        return CustomData.of(tag);}}
    public static class ThunderCopperSpearItem extends BaseSpearItem { public ThunderCopperSpearItem() { super(MutSpearStats.THUNDER_COPPER); }}
    public static class UncannyAmethystSpearItem extends BaseSpearItem { public UncannyAmethystSpearItem() { super(MutSpearStats.UNCANNY_AMETHYST,buildPropertiesWithAffix(MutSpearStats.UNCANNY_AMETHYST, createAffixData())); }private static CustomData createAffixData() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Affix", "regeneration_mark");
        tag.putInt("AffixLevel", 3);
        return CustomData.of(tag);}
    }

    /**
     public static class XxSpearItem extends BaseSpearItem {
     public XxSpearItem() { super(MutSpearStats.XX); }}
     ,buildPropertiesWithAffix(MutSpearStats.X, createAffixData())
     **/
}