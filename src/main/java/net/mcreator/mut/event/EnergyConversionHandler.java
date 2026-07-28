package net.mcreator.mut.event;

import net.mcreator.mut.affix.Affix;
import net.mcreator.mut.affix.event.AffixEventHandler;
import net.mcreator.mut.affix.effect.DurabilityRepairEffect;
import net.mcreator.mut.affix.json.AffixJsonLoader;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.food.FoodData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;

import java.util.List;

public class EnergyConversionHandler {

    @SubscribeEvent
    public void onItemUseFinish(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        ItemStack usedItem = event.getItem();
        if (usedItem.getItem().getFoodProperties(usedItem, player) == null) return;

        List<EquipmentSlot> affixSlots = AffixEventHandler.getDurabilityRepairSlots(player);
        if (affixSlots.isEmpty()) return;

        var foodProperties = usedItem.getItem().getFoodProperties(usedItem, player);
        if (foodProperties == null) return;

        FoodData foodData = player.getFoodData();
        float incomingSaturation = foodProperties.saturation() * 2.0F * foodProperties.nutrition();
        float currentSaturation = foodData.getSaturationLevel();
        float maxSaturation = 20.0F;
        float overflow = (currentSaturation + incomingSaturation) - maxSaturation;
        if (overflow <= 0) return;

        // 从 JSON 读取参数
        int saturationPerPoint = DurabilityRepairEffect.getSaturationPerPoint(player);
        if (saturationPerPoint <= 0) return;

        int durabilityToRestore = (int) (overflow / saturationPerPoint);
        if (durabilityToRestore <= 0) return;

        // 根据等级总和计算上限
        int levelSum = DurabilityRepairEffect.getTotalRepairLevel(player);
        int maxRepair = Math.round(levelSum * 0.5F);
        durabilityToRestore = Math.min(durabilityToRestore, maxRepair);

        for (EquipmentSlot slot : affixSlots) {
            ItemStack stack = player.getItemBySlot(slot);
            if (!stack.isEmpty() && stack.isDamaged()) {
                int newDamage = stack.getDamageValue() - durabilityToRestore;
                stack.setDamageValue(Math.max(newDamage, 0));
            }
        }
    }
}