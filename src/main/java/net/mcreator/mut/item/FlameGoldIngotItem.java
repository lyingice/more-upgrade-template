package net.mcreator.mut.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;

public class FlameGoldIngotItem extends Item {
	public FlameGoldIngotItem() {
		super(new Item.Properties().fireResistant().rarity(Rarity.UNCOMMON));
	}
}