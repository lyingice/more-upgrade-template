package net.mcreator.mut.affix;

import net.mcreator.mut.affix.data.*;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class AffixRoller {

    private static final Random RANDOM = new Random();

    public static RollResult roll(ItemStack target, MaterialContext materialCtx) {
        if (target == null || target.isEmpty()) return RollResult.builder().build();
        if (materialCtx == null) materialCtx = MaterialContext.empty();
        int baseEnchant = target.getItem().getEnchantmentValue(target);
        int enchantBonus = materialCtx.getEnchantBonus();
        int enchantValue = baseEnchant + enchantBonus;
        Affix existing = Affix.fromStack(target);
        int existingLevel = existing != null ? Affix.getLevelFromStack(target) : 0;
        int pityBefore = PityTracker.getPity(target);
        float pityMultiplier = PityTracker.getPityBonusMultiplier(target);
        LevelConfig[] levels = AffixDataLoader.getLevels();
        if (levels == null || levels.length == 0) return fallbackRoll(target, existing);
        Map<Integer, Double> weightMap = computeWeights(levels, enchantValue, existingLevel, materialCtx, pityMultiplier);
        Map<Integer, Double> probabilityMap = normalizeLinear(weightMap);
        int selectedLevel = selectLevel(probabilityMap);
        int originalLevel = selectedLevel;
        Affix chosen = selectAffix(target, existing, selectedLevel, materialCtx);
        int maxAllowedLevel = getMaxAllowedLevel(chosen, materialCtx);
        selectedLevel = Math.min(selectedLevel, maxAllowedLevel);
        int jsonMaxLevel = AffixDataLoader.getMaxLevel();
        selectedLevel = Math.min(selectedLevel, jsonMaxLevel);
        // 材料级保底等级强制提升（min_guaranteed_level），杜绝概率精度问题
        int minGuaranteed = materialCtx != null ? materialCtx.getMinGuaranteedLevel() : 0;
        if (minGuaranteed > 0) {
            selectedLevel = Math.max(selectedLevel, minGuaranteed);
        }
        boolean pityTriggered = false;
        int pityAfter = pityBefore;
        if (PityTracker.shouldReset(selectedLevel, target)) {
            PityTracker.resetPity(target); pityAfter = 0; pityTriggered = true;
        } else {
            PityTracker.incrementPity(target); pityAfter = PityTracker.getPity(target);
        }
        RollResult.Builder rb = RollResult.builder().affix(chosen).level(selectedLevel).originalLevel(originalLevel)
                .materialContext(materialCtx).pityBefore(pityBefore).pityAfter(pityAfter).pityTriggered(pityTriggered)
                .addDebug("EnchantValue: " + enchantValue).addDebug("PityBefore: " + pityBefore + " -> After: " + pityAfter)
                .addDebug("Material: " + (materialCtx.isEmpty() ? "none" : materialCtx.getAdditionStack().getItem().getDescription().getString()))
                .addDebug("SelectedLevel: " + selectedLevel + " (original: " + originalLevel + ")")
                .addDebug("Weights: " + weightMap.entrySet().stream().map(e -> "Lv" + e.getKey() + "=" + String.format("%.2f", e.getValue())).collect(Collectors.joining(", ")))
                .addDebug("Probabilities: " + probabilityMap.entrySet().stream().map(e -> "Lv" + e.getKey() + "=" + String.format("%.1f%%", e.getValue() * 100)).collect(Collectors.joining(", ")));
        if (chosen != null) rb.addDebug("ChosenAffix: " + chosen.getId() + " (maxLv: " + maxAllowedLevel + ")");
        if (materialCtx != null && !materialCtx.isEmpty()) {
            for (MaterialContext.AffixBonusEntry bonus : materialCtx.getAffixBonuses()) {
                rb.addDebug("  -> " + bonus.getTargetAffix() + ": fixedProb=" + (bonus.getFixedProbability() * 100) + "%, maxLv: " + bonus.getMaxLevel());
            }
            if (materialCtx.getMinGuaranteedLevel() > 0) rb.addDebug("MinGuaranteedLevel: " + materialCtx.getMinGuaranteedLevel());
            if (materialCtx.getMaxLevelCap() > 0) rb.addDebug("MaxLevelCap: " + materialCtx.getMaxLevelCap());
        }
        return rb.build();
    }

    public static RollResult roll(ItemStack target) { return roll(target, MaterialContext.empty()); }

    private static Map<Integer, Double> computeWeights(LevelConfig[] levels, int enchantValue, int existingLevel, MaterialContext materialCtx, float pityMultiplier) {
        if (AffixDataLoader.isNewPoolSystem()) return computeWeightsNewPool(levels, enchantValue, existingLevel, materialCtx, pityMultiplier);
        else return computeWeightsLegacy(levels, enchantValue, existingLevel, materialCtx, pityMultiplier);
    }

    private static Map<Integer, Double> computeWeightsNewPool(LevelConfig[] levels, int enchantValue, int existingLevel, MaterialContext materialCtx, float pityMultiplier) {
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
            double es = totalBudget / levelCount;
            for (LevelConfig lc : levels) result.put(lc.getLevel(), es);
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
                if (valid) totalValidScore += entry.getValue();
                else clearedScore += entry.getValue();
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

    private static Map<Integer, Double> computeWeightsLegacy(LevelConfig[] levels, int enchantValue, int existingLevel, MaterialContext materialCtx, float pityMultiplier) {
        Map<Integer, Double> result = new LinkedHashMap<>();
        float existingBonusAdd = existingLevel > 0 ? existingLevel * 0.1F : 0F;
        float pityAdd = pityMultiplier - 1.0F;
        double totalBonus = pityAdd + existingBonusAdd;
        double previousFinalScore = 0;
        double bonusPerLevel = AffixDataLoader.getBonusMultiplierPerLevel();
        int levelCount = levels.length;
        int maxLevel = levelCount > 0 ? levels[levelCount - 1].getLevel() : 0;
        // 材料等级限制
        int minLevel = materialCtx != null ? materialCtx.getMinGuaranteedLevel() : 0;
        int maxCap = materialCtx != null ? materialCtx.getMaxLevelCap() : 0;
        if (maxCap == 0) maxCap = AffixDataLoader.getDefaultMaxLevelCap();
        for (LevelConfig lc : levels) {
            int level = lc.getLevel();
            // 跳过超出等级限制的等级（权重归零）
            if ((minLevel > 0 && level < minLevel) || (maxCap > 0 && level > maxCap)) {
                result.put(level, 0.0);
                continue;
            }
            double baseScore = lc.getWeightCurve().computeScore(enchantValue);
            if (baseScore <= 0 && totalBonus > 0) baseScore = totalBonus / level;
            if (previousFinalScore > 0) {
                double leakRatio = Math.min(0.2 * (1.0 + totalBonus), 0.9);
                double inheritedSeed = previousFinalScore * leakRatio;
                baseScore = Math.max(baseScore, inheritedSeed / level);
            }
            double levelRatio = level * bonusPerLevel;
            double extraMult = lc.getExtraBonusMultiplier();
            double multiplier = 1.0 + totalBonus * (levelRatio + extraMult);
            double finalScore = baseScore * multiplier;
            result.put(level, Math.max(0, finalScore));
            previousFinalScore = finalScore;
        }
        return result;
    }

    public static Map<Integer, Double> computeSoftmax(Map<Integer, Double> weightMap) { return normalizeLinear(weightMap); }

    public static Map<Integer, Double> normalizeLinear(Map<Integer, Double> weightMap) {
        Map<Integer, Double> result = new LinkedHashMap<>();
        double totalScore = weightMap.values().stream().mapToDouble(Double::doubleValue).filter(v -> v > 0).sum();
        if (totalScore <= 0) {
            double up = 1.0 / weightMap.size();
            for (var entry : weightMap.entrySet()) result.put(entry.getKey(), up);
            return result;
        }
        double minProb = AffixDataLoader.getMinProbability();
        for (var entry : weightMap.entrySet()) result.put(entry.getKey(), Math.max(entry.getValue() / totalScore, minProb));
        double sumProbs = result.values().stream().mapToDouble(Double::doubleValue).sum();
        if (sumProbs > 0 && Math.abs(sumProbs - 1.0) > 0.001) {
            for (var entry : result.entrySet()) result.put(entry.getKey(), entry.getValue() / sumProbs);
        }
        return result;
    }

    private static int selectLevel(Map<Integer, Double> probabilityMap) {
        double roll = RANDOM.nextDouble();
        double cumulative = 0;
        for (var entry : probabilityMap.entrySet()) { cumulative += entry.getValue(); if (roll < cumulative) return entry.getKey(); }
        return probabilityMap.keySet().stream().mapToInt(Integer::intValue).max().orElse(1);
    }

    private static Affix selectAffix(ItemStack target, Affix existing, int selectedLevel, MaterialContext materialCtx) {
        List<Affix> affixPool = AffixDataLoader.getItemAffixCache().getAffixesForItem(target);
        if (affixPool.isEmpty()) affixPool = List.copyOf(AffixRegistry.getAll());
        if (affixPool.isEmpty()) return null;
        if (existing != null && RANDOM.nextFloat() < 0.6F) { if (affixPool.contains(existing)) return existing; }
        if (!materialCtx.isEmpty() && !materialCtx.getAffixBonuses().isEmpty()) {
            List<Affix> filtered = affixPool;
            if (existing != null) {
                List<Affix> f = affixPool.stream().filter(a -> !a.getId().equals(existing.getId())).toList();
                if (!f.isEmpty()) filtered = f;
            }
            return selectAffixFixedProbability(filtered, materialCtx);
        }
        if (existing != null) {
            List<Affix> filtered = affixPool.stream().filter(a -> !a.getId().equals(existing.getId())).toList();
            if (!filtered.isEmpty()) affixPool = filtered;
        }
        return affixPool.get(RANDOM.nextInt(affixPool.size()));
    }

    private static Affix selectAffixFixedProbability(List<Affix> pool, MaterialContext materialCtx) {
        Map<String, Double> fixedProbs = new HashMap<>();
        double totalFixed = 0;
        for (Affix affix : pool) {
            MaterialContext.AffixBonusEntry bonus = materialCtx.getBonusForAffix(affix.getId());
            if (bonus != null && bonus.getFixedProbability() > 0) { fixedProbs.put(affix.getId(), bonus.getFixedProbability()); totalFixed += bonus.getFixedProbability(); }
        }
        if (totalFixed > 1.0) { for (Map.Entry<String, Double> e : fixedProbs.entrySet()) { fixedProbs.put(e.getKey(), e.getValue() / totalFixed); } totalFixed = 1.0; }
        double remaining = 1.0 - totalFixed;
        int remainingCount = pool.size() - fixedProbs.size();
        Map<Affix, Double> finalProbMap = new LinkedHashMap<>();
        double checkSum = 0;
        for (Affix affix : pool) {
            double p;
            if (fixedProbs.containsKey(affix.getId())) { p = fixedProbs.get(affix.getId()); }
            else if (remainingCount > 0) { p = remaining / remainingCount; }
            else { p = 0; }
            if (p > 0) { finalProbMap.put(affix, p); checkSum += p; }
        }
        if (checkSum <= 0) { for (Affix a : pool) finalProbMap.put(a, 1.0 / pool.size()); checkSum = 1.0; }
        else if (Math.abs(checkSum - 1.0) > 0.001) { for (Map.Entry<Affix, Double> e : finalProbMap.entrySet()) finalProbMap.put(e.getKey(), e.getValue() / checkSum); }
        double roll = RANDOM.nextDouble();
        double cumulative = 0;
        for (Map.Entry<Affix, Double> e : finalProbMap.entrySet()) { cumulative += e.getValue(); if (roll < cumulative) return e.getKey(); }
        return finalProbMap.keySet().iterator().next();
    }

    private static List<Affix> applyMaterialWeight(List<Affix> pool, MaterialContext materialCtx) { return pool; }

    private static int getMaxAllowedLevel(Affix affix, MaterialContext materialCtx) {
        if (affix == null) return 6;
        if (materialCtx == null || materialCtx.isEmpty()) return affix.getMaxLevel();
        int maxLevel = affix.getMaxLevel();
        MaterialContext.AffixBonusEntry bonus = materialCtx.getBonusForAffix(affix.getId());
        if (bonus != null) maxLevel = Math.max(maxLevel, bonus.getMaxLevel());
        // 材料级全局等级上限（max_level_cap）取最小值，防止超限
        if (materialCtx.getMaxLevelCap() > 0) {
            maxLevel = Math.min(maxLevel, materialCtx.getMaxLevelCap());
        }
        return maxLevel;
    }

    private static final Random FALLBACK_RANDOM = new Random();

    private static RollResult fallbackRoll(ItemStack target, Affix existing) {
        if (target == null || target.isEmpty()) return RollResult.builder().addDebug("Fallback: target is empty").build();
        List<Affix> allAffixes = List.copyOf(AffixRegistry.getAll());
        if (allAffixes.isEmpty()) return RollResult.builder().addDebug("Fallback: no affixes registered").build();
        int enchantValue = target.getItem().getEnchantmentValue(target);
        int existingLevel = existing != null ? Affix.getLevelFromStack(target) : 0;
        int rolledLevel = fallbackRollLevel(enchantValue, existingLevel);
        Affix chosen;
        if (existing != null && FALLBACK_RANDOM.nextFloat() < 0.6F) { chosen = existing; }
        else {
            List<Affix> pool = allAffixes;
            if (existing != null) { pool = allAffixes.stream().filter(a -> !a.getId().equals(existing.getId())).toList(); }
            if (pool.isEmpty()) pool = allAffixes;
            chosen = pool.get(FALLBACK_RANDOM.nextInt(pool.size()));
        }
        rolledLevel = Math.min(rolledLevel, chosen.getMaxLevel());
        chosen.applyToStack(target, rolledLevel);
        return RollResult.builder().affix(chosen).level(rolledLevel).originalLevel(rolledLevel)
                .addDebug("Fallback roll - Level: " + rolledLevel + ", Affix: " + chosen.getId())
                .addDebug("EnchantValue: " + enchantValue).addDebug("ExistingLevel: " + existingLevel).build();
    }

    private static int fallbackRollLevel(int enchantValue, int existingLevel) {
        float bonus = existingLevel > 0 ? 0.2F : 0F;
        float baseCommon = Math.max(0, 50 - enchantValue * 2F);
        float baseUncommon = Math.max(0, 25 - enchantValue * 0.5F);
        float baseRare = enchantValue * 1.5F;
        float baseEpic = Math.max(0, enchantValue - 10F);
        float baseLegendary = Math.max(0, enchantValue - 18F);
        float baseMythic = Math.max(0, enchantValue - 25F);
        if (existingLevel > 0) { baseCommon *= (1F - bonus); baseUncommon *= (1F - bonus); baseRare *= (1F + bonus); baseEpic *= (1F + bonus * 2); baseLegendary *= (1F + bonus * 3); baseMythic *= (1F + bonus * 4); }
        float total = baseCommon + baseUncommon + baseRare + baseEpic + baseLegendary + baseMythic;
        float roll = FALLBACK_RANDOM.nextFloat() * total;
        if (roll < baseCommon) return 1; roll -= baseCommon;
        if (roll < baseUncommon) return 2; roll -= baseUncommon;
        if (roll < baseRare) return 3; roll -= baseRare;
        if (roll < baseEpic) return 4; roll -= baseEpic;
        if (roll < baseLegendary) return 5; return 6;
    }

    private static class WeightedAffix {
        final Affix affix;
        WeightedAffix(Affix affix, double weight) { this.affix = affix; }
    }
}
