package net.mcreator.mut.procedures;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.event.entity.EntityStruckByLightningEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.mcreator.mut.init.MutModItems;

@EventBusSubscriber
public class ThunderCopperStarConversion {

    @SubscribeEvent
    public static void onLightningStrike(EntityStruckByLightningEvent event) {
        if (!(event.getEntity() instanceof ItemEntity itemEntity)) return;

        ItemStack stack = itemEntity.getItem();
        if (!stack.is(Items.NETHER_STAR)) return;

        Level level = event.getEntity().level();
        if (level.isClientSide()) return;

        LightningBolt bolt = event.getLightning();

        // 移除原物品
        int count = stack.getCount();
        itemEntity.discard();

        // 在雷击位置掉落铜之星
        ItemStack result = new ItemStack(MutModItems.THUNDER_COPPER_STAR.get(), count);
        ItemEntity newEntity = new ItemEntity(level,
                bolt.getX(), bolt.getY(), bolt.getZ(), result);
        level.addFreshEntity(newEntity);
    }
}