package net.mcreator.mut.miracle;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

/** 奇迹之锭：奇迹锻造的原材料。暂不提供合成配方。 */
public class MiracleIngotItem extends Item {

    public MiracleIngotItem() {
        super(new Item.Properties().fireResistant().rarity(Rarity.RARE));
    }
}
