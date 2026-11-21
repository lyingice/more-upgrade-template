package net.mcreator.mut.porting.smithing;

import net.mcreator.mut.MutMod;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

/**
 * 把移植来的锻造模板挂进 MCreator 生成的“升级模板”创造模式标签，
 * 不直接修改 {@code MutModTabs}（该文件会被 MCreator 重新生成）。
 */
@EventBusSubscriber(modid = MutMod.MODID)
public final class MutSmithingTemplateCreativeTabs {
    private static final ResourceKey<CreativeModeTab> TEMPLATE_TAB =
            ResourceKey.create(
                    Registries.CREATIVE_MODE_TAB,
                    ResourceLocation.fromNamespaceAndPath(MutMod.MODID, "more_upgrade_tmplates")
            );

    @SubscribeEvent
    public static void onBuildContents(BuildCreativeModeTabContentsEvent event) {
        if (!event.getTabKey().equals(TEMPLATE_TAB)) {
            return;
        }
        event.accept(MutSmithingTemplateItems.ARROWHEAD_ARMOR_TRIM_SMITHING_TEMPLATE.get());
        event.accept(MutSmithingTemplateItems.NETHERITE_TRIM_SMITHING_TEMPLATE.get());
        event.accept(MutSmithingTemplateItems.NETHERITE_UPGRADE_SMITHING_TEMPLATE.get());
    }

    private MutSmithingTemplateCreativeTabs() {
    }
}
