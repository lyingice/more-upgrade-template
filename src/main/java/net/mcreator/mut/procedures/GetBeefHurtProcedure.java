package net.mcreator.mut.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.GameType;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionHand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.registries.Registries;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.Minecraft;

public class GetBeefHurtProcedure {
	public static void execute(LevelAccessor world, Entity entity, ItemStack itemstack) {
		if (entity == null)
			return;

		// 确保物品堆叠数量为1，避免消耗多个物品
		itemstack.setCount(1);

		// 仅在生存或冒险模式下消耗耐久
		GameType gameType = getEntityGameType(entity);
		if (gameType != GameType.SURVIVAL && gameType != GameType.ADVENTURE) {
			return;
		}

		if (!(world instanceof ServerLevel serverLevel)) {
			return;
		}

		// 获取耐久附魔等级（缓存结果避免重复注册表查找）
		int unbreakingLevel = itemstack.getEnchantmentLevel(
				world.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.UNBREAKING)
		);

		// 判断是否消耗耐久：无耐久附魔时必定消耗，有时按概率减免
		boolean shouldDamage = unbreakingLevel == 0
				|| Math.random() >= (double) unbreakingLevel / (unbreakingLevel + 1);

		if (shouldDamage) {
			itemstack.hurtAndBreak(1, serverLevel, null, _stkprov -> {
			});
		}

		// 检查物品是否已损坏（耐久度归零），若是则清空手持槽位
		if (itemstack.getDamageValue() > itemstack.getMaxDamage() - 1) {
			clearHandIfMatching(entity, itemstack, InteractionHand.OFF_HAND);
			clearHandIfMatching(entity, itemstack, InteractionHand.MAIN_HAND);
		}
	}

	/**
	 * 如果实体指定手中的物品与目标物品匹配，则清空该手槽位
	 */
	private static void clearHandIfMatching(Entity entity, ItemStack targetItem, InteractionHand hand) {
		if (!(entity instanceof LivingEntity livingEntity)) {
			return;
		}
		ItemStack handItem = livingEntity.getItemInHand(hand);
		if (handItem.getItem() != targetItem.getItem()) {
			return;
		}
		livingEntity.setItemInHand(hand, ItemStack.EMPTY);
		if (livingEntity instanceof Player player) {
			player.getInventory().setChanged();
		}
	}

	private static GameType getEntityGameType(Entity entity) {
		if (entity instanceof ServerPlayer serverPlayer) {
			return serverPlayer.gameMode.getGameModeForPlayer();
		} else if (entity instanceof Player player && player.level().isClientSide()) {
			PlayerInfo playerInfo = Minecraft.getInstance().getConnection().getPlayerInfo(player.getGameProfile().getId());
			if (playerInfo != null)
				return playerInfo.getGameMode();
		}
		return null;
	}
}