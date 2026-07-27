package net.mcreator.mut.affix;

import net.mcreator.mut.affix.data.*;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.ItemStack;
import java.util.*;
import java.util.stream.Collectors;

public class AffixProbabilityPreview {

    public static Map<Integer, Double> computeLevelProbabilities(
            int enchantValue, int existingLevel, MaterialContext materialCtx, int pityCount) {
        LevelConfig[] levels = AffixDataLoader.getLevels();
        if (levels == null || levels.length == 0) return fallbackLevelProbabilities(enchantValue, existingLevel);
        double pityBonus = AffixDataLoader.getPityConfig().getGlobal().getPityBonusPerPoint();
        float pityMultiplier = 1.0F + (float)(pityCount * pityBonus);
        if (pityMultiplier < 1.0F) pityMultiplier = 1.0F;

        Map<Integer, Double> scores = AffixDataLoader.isNewPoolSystem()
                ? computeScoresNewPool(levels, enchantValue, existingLevel, materialCtx, pityMultiplier)
                : computeScoresLegacy(levels, enchantValue, existingLevel, materialCtx, pityMultiplier);
        return AffixRoller.normalizeLinear(scores);
    }

    private static Map<Integer, Double> computeScoresNewPool(
            LevelConfig[] levels, int enchantValue, int existingLevel,
            MaterialContext materialCtx, float pityMultiplier) {
        Map<Integer, Double> result = new LinkedHashMap<>();
        int levelCount = levels.length;
        int maxLevel = levels[levelCount - 1].getLevel();
        double totalBudget = AffixDataLoader.getTotalPoolScaleFactor() * enchantValue * levelCount
                + AffixDataLoader.getTotalPoolBase();
        totalBudget = Math.max(totalBudget, levelCount * 10.0);

        double sumRatio = 0;
        Map<Integer, Double> ratioMap = new LinkedHashMap<>();
        for (LevelConfig lc : levels) {
            double ratio = Math.max(0, lc.getWeightCurve().computeScore(enchantValue));
            ratioMap.put(lc.getLevel(), ratio); sumRatio += ratio;
        }
        if (sumRatio <= 0) {
            double each = totalBudget / levelCount;
            for (LevelConfig lc : levels) result.put(lc.getLevel(), each);
            return result;
        }
        Map<Integer, Double> distributed = new LinkedHashMap<>();
        for (var e : ratioMap.entrySet())
            distributed.put(e.getKey(), Math.max(0, totalBudget * e.getValue() / sumRatio));

        int minLevel = materialCtx != null ? materialCtx.getMinGuaranteedLevel() : 0;
        int maxCap = materialCtx != null ? materialCtx.getMaxLevelCap() : 0;
        if (maxCap == 0) maxCap = AffixDataLoader.getDefaultMaxLevelCap();
        if (minLevel > 0 || maxCap < maxLevel) {
            double cleared = 0, validSum = 0;
            for (var e : distributed.entrySet()) {
                int lv = e.getKey();
                if ((minLevel > 0 && lv < minLevel) || lv > maxCap) cleared += e.getValue();
                else validSum += e.getValue();
            }
            if (cleared > 0 && validSum > 0) {
                for (var e : distributed.entrySet()) {
                    int lv = e.getKey();
                    if ((minLevel > 0 && lv < minLevel) || lv > maxCap) result.put(lv, 0.0);
                    else result.put(lv, e.getValue() + cleared * e.getValue() / validSum);
                }
            } else result.putAll(distributed);
        } else result.putAll(distributed);
        return result;
    }

    @Deprecated
    private static Map<Integer, Double> computeScoresLegacy(
            LevelConfig[] levels, int enchantValue, int existingLevel,
            MaterialContext materialCtx, float pityMultiplier) {
        Map<Integer, Double> scores = new LinkedHashMap<>();
        double previousScore = 0;
        float existingBonus = existingLevel > 0 ? existingLevel * 0.1F : 0F;
        float pityAdd = pityMultiplier - 1.0F;
        double totalBonus = pityAdd + existingBonus;
        double bonusPerLevel = AffixDataLoader.getBonusMultiplierPerLevel();
        int levelCount = levels.length;
        int maxLevel = levelCount > 0 ? levels[levelCount - 1].getLevel() : 0;
        int minLevel = materialCtx != null ? materialCtx.getMinGuaranteedLevel() : 0;
        int maxCap = materialCtx != null ? materialCtx.getMaxLevelCap() : 0;
        if (maxCap == 0) maxCap = AffixDataLoader.getDefaultMaxLevelCap();

        for (LevelConfig lc : levels) {
            int level = lc.getLevel();
            if ((minLevel > 0 && level < minLevel) || (maxCap > 0 && level > maxCap)) {
                scores.put(level, 0.0); continue;
            }
            double score = lc.getWeightCurve().computeScore(enchantValue);
            if (score <= 0 && totalBonus > 0) score = totalBonus / level;
            if (previousScore > 0) {
                double leak = Math.min(0.2 * (1.0 + totalBonus), 0.9);
                score = Math.max(score, previousScore * leak / level);
            }
            double multiplier = 1.0 + totalBonus * (level * bonusPerLevel + lc.getExtraBonusMultiplier());
            double finalScore = score * multiplier;
            scores.put(level, Math.max(0, finalScore));
            previousScore = finalScore;
        }
        return scores;
    }

    public static List<String> generateLevelProbabilityAsText(
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

    @Deprecated
    private static Map<Integer, Double> fallbackLevelProbabilities(int enchantValue, int existingLevel) {
        float bonus = existingLevel > 0 ? 0.2F : 0F;
        float[] bases = {
            Math.max(0, 50 - enchantValue * 2F),
            Math.max(0, 25 - enchantValue * 0.5F),
            enchantValue * 1.5F,
            Math.max(0, enchantValue - 10F),
            Math.max(0, enchantValue - 18F),
            Math.max(0, enchantValue - 25F)
        };
        if (existingLevel > 0) {
            bases[0] *= (1F - bonus); bases[1] *= (1F - bonus);
            bases[2] *= (1F + bonus); bases[3] *= (1F + bonus * 2);
            bases[4] *= (1F + bonus * 3); bases[5] *= (1F + bonus * 4);
        }
        float total = 0; for (float b : bases) total += b;
        if (total <= 0) total = 1;
        Map<Integer, Double> probs = new LinkedHashMap<>();
        for (int i = 0; i < 6; i++) probs.put(i + 1, (double)(bases[i] / total));
        return probs;
    }
}
