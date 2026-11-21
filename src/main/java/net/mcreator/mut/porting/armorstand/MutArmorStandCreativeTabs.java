package net.mcreator.mut.porting.armorstand;

import net.mcreator.mut.MutMod;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

/** 把展示架挂进 MCreator 已有的"额外物品"创造模式标签。 */
@EventBusSubscriber(modid = MutMod.MODID)
public final class MutArmorStandCreativeTabs {
    private static final ResourceKey<CreativeModeTab> EXTRA_ITEMS_TAB =
            ResourceKey.create(
                    Registries.CREATIVE_MODE_TAB,
                    ResourceLocation.fromNamespaceAndPath(MutMod.MODID, "more_upgrade_template_extra_items")
            );

    @SubscribeEvent
    public static void onBuildContents(BuildCreativeModeTabContentsEvent event) {
        if (!event.getTabKey().equals(EXTRA_ITEMS_TAB)) {
            return;
        }
        event.accept(MutArmorStandItems.HORSE_ARMOR_STAND.get());
        event.accept(MutArmorStandItems.WOLF_ARMOR_STAND.get());
    }

    private MutArmorStandCreativeTabs() {
    }
}
