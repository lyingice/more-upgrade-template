package net.mcreator.mut.event;

import net.mcreator.mut.affix.Affix;
import net.mcreator.mut.affix.EnergyConversionHelper;
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

        // 只有食物类物品才触发
        if (usedItem.getItem().getFoodProperties(usedItem, player) == null) return;

        // 检查身上是否有能量转化词缀
        List<EquipmentSlot> affixSlots = EnergyConversionHelper.getEnergyConversionSlots(player);
        if (affixSlots.isEmpty()) return;

        // 获取食物属性
        var foodProperties = usedItem.getItem().getFoodProperties(usedItem, player);
        if (foodProperties == null) return;

        // 计算溢出饱和度
        FoodData foodData = player.getFoodData();
        float incomingSaturation = foodProperties.saturation() * 2.0F * foodProperties.nutrition();
        float currentSaturation = foodData.getSaturationLevel();
        float maxSaturation = 20.0F;

        float overflow = (currentSaturation + incomingSaturation) - maxSaturation;
        if (overflow <= 0) return;

        // 计算可转化的耐久点数
        int durabilityToRestore = (int) (overflow / EnergyConversionHelper.SATURATION_PER_DURABILITY);
        if (durabilityToRestore <= 0) return;

        // 根据等级总和计算上限
        int maxRepair = EnergyConversionHelper.getMaxRepair(player);
        durabilityToRestore = Math.min(durabilityToRestore, maxRepair);

        // 给所有带词缀的装备恢复耐久
        for (EquipmentSlot slot : affixSlots) {
            ItemStack stack = player.getItemBySlot(slot);
            if (!stack.isEmpty() && stack.isDamaged()) {
                int newDamage = stack.getDamageValue() - durabilityToRestore;
                stack.setDamageValue(Math.max(newDamage, -stack.getMaxDamage()));
            }
        }
    }
}