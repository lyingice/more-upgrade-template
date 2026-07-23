package net.mcreator.mut.item;

import net.mcreator.mut.init.MutModBlocks;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SolidBucketItem;
import net.minecraft.world.level.block.Blocks;

public class SigilForgePowderSnowBucketItem extends SolidBucketItem {
	public SigilForgePowderSnowBucketItem() {
		super(MutModBlocks.SIGIL_FORGE_POWDER_SNOW.get(), SoundEvents.BUCKET_EMPTY_POWDER_SNOW, (new Item.Properties()).stacksTo(1));
	}
}