package net.mcreator.mut.item;

import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Holder;
import net.minecraft.Util;

import net.mcreator.mut.init.MutModItems;

import java.util.List;
import java.util.EnumMap;

@EventBusSubscriber
public abstract class CryingObsidianItem extends ArmorItem {
	public static Holder<ArmorMaterial> ARMOR_MATERIAL = null;

	@SubscribeEvent
	public static void registerArmorMaterial(RegisterEvent event) {
		event.register(Registries.ARMOR_MATERIAL, registerHelper -> {
			ArmorMaterial armorMaterial = new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
				map.put(ArmorItem.Type.BOOTS, 5);
				map.put(ArmorItem.Type.LEGGINGS, 10);
				map.put(ArmorItem.Type.CHESTPLATE, 12);
				map.put(ArmorItem.Type.HELMET, 5);
				map.put(ArmorItem.Type.BODY, 12);
			}), 1, DeferredHolder.create(Registries.SOUND_EVENT, ResourceLocation.parse("item.armor.equip_netherite")), () -> Ingredient.of(new ItemStack(MutModItems.CRYING_OBSIDIAN_INGOT.get())),
					List.of(new ArmorMaterial.Layer(ResourceLocation.parse("mut:crying_obsidian"))), 4f, 0.2f);
			registerHelper.register(ResourceLocation.parse("mut:crying_obsidian"), armorMaterial);
			ARMOR_MATERIAL = BuiltInRegistries.ARMOR_MATERIAL.wrapAsHolder(armorMaterial);
		});
	}

	public CryingObsidianItem(ArmorItem.Type type, Item.Properties properties) {
		super(ARMOR_MATERIAL, type, properties);
	}

	// 构建完整属性的方法
	private static ItemAttributeModifiers buildAttributes(ArmorItem.Type type) {
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
			case CHESTPLATE -> 12;
			case LEGGINGS -> 10;
			case BOOTS -> 5;
			default -> 0;
		};
		
		// 添加盔甲值
		builder.add(Attributes.ARMOR,
			new AttributeModifier(ResourceLocation.parse("mut:armor_" + type.getName()), armorValue, AttributeModifier.Operation.ADD_VALUE),
			slot);
		
		// 添加盔甲韧性
		builder.add(Attributes.ARMOR_TOUGHNESS,
			new AttributeModifier(ResourceLocation.parse("mut:toughness_" + type.getName()), 4.0, AttributeModifier.Operation.ADD_VALUE),
			slot);
		
		// 添加击退抗性
		builder.add(Attributes.KNOCKBACK_RESISTANCE,
			new AttributeModifier(ResourceLocation.parse("mut:knockback_" + type.getName()), 0.2, AttributeModifier.Operation.ADD_VALUE),
			slot);
		
		// 添加最大生命值 +7.5%
		builder.add(Attributes.MAX_HEALTH,
			new AttributeModifier(ResourceLocation.parse("mut:health_" + type.getName()), 0.075, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL),
			slot);
		
		// 添加移动速度 -2.5%
		builder.add(Attributes.MOVEMENT_SPEED,
			new AttributeModifier(ResourceLocation.parse("mut:speed_" + type.getName()), -0.025, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL),
			slot);
		
		return builder.build();
	}

	public static class Helmet extends CryingObsidianItem {
		public Helmet() {
			super(ArmorItem.Type.HELMET, new Item.Properties()
				.durability(ArmorItem.Type.HELMET.getDurability(100))
				.fireResistant()
				.rarity(Rarity.EPIC)
				.attributes(buildAttributes(ArmorItem.Type.HELMET)));
		}
	}

	public static class Chestplate extends CryingObsidianItem {
		public Chestplate() {
			super(ArmorItem.Type.CHESTPLATE, new Item.Properties()
				.durability(ArmorItem.Type.CHESTPLATE.getDurability(100))
				.fireResistant()
				.rarity(Rarity.EPIC)
				.attributes(buildAttributes(ArmorItem.Type.CHESTPLATE)));
		}
	}

	public static class Leggings extends CryingObsidianItem {
		public Leggings() {
			super(ArmorItem.Type.LEGGINGS, new Item.Properties()
				.durability(ArmorItem.Type.LEGGINGS.getDurability(100))
				.fireResistant()
				.rarity(Rarity.EPIC)
				.attributes(buildAttributes(ArmorItem.Type.LEGGINGS)));
		}
	}

	public static class Boots extends CryingObsidianItem {
		public Boots() {
			super(ArmorItem.Type.BOOTS, new Item.Properties()
				.durability(ArmorItem.Type.BOOTS.getDurability(100))
				.fireResistant()
				.rarity(Rarity.EPIC)
				.attributes(buildAttributes(ArmorItem.Type.BOOTS)));
		}
	}
}