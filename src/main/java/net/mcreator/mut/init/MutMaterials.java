package net.mcreator.mut.init;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.ItemStack;
import net.mcreator.mut.item.*;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

/**
 * 材质数据存储中心
 */
public class MutMaterials {

    public enum CraftingType { NORMAL, SMITHING }

    public record MutMaterial(
            String name,
            Rarity rarity,
            boolean fireResistant,
            CraftingType craftingType,
            int miningLevel,
            float toolSpeed,
            float swordDamage, float shovelDamage, float pickaxeDamage, float axeDamage, float hoeDamage,
            float swordSpeed, float shovelSpeed, float pickaxeSpeed, float axeSpeed, float hoeSpeed,
            int enchantmentValue,
            int durability,
            Supplier<Ingredient> repairIngredient,
            @Nullable ItemAttributeModifiers toolAttributes,
            boolean hasArmor,
            int[] armorValues,
            int armorEnchantmentValue,
            float armorToughness,
            float armorKnockbackResistance,
            int armorDurabilityFactor,
            boolean piglinNeutral,
            Holder<SoundEvent> equipSound,
            @Nullable ItemAttributeModifiers armorAttributes,
            String textureFolder
    ) {
        public MutMaterial {
            if (hasArmor && (armorValues == null || armorValues.length != 5)) {
                throw new IllegalArgumentException(
                        "材质 '" + name + "' 的 armorValues 长度必须为5 [头,胸,腿,脚,身体]");
            }
        }

        public int helmetValue()     { return hasArmor ? armorValues[0] : 0; }
        public int chestplateValue() { return hasArmor ? armorValues[1] : 0; }
        public int leggingsValue()   { return hasArmor ? armorValues[2] : 0; }
        public int bootsValue()      { return hasArmor ? armorValues[3] : 0; }
        public int bodyValue()       { return hasArmor ? armorValues[4] : 0; }
        public int armorDurabilityMultiplier() { return armorDurabilityFactor; }

        public float getDamageFor(ToolType type) {
            return switch (type) {
                case SWORD   -> swordDamage;
                case SHOVEL  -> shovelDamage;
                case PICKAXE -> pickaxeDamage;
                case AXE     -> axeDamage;
                case HOE     -> hoeDamage;
            };
        }

        public float getSpeedFor(ToolType type) {
            return switch (type) {
                case SWORD   -> swordSpeed;
                case SHOVEL  -> shovelSpeed;
                case PICKAXE -> pickaxeSpeed;
                case AXE     -> axeSpeed;
                case HOE     -> hoeSpeed;
            };
        }

        public Tier asToolTier() {
            MutMaterial m = this;
            return new Tier() {
                @Override public int getUses() { return m.durability(); }
                @Override public float getSpeed() { return m.toolSpeed(); }
                @Override public float getAttackDamageBonus() { return m.swordDamage(); }
                @Override public int getEnchantmentValue() { return m.enchantmentValue(); }
                @Override public Ingredient getRepairIngredient() { return m.repairIngredient().get(); }
                @Override public TagKey<Block> getIncorrectBlocksForDrops() { return null; }
            };
        }

        public Tier asToolTier(ToolType type) {
            MutMaterial m = this;
            return new Tier() {
                @Override public int getUses() { return m.durability(); }
                @Override public float getSpeed() { return m.toolSpeed(); }
                @Override public float getAttackDamageBonus() { return m.getDamageFor(type); }
                @Override public int getEnchantmentValue() { return m.enchantmentValue(); }
                @Override public Ingredient getRepairIngredient() { return m.repairIngredient().get(); }
                @Override public TagKey<Block> getIncorrectBlocksForDrops() { return null; }
            };
        }

        public ArmorMaterial asArmorMaterial() {
            return new ArmorMaterial(
                    Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                        map.put(ArmorItem.Type.HELMET, helmetValue());
                        map.put(ArmorItem.Type.CHESTPLATE, chestplateValue());
                        map.put(ArmorItem.Type.LEGGINGS, leggingsValue());
                        map.put(ArmorItem.Type.BOOTS, bootsValue());
                        map.put(ArmorItem.Type.BODY, bodyValue());
                    }),
                    armorEnchantmentValue(), equipSound(),
                    () -> repairIngredient().get(),
                    List.of(new ArmorMaterial.Layer(
                            ResourceLocation.fromNamespaceAndPath("mut", textureFolder()))),
                    armorToughness(), armorKnockbackResistance());
        }
    }

    public enum ToolType { SWORD, SHOVEL, PICKAXE, AXE, HOE }

    private static final List<MutMaterial> ALL_MATERIALS = new ArrayList<>();
    public static List<MutMaterial> getAll() { return Collections.unmodifiableList(ALL_MATERIALS); }

    // =====================================================================
    // 属性构建器（绿色基础属性 + 兼容 MutMoreAttributeMaterials）
    // =====================================================================

    public static ItemAttributeModifiers createToolAttributes(MutMaterial mat, ToolType type) {
        var b = ItemAttributeModifiers.builder();

        b.add(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(ResourceLocation.withDefaultNamespace("base_attack_damage"),
                        mat.getDamageFor(type), AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND);
        b.add(Attributes.ATTACK_SPEED,
                new AttributeModifier(ResourceLocation.withDefaultNamespace("base_attack_speed"),
                        mat.getSpeedFor(type), AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND);

        if (mat.toolAttributes() != null)
            mat.toolAttributes().modifiers().forEach(e -> b.add(e.attribute(), e.modifier(), e.slot()));

        var extra = MutMoreAttributeMaterials.buildModifiers(mat.name(),
                MutMoreAttributeMaterials.getToolAttributes(mat.name()));
        if (extra != null)
            extra.modifiers().forEach(e -> b.add(e.attribute(), e.modifier(), e.slot()));

        return b.build();
    }

    public static ItemAttributeModifiers createArmorAttributes(MutMaterial mat, ArmorItem.Type type) {
        var b = ItemAttributeModifiers.builder();
        String p = mat.name().toLowerCase(Locale.ROOT);

        double val = switch (type) {
            case HELMET -> mat.helmetValue();
            case CHESTPLATE -> mat.chestplateValue();
            case LEGGINGS -> mat.leggingsValue();
            case BOOTS -> mat.bootsValue();
            case BODY -> mat.bodyValue();
        };

        if (val > 0)
            b.add(Attributes.ARMOR,
                    new AttributeModifier(ResourceLocation.fromNamespaceAndPath("mut", p + "_armor_" + type.getName()),
                            val, AttributeModifier.Operation.ADD_VALUE),
                    EquipmentSlotGroup.bySlot(type.getSlot()));
        if (mat.armorToughness() > 0)
            b.add(Attributes.ARMOR_TOUGHNESS,
                    new AttributeModifier(ResourceLocation.fromNamespaceAndPath("mut", p + "_toughness"),
                            mat.armorToughness(), AttributeModifier.Operation.ADD_VALUE),
                    EquipmentSlotGroup.bySlot(type.getSlot()));
        if (mat.armorKnockbackResistance() > 0)
            b.add(Attributes.KNOCKBACK_RESISTANCE,
                    new AttributeModifier(ResourceLocation.fromNamespaceAndPath("mut", p + "_knockback"),
                            mat.armorKnockbackResistance(), AttributeModifier.Operation.ADD_VALUE),
                    EquipmentSlotGroup.bySlot(type.getSlot()));

        if (mat.armorAttributes() != null)
            mat.armorAttributes().modifiers().forEach(e -> b.add(e.attribute(), e.modifier(), e.slot()));

        // ── 来自 MutMoreAttributeMaterials 的额外盔甲属性（按部位）──
        List<MutMoreAttributeMaterials.AttributeEntry> extraArmor = switch (type) {
            case HELMET -> MutMoreAttributeMaterials.getHelmetAttributes(mat.name());
            case CHESTPLATE -> MutMoreAttributeMaterials.getChestplateAttributes(mat.name());
            case LEGGINGS -> MutMoreAttributeMaterials.getLeggingsAttributes(mat.name());
            case BOOTS -> MutMoreAttributeMaterials.getBootsAttributes(mat.name());
            case BODY -> Collections.emptyList();
        };
        ItemAttributeModifiers extraArmorModifiers = MutMoreAttributeMaterials.buildModifiers(mat.name(), extraArmor);
        if (extraArmorModifiers != null)
            extraArmorModifiers.modifiers().forEach(e -> b.add(e.attribute(), e.modifier(), e.slot()));

        return b.build();
    }

    // =====================================================================
    // 材质预设
    // =====================================================================

    // ── 铜 ──
    public static final MutMaterial COPPER = new MutMaterial(
            "copper", Rarity.COMMON, false, CraftingType.NORMAL,
            1, 5.0F,
            4.0F, 2.0F, 8.0F, 2.5F, 0.5F,
            -2.4F, -2.8F, -3.1F, -3.0F, 0.0F,
            13, 190, () -> Ingredient.of(Items.COPPER_INGOT), null,
            true, new int[]{2, 4, 3, 1, 5},
            8, 0.0F, 0.0F, 11, false, SoundEvents.ARMOR_EQUIP_IRON, null, "copper");

    // ── 下界合金铜 ──
    public static final MutMaterial NETHERITE_COPPER = new MutMaterial(
            "netherite_copper", Rarity.COMMON, true, CraftingType.SMITHING,
            4, 8.0F,
            6.0F, 4.5F, 4.0F, 8.0F, 1.5F,
            -2.4F, -3.0F, -2.8F, -3.0F, 0.0F,
            13, 1231, () -> Ingredient.of(Items.COPPER_BLOCK), null,
            true, new int[]{3, 8, 6, 3, 0},
            8, 2.0F, 0.1F, 34, false, SoundEvents.ARMOR_EQUIP_NETHERITE, null, "netherite_copper");

    // ── 钢 ──
    public static final MutMaterial STEEL = new MutMaterial(
            "steel", Rarity.COMMON, true, CraftingType.SMITHING,
            3, 8.5F,
            6.5F, 4.5F, 8.5F, 5.0F, 1.75F,
            -2.3F, -2.7F, -2.9F, -2.9F, 0.1F,
            15, 750, () -> Ingredient.of(new ItemStack(MutModItems.STEEL_INGOT.get())), null,
            true, new int[]{3, 8, 6, 3, 0},
            9, 2.5F, 0.1F, 35, false, SoundEvents.ARMOR_EQUIP_NETHERITE, null, "steel");

    // ── 精钢 ──
    public static final MutMaterial ADVANCED_STEEL = new MutMaterial(
            "advanced_steel", Rarity.COMMON, true, CraftingType.SMITHING,
            4, 10.0F,
            8.0F, 10.0F, 6.0F, 6.5F, 2.5F,
            -2.3F, -2.9F, -2.7F, -2.9F, 0.1F,
            15, 2250, () -> Ingredient.of(new ItemStack(MutModItems.ADVANCED_STEEL_INGOT.get())), null,
            true, new int[]{4, 10, 7, 4, 0},
            9, 3.0F, 0.15F, 55, false, SoundEvents.ARMOR_EQUIP_NETHERITE, null, "advanced_steel");

    // ── 鎏金 ──
    public static final MutMaterial GILDING = new MutMaterial(
            "gilding", Rarity.COMMON, true, CraftingType.SMITHING,
            3, 12.0F,
            6.0F, 8.0F, 4.0F, 4.5F, 1.5F,
            -2.2F, -2.8F, -2.6F, -2.8F, 0.2F,
            22, 1561, () -> Ingredient.of(new ItemStack(MutModItems.GILDING_INGOT.get())), null,
            true, new int[]{3, 8, 6, 3, 0},
            25, 4.0F, 0.1F, 33, true, SoundEvents.ARMOR_EQUIP_NETHERITE, null, "gilding");

    // ── 蓝钻合金 ──
    public static final MutMaterial BLUE_DIAMOND = new MutMaterial(
            "blue_diamond", Rarity.COMMON, true, CraftingType.SMITHING,
            4, 10.0F,
            8.0F, 10.0F, 6.0F, 6.5F, 2.5F,
            -2.2F, -2.8F, -2.6F, -2.8F, 0.2F,
            18, 2031, () -> Ingredient.of(new ItemStack(MutModItems.BLUE_DIAMOND_INGOT.get())), null,
            true, new int[]{4, 10, 8, 4, 0},
            12, 3.0F, 0.125F, 45, false, SoundEvents.ARMOR_EQUIP_NETHERITE, null, "blue_diamond");

    // ── 黑曜石 ──
    public static final MutMaterial OBSIDIAN = new MutMaterial(
            "obsidian", Rarity.COMMON, true, CraftingType.SMITHING,
            3, 9.0F,
            6.5F, 5.0F, 4.5F, 8.5F, 1.75F,
            -2.5F, -3.1F, -2.9F, -3.1F, -0.1F,
            1, 2650, () -> Ingredient.of(Items.OBSIDIAN), null,
            true, new int[]{3, 8, 6, 3, 0},
            1, 2.0F, 0.075F, 40, false, SoundEvents.ARMOR_EQUIP_NETHERITE, null, "obsidian");

    // ── 下界合金黑曜石 ──
    public static final MutMaterial NETHERITE_OBSIDIAN = new MutMaterial(
            "netherite_obsidian", Rarity.COMMON, true, CraftingType.SMITHING,
            4, 10.0F,
            8.0F, 6.5F, 6.0F, 10.0F, 2.5F,
            -2.6F, -3.2F, -3.0F, -3.2F, -0.2F,
            1, 3650, () -> Ingredient.of(new ItemStack(MutModItems.OBSIDIAN_INGOT.get())), null,
            true, new int[]{4, 10, 7, 4, 0},
            1, 4.0F, 0.15F, 60, false, SoundEvents.ARMOR_EQUIP_NETHERITE, null, "netherite_obsidian");

    // ── 悲悯黑曜石 ──
    public static final MutMaterial CRYING_OBSIDIAN = new MutMaterial(
            "crying_obsidian", Rarity.EPIC, true, CraftingType.SMITHING,
            4, 11.0F,
            11.0F, 9.5F, 9.0F, 13.0F, 4.0F,
            -2.8F, -3.4F, -3.2F, -3.4F, -0.4F,
            1, 5650, () -> Ingredient.of(new ItemStack(MutModItems.CRYING_OBSIDIAN_INGOT.get())), null,
            true, new int[]{5, 12, 10, 5, 0},
            1, 5.0F, 0.2F, 100, false, SoundEvents.ARMOR_EQUIP_NETHERITE, null, "crying_obsidian");

    // ── 龙 ──
    public static final MutMaterial DRAGON = new MutMaterial(
            "dragon", Rarity.EPIC, true, CraftingType.SMITHING,
            4, 15.0F,
            13.0F, 11.5F, 11.0F, 15.0F, 5.0F,
            -2.4F, -3.0F, -2.8F, -3.0F, 0.0F,
            30, 5888, () -> Ingredient.of(new ItemStack(MutModItems.DRAGON_SCALE_CLUSTER.get())), null,
            true, new int[]{5, 11, 9, 5, 0},
            30, 4.0F, 0.1F, 68, false, SoundEvents.ARMOR_EQUIP_NETHERITE, null, "dragon");

    // ── 下界之星 ──
    public static final MutMaterial NETHER_STAR = new MutMaterial(
            "nether_star", Rarity.EPIC, true, CraftingType.SMITHING,
            4, 14.0F,
            10.0F, 8.5F, 8.0F, 12.0F, 3.5F,
            -2.4F, -3.0F, -2.8F, -3.0F, 0.0F,
            22, 3000, () -> Ingredient.of(Items.NETHER_STAR), null,
            true, new int[]{3, 8, 6, 3, 0},
            22, 10.0F, 0.1F, 88, false, SoundEvents.ARMOR_EQUIP_NETHERITE, null, "nether_star");

    // ── 下界合金红石 ──
    public static final MutMaterial NETHERITE_REDSTONE = new MutMaterial(
            "netherite_redstone", Rarity.COMMON, true, CraftingType.SMITHING,
            4, 12.0F,
            7.0F, 9.0F, 5.0F, 5.5F, 2.0F,
            -2.0F, -2.6F, -2.4F, -2.6F, 0.4F,
            15, 2031, () -> Ingredient.of(new ItemStack(MutModItems.NETHERITE_REDSTONE_INGOT.get())), null,
            true, new int[]{3, 8, 6, 3, 0},
            20, 3.0F, 0.1F, 39, false, SoundEvents.ARMOR_EQUIP_NETHERITE, null, "netherite_redstone");

    // ── 绿宝石 ──
    public static final MutMaterial EMERALD = new MutMaterial(
            "emerald", Rarity.COMMON, false, CraftingType.NORMAL,
            3, 7.0F,
            6.0F, 8.0F, 4.0F, 4.5F, 1.5F,
            -2.4F, -3.0F, -2.8F, -3.0F, 0.0F,
            20, 888, () -> Ingredient.of(Items.EMERALD), null,
            true, new int[]{3, 8, 6, 3, 0},
            9, 1.0F, 0.0F, 30, false, SoundEvents.ARMOR_EQUIP_DIAMOND, null, "emerald");

    // ── 下界合金绿宝石 ──
    public static final MutMaterial NETHERITE_EMERALD = new MutMaterial(
            "netherite_emerald", Rarity.COMMON, true, CraftingType.SMITHING,
            4, 12.0F,
            8.0F, 6.5F, 6.0F, 10.0F, 2.5F,
            -2.4F, -3.0F, -2.8F, -3.0F, 0.0F,
            20, 1888, () -> Ingredient.of(new ItemStack(MutModItems.NETHERITE_EMERALD_INGOT.get())), null,
            true, new int[]{4, 9, 8, 4, 0},
            9, 3.0F, 0.1F, 40, false, SoundEvents.ARMOR_EQUIP_NETHERITE, null, "netherite_emerald");

    // ── 紫水晶 ──
    public static final MutMaterial AMETHYST = new MutMaterial(
            "amethyst", Rarity.COMMON, false, CraftingType.NORMAL,
            2, 7.0F,
            5.5F, 4.0F, 3.5F, 8F, 1.75F,
            -2.4F, -3.0F, -2.8F, -3.0F, 0.0F,
            16, 350, () -> Ingredient.of(Items.AMETHYST_SHARD), null,
            true, new int[]{2, 6, 5, 2, 5}, 16, 0.5F, 0.0F, 21, false,
            SoundEvents.ARMOR_EQUIP_IRON, null, "amethyst");
    public static final MutMaterial NETHERITE_AMETHYST = new MutMaterial(
            "netherite_amethyst", Rarity.UNCOMMON, true, CraftingType.SMITHING,
            4, 10.0F,
            7.5F, 6.0F, 5.5F, 9.5F, 2.25F,
            -2.4F, -3.0F, -2.8F, -3.0F, 0.0F,
            16, 1400,
            () -> Ingredient.of(MutModItems.NETHERITE_AMETHYST_INGOT), null,
            true, new int[]{3, 8, 6, 3, 0}, 14, 3.5F, 0.1F, 42, false,
            SoundEvents.ARMOR_EQUIP_NETHERITE, null,
            "netherite_amethyst"
    );
}