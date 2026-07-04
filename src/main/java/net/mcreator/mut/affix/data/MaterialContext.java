package net.mcreator.mut.affix.data;

import net.minecraft.world.item.ItemStack;
import javax.annotation.Nullable;
import java.util.List;

public class MaterialContext {
    private final ItemStack additionStack;
    private final int enchantBonus;
    private final List<AffixBonusEntry> affixBonuses;
    private final int minGuaranteedLevel;
    private final int maxLevelCap;

    public MaterialContext(ItemStack additionStack, int enchantBonus,
                           List<AffixBonusEntry> affixBonuses,
                           int minGuaranteedLevel, int maxLevelCap) {
        this.additionStack = additionStack;
        this.enchantBonus = enchantBonus;
        this.affixBonuses = affixBonuses;
        this.minGuaranteedLevel = minGuaranteedLevel;
        this.maxLevelCap = maxLevelCap;
    }

    public ItemStack getAdditionStack() { return additionStack; }
    public int getEnchantBonus() { return enchantBonus; }
    public List<AffixBonusEntry> getAffixBonuses() { return affixBonuses; }
    public int getMinGuaranteedLevel() { return minGuaranteedLevel; }
    public int getMaxLevelCap() { return maxLevelCap; }
    public boolean isEmpty() { return additionStack.isEmpty(); }

    @Nullable
    public AffixBonusEntry getBonusForAffix(String affixId) {
        for (AffixBonusEntry entry : affixBonuses) {
            if (entry.targetAffix.equals(affixId)) return entry;
        }
        return null;
    }

    public boolean hasAffinityFor(String affixId) { return getBonusForAffix(affixId) != null; }

    public static MaterialContext empty() { return new MaterialContext(ItemStack.EMPTY, 0, List.of(), 0, 0); }

    public static class AffixBonusEntry {
        private final String targetAffix;
        private final double fixedProbability;
        private final int minLevel;
        private final int maxLevel;

        public AffixBonusEntry(String targetAffix, double fixedProbability, int minLevel, int maxLevel) {
            this.targetAffix = targetAffix;
            this.fixedProbability = fixedProbability;
            this.minLevel = minLevel;
            this.maxLevel = maxLevel;
        }

        public String getTargetAffix() { return targetAffix; }
        public double getFixedProbability() { return fixedProbability; }
        public int getMinLevel() { return minLevel; }
        public int getMaxLevel() { return maxLevel; }
    }
}
