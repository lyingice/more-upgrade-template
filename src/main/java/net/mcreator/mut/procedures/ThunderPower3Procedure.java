package net.mcreator.mut.procedures;

import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.core.component.DataComponents;

@EventBusSubscriber
public class ThunderPower3Procedure {

    @SubscribeEvent
    public static void onEntityAttacked(LivingDamageEvent.Pre event) {
        Entity target = event.getEntity();
        if (target == null) return;

        // 防递归：闪电伤害直接跳过
        if (event.getSource().is(DamageTypes.LIGHTNING_BOLT)) {
            return;
        }

        Entity source = event.getSource().getEntity();
        if (!(source instanceof LivingEntity attacker)) {
            return;
        }

        ItemStack mainHand = attacker.getMainHandItem();
        ItemStack offHand = attacker.getOffhandItem();

        int mainPower = getThunderPower(mainHand);
        int offPower = getThunderPower(offHand);
        int totalPower = mainPower + offPower;

        if (totalPower <= 0) {
            return;
        }

        // 双手都有储能 → 除以100；单手 → 除以100
        float extraDamage;
        if (mainPower >= 1 && offPower >= 1) {
            extraDamage = 10F * (float)(Math.random() + 0.5) * (totalPower / 100F);
        } else {
            extraDamage = totalPower * 0.1F;
        }
        extraDamage = Math.max(1, Math.min(extraDamage, 50));


        // 重置无敌时间，让闪电伤害不会被剑的免疫帧吞掉
        target.invulnerableTime = 0;
        target.hurt(target.damageSources().lightningBolt(), extraDamage);
        target.invulnerableTime = 0;

        target.hurt(target.damageSources().lightningBolt(), extraDamage);

        // 消耗储能
        // 消耗储能：双手都有时各扣1层
        if (mainPower > 0 && offPower > 0) {
            setThunderPower(mainHand, mainPower - 1);
            setThunderPower(offHand, offPower - 1);
        } else if (mainPower > 0) {
            setThunderPower(mainHand, mainPower - 1);
        } else if (offPower > 0) {
            setThunderPower(offHand, offPower - 1);
        }
    }

    private static int getThunderPower(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                .copyTag().getInt("ThunderPower");
    }

    private static void setThunderPower(ItemStack stack, int value) {
        if (stack.isEmpty()) return;
        CustomData.update(DataComponents.CUSTOM_DATA, stack,
                tag -> tag.putInt("ThunderPower", Math.max(0, value)));
    }
}