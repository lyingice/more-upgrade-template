/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.mut.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.EntityType;
import net.minecraft.core.registries.BuiltInRegistries;

import net.mcreator.mut.MutMod;

@EventBusSubscriber
public class MutModAttributes {
	public static final DeferredRegister<Attribute> REGISTRY = DeferredRegister.create(BuiltInRegistries.ATTRIBUTE, MutMod.MODID);
	public static final DeferredHolder<Attribute, Attribute> THUNDER_POWER = REGISTRY.register("thunder_power", () -> new RangedAttribute("attribute.mut.thunder_power", 10, 0, 100).setSyncable(true));

	@SubscribeEvent
	public static void addAttributes(EntityAttributeModificationEvent event) {
		event.add(EntityType.PLAYER, THUNDER_POWER);
	}
}