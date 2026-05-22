package net.mcreator.mut.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;

public class FlameGoldNuggetItem extends Item {
	public FlameGoldNuggetItem() {
		super(new Item.Properties().fireResistant().rarity(Rarity.UNCOMMON));
	}
}