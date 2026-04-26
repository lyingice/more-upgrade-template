package net.mcreator.mut.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;

public class DragonScaleClusterItem extends Item {
	public DragonScaleClusterItem() {
		super(new Item.Properties().stacksTo(16).fireResistant().rarity(Rarity.EPIC));
	}
}