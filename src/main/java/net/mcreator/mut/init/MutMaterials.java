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

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;

/**
 * 材质数据存储中心
 */
public class MutMaterials {

    public enum CraftingType { NORMAL, SMITHING }

    public enum ToolType { SWORD, SHOVEL, PICKAXE, AXE, HOE }

    // ========== 材质配置类 ==========
    public static class MutMaterial {
        public final String name;
        public final Rarity rarity;
        public final boolean fireResistant;
        public final CraftingType craftingType;
        public final int miningLevel;
        public final float toolSpeed;
        public final float swordDamage, shovelDamage, pickaxeDamage, axeDamage, hoeDamage;
        public final float swordSpeed, shovelSpeed, pickaxeSpeed, axeSpeed, hoeSpeed;
        public final int enchantmentValue;
        public final int durability;
        public final Supplier<Ingredient> repairIngredient;
        @Nullable public final ItemAttributeModifiers toolAttributes;
        public final boolean hasTools;           // ⬅️ 新增：是否生成工具
        public final boolean hasArmor;
        public final int[] armorValues;
        public final int armorEnchantmentValue;
        public final float armorToughness;
        public final float armorKnockbackResistance;
        public final int armorDurabilityFactor;
        public final boolean piglinNeutral;
        public final Holder<SoundEvent> equipSound;
        @Nullable public final ItemAttributeModifiers armorAttributes;
        public final String textureFolder;

        public MutMaterial(
                String name, Rarity rarity, boolean fireResistant, CraftingType craftingType,
                int miningLevel, float toolSpeed,
                float swordDamage, float shovelDamage, float pickaxeDamage, float axeDamage, float hoeDamage,
                float swordSpeed, float shovelSpeed, float pickaxeSpeed, float axeSpeed, float hoeSpeed,
                int enchantmentValue, int durability,
                Supplier<Ingredient> repairIngredient,
                @Nullable ItemAttributeModifiers toolAttributes,
                boolean hasTools, boolean hasArmor, int[] armorValues,
                int armorEnchantmentValue, float armorToughness, float armorKnockbackResistance,
                int armorDurabilityFactor, boolean piglinNeutral,
                Holder<SoundEvent> equipSound,
                @Nullable ItemAttributeModifiers armorAttributes,
                String textureFolder
        ) {
            this.name = name;
            this.rarity = rarity;
            this.fireResistant = fireResistant;
            this.craftingType = craftingType;
            this.miningLevel = miningLevel;
            this.toolSpeed = toolSpeed;
            this.swordDamage = swordDamage;
            this.shovelDamage = shovelDamage;
            this.pickaxeDamage = pickaxeDamage;
            this.axeDamage = axeDamage;
            this.hoeDamage = hoeDamage;
            this.swordSpeed = swordSpeed;
            this.shovelSpeed = shovelSpeed;
            this.pickaxeSpeed = pickaxeSpeed;
            this.axeSpeed = axeSpeed;
            this.hoeSpeed = hoeSpeed;
            this.enchantmentValue = enchantmentValue;
            this.durability = durability;
            this.repairIngredient = repairIngredient;
            this.toolAttributes = toolAttributes;
            this.hasTools = hasTools;
            this.hasArmor = hasArmor;
            this.armorValues = armorValues != null ? armorValues.clone() : new int[]{0,0,0,0,0};
            this.armorEnchantmentValue = armorEnchantmentValue;
            this.armorToughness = armorToughness;
            this.armorKnockbackResistance = armorKnockbackResistance;
            this.armorDurabilityFactor = armorDurabilityFactor;
            this.piglinNeutral = piglinNeutral;
            this.equipSound = equipSound;
            this.armorAttributes = armorAttributes;
            this.textureFolder = textureFolder;
        }

        public int helmetValue()     { return hasArmor ? armorValues[0] : 0; }
        public int chestplateValue() { return hasArmor ? armorValues[1] : 0; }
        public int leggingsValue()   { return hasArmor ? armorValues[2] : 0; }
        public int bootsValue()      { return hasArmor ? armorValues[3] : 0; }
        public int bodyValue()       { return hasArmor ? armorValues[4] : 0; }
        public int armorDurabilityMultiplier() { return armorDurabilityFactor; }

        public float getDamageFor(ToolType type) {
            return switch (type) {
                case SWORD -> swordDamage;
                case SHOVEL -> shovelDamage;
                case PICKAXE -> pickaxeDamage;
                case AXE -> axeDamage;
                case HOE -> hoeDamage;
            };
        }

        public float getSpeedFor(ToolType type) {
            return switch (type) {
                case SWORD -> swordSpeed;
                case SHOVEL -> shovelSpeed;
                case PICKAXE -> pickaxeSpeed;
                case AXE -> axeSpeed;
                case HOE -> hoeSpeed;
            };
        }

