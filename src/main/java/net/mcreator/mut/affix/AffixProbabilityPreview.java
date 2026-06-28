package net.mcreator.mut.affix;

import net.mcreator.mut.affix.data.*;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 概率预览计算器 - 在 GUI/JEI 中显示各等级和各种词缀的出现概率
 * 纯计算，不修改物品，可安全在客户端调用
 *
 * 二改：同步支持新"总分池+比率分配+分数再分配"方案与旧方案
 */
public class AffixProbabilityPreview {

    /**
     * 计算各等级的出现概率
     *
     * @param enchantValue   物品附魔能力
     * @param existingLevel  已有词缀等级（0=无）
     * @param materialCtx    材料上下文
     * @param pityCount      当前 pity 值
     * @return Map<等级, 概率百分比 (0-100)>
     */
    public static Map<Integer, Double> computeLevelProbabilities(
            int enchantValue, int existingLevel, MaterialContext materialCtx, int pityCount) {

        LevelConfig[] levels = AffixDataLoader.getLevels();
        if (levels == null || levels.length == 0) {
            return fallbackLevelProbabilities(enchantValue, existingLevel);
        }

        // 计算 pity 倍率
        double pityBonusPerPoint = AffixDataLoader.getPityConfig()
                .getGlobal().getPityBonusPerPoint();
        float pityMultiplier = 1.0F + (float)(pityCount * pityBonusPerPoint);
        if (pityMultiplier < 1.0F) pityMultiplier = 1.0F;

        Map<Integer, Double> scores;
        if (AffixDataLoader.isNewPoolSystem()) {
            scores = computeScoresNewPool(levels, enchantValue, existingLevel, materialCtx, pityMultiplier);
        } else {
            scores = computeScoresLegacy(levels, enchantValue, existingLevel, materialCtx, pityMultiplier);
        }

        // 线性归一化（取代 Softmax）
        return AffixRoller.normalizeLinear(scores);
    }

    // ====== 新：总分池+比率分配+分数再分配（与 AffixRoller 对称） ======

