package net.mcreator.mut.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;

public class CryingObsidianIngotItem extends Item {
	public CryingObsidianIngotItem() {
		super(new Item.Properties().fireResistant().rarity(Rarity.EPIC));
	}
}