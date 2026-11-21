package net.mcreator.mut.beastiary;

import net.mcreator.mut.MutMod;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** 新生物的刷怪蛋 + 投掷物对应的物品。 */
@EventBusSubscriber(modid = MutMod.MODID)
public final class BeastiaryItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MutMod.MODID);

    public static final DeferredItem<Item> TUSKED_WARLORD_SPAWN_EGG = egg("tusked_warlord_spawn_egg",
            BeastiaryEntities.TUSKED_WARLORD, 0xC24B2C, 0x2A1208);
    public static final DeferredItem<Item> PIGLIN_MERCENARY_RANGED_SPAWN_EGG = egg("piglin_mercenary_ranged_spawn_egg",
            BeastiaryEntities.PIGLIN_MERCENARY_RANGED, 0xE8B44A, 0x8C9CE8);
    public static final DeferredItem<Item> PIGLIN_MERCENARY_DEFENDER_SPAWN_EGG = egg("piglin_mercenary_defender_spawn_egg",
            BeastiaryEntities.PIGLIN_MERCENARY_DEFENDER, 0xE8B44A, 0xC8C8D2);
    public static final DeferredItem<Item> SPIDER_QUEEN_SPAWN_EGG = egg("spider_queen_spawn_egg",
            BeastiaryEntities.SPIDER_QUEEN, 0x6B1E6B, 0xB428B4);
    public static final DeferredItem<Item> DARK_SPIDER_SPAWN_EGG = egg("dark_spider_spawn_egg",
            BeastiaryEntities.DARK_SPIDER, 0x1A1A28, 0x30303A);
    public static final DeferredItem<Item> DEEP_LEAPING_SPIDER_SPAWN_EGG = egg("deep_leaping_spider_spawn_egg",
            BeastiaryEntities.DEEP_LEAPING_SPIDER, 0x24384A, 0x3C5A74);
    public static final DeferredItem<Item> FALLEN_TEMPLATE_SAGE_SPAWN_EGG = egg("fallen_template_sage_spawn_egg",
            BeastiaryEntities.FALLEN_TEMPLATE_SAGE, 0x78C8A0, 0x2E4A38);
    public static final DeferredItem<Item> TEMPLATE_GOD_REMAINS_SPAWN_EGG = egg("template_god_remains_spawn_egg",
            BeastiaryEntities.TEMPLATE_GOD_REMAINS, 0xC8B4FF, 0x3A2A66);
    public static final DeferredItem<Item> FALLEN_ARTISAN_SPAWN_EGG = egg("fallen_artisan_spawn_egg",
            BeastiaryEntities.FALLEN_ARTISAN, 0xAA7840, 0x402A14);
    public static final DeferredItem<Item> ABYSS_SKELETON_SPAWN_EGG = egg("abyss_skeleton_spawn_egg",
            BeastiaryEntities.ABYSS_SKELETON, 0x503C8C, 0x1A1230);
    public static final DeferredItem<Item> ABYSS_CREEPER_SPAWN_EGG = egg("abyss_creeper_spawn_egg",
            BeastiaryEntities.ABYSS_CREEPER, 0x46145A, 0x0E0614);

    /** 牙矛标枪（投射物渲染用） */
    public static final DeferredItem<Item> TUSK_JAVELIN =
            ITEMS.register("tusk_javelin", () -> new Item(new Item.Properties().stacksTo(1)));
    /** 铁重锤（投射物渲染用） */
    public static final DeferredItem<Item> IRON_HAMMER =
            ITEMS.register("iron_hammer", () -> new Item(new Item.Properties().stacksTo(1)));
    /** 投掷工具（投射物渲染用） */
    public static final DeferredItem<Item> THROWN_TOOL =
            ITEMS.register("thrown_tool", () -> new Item(new Item.Properties().stacksTo(1)));

    private static DeferredItem<Item> egg(String id,
                                          net.neoforged.neoforge.registries.DeferredHolder<net.minecraft.world.entity.EntityType<?>, ? extends net.minecraft.world.entity.EntityType<? extends net.minecraft.world.entity.Mob>> type,
                                          int bg, int hl) {
        return ITEMS.register(id, () -> new DeferredSpawnEggItem(type, bg, hl, new Item.Properties()));
    }

    @SubscribeEvent
    public static void onBuildContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() != CreativeModeTabs.SPAWN_EGGS) {
            return;
        }
        event.accept(TUSKED_WARLORD_SPAWN_EGG.get());
        event.accept(PIGLIN_MERCENARY_RANGED_SPAWN_EGG.get());
        event.accept(PIGLIN_MERCENARY_DEFENDER_SPAWN_EGG.get());
        event.accept(SPIDER_QUEEN_SPAWN_EGG.get());
        event.accept(DARK_SPIDER_SPAWN_EGG.get());
        event.accept(DEEP_LEAPING_SPIDER_SPAWN_EGG.get());
        event.accept(FALLEN_TEMPLATE_SAGE_SPAWN_EGG.get());
        event.accept(TEMPLATE_GOD_REMAINS_SPAWN_EGG.get());
        event.accept(FALLEN_ARTISAN_SPAWN_EGG.get());
        event.accept(ABYSS_SKELETON_SPAWN_EGG.get());
        event.accept(ABYSS_CREEPER_SPAWN_EGG.get());
    }

    private BeastiaryItems() {
    }
}
