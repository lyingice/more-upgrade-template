package net.mcreator.mut.affix;

import net.mcreator.mut.affix.data.*;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.ItemStack;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class AffixProbabilityPreview {

    public static Map<Integer, Double> computeLevelProbabilities(
            int enchantValue, int existingLevel, MaterialContext materialCtx, int pityCount) {
        LevelConfig[] levels = AffixDataLoader.getLevels();
        if (levels == null || levels.length == 0) return fallbackLevelProbabilities(enchantValue, existingLevel);
        double pityBonusPerPoint = AffixDataLoader.getPityConfig().getGlobal().getPityBonusPerPoint();
        float pityMultiplier = 1.0F + (float)(pityCount * pityBonusPerPoint);
        if (pityMultiplier < 1.0F) pityMultiplier = 1.0F;
        Map<Integer, Double> scores;
        if (AffixDataLoader.isNewPoolSystem()) {
            scores = computeScoresNewPool(levels, enchantValue, existingLevel, materialCtx, pityMultiplier);
        } else {
            scores = computeScoresLegacy(levels, enchantValue, existingLevel, materialCtx, pityMultiplier);
        }
        return AffixRoller.normalizeLinear(scores);
    }

    private static Map<Integer, Double> computeScoresNewPool(
            LevelConfig[] levels, int enchantValue, int existingLevel,
            MaterialContext materialCtx, float pityMultiplier) {
        Map<Integer, Double> result = new LinkedHashMap<>();
        int levelCount = levels.length;
        int maxLevel = levels[levelCount - 1].getLevel();
        double scaleFactor = AffixDataLoader.getTotalPoolScaleFactor();
        double basePool = AffixDataLoader.getTotalPoolBase();
        double totalBudget = scaleFactor * enchantValue * levelCount + basePool;
        totalBudget = Math.max(totalBudget, levelCount * 10.0);
        double sumRatio = 0;
        Map<Integer, Double> ratioMap = new LinkedHashMap<>();
        for (LevelConfig lc : levels) {
            int level = lc.getLevel();
            double baseRatio = lc.getWeightCurve().computeScore(enchantValue);
            baseRatio = Math.max(baseRatio, 0);
            double ratio = baseRatio;
            ratio = Math.max(0, ratio);
            ratioMap.put(level, ratio);
            sumRatio += ratio;
        }
        if (sumRatio <= 0) {
            double equalShare = totalBudget / levelCount;
            for (LevelConfig lc : levels) result.put(lc.getLevel(), equalShare);
            return result;
        }
        Map<Integer, Double> distributedScores = new LinkedHashMap<>();
        for (var entry : ratioMap.entrySet()) {
            double score = totalBudget * entry.getValue() / sumRatio;
            distributedScores.put(entry.getKey(), Math.max(0, score));
        }
        int minLevel = materialCtx != null ? materialCtx.getMinGuaranteedLevel() : 0;
        int maxCap = materialCtx != null ? materialCtx.getMaxLevelCap() : 0;
        if (maxCap == 0) maxCap = AffixDataLoader.getDefaultMaxLevelCap();
        if (minLevel > 0 || maxCap < maxLevel) {
            double clearedScore = 0;
            double totalValidScore = 0;
            for (var entry : distributedScores.entrySet()) {
                int level = entry.getKey();
                boolean valid = true;
                if (minLevel > 0 && level < minLevel) valid = false;
                if (level > maxCap) valid = false;
                if (valid) { totalValidScore += entry.getValue(); }
                else { clearedScore += entry.getValue(); }
            }
            if (clearedScore > 0 && totalValidScore > 0) {
                for (var entry : distributedScores.entrySet()) {
                    int level = entry.getKey();
                    boolean valid = true;
                    if (minLevel > 0 && level < minLevel) valid = false;
                    if (level > maxCap) valid = false;
                    if (valid) {
                        double extra = clearedScore * entry.getValue() / totalValidScore;
                        result.put(level, entry.getValue() + extra);
                    }
                }
            } else { result.putAll(distributedScores); }
        } else { result.putAll(distributedScores); }
        return result;
    }

    private static Map<Integer, Double> computeScoresLegacy(
            LevelConfig[] levels, int enchantValue, int existingLevel,
            MaterialContext materialCtx, float pityMultiplier) {
        Map<Integer, Double> scores = new LinkedHashMap<>();
        double previousFinalScore = 0;
        float existingBonusAdd = existingLevel > 0 ? existingLevel * 0.1F : 0F;
        float pityAdd = pityMultiplier - 1.0F;
        double totalBonus = pityAdd + existingBonusAdd;
        double bonusPerLevel = AffixDataLoader.getBonusMultiplierPerLevel();
        int levelCount = levels.length;
        int maxLevel = levelCount > 0 ? levels[levelCount - 1].getLevel() : 0;
        // 材料等级限制（Legacy 路径也需要，防御性编程）
        int minLevel = materialCtx != null ? materialCtx.getMinGuaranteedLevel() : 0;
        int maxCap = materialCtx != null ? materialCtx.getMaxLevelCap() : 0;
        if (maxCap == 0) maxCap = AffixDataLoader.getDefaultMaxLevelCap();
        for (LevelConfig lc : levels) {
            int level = lc.getLevel();
            // 跳过超出等级限制的等级（权重归零）
            if ((minLevel > 0 && level < minLevel) || (maxCap > 0 && level > maxCap)) {
                scores.put(level, 0.0);
                continue;
            }
            double score = lc.getWeightCurve().computeScore(enchantValue);
            if (score <= 0 && totalBonus > 0) score = totalBonus / level;
            if (previousFinalScore > 0) {
                double leakRatio = Math.min(0.2 * (1.0 + totalBonus), 0.9);
                double inheritedSeed = previousFinalScore * leakRatio;
                score = Math.max(score, inheritedSeed / level);
            }
            double levelRatio = level * bonusPerLevel;
            double extraMult = lc.getExtraBonusMultiplier();
            double multiplier = 1.0 + totalBonus * (levelRatio + extraMult);
            double finalScore = score * multiplier;
            scores.put(level, Math.max(0, finalScore));
            previousFinalScore = finalScore;
        }
        return scores;
    }

    public static java.util.List<String> generateProbabilityLines(
            int enchantValue, int existingLevel, MaterialContext materialCtx, int pityCount) {
        return generateLevelProbabilityAsText(enchantValue, existingLevel, materialCtx, pityCount);
    }

    public static java.util.List<String> generateLevelProbabilityAsText(
            int enchantValue, int existingLevel, MaterialContext materialCtx, int pityCount) {
        Map<Integer, Double> probs = computeLevelProbabilities(enchantValue, existingLevel, materialCtx, pityCount);
        return probs.entrySet().stream().map(e -> {
            LevelConfig config = AffixDataLoader.getLevel(e.getKey());
            String colorCode = "§f";
            if (config != null && config.getFormatting() != null) {
                ChatFormatting fmt = ChatFormatting.getByName(config.getFormatting());
                if (fmt != null) colorCode = "§" + fmt.getChar();
            }
            String bar = generateBar(e.getValue(), 16);
            return String.format(" %sLv%d %s§7 %5.1f%%", colorCode, e.getKey(), bar, e.getValue() * 100);
        }).collect(Collectors.toList());
    }

    private static String generateBar(double probability, int maxChars) {
        int filled = (int) Math.round(probability * maxChars);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < maxChars; i++) sb.append(i < filled ? '■' : ' ');
        return sb.toString();
    }

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
