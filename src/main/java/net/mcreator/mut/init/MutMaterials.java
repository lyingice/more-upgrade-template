package net.mcreator.mut.init;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
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
        public final boolean hasTools;
        public final boolean hasArmor;
        public final int[] armorValues;
        public final int armorEnchantmentValue;
        public final float armorToughness;
        public final float armorKnockbackResistance;
        public final int armorDurability;
        public final boolean piglinNeutral;
        public final Supplier<Holder<SoundEvent>> equipSound;  // 改为 Supplier 延迟加载
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
                int armorDurability, boolean piglinNeutral,
                String equipSoundId,  // 改为 String 类型（音效ID）
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
            this.armorDurability = armorDurability;
            this.piglinNeutral = piglinNeutral;
            // 延迟加载：只在调用时才获取 Holder
            this.equipSound = () -> {
                ResourceLocation location = ResourceLocation.parse(equipSoundId);
                SoundEvent soundEvent = BuiltInRegistries.SOUND_EVENT.get(location);
                if (soundEvent == null) {
                    soundEvent = SoundEvent.createVariableRangeEvent(location);
                }
                return Holder.direct(soundEvent);
            };
            this.armorAttributes = armorAttributes;
            this.textureFolder = textureFolder;
        }

        public int helmetValue()     { return hasArmor ? armorValues[0] : 0; }
        public int chestplateValue() { return hasArmor ? armorValues[1] : 0; }
        public int leggingsValue()   { return hasArmor ? armorValues[2] : 0; }
        public int bootsValue()      { return hasArmor ? armorValues[3] : 0; }
        public int bodyValue()       { return hasArmor ? armorValues[4] : 0; }
        public int armorDurabilityMultiplier() { return armorDurability; }

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

        public Tier asToolTier(ToolType type) {
            MutMaterial m = this;
            TagKey<Block> incorrectTag = switch (m.miningLevel) {
                case 0 -> BlockTags.INCORRECT_FOR_WOODEN_TOOL;
                case 1 -> BlockTags.INCORRECT_FOR_STONE_TOOL;
                case 2 -> BlockTags.INCORRECT_FOR_IRON_TOOL;
                case 3 -> BlockTags.INCORRECT_FOR_DIAMOND_TOOL;
                case 4 -> BlockTags.INCORRECT_FOR_NETHERITE_TOOL;
                default -> BlockTags.INCORRECT_FOR_IRON_TOOL;
            };

            return new Tier() {
                @Override public int getUses() { return m.durability; }
                @Override public float getSpeed() { return m.toolSpeed; }
                @Override public float getAttackDamageBonus() { return m.getDamageFor(type); }
                @Override public TagKey<Block> getIncorrectBlocksForDrops() { return incorrectTag; }
                @Override public int getEnchantmentValue() { return m.enchantmentValue; }
                @Override public Ingredient getRepairIngredient() { return m.repairIngredient.get(); }
            };
        }
        public Item.Properties createToolProperties(ToolType type) {
            Item.Properties props = new Item.Properties()
                    .rarity(this.rarity)  // 稀有度
                    .attributes(createToolAttributes(this, type));
            if (this.fireResistant) {
                props.fireResistant();
            }
            return props;
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
                    armorEnchantmentValue,
                    equipSound.get(),  // 延迟加载，调用 .get()
                    () -> repairIngredient.get(),
                    List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath("mut", textureFolder))),
                    armorToughness,
                    armorKnockbackResistance
            );
        }
        // 在 MutMaterials.MutMaterial 类中添加
        public Item.Properties createArmorProperties(ArmorItem.Type armorType) {
            int durability = this.armorDurability;  // 从材质中获取倍数
            Item.Properties props = new Item.Properties()
                    .rarity(this.rarity)
                    .durability(armorType.getDurability(durability))
                    .attributes(createArmorAttributes(this, armorType));
            if (this.fireResistant) {
                props.fireResistant();
            }
            return props;
        }
    }

    // ========== 配置存储 ==========
    private static final List<MutMaterial> ALL_MATERIALS = new ArrayList<>();
    public static List<MutMaterial> getAll() { return Collections.unmodifiableList(ALL_MATERIALS); }
    private static final Map<String, MutMaterial> MATERIALS_BY_NAME = new HashMap<>();
    private static MutMaterial register(MutMaterial material) {
        ALL_MATERIALS.add(material);
        MATERIALS_BY_NAME.put(material.name, material);
        return material;
    }
    public static MutMaterial get(String name) {
        MutMaterial material = MATERIALS_BY_NAME.get(name);
        if (material == null) {
            throw new IllegalArgumentException("Unknown material: " + name);
        }
        return material;
    }
    // ========== 材质定义（使用字符串音效ID）==========

    // 紫水晶
    public static final MutMaterial AMETHYST = register(new MutMaterial(
            "amethyst", Rarity.COMMON, false, CraftingType.NORMAL,
            2, 7.0F,
            5.5F, 4.0F, 3.5F, 8.0F, 1.25F,
            -2.4F, -3.0F, -2.8F, -3.1F, 0.0F,
            16, 350,
            () -> Ingredient.of(Items.AMETHYST_SHARD),
            null,
            true, true,
            new int[]{2, 6, 5, 2, 0},
            14, 0.5F, 0.0F, 21, false,
            "minecraft:item.armor.equip_iron",
            null,
            "amethyst"
    ));

    // 下界合金紫水晶
    public static final MutMaterial NETHERITE_AMETHYST = register(new MutMaterial(
            "netherite_amethyst", Rarity.UNCOMMON, true, CraftingType.SMITHING,
            4, 10.0F,
            7.5F, 6.0F, 5.5F, 9.5F, 2.25F,
            -2.4F, -3.0F, -2.8F, -3.0F, 0.0F,
            16, 1400,
            () -> Ingredient.of(MutModItems.NETHERITE_AMETHYST_INGOT.get()),
            null,
            true, true,
            new int[]{3, 8, 6, 3, 0},
            14, 3.5F, 0.1F, 42, false,
            "minecraft:item.armor.equip_netherite",
            null,
            "netherite_amethyst"
    ));

    // 凋零
    public static final MutMaterial WITHER = register(new MutMaterial(
            "wither", Rarity.EPIC, true, CraftingType.SMITHING,
            4, 14.0F,
            11.0F, 9.5F, 9.0F, 13.0F, 4.5F,
            -2.2F, -2.8F, -2.6F, -2.8F, 0.2F,
            22, 3000,
            () -> Ingredient.of(Items.NETHER_STAR),
            null,
            true, true,
            new int[]{4, 9, 8, 4, 0},
            22, 6.0F, 0.1F, 88, false,
            "minecraft:item.armor.equip_netherite",
            null,
            "wither"
    ));

    public static final MutMaterial SUPER_NETHERITE = register(new MutMaterial(
            "super_netherite", Rarity.UNCOMMON, true, CraftingType.SMITHING,
            4, 14.0F,
            0.0F, 0.0F, 0.0F, 0.0F, 0.0F,
            -2.4F, -3.0F, -2.8F, -3F, -3F,
            15, 3031,
            () -> Ingredient.of(Items.NETHER_STAR),
            null,
            false, true,
            new int[]{4, 10, 7, 3, 0},
            15, 5.0F, 0.2F, 50, false,
            "minecraft:item.armor.equip_netherite",
            null,
            "super_netherite"
    ));

    // ========== 属性构建器 ==========

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