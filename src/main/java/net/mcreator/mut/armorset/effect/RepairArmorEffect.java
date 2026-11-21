package net.mcreator.mut.armorset.effect;

import net.mcreator.mut.armorset.ArmorSet;
import net.mcreator.mut.armorset.ArmorSetEffect;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * 为穿在身上的本套护甲恢复耐久。
 * 只处理属于该套装的部件，不会顺带修复别的护甲。
 */
public class RepairArmorEffect implements ArmorSetEffect {

    private final String trigger;
    private final int amount;
    private final int intervalSeconds;
    private final float chance;

    public RepairArmorEffect(String trigger, int amount, int intervalSeconds, float chance) {
        this.trigger = trigger;
        this.amount = amount;
        this.intervalSeconds = intervalSeconds;
        this.chance = chance;
    }

    @Override
    public String getTrigger() {
        return trigger;
    }

    @Override
    public int getIntervalSeconds() {
        return intervalSeconds;
    }

    @Override
    public void apply(Player player, ArmorSet set) {
        if (chance < 1.0F && player.getRandom().nextFloat() >= chance) return;
        for (EquipmentSlot slot : ArmorSet.SLOTS) {
            ItemStack stack = player.getItemBySlot(slot);
            if (stack.isEmpty() || !stack.isDamageableItem() || !set.matches(stack)) continue;
            int damaged = stack.getDamageValue();
            if (damaged <= 0) continue;
            stack.setDamageValue(Math.max(0, damaged - amount));
        }
    }
}
