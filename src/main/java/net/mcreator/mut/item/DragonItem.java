package net.mcreator.mut.item;

import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorItem;
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

import java.util.Map;
import java.util.List;
import java.util.EnumMap;
import java.util.Collections;

@EventBusSubscriber
public abstract class DragonItem extends ArmorItem {
	public static Holder<ArmorMaterial> ARMOR_MATERIAL = null;

	@SubscribeEvent
	public static void registerArmorMaterial(RegisterEvent event) {
		event.register(Registries.ARMOR_MATERIAL, registerHelper -> {
			ArmorMaterial armorMaterial = new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
				map.put(ArmorItem.Type.BOOTS, 5);
				map.put(ArmorItem.Type.LEGGINGS, 9);
				map.put(ArmorItem.Type.CHESTPLATE, 11);
				map.put(ArmorItem.Type.HELMET, 5);
				map.put(ArmorItem.Type.BODY, 11);
			}), 30, DeferredHolder.create(Registries.SOUND_EVENT, ResourceLocation.parse("item.armor.equip_netherite")), () -> Ingredient.of(), List.of(new ArmorMaterial.Layer(ResourceLocation.parse("mut:dragon_armor"))), 4f, 0.1f);
			registerHelper.register(ResourceLocation.parse("mut:dragon"), armorMaterial);
			ARMOR_MATERIAL = BuiltInRegistries.ARMOR_MATERIAL.wrapAsHolder(armorMaterial);
		});
	}

	@SubscribeEvent
	public static void registerItemExtensions(RegisterClientExtensionsEvent event) {
		event.registerItem(new IClientItemExtensions() {
			private HumanoidModel armorModel = null;

			@Override
			@OnlyIn(Dist.CLIENT)
			public HumanoidModel getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel defaultModel) {
				if (armorModel == null) {
					armorModel = new HumanoidModel(new ModelPart(Collections.emptyList(),
							Map.of("head", new ModelCustomModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelCustomModel.LAYER_LOCATION)).Head, "hat", new ModelPart(Collections.emptyList(), Collections.emptyMap()), "body",
									new ModelPart(Collections.emptyList(), Collections.emptyMap()), "right_arm", new ModelPart(Collections.emptyList(), Collections.emptyMap()), "left_arm",
									new ModelPart(Collections.emptyList(), Collections.emptyMap()), "right_leg", new ModelPart(Collections.emptyList(), Collections.emptyMap()), "left_leg",
									new ModelPart(Collections.emptyList(), Collections.emptyMap()))));
				}
				armorModel.crouching = living.isShiftKeyDown();
				armorModel.riding = defaultModel.riding;
				armorModel.young = living.isBaby();
				return armorModel;
			}
		}, MutModItems.DRAGON_HELMET.get());
		event.registerItem(new IClientItemExtensions() {
			private HumanoidModel armorModel = null;

			@Override
			@OnlyIn(Dist.CLIENT)
			public HumanoidModel getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel defaultModel) {
				if (armorModel == null) {
					ModelCustomModel model = new ModelCustomModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelCustomModel.LAYER_LOCATION));
					armorModel = new HumanoidModel(new ModelPart(Collections.emptyList(),
							Map.of("body", model.Body, "left_arm", model.LeftArm, "right_arm", model.RightArm, "head", new ModelPart(Collections.emptyList(), Collections.emptyMap()), "hat",
									new ModelPart(Collections.emptyList(), Collections.emptyMap()), "right_leg", new ModelPart(Collections.emptyList(), Collections.emptyMap()), "left_leg",
									new ModelPart(Collections.emptyList(), Collections.emptyMap()))));
				}
				armorModel.crouching = living.isShiftKeyDown();
				armorModel.riding = defaultModel.riding;
				armorModel.young = living.isBaby();
				return armorModel;
			}
		}, MutModItems.DRAGON_CHESTPLATE.get());
		event.registerItem(new IClientItemExtensions() {
			private HumanoidModel armorModel = null;

			@Override
			@OnlyIn(Dist.CLIENT)
			public HumanoidModel getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel defaultModel) {
				if (armorModel == null) {
					ModelCustomModel model = new ModelCustomModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelCustomModel.LAYER_LOCATION));
					armorModel = new HumanoidModel(new ModelPart(Collections.emptyList(),
							Map.of("left_leg", model.LeftLeg, "right_leg", model.RightLeg, "head", new ModelPart(Collections.emptyList(), Collections.emptyMap()), "hat", new ModelPart(Collections.emptyList(), Collections.emptyMap()), "body",
									new ModelPart(Collections.emptyList(), Collections.emptyMap()), "right_arm", new ModelPart(Collections.emptyList(), Collections.emptyMap()), "left_arm",
									new ModelPart(Collections.emptyList(), Collections.emptyMap()))));
				}
				armorModel.crouching = living.isShiftKeyDown();
				armorModel.riding = defaultModel.riding;
				armorModel.young = living.isBaby();
				return armorModel;
			}
		}, MutModItems.DRAGON_BOOTS.get());
	}

	public DragonItem(ArmorItem.Type type, Item.Properties properties) {
		super(ARMOR_MATERIAL, type, properties);
	}

	// 构建包含生命值加成的属性
	private static ItemAttributeModifiers buildAttributes(ArmorItem.Type type, double healthBonus) {
		ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
		
		// 获取装备槽位
		EquipmentSlotGroup slot = switch (type) {
			case HELMET -> EquipmentSlotGroup.HEAD;
			case CHESTPLATE -> EquipmentSlotGroup.CHEST;
			case LEGGINGS -> EquipmentSlotGroup.LEGS;
			case BOOTS -> EquipmentSlotGroup.FEET;
			default -> EquipmentSlotGroup.ANY;
		};
		
		// 盔甲值
		double armorValue = switch (type) {
			case HELMET -> 5;
			case CHESTPLATE -> 11;
			case LEGGINGS -> 9;
			case BOOTS -> 5;
			default -> 0;
		};
		
		// 添加盔甲值
		builder.add(Attributes.ARMOR,
			new AttributeModifier(ResourceLocation.parse("mut:dragon_armor_" + type.getName()), armorValue, AttributeModifier.Operation.ADD_VALUE),
			slot);
		
		// 添加盔甲韧性 (4f)
		builder.add(Attributes.ARMOR_TOUGHNESS,
			new AttributeModifier(ResourceLocation.parse("mut:dragon_toughness_" + type.getName()), 4.0, AttributeModifier.Operation.ADD_VALUE),
			slot);
		
		// 添加击退抗性 (0.1f)
		builder.add(Attributes.KNOCKBACK_RESISTANCE,
			new AttributeModifier(ResourceLocation.parse("mut:dragon_knockback_" + type.getName()), 0.1, AttributeModifier.Operation.ADD_VALUE),
			slot);
		
		// 添加最大生命值加成
		builder.add(Attributes.MAX_HEALTH,
			new AttributeModifier(ResourceLocation.parse("mut:dragon_health_" + type.getName()), healthBonus, AttributeModifier.Operation.ADD_VALUE),
			slot);
		
		return builder.build();
	}

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