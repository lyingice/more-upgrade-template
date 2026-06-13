package net.mcreator.mut.client;

import net.mcreator.mut.MutMod;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddPackFindersEvent;

@EventBusSubscriber(modid = MutMod.MODID,value = Dist.CLIENT)
public class MutBuiltinPacks {

    @SubscribeEvent
    public static void addPacks(AddPackFindersEvent event) {
        // 内置旧版材质包
        event.addPackFinders(
                ResourceLocation.fromNamespaceAndPath("mut", "resourcepacks/mutextra"),
                net.minecraft.server.packs.PackType.CLIENT_RESOURCES,
                Component.literal("Mut符锻装备材质by说谎冰"),
                net.minecraft.server.packs.repository.PackSource.BUILT_IN,
                false,  // 不是必选
                net.minecraft.server.packs.repository.Pack.Position.TOP
        );
        event.addPackFinders(
                ResourceLocation.fromNamespaceAndPath("mut", "resourcepacks/tuxturesfix"),
                net.minecraft.server.packs.PackType.CLIENT_RESOURCES,
                Component.literal("Mut呦爺爺修复包"),
                net.minecraft.server.packs.repository.PackSource.BUILT_IN,
                false,  // 不是必选
                net.minecraft.server.packs.repository.Pack.Position.TOP
        );
    }
}