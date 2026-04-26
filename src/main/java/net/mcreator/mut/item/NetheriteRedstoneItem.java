package net.mcreator.mut.item;

import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Holder;
import net.minecraft.Util;

import java.util.List;
import java.util.EnumMap;

@EventBusSubscriber
public abstract class NetheriteRedstoneItem extends ArmorItem {
	public static Holder<ArmorMaterial> ARMOR_MATERIAL = null;

	@SubscribeEvent
	public static void registerArmorMaterial(RegisterEvent event) {
		event.register(Registries.ARMOR_MATERIAL, registerHelper -> {
			ArmorMaterial armorMaterial = new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
				map.put(ArmorItem.Type.BOOTS, 3);
				map.put(ArmorItem.Type.LEGGINGS, 6);
				map.put(ArmorItem.Type.CHESTPLATE, 8);
				map.put(ArmorItem.Type.HELMET, 3);
				map.put(ArmorItem.Type.BODY, 6);
			}), 99, DeferredHolder.create(Registries.SOUND_EVENT, ResourceLocation.parse("item.armor.equip_netherite")), () -> Ingredient.of(), List.of(new ArmorMaterial.Layer(ResourceLocation.parse("mut:netherite_redstone"))), 3f, 0.1f);
			registerHelper.register(ResourceLocation.parse("mut:netherite_redstone"), armorMaterial);
			ARMOR_MATERIAL = BuiltInRegistries.ARMOR_MATERIAL.wrapAsHolder(armorMaterial);
		});
	}

	public NetheriteRedstoneItem(ArmorItem.Type type, Item.Properties properties) {
		super(ARMOR_MATERIAL, type, properties);
	}

	// 构建完整属性（包含所有属性）
	private static ItemAttributeModifiers buildFullAttributes(ArmorItem.Type type, boolean isBoots) {
		ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
		
		EquipmentSlotGroup slot = switch (type) {
			case HELMET -> EquipmentSlotGroup.HEAD;
			case CHESTPLATE -> EquipmentSlotGroup.CHEST;
			case LEGGINGS -> EquipmentSlotGroup.LEGS;
			case BOOTS -> EquipmentSlotGroup.FEET;
			default -> EquipmentSlotGroup.ANY;
		};
		
		// 盔甲值
		double armorValue = switch (type) {
			case HELMET -> 3;
			case CHESTPLATE -> 8;
			case LEGGINGS -> 6;
			case BOOTS -> 3;
			default -> 0;
		};
		
		// 添加盔甲值
		builder.add(Attributes.ARMOR,
			new AttributeModifier(ResourceLocation.parse("mut:netherite_redstone_armor_" + type.getName()), armorValue, AttributeModifier.Operation.ADD_VALUE),
			slot);
		
		// 添加盔甲韧性 (3f)
		builder.add(Attributes.ARMOR_TOUGHNESS,
			new AttributeModifier(ResourceLocation.parse("mut:netherite_redstone_toughness_" + type.getName()), 3.0, AttributeModifier.Operation.ADD_VALUE),
			slot);
		
		// 添加击退抗性 (0.1f)
		builder.add(Attributes.KNOCKBACK_RESISTANCE,
			new AttributeModifier(ResourceLocation.parse("mut:netherite_redstone_knockback_" + type.getName()), 0.1, AttributeModifier.Operation.ADD_VALUE),
			slot);
		
		// 添加工具效率 +1
		builder.add(Attributes.MINING_EFFICIENCY,
			new AttributeModifier(ResourceLocation.parse("mut:netherite_redstone_efficiency_" + type.getName()), 1.0, AttributeModifier.Operation.ADD_VALUE),
			slot);
		
		// 添加攻击速度 +1
		builder.add(Attributes.ATTACK_SPEED,
			new AttributeModifier(ResourceLocation.parse("mut:netherite_redstone_attack_speed_" + type.getName()), 0.1, AttributeModifier.Operation.ADD_VALUE),
			slot);
		
		// 如果是靴子，额外添加移速 +20%
		if (isBoots) {
			builder.add(Attributes.MOVEMENT_SPEED,
				new AttributeModifier(ResourceLocation.parse("mut:netherite_redstone_speed_boots"), 0.2, AttributeModifier.Operation.ADD_MULTIPLIED_BASE),
				EquipmentSlotGroup.FEET);
		}
		
		return builder.build();
	}

	public static class Helmet extends NetheriteRedstoneItem {
		public Helmet() {
			super(ArmorItem.Type.HELMET, new Item.Properties()
				.durability(ArmorItem.Type.HELMET.getDurability(15))
				.attributes(buildFullAttributes(ArmorItem.Type.HELMET, false)));
		}
	}

	public static class Chestplate extends NetheriteRedstoneItem {
		public Chestplate() {
			super(ArmorItem.Type.CHESTPLATE, new Item.Properties()
				.durability(ArmorItem.Type.CHESTPLATE.getDurability(15))
				.attributes(buildFullAttributes(ArmorItem.Type.CHESTPLATE, false)));
		}
	}

	public static class Leggings extends NetheriteRedstoneItem {
		public Leggings() {
			super(ArmorItem.Type.LEGGINGS, new Item.Properties()
				.durability(ArmorItem.Type.LEGGINGS.getDurability(15))
				.attributes(buildFullAttributes(ArmorItem.Type.LEGGINGS, false)));
		}
	}

	public static class Boots extends NetheriteRedstoneItem {
		public Boots() {
			super(ArmorItem.Type.BOOTS, new Item.Properties()
				.durability(ArmorItem.Type.BOOTS.getDurability(15))
				.attributes(buildFullAttributes(ArmorItem.Type.BOOTS, true)));
		}
	}
}