package net.mcreator.mut.porting.ruingolem;

import net.mcreator.mut.MutMod;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

/**
 * 把傀儡刷怪蛋放进原版的"刷怪蛋"创造模式标签。
 * 不直接改 {@code MutModTabs}（该文件会被 MCreator 重新生成）。
 */
@EventBusSubscriber(modid = MutMod.MODID)
public final class RuinGolemCreativeTabs {

    @SubscribeEvent
    public static void onBuildContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() != CreativeModeTabs.SPAWN_EGGS) {
            return;
        }
        event.accept(RuinGolemItems.OBSIDIAN_RUIN_GOLEM_SPAWN_EGG.get());
        event.accept(RuinGolemItems.CRYING_OBSIDIAN_RUIN_GOLEM_SPAWN_EGG.get());
        event.accept(RuinGolemItems.ROCK_RUIN_GOLEM_SPAWN_EGG.get());
    }

    private RuinGolemCreativeTabs() {
    }
}
