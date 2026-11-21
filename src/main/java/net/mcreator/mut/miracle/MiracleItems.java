package net.mcreator.mut.miracle;

import net.mcreator.mut.MutMod;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** 奇迹装备 / 金质号角 / 奇迹锻造模板的物品注册（独立于 MCreator 生成的 MutModItems）。 */
public final class MiracleItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MutMod.MODID);

    public static final DeferredItem<Item> MIRACLE_HELMET =
            ITEMS.register("miracle_helmet", MiracleArmorItem.Helmet::new);
    public static final DeferredItem<Item> MIRACLE_CHESTPLATE =
            ITEMS.register("miracle_chestplate", MiracleArmorItem.Chestplate::new);
    public static final DeferredItem<Item> MIRACLE_LEGGINGS =
            ITEMS.register("miracle_leggings", MiracleArmorItem.Leggings::new);
    public static final DeferredItem<Item> MIRACLE_BOOTS =
            ITEMS.register("miracle_boots", MiracleArmorItem.Boots::new);
    public static final DeferredItem<Item> MIRACLE_SWORD =
            ITEMS.register("miracle_sword", MiracleSwordItem::new);

    public static final DeferredItem<Item> GOLDEN_HORN =
            ITEMS.register("golden_horn", GoldenHornItem::new);

    public static final DeferredItem<Item> MIRACLE_UPGRADE_SMITHING_TEMPLATE =
            ITEMS.register("miracle_upgrade_smithing_template", MiracleUpgradeSmithingTemplateItem::new);

    /** 奇迹之锭：锻造奇迹装备的原材料 */
    public static final DeferredItem<Item> MIRACLE_INGOT =
            ITEMS.register("miracle_ingot", MiracleIngotItem::new);

    private MiracleItems() {
    }
}
