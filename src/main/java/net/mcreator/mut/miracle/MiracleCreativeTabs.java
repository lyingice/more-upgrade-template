package net.mcreator.mut.miracle;

import net.mcreator.mut.MutMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

/**
 * 把奇迹系列塞进 MCreator 已有的创造模式标签页。
 * 不直接改 {@code MutModTabs}（那个文件会被重新生成）。
 */
@EventBusSubscriber(modid = MutMod.MODID)
public final class MiracleCreativeTabs {

    private static final ResourceKey<CreativeModeTab> TEMPLATE_TAB = tab("more_upgrade_tmplates");
    private static final ResourceKey<CreativeModeTab> COMBAT_TAB = tab("more_upgrade_template_combat_items");
    private static final ResourceKey<CreativeModeTab> EXTRA_TAB = tab("more_upgrade_template_extra_items");

    private static ResourceKey<CreativeModeTab> tab(String name) {
        return ResourceKey.create(Registries.CREATIVE_MODE_TAB,
                ResourceLocation.fromNamespaceAndPath(MutMod.MODID, name));
    }

    @SubscribeEvent
    public static void onBuildContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(COMBAT_TAB)) {
            event.accept(MiracleItems.MIRACLE_HELMET.get());
            event.accept(MiracleItems.MIRACLE_CHESTPLATE.get());
            event.accept(MiracleItems.MIRACLE_LEGGINGS.get());
            event.accept(MiracleItems.MIRACLE_BOOTS.get());
            event.accept(MiracleItems.MIRACLE_SWORD.get());
        } else if (event.getTabKey().equals(TEMPLATE_TAB)) {
            event.accept(MiracleItems.MIRACLE_UPGRADE_SMITHING_TEMPLATE.get());
        } else if (event.getTabKey().equals(EXTRA_TAB)) {
            event.accept(MiracleItems.GOLDEN_HORN.get());
            event.accept(MiracleItems.MIRACLE_INGOT.get());
        }
    }

    private MiracleCreativeTabs() {
    }
}
