package net.mcreator.mut.event;

import net.mcreator.mut.affix.Affix;
import net.mcreator.mut.affix.AffixRegistry;
import net.mcreator.mut.config.AffixConfig;
import net.minecraft.spearcore.event.SpearDamageEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.EventPriority;

/** 仅在 spearcore 模组加载时才会被手动注册 */
public class SpearAffixListener {

    private static final String AFFIX_ID = "piercing_spear";

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onSpearDamage(SpearDamageEvent event) {
        LivingEntity attacker = event.getAttacker();
        Affix affix = AffixRegistry.get(AFFIX_ID);
        if (affix == null) return;

        int totalLevel = 0;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = attacker.getItemBySlot(slot);
            Affix onStack = Affix.fromStack(stack);
            if (onStack != null && AFFIX_ID.equals(onStack.getId())) {
                totalLevel += Affix.getLevelFromStack(stack);
            }
        }
        if (totalLevel <= 0) return;

        boolean charge = event.getAttackType() == SpearDamageEvent.AttackType.CHARGE;
        float bonusPerLevel = (float) AffixConfig.getCoefficient(
                AFFIX_ID, charge ? "charge" : "stab");

        event.addMultiplier(1.0F + totalLevel * bonusPerLevel);
    }
}