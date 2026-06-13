package net.mcreator.mut.affix.data;

import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 材料上下文 - 包含槽位2中的材料及其所有加成信息
 */
public class MaterialContext {

    private final ItemStack additionStack;
    private final double universalLevelBonus;       // 通用等级权重加成
    private final List<AffixBonusEntry> affixBonuses; // 针对特定词缀的加成
    private final int materialTier;                   // 材料品质层级
    private final double tierLevelBonus;              // 层级带来的额外等级加成

    public MaterialContext(ItemStack additionStack, double universalLevelBonus,
                           List<AffixBonusEntry> affixBonuses,
                           int materialTier, double tierLevelBonus) {
        this.additionStack = additionStack;
        this.universalLevelBonus = universalLevelBonus;
        this.affixBonuses = affixBonuses;
        this.materialTier = materialTier;
        this.tierLevelBonus = tierLevelBonus;
    }

    public ItemStack getAdditionStack() { return additionStack; }
    public double getUniversalLevelBonus() { return universalLevelBonus; }
    public List<AffixBonusEntry> getAffixBonuses() { return affixBonuses; }
    public int getMaterialTier() { return materialTier; }
    public double getTierLevelBonus() { return tierLevelBonus; }

    public boolean isEmpty() {
        return additionStack.isEmpty();
    }

    /**
     * 获取针对特定词缀的加成
     */
    @Nullable
    public AffixBonusEntry getBonusForAffix(String affixId) {
        for (AffixBonusEntry entry : affixBonuses) {
            if (entry.targetAffix.equals(affixId)) {
                return entry;
            }
        }
        return null;
    }

    /**
     * 判断材料是否指定了某词缀的 multiplier
     */
    public boolean hasAffinityFor(String affixId) {
        return getBonusForAffix(affixId) != null;
    }

    /**
     * 创建一个空的材料上下文（没有额外材料时使用）
     */
    public static MaterialContext empty() {
        return new MaterialContext(ItemStack.EMPTY, 0.0, List.of(), 0, 0.0);
    }

    /**
     * 词缀加成条目（运行时使用）
     */
    public static class AffixBonusEntry {
        private final String targetAffix;
        private final double affixWeightMultiplier;
        private final double levelWeightBonus;
        private final int minLevel;
        private final int maxLevel;

        public AffixBonusEntry(String targetAffix, double affixWeightMultiplier,
                               double levelWeightBonus, int minLevel, int maxLevel) {
            this.targetAffix = targetAffix;
            this.affixWeightMultiplier = affixWeightMultiplier;
            this.levelWeightBonus = levelWeightBonus;
            this.minLevel = minLevel;
            this.maxLevel = maxLevel;
        }

        public String getTargetAffix() { return targetAffix; }
        public double getAffixWeightMultiplier() { return affixWeightMultiplier; }
        public double getLevelWeightBonus() { return levelWeightBonus; }
        public int getMinLevel() { return minLevel; }
        public int getMaxLevel() { return maxLevel; }
    }
}
