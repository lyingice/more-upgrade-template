package net.mcreator.mut.affix.effect;

import net.mcreator.mut.affix.Affix;
import net.mcreator.mut.affix.json.AffixJsonLoader;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class DurabilityRepairEffect implements AffixEffect {
    private final String trigger;
    private final int perDurability, saturationPerPoint;

    public DurabilityRepairEffect(String trigger, int perDurability, int saturationPerPoint) {
        this.trigger = trigger; this.perDurability = perDurability; this.saturationPerPoint = saturationPerPoint;
    }
    @Override public String getTrigger() { return trigger; }
    @Override
    public void apply(LivingEntity user, LivingEntity target, ItemStack stack, int level, BlockState brokenBlock) {}
    public int getPerDurability() { return perDurability; }
    public int getSaturationPerPoint() { return saturationPerPoint; }
    public int getMaxRepair(int level) { return Math.round(level * 0.5F); }
    /** 获取所有装备上身第一个 durability_repair 效果的 saturationPerPoint */
    public static int getSaturationPerPoint(LivingEntity entity) {
        for (var slot : EquipmentSlot.values()) {
            Affix affix = Affix.fromStack(entity.getItemBySlot(slot));
            if (affix == null) continue;
            for (AffixEffect e : AffixJsonLoader.getEffects(affix.getId())) {
                if (e instanceof DurabilityRepairEffect dre) return dre.getSaturationPerPoint();
            }
        }
        return 4;
    }

    /** 统计全身 durability_repair 等级总和 */
    public static int getTotalRepairLevel(LivingEntity entity) {
        int total = 0;
        for (var slot : EquipmentSlot.values()) {
            Affix affix = Affix.fromStack(entity.getItemBySlot(slot));
            if (affix == null) continue;
            for (AffixEffect e : AffixJsonLoader.getEffects(affix.getId())) {
                if (e instanceof DurabilityRepairEffect) {
                    total += Affix.getLevelFromStack(entity.getItemBySlot(slot));
                    break;
                }
            }
        }
        return total;
    }
}