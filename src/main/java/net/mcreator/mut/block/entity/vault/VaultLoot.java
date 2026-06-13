package net.mcreator.mut.block.entity.vault;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class VaultLoot {

    /**
     * 从战利品表解析奖励物品
     */
    public static List<ItemStack> resolveLoot(ServerLevel level, LootTable lootTable, BlockPos pos, Player player) {
        LootParams params = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .withLuck(player.getLuck())
                .withParameter(LootContextParams.THIS_ENTITY, player)
                .create(LootContextParamSets.VAULT);
        return lootTable.getRandomItems(params);
    }

    /**
     * 随机获取一个展示物品
     */
    public static ItemStack getRandomDisplayItem(ServerLevel level, LootTable lootTable, BlockPos pos) {
        LootParams params = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .create(LootContextParamSets.VAULT);
        List<ItemStack> items = lootTable.getRandomItems(params, level.getRandom());
        return items.isEmpty() ? ItemStack.EMPTY : items.get(level.getRandom().nextInt(items.size()));
    }
}