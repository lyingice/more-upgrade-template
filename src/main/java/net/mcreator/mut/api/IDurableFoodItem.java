package net.mcreator.mut.api;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.GameType;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.registries.Registries;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.Minecraft;

/**
 * 可消耗耐久的食物物品接口
 * 配合 MixinItem 使用，在物品使用完成时拦截
 */
public interface IDurableFoodItem {

    ItemStack getItemStack();
    Entity getEntity();
    LevelAccessor getWorld();
    void setContext(ItemStack stack, Entity entity, LevelAccessor world);

    /**
     * 尝试消耗耐久，在物品上直接操作
     *
     * @return true = 耐久已消耗，阻止物品被消耗；
     *         false = 物品已损坏/不可损坏/非生存模式，走原版逻辑
     */
    default boolean handleDurabilityConsumption() {
        Entity entity = getEntity();
        ItemStack itemstack = getItemStack();
        LevelAccessor world = getWorld();

        if (entity == null || itemstack == null) {
            return false;
        }

        if (!itemstack.isDamageableItem() || isItemBroken()) {
            return false;
        }

        GameType gameType = getEntityGameType(entity);
        if (gameType != GameType.SURVIVAL && gameType != GameType.ADVENTURE) {
            return false;
        }

        // 客户端不处理，只在服务端扣耐久
        if (world.isClientSide()) {
            return true; // 阻止原版消耗，但不扣耐久（等服务端同步）
        }

        ServerLevel serverLevel = (ServerLevel) world;

        int unbreakingLevel = itemstack.getEnchantmentLevel(
                world.registryAccess()
                        .lookupOrThrow(Registries.ENCHANTMENT)
                        .getOrThrow(Enchantments.UNBREAKING)
        );

        boolean shouldDamage = unbreakingLevel == 0
                || Math.random() >= (double) unbreakingLevel / (unbreakingLevel + 1);

        if (shouldDamage) {
            itemstack.hurtAndBreak(1, serverLevel, null, _stkprov -> {});
        }

        return true;
    }

    default boolean isItemBroken() {
        ItemStack itemstack = getItemStack();
        return itemstack.getDamageValue() >= itemstack.getMaxDamage() - 1;
    }

    default GameType getEntityGameType(Entity entity) {
        if (entity instanceof ServerPlayer serverPlayer) {
            return serverPlayer.gameMode.getGameModeForPlayer();
        } else if (entity instanceof Player player && player.level().isClientSide()) {
            PlayerInfo playerInfo = Minecraft.getInstance()
                    .getConnection()
                    .getPlayerInfo(player.getGameProfile().getId());
            if (playerInfo != null) {
                return playerInfo.getGameMode();
            }
        }
        return null;
    }
}