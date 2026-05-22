/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.mut.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;

import net.mcreator.mut.MutMod;

public class MutModSounds {
	public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(Registries.SOUND_EVENT, MutMod.MODID);
	public static final DeferredHolder<SoundEvent, SoundEvent> ITEM_SPEAR_ATTACK = REGISTRY.register("item.spear.attack", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("mut", "item.spear.attack")));
	public static final DeferredHolder<SoundEvent, SoundEvent> ITEM_SPEAR_HIT = REGISTRY.register("item.spear.hit", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("mut", "item.spear.hit")));
	public static final DeferredHolder<SoundEvent, SoundEvent> ITEM_SPEAR_USE = REGISTRY.register("item.spear.use", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("mut", "item.spear.use")));
	public static final DeferredHolder<SoundEvent, SoundEvent> ITEM_SPEAR_WOOD_ATTACK = REGISTRY.register("item.spear_wood.attack", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("mut", "item.spear_wood.attack")));
	public static final DeferredHolder<SoundEvent, SoundEvent> ITEM_SPEAR_WOOD_USE = REGISTRY.register("item.spear_wood.use", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("mut", "item.spear_wood.use")));
	public static final DeferredHolder<SoundEvent, SoundEvent> ITEM_SPEAR_WOOD_HIT = REGISTRY.register("item.spear_wood.hit", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("mut", "item.spear_wood.hit")));
}