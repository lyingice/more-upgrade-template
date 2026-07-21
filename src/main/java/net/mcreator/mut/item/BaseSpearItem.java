package net.mcreator.mut.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.spearcore.init.SpearStats;
import net.mcreator.mut.MutMod;
import net.mcreator.mut.init.MutMoreAttributeMaterials;
import net.mcreator.mut.init.MutSpearStats;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

/**
 * mut 长矛物品基类。运行时继承 spearcore 的 {@link net.minecraft.spearcore.item.BaseSpearItem}，
 * 仅保留 mut 材质子类、额外属性与词缀数据。
 * <p>
 * 本类直接依赖 spearcore，只能在 spearcore 已加载时通过条件注册路径触达。
 */
public abstract class BaseSpearItem extends net.minecraft.spearcore.item.BaseSpearItem {

    protected BaseSpearItem(SpearStats.Stats stats) {
        super(stats);
    }

    protected BaseSpearItem(SpearStats.Stats stats, Properties customProps) {
        super(stats, customProps);
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        ItemAttributeModifiers parent = super.getDefaultAttributeModifiers(stack);
        SpearStats.Stats stats = SpearStats.get(this);
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();

        for (ItemAttributeModifiers.Entry entry : parent.modifiers()) {
            builder.add(entry.attribute(), entry.modifier(), entry.slot());
        }

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
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        SpearStats.Stats stats = SpearStats.get(this);
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
        public WitherSpearItem() { super(MutSpearStats.WITHER, buildPropertiesWithAffix(MutSpearStats.WITHER, createWitherMarkData())); }
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
    public static class LapisLazuliSpearItem extends BaseSpearItem {
        public LapisLazuliSpearItem() { super(MutSpearStats.LAPIS_LAZULI); }
    }
    public static class NetheriteLapisLazuliSpearItem extends BaseSpearItem {
        public NetheriteLapisLazuliSpearItem() { super(MutSpearStats.NETHERITE_LAPIS_LAZULI); }
    }
    public static class EchoiteSpearItem extends BaseSpearItem {
        public EchoiteSpearItem() { super(MutSpearStats.ECHOITE); }
    }
    public static class PoisonSteelSpearItem extends BaseSpearItem {
        public PoisonSteelSpearItem() { super(MutSpearStats.POISON_STEEL, buildPropertiesWithAffix(MutSpearStats.POISON_STEEL, createAffixData())); }
        private static CustomData createAffixData() {
            CompoundTag tag = new CompoundTag();
            tag.putString("Affix", "poison_mark");
            tag.putInt("AffixLevel", 3);
            return CustomData.of(tag);
        }
    }
    public static class FlameGoldSpearItem extends BaseSpearItem {
        public FlameGoldSpearItem() { super(MutSpearStats.FLAME_GOLD, buildPropertiesWithAffix(MutSpearStats.FLAME_GOLD, createAffixData())); }
        private static CustomData createAffixData() {
            CompoundTag tag = new CompoundTag();
            tag.putString("Affix", "fire_mark");
            tag.putInt("AffixLevel", 3);
            return CustomData.of(tag);
        }
    }
    public static class ThunderCopperSpearItem extends BaseSpearItem {
        public ThunderCopperSpearItem() { super(MutSpearStats.THUNDER_COPPER); }
    }
    public static class UncannyAmethystSpearItem extends BaseSpearItem {
        public UncannyAmethystSpearItem() { super(MutSpearStats.UNCANNY_AMETHYST, buildPropertiesWithAffix(MutSpearStats.UNCANNY_AMETHYST, createAffixData())); }
        private static CustomData createAffixData() {
            CompoundTag tag = new CompoundTag();
            tag.putString("Affix", "regeneration_mark");
            tag.putInt("AffixLevel", 3);
            return CustomData.of(tag);
        }
    }
}