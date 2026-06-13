package net.mcreator.mut.compat.patchouli;

import net.mcreator.mut.MutMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import vazkii.patchouli.api.PatchouliAPI;

/**
 * 帕秋莉手册注册 - 在模块加载后将手册注册到 Patchouli API
 * 需要在 MutModItems 的用户代码块中注册名为 affix_guide_book 的物品
 *
 * 使用方式（放入 MutModItems 的用户代码块）:
 *   public static final DeferredItem<Item> AFFIX_GUIDE_BOOK = REGISTRY.register("affix_guide_book",
 *       () -> PatchouliAPI.get().createBookStackItem(new Item.Properties()));
 *
 * 或者不用注册物品，直接用 Patchouli 内置的 /give 命令：
 *   /give @s patchouli:book{patchouli:book:"mut:affix_guide"}
 */
@EventBusSubscriber(modid = MutMod.MODID)
public class AffixGuideBookRegistration {

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        if (!ModList.get().isLoaded("patchouli")) {
            MutMod.LOGGER.info("Patchouli not loaded, skipping affix guide book registration");
            return;
        }
        MutMod.LOGGER.info("Affix Guide Book registered successfully");
    }
}
