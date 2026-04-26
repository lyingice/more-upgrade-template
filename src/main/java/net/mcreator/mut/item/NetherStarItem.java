package net.mcreator.mut.item;

import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

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
public abstract class NetherStarItem extends ArmorItem {
	public static Holder<ArmorMaterial> ARMOR_MATERIAL = null;

	@SubscribeEvent
	public static void registerArmorMaterial(RegisterEvent event) {
		event.register(Registries.ARMOR_MATERIAL, registerHelper -> {
			ArmorMaterial armorMaterial = new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
				map.put(ArmorItem.Type.BOOTS, 3);
				map.put(ArmorItem.Type.LEGGINGS, 6);
				map.put(ArmorItem.Type.CHESTPLATE, 8);
				map.put(ArmorItem.Type.HELMET, 3);
				map.put(ArmorItem.Type.BODY, 8);
			}), 22, DeferredHolder.create(Registries.SOUND_EVENT, ResourceLocation.parse("item.armor.equip_netherite")), () -> Ingredient.of(new ItemStack(Items.NETHER_STAR)),
					List.of(new ArmorMaterial.Layer(ResourceLocation.parse("mut:nether_star"))), 10f, 0.1f);
			registerHelper.register(ResourceLocation.parse("mut:nether_star"), armorMaterial);
			ARMOR_MATERIAL = BuiltInRegistries.ARMOR_MATERIAL.wrapAsHolder(armorMaterial);
		});
	}

	public NetherStarItem(ArmorItem.Type type, Item.Properties properties) {
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
		
		// 盔甲值（从 ArmorMaterial 中获取）
		double armorValue = switch (type) {
			case HELMET -> 3;
			case CHESTPLATE -> 8;
			case LEGGINGS -> 6;
			case BOOTS -> 3;
			default -> 0;
		};
		
		// 添加盔甲值
		builder.add(Attributes.ARMOR,
			new AttributeModifier(ResourceLocation.parse("mut:nether_star_armor_" + type.getName()), armorValue, AttributeModifier.Operation.ADD_VALUE),
			slot);
		
		// 添加盔甲韧性 (10f)
		builder.add(Attributes.ARMOR_TOUGHNESS,
			new AttributeModifier(ResourceLocation.parse("mut:nether_star_toughness_" + type.getName()), 10.0, AttributeModifier.Operation.ADD_VALUE),
			slot);
		
		// 添加击退抗性 (0.1f)
		builder.add(Attributes.KNOCKBACK_RESISTANCE,
			new AttributeModifier(ResourceLocation.parse("mut:nether_star_knockback_" + type.getName()), 0.1, AttributeModifier.Operation.ADD_VALUE),
			slot);
		
		// 添加最大生命值加成
		builder.add(Attributes.MAX_HEALTH,
			new AttributeModifier(ResourceLocation.parse("mut:nether_star_health_" + type.getName()), healthBonus, AttributeModifier.Operation.ADD_VALUE),
			slot);
		
		return builder.build();
	}

	public static class Helmet extends NetherStarItem {
		public Helmet() {
			super(ArmorItem.Type.HELMET, new Item.Properties()
				.durability(ArmorItem.Type.HELMET.getDurability(88))
				.rarity(Rarity.EPIC)
				.attributes(buildAttributes(ArmorItem.Type.HELMET, 7.5)));
		}

		@Override
		@OnlyIn(Dist.CLIENT)
		public boolean isFoil(ItemStack itemstack) {
			return true;
		}
	}

	public static class Chestplate extends NetherStarItem {
		public Chestplate() {
			super(ArmorItem.Type.CHESTPLATE, new Item.Properties()
				.durability(ArmorItem.Type.CHESTPLATE.getDurability(88))
				.rarity(Rarity.EPIC)
				.attributes(buildAttributes(ArmorItem.Type.CHESTPLATE, 7.5)));
		}

		@Override
		@OnlyIn(Dist.CLIENT)
		public boolean isFoil(ItemStack itemstack) {
			return true;
		}
	}

	public static class Leggings extends NetherStarItem {
		public Leggings() {
			super(ArmorItem.Type.LEGGINGS, new Item.Properties()
				.durability(ArmorItem.Type.LEGGINGS.getDurability(88))
				.rarity(Rarity.EPIC)
				.attributes(buildAttributes(ArmorItem.Type.LEGGINGS, 7.5)));
		}

		@Override
		@OnlyIn(Dist.CLIENT)
		public boolean isFoil(ItemStack itemstack) {
			return true;
		}
	}

	public static class Boots extends NetherStarItem {
		public Boots() {
			super(ArmorItem.Type.BOOTS, new Item.Properties()
				.durability(ArmorItem.Type.BOOTS.getDurability(88))
				.rarity(Rarity.EPIC)
				.attributes(buildAttributes(ArmorItem.Type.BOOTS, 7.5)));
		}

		@Override
		@OnlyIn(Dist.CLIENT)
		public boolean isFoil(ItemStack itemstack) {
			return true;
		}
	}
}