        public Tier asToolTier() {
            MutMaterial m = this;
            return new Tier() {
                @Override public int getUses() { return m.durability; }
                @Override public float getSpeed() { return m.toolSpeed; }
                @Override public float getAttackDamageBonus() { return m.swordDamage; }
                @Override public int getEnchantmentValue() { return m.enchantmentValue; }
                @Override public Ingredient getRepairIngredient() { return m.repairIngredient.get(); }
                @Override public TagKey<Block> getIncorrectBlocksForDrops() { return null; }
            };
        }

        public Tier asToolTier(ToolType type) {
            MutMaterial m = this;
            return new Tier() {
                @Override public int getUses() { return m.durability; }
                @Override public float getSpeed() { return m.toolSpeed; }
                @Override public float getAttackDamageBonus() { return m.getDamageFor(type); }
                @Override public int getEnchantmentValue() { return m.enchantmentValue; }
                @Override public Ingredient getRepairIngredient() { return m.repairIngredient.get(); }
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
                    armorEnchantmentValue, equipSound,
                    () -> repairIngredient.get(),
                    List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath("mut", textureFolder))),
                    armorToughness, armorKnockbackResistance
            );
        }
    }

    // ========== 配置存储 ==========
    private static final List<MutMaterial> ALL_MATERIALS = new ArrayList<>();
    public static List<MutMaterial> getAll() { return Collections.unmodifiableList(ALL_MATERIALS); }

    private static MutMaterial register(MutMaterial material) {
        ALL_MATERIALS.add(material);
        return material;
    }

    // ========== 材质定义 ==========

    // 铜（有工具，有盔甲）
    public static final MutMaterial COPPER = register(new MutMaterial(
            "copper", Rarity.COMMON, false, CraftingType.NORMAL,
            1, 5.0F,
            4.0F, 2.0F, 8.0F, 2.5F, 0.5F,
            -2.4F, -2.8F, -3.1F, -3.0F, 0.0F,
            13, 190, () -> Ingredient.of(Items.COPPER_INGOT), null,
            true,  // hasTools = true
            true,  // hasArmor = true
            new int[]{2, 4, 3, 1, 5},
            8, 0.0F, 0.0F, 11, false, SoundEvents.ARMOR_EQUIP_IRON, null, "copper"
    ));

    // 哭曜黑曜石（有盔甲，无工具）
    public static final MutMaterial CRYING_OBSIDIAN = register(new MutMaterial(
            "crying_obsidian", Rarity.EPIC, true, CraftingType.SMITHING,
            4, 11.0F,
            11.0F, 9.5F, 9.0F, 13.0F, 4.0F,
            -2.8F, -3.4F, -3.2F, -3.4F, -0.4F,
            1, 5650, () -> Ingredient.of(new ItemStack(MutModItems.CRYING_OBSIDIAN_INGOT.get())), null,
            false, // hasTools = false（不生成工具）
            true,  // hasArmor = true
            new int[]{5, 12, 10, 5, 0},
            1, 5.0F, 0.2F, 100, false, SoundEvents.ARMOR_EQUIP_NETHERITE, null, "crying_obsidian"
    ));

    // 木头（有工具，无盔甲）
    public static final MutMaterial WOOD = register(new MutMaterial(
            "wood", Rarity.COMMON, false, CraftingType.NORMAL,
            0, 2.0F,
            3.0F, 1.5F, 2.0F, 7.0F, 0.0F,
            -2.4F, -3.0F, -2.8F, -3.2F, 0.0F,
            15, 59, () -> Ingredient.of(Items.OAK_PLANKS), null,
            true,  // hasTools = true
            false,  // hasArmor = false
            new int[]{0, 0, 0, 0, 0},
            0, 0.0F, 0.0F, 0, false, SoundEvents.ARMOR_EQUIP_GENERIC, null, "wood"
    ));

    // 紫水晶 - 工具 + 盔甲
    public static final MutMaterial AMETHYST = register(new MutMaterial(
            "amethyst", Rarity.COMMON, false, CraftingType.NORMAL,
            2, 7.0F,  // 挖掘等级2（铁器），挖掘效率7.0
            6.5F, 5.0F, 4.5F, 9.0F, 2.25F,  // 剑/锹/镐/斧/锄 伤害
            -2.4F, -3.0F, -2.8F, -3.1F, 0.0F,  // 剑/锹/镐/斧/锄 攻速
            16, 350,  // 附魔能力16，耐久350
            () -> Ingredient.of(Items.AMETHYST_SHARD),  // 修复材料：紫水晶碎片
            null,  // 工具额外属性
            true,  // hasTools = true
            true,  // hasArmor = true
            new int[]{2, 6, 5, 2, 0},  // 头盔/胸甲/护腿/靴子/身体 护甲值
            14,  // 盔甲附魔能力14
            0.5F,  // 盔甲韧性0.5
            0.0F,  // 击退抗性0
            21,  // 盔甲耐久倍率21
            false,  // 猪灵中立 false
            SoundEvents.ARMOR_EQUIP_IRON,  // 装备音效：铁
            null,  // 盔甲额外属性
            "amethyst"  // 纹理文件夹
    ));

    // 下界合金紫水晶 - 工具 + 盔甲
    public static final MutMaterial NETHERITE_AMETHYST = register(new MutMaterial(
            "netherite_amethyst", Rarity.UNCOMMON, true, CraftingType.SMITHING,
            4, 10.0F,  // 挖掘等级4（下界合金），挖掘效率10.0
            8.5F, 7.0F, 6.5F, 10.5F, 3.25F,  // 剑/锹/镐/斧/锄 伤害
            -2.4F, -3.0F, -2.8F, -3.0F, 0.0F,  // 剑/锹/镐/斧/锄 攻速
            16, 1400,  // 附魔能力16，耐久1400
            () -> Ingredient.of(MutModItems.NETHERITE_AMETHYST_INGOT.get()),  // 修复材料：下界合金紫水晶锭
            null,  // 工具额外属性
            true,  // hasTools = true
            true,  // hasArmor = true
            new int[]{3, 8, 6, 3, 0},  // 头盔/胸甲/护腿/靴子/身体 护甲值
            14,  // 盔甲附魔能力14
            3.5F,  // 盔甲韧性3.5
            0.1F,  // 击退抗性0.1
            42,  // 盔甲耐久倍率42
            false,  // 猪灵中立 false
            SoundEvents.ARMOR_EQUIP_NETHERITE,  // 装备音效：下界合金
            null,  // 盔甲额外属性
            "netherite_amethyst"  // 纹理文件夹
    ));

    // ========== 属性构建器（保持与 MutMoreAttributeMaterials 兼容）==========

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

        if (mat.toolAttributes != null)
            mat.toolAttributes.modifiers().forEach(e -> b.add(e.attribute(), e.modifier(), e.slot()));

        var extra = MutMoreAttributeMaterials.buildModifiers(mat.name,
                MutMoreAttributeMaterials.getToolAttributes(mat.name));
        if (extra != null)
            extra.modifiers().forEach(e -> b.add(e.attribute(), e.modifier(), e.slot()));

        return b.build();
    }

    public static ItemAttributeModifiers createArmorAttributes(MutMaterial mat, ArmorItem.Type type) {
        var b = ItemAttributeModifiers.builder();

        double val = switch (type) {
            case HELMET -> mat.helmetValue();
            case CHESTPLATE -> mat.chestplateValue();
            case LEGGINGS -> mat.leggingsValue();
            case BOOTS -> mat.bootsValue();
            case BODY -> mat.bodyValue();
        };

        if (val > 0)
            b.add(Attributes.ARMOR,
                    new AttributeModifier(ResourceLocation.fromNamespaceAndPath("mut", mat.name + "_armor_" + type.getName()),
                            val, AttributeModifier.Operation.ADD_VALUE),
                    EquipmentSlotGroup.bySlot(type.getSlot()));
        if (mat.armorToughness > 0)
            b.add(Attributes.ARMOR_TOUGHNESS,
                    new AttributeModifier(ResourceLocation.fromNamespaceAndPath("mut", mat.name + "_toughness"),
                            mat.armorToughness, AttributeModifier.Operation.ADD_VALUE),
                    EquipmentSlotGroup.bySlot(type.getSlot()));
        if (mat.armorKnockbackResistance > 0)
            b.add(Attributes.KNOCKBACK_RESISTANCE,
                    new AttributeModifier(ResourceLocation.fromNamespaceAndPath("mut", mat.name + "_knockback"),
                            mat.armorKnockbackResistance, AttributeModifier.Operation.ADD_VALUE),
                    EquipmentSlotGroup.bySlot(type.getSlot()));

        if (mat.armorAttributes != null)
            mat.armorAttributes.modifiers().forEach(e -> b.add(e.attribute(), e.modifier(), e.slot()));

        List<MutMoreAttributeMaterials.AttributeEntry> extraArmor = switch (type) {
            case HELMET -> MutMoreAttributeMaterials.getHelmetAttributes(mat.name);
            case CHESTPLATE -> MutMoreAttributeMaterials.getChestplateAttributes(mat.name);
            case LEGGINGS -> MutMoreAttributeMaterials.getLeggingsAttributes(mat.name);
            case BOOTS -> MutMoreAttributeMaterials.getBootsAttributes(mat.name);
            case BODY -> Collections.emptyList();
        };
        ItemAttributeModifiers extraArmorModifiers = MutMoreAttributeMaterials.buildModifiers(mat.name, extraArmor);
        if (extraArmorModifiers != null)
            extraArmorModifiers.modifiers().forEach(e -> b.add(e.attribute(), e.modifier(), e.slot()));

        return b.build();
    }
}