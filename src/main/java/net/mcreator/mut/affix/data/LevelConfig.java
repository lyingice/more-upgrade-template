package net.mcreator.mut.affix.data;

import com.google.gson.annotations.SerializedName;

/**
 * LevelConfig 的根容器 - 用于解析带 "levels" 字段的 JSON
 */
public class LevelConfig {

    private int level;
    @SerializedName("name_key")
    private String nameKey;
    private String color;
    private String formatting;
    @SerializedName("weight_curve")
    private WeightCurve weightCurve;
    @SerializedName("extra_bonus_multiplier")
    private double extraBonusMultiplier = 0.0;

    public int getLevel() { return level; }
    public String getNameKey() { return nameKey; }
    public String getColor() { return color; }
    public String getFormatting() { return formatting; }
    public WeightCurve getWeightCurve() { return weightCurve; }
    public double getExtraBonusMultiplier() { return extraBonusMultiplier; }

    public void setLevel(int level) { this.level = level; }
    public void setNameKey(String nameKey) { this.nameKey = nameKey; }
    public void setColor(String color) { this.color = color; }
    public void setFormatting(String formatting) { this.formatting = formatting; }
    public void setWeightCurve(WeightCurve weightCurve) { this.weightCurve = weightCurve; }
    public void setExtraBonusMultiplier(double extraBonusMultiplier) { this.extraBonusMultiplier = extraBonusMultiplier; }

    public static class Root {
        private LevelConfig[] levels;
        @SerializedName("min_probability_per_level")
        private double minProbabilityPerLevel = 0.003;
        @SerializedName("bonus_multiplier_per_level")
        private double bonusMultiplierPerLevel = 0.3;

        @SerializedName("total_pool_scale_factor")
        private double totalPoolScaleFactor = 0;

        @SerializedName("total_pool_base")
        private double totalPoolBase = 0;

        /** 默认最大等级限制（材料未设置 max_level_cap 时使用，默认5） */
        @SerializedName("default_max_level_cap")
        private int defaultMaxLevelCap = 5;

        public LevelConfig[] getLevels() { return levels; }
        public double getMinProbabilityPerLevel() { return minProbabilityPerLevel; }
        public double getBonusMultiplierPerLevel() { return bonusMultiplierPerLevel; }
        public double getTotalPoolScaleFactor() { return totalPoolScaleFactor; }
        public double getTotalPoolBase() { return totalPoolBase; }
        public int getDefaultMaxLevelCap() { return defaultMaxLevelCap; }

        public void setLevels(LevelConfig[] levels) { this.levels = levels; }
        public void setMinProbabilityPerLevel(double minProbabilityPerLevel) { this.minProbabilityPerLevel = minProbabilityPerLevel; }
        public void setBonusMultiplierPerLevel(double bonusMultiplierPerLevel) { this.bonusMultiplierPerLevel = bonusMultiplierPerLevel; }
        public void setTotalPoolScaleFactor(double totalPoolScaleFactor) { this.totalPoolScaleFactor = totalPoolScaleFactor; }
        public void setTotalPoolBase(double totalPoolBase) { this.totalPoolBase = totalPoolBase; }
        public void setDefaultMaxLevelCap(int defaultMaxLevelCap) { this.defaultMaxLevelCap = defaultMaxLevelCap; }

        public boolean isNewPoolSystem() {
            return totalPoolScaleFactor > 0;
        }
    }

    public static class WeightCurve {
        private double base = 0;
        private double decay = 0;
        private double growth = 0;
        private int threshold = 0;

        public double getBase() { return base; }
        public double getDecay() { return decay; }
        public double getGrowth() { return growth; }
        public int getThreshold() { return threshold; }

        public void setBase(double base) { this.base = base; }
        public void setDecay(double decay) { this.decay = decay; }
        public void setGrowth(double growth) { this.growth = growth; }
        public void setThreshold(int threshold) { this.threshold = threshold; }

        public double computeScore(int enchantValue) {
            double baseScore = base * (1.0 - decay * 0.5);
            if (enchantValue > threshold) {
                return baseScore + (enchantValue - threshold) * growth;
            }
            return Math.max(0, baseScore);
        }
    }
}
