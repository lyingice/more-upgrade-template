/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.mut.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.core.registries.Registries;

import net.mcreator.mut.potion.WitherMarkMobEffect;
import net.mcreator.mut.potion.PoisonMarkMobEffect;
import net.mcreator.mut.potion.FireMarkMobEffect;
import net.mcreator.mut.potion.CurrentOverloadMobEffect;
import net.mcreator.mut.MutMod;

public class MutModMobEffects {
	public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(Registries.MOB_EFFECT, MutMod.MODID);
	public static final DeferredHolder<MobEffect, MobEffect> POISON_MARK = REGISTRY.register("poison_mark", () -> new PoisonMarkMobEffect());
	public static final DeferredHolder<MobEffect, MobEffect> FIRE_MARK = REGISTRY.register("fire_mark", () -> new FireMarkMobEffect());
	public static final DeferredHolder<MobEffect, MobEffect> WITHER_MARK = REGISTRY.register("wither_mark", () -> new WitherMarkMobEffect());
	public static final DeferredHolder<MobEffect, MobEffect> CURRENT_OVERLOAD = REGISTRY.register("current_overload", () -> new CurrentOverloadMobEffect());
}