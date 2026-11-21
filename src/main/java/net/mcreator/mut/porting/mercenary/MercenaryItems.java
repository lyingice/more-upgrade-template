package net.mcreator.mut.porting.mercenary;

import net.mcreator.mut.MutMod;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** 猪灵雇佣兵刷怪蛋，并挂进原版"刷怪蛋"标签页。 */
@EventBusSubscriber(modid = MutMod.MODID)
public final class MercenaryItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MutMod.MODID);

    public static final DeferredItem<Item> PIGLIN_MERCENARY_SPAWN_EGG =
            ITEMS.register("piglin_mercenary_spawn_egg",
                    () -> new DeferredSpawnEggItem(MercenaryEntities.PIGLIN_MERCENARY,
                            0xE8B44A, 0x3B2A1A, new Item.Properties()));

    @SubscribeEvent
    public static void onBuildContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(PIGLIN_MERCENARY_SPAWN_EGG.get());
        }
    }

    private MercenaryItems() {
    }
}