    private static Map<Integer, Double> computeScoresNewPool(
            LevelConfig[] levels, int enchantValue, int existingLevel,
            MaterialContext materialCtx, float pityMultiplier) {

        Map<Integer, Double> result = new LinkedHashMap<>();
        int levelCount = levels.length;
        int maxLevel = levels[levelCount - 1].getLevel();

        // 总分池
        double scaleFactor = AffixDataLoader.getTotalPoolScaleFactor();
        double basePool = AffixDataLoader.getTotalPoolBase();
        double totalBudget = scaleFactor * enchantValue * levelCount + basePool;
        totalBudget = Math.max(totalBudget, levelCount * 10.0);

        // 材料加成
        double materialLevelBonus = materialCtx != null ? materialCtx.getUniversalLevelBonus() : 0.0;
        double materialTierBonus = materialCtx != null ? materialCtx.getTierLevelBonus() : 0.0;
        float pityAdd = pityMultiplier - 1.0F;
        float existingBonusAdd = existingLevel > 0 ? existingLevel * 0.1F : 0F;
        double totalBonus = materialLevelBonus + materialTierBonus + pityAdd + existingBonusAdd;

        // 计算比率
        LevelFactorConfig factorConfig = AffixDataLoader.getLevelFactorConfig();
        double sumRatio = 0;
        Map<Integer, Double> ratioMap = new LinkedHashMap<>();

        for (LevelConfig lc : levels) {
            int level = lc.getLevel();

            double baseRatio = lc.getWeightCurve().computeScore(enchantValue);
            baseRatio = Math.max(baseRatio, 0);

            // 材料加成影响比率
            if (totalBonus > 0) {
                double levelWeightedBonus = totalBonus * (double) level / maxLevel;
                baseRatio = baseRatio * (1.0 + levelWeightedBonus);
            }

            // 羽化因子
            double ascension = 0;
            if (factorConfig != null && factorConfig.isEnabled()) {
                double baseAsc = factorConfig.getAscensionFactorial();
                double individual = 0;
                LevelFactorConfig.PerLevelOverride override = factorConfig.getPerLevelOverride(level);
                if (override != null) individual = override.getAscensionExtra();
                ascension = (level - 1) * baseAsc + individual;
            }

            // 蜕变因子
            double degeneration = 0;
            if (factorConfig != null && factorConfig.isEnabled()) {
                double baseDeg = factorConfig.getDegenerationFactorial();
                double individual = 0;
                LevelFactorConfig.PerLevelOverride override = factorConfig.getPerLevelOverride(level);
                if (override != null) individual = override.getDegenerationExtra();
                degeneration = (maxLevel - level) * baseDeg + individual;
            }

            double ratio = baseRatio + ascension - degeneration;
            ratio = Math.max(0, ratio);

            ratioMap.put(level, ratio);
            sumRatio += ratio;
        }

        if (sumRatio <= 0) {
            double equalShare = totalBudget / levelCount;
            for (LevelConfig lc : levels) {
                result.put(lc.getLevel(), equalShare);
            }
            return result;
        }

        Map<Integer, Double> distributedScores = new LinkedHashMap<>();
        for (var entry : ratioMap.entrySet()) {
            double score = totalBudget * entry.getValue() / sumRatio;
            distributedScores.put(entry.getKey(), Math.max(0, score));
        }

        // 材料等级限制 → 分数再分配
        int minLevel = materialCtx != null ? materialCtx.getMinGuaranteedLevel() : 0;
        int maxCap = materialCtx != null ? materialCtx.getMaxLevelCap() : 0;

        if (minLevel > 0 || maxCap > 0) {
            double clearedScore = 0;
            double totalValidScore = 0;

            for (var entry : distributedScores.entrySet()) {
                int level = entry.getKey();
                boolean valid = true;
                if (minLevel > 0 && level < minLevel) valid = false;
                if (maxCap > 0 && level > maxCap) valid = false;

                if (valid) {
                    totalValidScore += entry.getValue();
                } else {
                    clearedScore += entry.getValue();
                }
            }

            if (clearedScore > 0 && totalValidScore > 0) {
                for (var entry : distributedScores.entrySet()) {
                    int level = entry.getKey();
                    boolean valid = true;
                    if (minLevel > 0 && level < minLevel) valid = false;
                    if (maxCap > 0 && level > maxCap) valid = false;

                    if (valid) {
                        double extra = clearedScore * entry.getValue() / totalValidScore;
                        result.put(level, entry.getValue() + extra);
                    }
                }
            } else {
                result.putAll(distributedScores);
            }
        } else {
            result.putAll(distributedScores);
        }

        return result;
    }

    // ====== 旧：基分×乘法修正（向后兼容） ======

    private static Map<Integer, Double> computeScoresLegacy(
            LevelConfig[] levels, int enchantValue, int existingLevel,
            MaterialContext materialCtx, float pityMultiplier) {

        Map<Integer, Double> scores = new LinkedHashMap<>();
        double previousFinalScore = 0;

        float existingBonusAdd = existingLevel > 0 ? existingLevel * 0.1F : 0F;
        double matLevelBonus = materialCtx != null ? materialCtx.getUniversalLevelBonus() : 0.0;
        double matTierBonus = materialCtx != null ? materialCtx.getTierLevelBonus() : 0.0;
        float pityAdd = pityMultiplier - 1.0F;
        double totalBonus = matLevelBonus + matTierBonus + pityAdd + existingBonusAdd;
        double bonusPerLevel = AffixDataLoader.getBonusMultiplierPerLevel();

        for (LevelConfig lc : levels) {
            double score = lc.getWeightCurve().computeScore(enchantValue);

            if (score <= 0 && totalBonus > 0) {
                score = totalBonus / lc.getLevel();
            }

            if (previousFinalScore > 0) {
                double leakRatio = Math.min(0.2 * (1.0 + totalBonus), 0.9);
                double inheritedSeed = previousFinalScore * leakRatio;
                score = Math.max(score, inheritedSeed / lc.getLevel());
            }

            double levelRatio = lc.getLevel() * bonusPerLevel;
            double extraMult = lc.getExtraBonusMultiplier();
            double multiplier = 1.0 + totalBonus * (levelRatio + extraMult);
            double finalScore = score * multiplier;

            scores.put(lc.getLevel(), Math.max(0, finalScore));
            previousFinalScore = finalScore;
        }

        return scores;
    }

