package net.mcreator.mut.item;

import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Holder;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.Minecraft;
import net.minecraft.Util;

import net.mcreator.mut.init.MutModItems;
import net.mcreator.mut.client.model.ModelCustomModel;

import java.util.*;
import java.util.function.Consumer;

@EventBusSubscriber
public abstract class DragonItem extends ArmorItem {
    public static Holder<ArmorMaterial> ARMOR_MATERIAL = null;

    // 存储所有需要注册模型的物品
    private static final Map<Item, ArmorItem.Type> ITEMS_TO_REGISTER = new HashMap<>();

    @SubscribeEvent
    public static void registerArmorMaterial(RegisterEvent event) {
        event.register(Registries.ARMOR_MATERIAL, registerHelper -> {
            System.out.println("[DragonArmor] Registering armor material...");
            ArmorMaterial armorMaterial = new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.BOOTS, 5);
                map.put(ArmorItem.Type.LEGGINGS, 9);
                map.put(ArmorItem.Type.CHESTPLATE, 11);
                map.put(ArmorItem.Type.HELMET, 5);
                map.put(ArmorItem.Type.BODY, 11);
            }), 30, DeferredHolder.create(Registries.SOUND_EVENT, ResourceLocation.parse("item.armor.equip_netherite")),
                    () -> Ingredient.of(new ItemStack(Items.NETHER_STAR)),
                    List.of(new ArmorMaterial.Layer(ResourceLocation.parse("mut:dragon"))),
                    4f, 0.1f);
            registerHelper.register(ResourceLocation.parse("mut:dragon"), armorMaterial);
            ARMOR_MATERIAL = BuiltInRegistries.ARMOR_MATERIAL.wrapAsHolder(armorMaterial);
        });
    }

    @SubscribeEvent
    public static void registerItemExtensions(RegisterClientExtensionsEvent event) {
        // 遍历所有已注册的龙盔甲物品
        for (Map.Entry<Item, ArmorItem.Type> entry : ITEMS_TO_REGISTER.entrySet()) {
            Item item = entry.getKey();
            ArmorItem.Type armorType = entry.getValue();

            IClientItemExtensions extensions = createExtensionsForType(armorType);
            if (extensions != null) {
                event.registerItem(extensions, item);
                System.out.println("[DragonArmor] Registered extensions for: " + BuiltInRegistries.ITEM.getKey(item));
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static IClientItemExtensions createExtensionsForType(ArmorItem.Type type) {
        return switch (type) {
            case HELMET -> createHelmetExtensions();
            case CHESTPLATE -> createChestplateExtensions();
            case BOOTS -> createBootsExtensions();
            default -> null;
        };
    }

    @OnlyIn(Dist.CLIENT)
    private static IClientItemExtensions createHelmetExtensions() {
        return new IClientItemExtensions() {
            private HumanoidModel armorModel = null;

            @Override
            public HumanoidModel getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel defaultModel) {
                if (armorModel == null) {
                    try {
                        var modelPart = Minecraft.getInstance().getEntityModels().bakeLayer(ModelCustomModel.LAYER_LOCATION);
                        ModelCustomModel customModel = new ModelCustomModel(modelPart);

                        armorModel = new HumanoidModel(new ModelPart(Collections.emptyList(),
                                Map.of("head", customModel.Head,
                                        "hat", new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                                        "body", new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                                        "right_arm", new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                                        "left_arm", new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                                        "right_leg", new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                                        "left_leg", new ModelPart(Collections.emptyList(), Collections.emptyMap()))));
                    } catch (Exception e) {
                        e.printStackTrace();
                        armorModel = defaultModel;
                    }
                }
                armorModel.crouching = living.isShiftKeyDown();
                armorModel.riding = defaultModel.riding;
                armorModel.young = living.isBaby();
                return armorModel;
            }
        };
    }

    @OnlyIn(Dist.CLIENT)
    private static IClientItemExtensions createChestplateExtensions() {
        return new IClientItemExtensions() {
            private HumanoidModel armorModel = null;

            @Override
            public HumanoidModel getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel defaultModel) {
                if (armorModel == null) {
                    try {
                        var modelPart = Minecraft.getInstance().getEntityModels().bakeLayer(ModelCustomModel.LAYER_LOCATION);
                        ModelCustomModel customModel = new ModelCustomModel(modelPart);

                        armorModel = new HumanoidModel(new ModelPart(Collections.emptyList(),
                                Map.of("body", customModel.Body,
                                        "left_arm", customModel.LeftArm,
                                        "right_arm", customModel.RightArm,
                                        "head", new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                                        "hat", new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                                        "right_leg", new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                                        "left_leg", new ModelPart(Collections.emptyList(), Collections.emptyMap()))));
                    } catch (Exception e) {
                        e.printStackTrace();
                        armorModel = defaultModel;
                    }
                }
                armorModel.crouching = living.isShiftKeyDown();
                armorModel.riding = defaultModel.riding;
                armorModel.young = living.isBaby();
                return armorModel;
            }
        };
    }

    @OnlyIn(Dist.CLIENT)
    private static IClientItemExtensions createBootsExtensions() {
        return new IClientItemExtensions() {
            private HumanoidModel armorModel = null;

            @Override
            public HumanoidModel getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel defaultModel) {
                if (armorModel == null) {
                    try {
                        var modelPart = Minecraft.getInstance().getEntityModels().bakeLayer(ModelCustomModel.LAYER_LOCATION);
                        ModelCustomModel customModel = new ModelCustomModel(modelPart);

                        armorModel = new HumanoidModel(new ModelPart(Collections.emptyList(),
                                Map.of("left_leg", customModel.LeftLeg,
                                        "right_leg", customModel.RightLeg,
                                        "head", new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                                        "hat", new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                                        "body", new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                                        "right_arm", new ModelPart(Collections.emptyList(), Collections.emptyMap()),
                                        "left_arm", new ModelPart(Collections.emptyList(), Collections.emptyMap()))));
                    } catch (Exception e) {
                        e.printStackTrace();
                        armorModel = defaultModel;
                    }
                }
                armorModel.crouching = living.isShiftKeyDown();
                armorModel.riding = defaultModel.riding;
                armorModel.young = living.isBaby();
                return armorModel;
            }
        };
    }

    protected DragonItem(ArmorItem.Type type, Item.Properties properties) {
        super(ARMOR_MATERIAL, type, properties);
        // 自动将当前物品添加到注册表
        ITEMS_TO_REGISTER.put(this, type);
    }

    private static ItemAttributeModifiers buildAttributes(ArmorItem.Type type, double healthBonus) {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();

        EquipmentSlotGroup slot = switch (type) {
            case HELMET -> EquipmentSlotGroup.HEAD;
            case CHESTPLATE -> EquipmentSlotGroup.CHEST;
            case LEGGINGS -> EquipmentSlotGroup.LEGS;
            case BOOTS -> EquipmentSlotGroup.FEET;
            default -> EquipmentSlotGroup.ANY;
        };

        double armorValue = switch (type) {
            case HELMET -> 5;
            case CHESTPLATE -> 11;
            case LEGGINGS -> 9;
            case BOOTS -> 5;
            default -> 0;
        };

        builder.add(Attributes.ARMOR,
                new AttributeModifier(ResourceLocation.parse("mut:dragon_armor_" + type.getName()), armorValue, AttributeModifier.Operation.ADD_VALUE),
                slot);

        builder.add(Attributes.ARMOR_TOUGHNESS,
                new AttributeModifier(ResourceLocation.parse("mut:dragon_toughness_" + type.getName()), 4.0, AttributeModifier.Operation.ADD_VALUE),
                slot);

        builder.add(Attributes.KNOCKBACK_RESISTANCE,
                new AttributeModifier(ResourceLocation.parse("mut:dragon_knockback_" + type.getName()), 0.1, AttributeModifier.Operation.ADD_VALUE),
                slot);

        builder.add(Attributes.MAX_HEALTH,
                new AttributeModifier(ResourceLocation.parse("mut:dragon_health_" + type.getName()), healthBonus, AttributeModifier.Operation.ADD_VALUE),
                slot);

        return builder.build();
    }

    // ========== 子类定义 ==========

    public static class Helmet extends DragonItem {
        public Helmet() {
            super(ArmorItem.Type.HELMET, new Item.Properties()
                    .durability(ArmorItem.Type.HELMET.getDurability(68))
                    .fireResistant()
                    .rarity(Rarity.EPIC)
                    .attributes(buildAttributes(ArmorItem.Type.HELMET, 5.0)));
        }
    }

    public static class Chestplate extends DragonItem {
        public Chestplate() {
            super(ArmorItem.Type.CHESTPLATE, new Item.Properties()
                    .durability(ArmorItem.Type.CHESTPLATE.getDurability(68))
                    .fireResistant()
                    .rarity(Rarity.EPIC)
                    .attributes(buildAttributes(ArmorItem.Type.CHESTPLATE, 5.0)));
        }
    }

    public static class Leggings extends DragonItem {
        public Leggings() {
            super(ArmorItem.Type.LEGGINGS, new Item.Properties()
                    .durability(ArmorItem.Type.LEGGINGS.getDurability(68))
                    .fireResistant()
                    .rarity(Rarity.EPIC)
                    .attributes(buildAttributes(ArmorItem.Type.LEGGINGS, 5.0)));
        }
    }

    public static class Boots extends DragonItem {
        public Boots() {
            super(ArmorItem.Type.BOOTS, new Item.Properties()
                    .durability(ArmorItem.Type.BOOTS.getDurability(68))
                    .fireResistant()
                    .rarity(Rarity.EPIC)
                    .attributes(buildAttributes(ArmorItem.Type.BOOTS, 5.0)));
        }
    }
}