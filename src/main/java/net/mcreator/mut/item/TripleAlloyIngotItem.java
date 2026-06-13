package net.mcreator.mut.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;

public class TripleAlloyIngotItem extends Item {
	public TripleAlloyIngotItem() {
		super(new Item.Properties().fireResistant().rarity(Rarity.UNCOMMON));
	}
}