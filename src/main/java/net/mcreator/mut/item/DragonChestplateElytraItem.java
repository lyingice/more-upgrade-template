package net.mcreator.mut.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.neoforge.common.extensions.IItemExtension;

public class DragonChestplateElytraItem extends DragonItem.Chestplate implements IItemExtension {

    public DragonChestplateElytraItem() {
        super();
    }

    // 允许鞘翅飞行
    public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
        return true;
    }

    // 飞行时消耗耐久（每20 tick消耗1点）
    public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
        if (!entity.level().isClientSide && (flightTicks + 1) % 20 == 0) {
            stack.hurtAndBreak(1, entity, EquipmentSlot.CHEST);
        }
        return true;
    }
}