    // ========== 可读文本生成 ==========

    /**
     * 生成可读的概率文本行（带进度条）
     */
    public static java.util.List<String> generateProbabilityLines(
            int enchantValue, int existingLevel, MaterialContext materialCtx, int pityCount) {

        return generateLevelProbabilityAsText(enchantValue, existingLevel, materialCtx, pityCount);
    }

    /**
     * 生成可读的概率文本行（带进度条）
     */
    public static java.util.List<String> generateLevelProbabilityAsText(
            int enchantValue, int existingLevel, MaterialContext materialCtx, int pityCount) {

        Map<Integer, Double> probs = computeLevelProbabilities(enchantValue, existingLevel, materialCtx, pityCount);

        return probs.entrySet().stream()
                .map(e -> {
                    LevelConfig config = AffixDataLoader.getLevel(e.getKey());
                    String colorCode = "§f";
                    if (config != null && config.getFormatting() != null) {
                        ChatFormatting fmt = ChatFormatting.getByName(config.getFormatting());
                        if (fmt != null) {
                            colorCode = "§" + fmt.getChar();
                        }
                    }
                    String bar = generateBar(e.getValue(), 16);
                    return String.format(" %sLv%d %s§7 %5.1f%%",
                            colorCode, e.getKey(), bar, e.getValue() * 100);
                })
                .collect(Collectors.toList());
    }

    /**
     * 生成 ASCII 进度条
     */
    private static String generateBar(double probability, int maxChars) {
        int filled = (int) Math.round(probability * maxChars);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < maxChars; i++) {
            sb.append(i < filled ? '■' : '□');
        }
        return sb.toString();
    }

    // ========== 回退 ==========

    /**
     * 回退到旧版概率计算（当 JSON 未加载时）
     */
    private static Map<Integer, Double> fallbackLevelProbabilities(int enchantValue, int existingLevel) {
        float bonus = existingLevel > 0 ? 0.2F : 0F;

        float baseCommon = Math.max(0, 50 - enchantValue * 2F);
        float baseUncommon = Math.max(0, 25 - enchantValue * 0.5F);
        float baseRare = enchantValue * 1.5F;
        float baseEpic = Math.max(0, enchantValue - 10F);
        float baseLegendary = Math.max(0, enchantValue - 18F);
        float baseMythic = Math.max(0, enchantValue - 25F);

        if (existingLevel > 0) {
            baseCommon *= (1F - bonus);
            baseUncommon *= (1F - bonus);
            baseRare *= (1F + bonus);
            baseEpic *= (1F + bonus * 2);
            baseLegendary *= (1F + bonus * 3);
            baseMythic *= (1F + bonus * 4);
        }

        float total = baseCommon + baseUncommon + baseRare + baseEpic + baseLegendary + baseMythic;
        if (total <= 0) total = 1;

        Map<Integer, Double> probs = new LinkedHashMap<>();
        probs.put(1, (double)(baseCommon / total));
        probs.put(2, (double)(baseUncommon / total));
        probs.put(3, (double)(baseRare / total));
        probs.put(4, (double)(baseEpic / total));
        probs.put(5, (double)(baseLegendary / total));
        probs.put(6, (double)(baseMythic / total));
        return probs;
    }
}
