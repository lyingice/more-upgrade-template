package net.mcreator.mut.porting.armorstand;

import net.mcreator.mut.MutMod;

import net.minecraft.world.item.Item;

import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** 展示架物品注册（独立于 MCreator 生成的 MutModItems）。 */
public final class MutArmorStandItems {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MutMod.MODID);

    public static final DeferredItem<Item> HORSE_ARMOR_STAND =
            ITEMS.register("horse_armor_stand", () -> new HorseArmorStandItem(new Item.Properties()));

    public static final DeferredItem<Item> WOLF_ARMOR_STAND =
            ITEMS.register("wolf_armor_stand", () -> new WolfArmorStandItem(new Item.Properties()));

    private MutArmorStandItems() {
    }
}
