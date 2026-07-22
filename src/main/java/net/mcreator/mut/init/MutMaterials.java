package net.mcreator.mut.init;

import com.google.gson.JsonObject;
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
    // ========== 数据包配置支持 ==========

    /**
     * 应用数据包配置，覆盖现有材质
     * 如果 JSON 中缺少某些字段，则保持原值
     */
    public static void applyDataPackConfigs(Map<String, JsonObject> configs) {
        for (var entry : configs.entrySet()) {
            String name = entry.getKey();
            JsonObject obj = entry.getValue();
            MutMaterial material = MATERIALS_BY_NAME.get(name);

            if (material == null) {
                System.err.println("[MutMaterials] Cannot apply config: material " + name + " not found");
                continue;
            }

            // 使用反射或创建新对象替换
            // 由于 MutMaterial 字段是 final 的，需要创建一个新对象并替换
            MutMaterial newMaterial = createMaterialFromJson(name, material, obj);
            if (newMaterial != null) {
                // 替换材质
                int index = ALL_MATERIALS.indexOf(material);
                if (index >= 0) {
                    ALL_MATERIALS.set(index, newMaterial);
                }
                MATERIALS_BY_NAME.put(name, newMaterial);
            }
        }
    }

    /**
     * 从 JSON 配置创建新的 MutMaterial（基于原有材质，用 JSON 覆盖）
     */
    private static MutMaterial createMaterialFromJson(String name, MutMaterial original, JsonObject obj) {
        try {
            // 解析 JSON 中的值，如果不存在则使用原值
            Rarity rarity = obj.has("rarity") ? Rarity.valueOf(obj.get("rarity").getAsString()) : original.rarity;
            boolean fireResistant = obj.has("fire_resistant") ? obj.get("fire_resistant").getAsBoolean() : original.fireResistant;
            CraftingType craftingType = obj.has("crafting_type") ?
                    CraftingType.valueOf(obj.get("crafting_type").getAsString()) : original.craftingType;
            int miningLevel = obj.has("mining_level") ? obj.get("mining_level").getAsInt() : original.miningLevel;
            float toolSpeed = obj.has("tool_speed") ? obj.get("tool_speed").getAsFloat() : original.toolSpeed;

            // 工具伤害
            float swordDamage = obj.has("sword_damage") ? obj.get("sword_damage").getAsFloat() : original.swordDamage;
            float shovelDamage = obj.has("shovel_damage") ? obj.get("shovel_damage").getAsFloat() : original.shovelDamage;
            float pickaxeDamage = obj.has("pickaxe_damage") ? obj.get("pickaxe_damage").getAsFloat() : original.pickaxeDamage;
            float axeDamage = obj.has("axe_damage") ? obj.get("axe_damage").getAsFloat() : original.axeDamage;
            float hoeDamage = obj.has("hoe_damage") ? obj.get("hoe_damage").getAsFloat() : original.hoeDamage;

            // 工具攻速
            float swordSpeed = obj.has("sword_speed") ? obj.get("sword_speed").getAsFloat() : original.swordSpeed;
            float shovelSpeed = obj.has("shovel_speed") ? obj.get("shovel_speed").getAsFloat() : original.shovelSpeed;
            float pickaxeSpeed = obj.has("pickaxe_speed") ? obj.get("pickaxe_speed").getAsFloat() : original.pickaxeSpeed;
            float axeSpeed = obj.has("axe_speed") ? obj.get("axe_speed").getAsFloat() : original.axeSpeed;
            float hoeSpeed = obj.has("hoe_speed") ? obj.get("hoe_speed").getAsFloat() : original.hoeSpeed;

            int enchantmentValue = obj.has("enchantment_value") ? obj.get("enchantment_value").getAsInt() : original.enchantmentValue;
            int durability = obj.has("durability") ? obj.get("durability").getAsInt() : original.durability;

            // 盔甲属性
            boolean hasTools = obj.has("has_tools") ? obj.get("has_tools").getAsBoolean() : original.hasTools;
            boolean hasArmor = obj.has("has_armor") ? obj.get("has_armor").getAsBoolean() : original.hasArmor;

            int[] armorValues = original.armorValues.clone();
            if (obj.has("armor_helmet")) armorValues[0] = obj.get("armor_helmet").getAsInt();
            if (obj.has("armor_chestplate")) armorValues[1] = obj.get("armor_chestplate").getAsInt();
            if (obj.has("armor_leggings")) armorValues[2] = obj.get("armor_leggings").getAsInt();
            if (obj.has("armor_boots")) armorValues[3] = obj.get("armor_boots").getAsInt();
            if (obj.has("armor_body")) armorValues[4] = obj.get("armor_body").getAsInt();

            int armorEnchantmentValue = obj.has("armor_enchantment_value") ? obj.get("armor_enchantment_value").getAsInt() : original.armorEnchantmentValue;
            float armorToughness = obj.has("armor_toughness") ? obj.get("armor_toughness").getAsFloat() : original.armorToughness;
            float armorKnockbackResistance = obj.has("armor_knockback_resistance") ? obj.get("armor_knockback_resistance").getAsFloat() : original.armorKnockbackResistance;
            int armorDurability = obj.has("armor_durability") ? obj.get("armor_durability").getAsInt() : original.armorDurability;

            String equipSoundId = obj.has("equip_sound") ? obj.get("equip_sound").getAsString() : original.equipSound.get().value().getLocation().toString();
            String textureFolder = obj.has("texture_folder") ? obj.get("texture_folder").getAsString() : original.textureFolder;

            // 创建新的 MutMaterial
            return new MutMaterial(
                    name, rarity, fireResistant, craftingType,
                    miningLevel, toolSpeed,
                    swordDamage, shovelDamage, pickaxeDamage, axeDamage, hoeDamage,
                    swordSpeed, shovelSpeed, pickaxeSpeed, axeSpeed, hoeSpeed,
                    enchantmentValue, durability,
                    original.repairIngredient,
                    original.toolAttributes,
                    hasTools, hasArmor, armorValues,
                    armorEnchantmentValue, armorToughness, armorKnockbackResistance,
                    armorDurability, original.piglinNeutral,
                    equipSoundId, original.armorAttributes, textureFolder
            );
        } catch (Exception e) {
            System.err.println("[MutMaterials] Failed to create material from config: " + name);
            e.printStackTrace();
            return null;
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
    public static final MutMaterial LAPIS_LAZULI = register(new MutMaterial(
            "lapis_lazuli",Rarity.COMMON,true,CraftingType.NORMAL,
            1,8,3.5F,3.0F,2.5F,6.5F,0.75F,
            -2.4F,-3.0F,-2.8F,-3F,0F,30,200,() -> Ingredient.of(Items.LAPIS_LAZULI),
            null,true,true,new int[]{2,4,3,1,0},30,0,0F,11,false,
            "minecraft:item.armor.equip_iron",null,"lapis_lazuli"
    ));
    public static final MutMaterial NETHERITE_LAPIS_LAZULI = register(new MutMaterial(
            "netherite_lapis_lazuli",Rarity.COMMON,true,CraftingType.SMITHING,
            4,8,6.5F,5.0F,4.5F,8.5F,1.75F,
            -2.4F,-3.0F,-2.8F,-3F,0F,60,1000,() -> Ingredient.of(MutModItems.NETHERITE_LAPIS_LAZULI_INGOT),
            null,true,true,new int[]{3,8,6,3,0},60,2,0.1F,45,false,
            "minecraft:item.armor.equip_netherite",null,"netherite_lapis_lazuli"
    ));
    public static final MutMaterial POSITION_STEEL = register(new MutMaterial(
            "position_steel",Rarity.UNCOMMON,true,CraftingType.SMITHING,
            4,10,7.0F,5.5F,5F,9F,2F,
            -2.2F,-2.8F,-2.6F,-2.8F,0.2F,15,2031,() -> Ingredient.of(MutModItems.POSITION_STEEL_INGOT),
            null,true,true,new int[]{4,9,8,4,0},9,4,0.1F,45,false,
            "minecraft:item.armor.equip_netherite",null,"position_steel"
    ));
    public static final MutMaterial FLAME_GOLD = register(new MutMaterial(
            "flame_gold",Rarity.UNCOMMON,true,CraftingType.SMITHING,
            4,15,7.0F,5.5F,5F,9F,2F,
            -2.2F,-2.8F,-2.6F,-2.8F,0.2F,25,2031,() -> Ingredient.of(MutModItems.FLAME_GOLD_INGOT),
            null,true,true,new int[]{4,9,8,4,0},25,4,0.1F,43,true,
            "minecraft:item.armor.equip_netherite",null,"flame_gold"
    ));
    public static final MutMaterial ECHOITE = register(new MutMaterial(
            "echoite",Rarity.EPIC,true,CraftingType.SMITHING,
            4,11,9F,7.5F,7F,11F,4F,
            -2.5F,-3.1F,-2.9F,-3.1F,-0.1F,30,4062,() -> Ingredient.of(MutModItems.ECHOITE_INGOT),
            null,true,true,new int[]{4,10,9,4,0},30,6,0.2F,59,false,
            "minecraft:item.armor.equip_netherite",null,"echoite"
    ));
    public static final MutMaterial UNCANNY_AMETHYST = register(new MutMaterial(
            "uncanny_amethyst",Rarity.UNCOMMON,true,CraftingType.SMITHING,
            4,13,9.5F,8F,7.5F,11.5F,3.25F,
            -2.3F,-2.9F,-2.7F,-2.9F,0.1F,16,2800,() -> Ingredient.of(MutModItems.NETHERITE_AMETHYST_INGOT),
            null,true,true,new int[]{4,9,8,4,0},14,4,0.1F,69,false,
            "minecraft:item.armor.equip_netherite",null,"uncanny_amethyst"
    ));
    public static final MutMaterial THUNDER_COPPER = register(new MutMaterial(
            "thunder_copper",Rarity.UNCOMMON,true,CraftingType.SMITHING,
            4,12,8F,6.F,6F,10F,3.5F,
            -2.1F,-2.7F,-2.5F,-2.7F,0.3F,1,2031,() -> Ingredient.EMPTY,
            null,true,true,new int[]{4,9,8,4,0},1,4,0.1F,51,false,
            "minecraft:item.armor.equip_netherite",null,"thunder_copper"
    ));
    /**
     public static final MutMaterial XX = register(new MutMaterial(
     "xx",Rarity.COMMON,true,CraftingType.SMITHING,
     4,10,0F,0F,0F,0F,0F,
     -2.4F,-3F,-2.8F,-3F,0F,10,1000,() -> Ingredient.of(MutModItems.XX),
     null,true,true,new int[]{3,8,6,3,0},25,4,0.1F,43,false,
     "minecraft:item.armor.equip_netherite",null,"xx"
     ));
    **/
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

        String partName = type.getName(); // "helmet", "chestplate", "leggings", "boots", "body"

        // 盔甲值 - 这个已经是部件区分的，没问题
        if (val > 0)
            b.add(Attributes.ARMOR,
                    new AttributeModifier(ResourceLocation.fromNamespaceAndPath("mut", mat.name + "_armor_" + partName),
                            val, AttributeModifier.Operation.ADD_VALUE),
                    EquipmentSlotGroup.bySlot(type.getSlot()));

        // 盔甲韧性 - 修正：加入部件名区分
        if (mat.armorToughness > 0)
            b.add(Attributes.ARMOR_TOUGHNESS,
                    new AttributeModifier(ResourceLocation.fromNamespaceAndPath("mut", mat.name + "_toughness_" + partName),
                            mat.armorToughness, AttributeModifier.Operation.ADD_VALUE),
                    EquipmentSlotGroup.bySlot(type.getSlot()));

        // 击退抗性 - 修正：加入部件名区分
        if (mat.armorKnockbackResistance > 0)
            b.add(Attributes.KNOCKBACK_RESISTANCE,
                    new AttributeModifier(ResourceLocation.fromNamespaceAndPath("mut", mat.name + "_knockback_" + partName),
                            mat.armorKnockbackResistance, AttributeModifier.Operation.ADD_VALUE),
                    EquipmentSlotGroup.bySlot(type.getSlot()));

        // 额外属性（如果有的话也需要检查是否区分了部件）
        if (mat.armorAttributes != null)
            mat.armorAttributes.modifiers().forEach(e -> b.add(e.attribute(), e.modifier(), e.slot()));

        // 从 MutMoreAttributeMaterials 获取的额外属性
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