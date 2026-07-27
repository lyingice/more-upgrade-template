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
        int enchantValue = baseEnchant + materialCtx.getEnchantBonus();

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

        int minGuaranteed = materialCtx != null ? materialCtx.getMinGuaranteedLevel() : 0;
        if (minGuaranteed > 0) selectedLevel = Math.max(selectedLevel, minGuaranteed);

        boolean pityTriggered = false;
        int pityAfter = pityBefore;
        if (PityTracker.shouldReset(selectedLevel, target)) {
            PityTracker.resetPity(target); pityAfter = 0; pityTriggered = true;
        } else {
            PityTracker.incrementPity(target); pityAfter = PityTracker.getPity(target);
        }

        RollResult.Builder rb = RollResult.builder()
                .affix(chosen).level(selectedLevel).originalLevel(originalLevel)
                .materialContext(materialCtx).pityBefore(pityBefore).pityAfter(pityAfter)
                .pityTriggered(pityTriggered)
                .addDebug("EnchantValue: " + enchantValue)
                .addDebug("PityBefore: " + pityBefore + " -> After: " + pityAfter)
                .addDebug("Material: " + (materialCtx.isEmpty() ? "none" : materialCtx.getAdditionStack().getItem().getDescription().getString()))
                .addDebug("SelectedLevel: " + selectedLevel + " (original: " + originalLevel + ")")
                .addDebug("Weights: " + weightMap.entrySet().stream().map(e -> "Lv" + e.getKey() + "=" + String.format("%.2f", e.getValue())).collect(Collectors.joining(", ")))
                .addDebug("Probabilities: " + probabilityMap.entrySet().stream().map(e -> "Lv" + e.getKey() + "=" + String.format("%.1f%%", e.getValue() * 100)).collect(Collectors.joining(", ")));
        if (chosen != null) rb.addDebug("ChosenAffix: " + chosen.getId() + " (maxLv: " + maxAllowedLevel + ")");
        if (materialCtx != null && !materialCtx.isEmpty()) {
            for (MaterialContext.AffixBonusEntry bonus : materialCtx.getAffixBonuses())
                rb.addDebug("  -> " + bonus.getTargetAffix() + ": fixedProb=" + (bonus.getFixedProbability() * 100) + "%, maxLv: " + bonus.getMaxLevel());
            if (materialCtx.getMinGuaranteedLevel() > 0) rb.addDebug("MinGuaranteedLevel: " + materialCtx.getMinGuaranteedLevel());
            if (materialCtx.getMaxLevelCap() > 0) rb.addDebug("MaxLevelCap: " + materialCtx.getMaxLevelCap());
        }
        return rb.build();
    }

    public static RollResult roll(ItemStack target) { return roll(target, MaterialContext.empty()); }

    private static Map<Integer, Double> computeWeights(LevelConfig[] levels, int enchantValue,
            int existingLevel, MaterialContext materialCtx, float pityMultiplier) {
        return AffixDataLoader.isNewPoolSystem()
                ? computeWeightsNewPool(levels, enchantValue, existingLevel, materialCtx, pityMultiplier)
                : computeWeightsLegacy(levels, enchantValue, existingLevel, materialCtx, pityMultiplier);
    }

    private static Map<Integer, Double> computeWeightsNewPool(LevelConfig[] levels, int enchantValue,
            int existingLevel, MaterialContext materialCtx, float pityMultiplier) {
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
            double equal = totalBudget / levelCount;
            for (LevelConfig lc : levels) result.put(lc.getLevel(), equal);
            return result;
        }
        for (var e : ratioMap.entrySet())
            result.put(e.getKey(), Math.max(0, totalBudget * e.getValue() / sumRatio));

        int minLevel = materialCtx != null ? materialCtx.getMinGuaranteedLevel() : 0;
        int maxCap = materialCtx != null ? materialCtx.getMaxLevelCap() : 0;
        if (maxCap == 0) maxCap = AffixDataLoader.getDefaultMaxLevelCap();
        if (minLevel > 0 || maxCap < maxLevel)
            result = redistributeClearedScores(result, minLevel, maxCap);
        return result;
    }

    @Deprecated
    private static Map<Integer, Double> computeWeightsLegacy(LevelConfig[] levels, int enchantValue,
            int existingLevel, MaterialContext materialCtx, float pityMultiplier) {
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
            double levelRatio = level * bonusPerLevel;
            double multiplier = 1.0 + totalBonus * (levelRatio + lc.getExtraBonusMultiplier());
            double finalScore = score * multiplier;
            scores.put(level, Math.max(0, finalScore));
            previousScore = finalScore;
        }
        return scores;
    }

    private static Map<Integer, Double> redistributeClearedScores(Map<Integer, Double> input, int minLevel, int maxCap) {
        double cleared = 0, validSum = 0;
        for (var e : input.entrySet()) {
            int lv = e.getKey();
            if ((minLevel > 0 && lv < minLevel) || lv > maxCap) cleared += e.getValue();
            else validSum += e.getValue();
        }
        if (cleared <= 0 || validSum <= 0) return input;
        Map<Integer, Double> out = new LinkedHashMap<>();
        for (var e : input.entrySet()) {
            int lv = e.getKey();
            if ((minLevel > 0 && lv < minLevel) || lv > maxCap) out.put(lv, 0.0);
            else out.put(lv, e.getValue() + cleared * e.getValue() / validSum);
        }
        return out;
    }

    static Map<Integer, Double> normalizeLinear(Map<Integer, Double> scores) {
        double sum = scores.values().stream().mapToDouble(Double::doubleValue).sum();
        if (sum <= 0) {
            Map<Integer, Double> uniform = new LinkedHashMap<>();
            double each = 1.0 / scores.size();
            for (int k : scores.keySet()) uniform.put(k, each);
            return uniform;
        }
        Map<Integer, Double> result = new LinkedHashMap<>();
        for (var e : scores.entrySet()) result.put(e.getKey(), e.getValue() / sum);
        return result;
    }

    private static int selectLevel(Map<Integer, Double> probabilityMap) {
        double roll = RANDOM.nextDouble();
        double cumulative = 0;
        for (var e : probabilityMap.entrySet()) {
            cumulative += e.getValue();
            if (roll < cumulative) return e.getKey();
        }
        return probabilityMap.keySet().iterator().next();
    }

    private static Affix selectAffix(ItemStack target, Affix existing, int level, MaterialContext materialCtx) {
        List<Affix> pool = AffixDataLoader.getItemAffixCache().getAffixesForItem(target);
        if (pool.isEmpty()) pool = new ArrayList<>(AffixRegistry.getAll());
        if (pool.isEmpty()) return null;

        Map<String, Double> fixedProbs = new LinkedHashMap<>();
        double totalFixed = 0;
        for (MaterialContext.AffixBonusEntry bonus : materialCtx.getAffixBonuses()) {
            if (bonus.getFixedProbability() > 0 && level >= bonus.getMinLevel()) {
                fixedProbs.merge(bonus.getTargetAffix(), bonus.getFixedProbability(), Double::sum);
                totalFixed = Math.min(1.0, totalFixed + bonus.getFixedProbability());
            }
        }
        if (totalFixed > 1.0) {
            for (var e : fixedProbs.entrySet()) fixedProbs.put(e.getKey(), e.getValue() / totalFixed);
            totalFixed = 1.0;
        }

        double remaining = 1.0 - totalFixed;
        List<Affix> unfixed = pool.stream().filter(a -> !fixedProbs.containsKey(a.getId())).toList();
        double roll = RANDOM.nextDouble();
        double cumulative = 0;
        Map<String, Affix> idMap = new HashMap<>();
        for (Affix a : pool) idMap.put(a.getId(), a);
        for (var e : fixedProbs.entrySet()) {
            cumulative += e.getValue();
            if (roll < cumulative) {
                Affix a = idMap.get(e.getKey());
                if (a == null) a = AffixRegistry.get(e.getKey());
                if (a != null) return a;
            }
        }
        if (unfixed.isEmpty()) return pool.get(RANDOM.nextInt(pool.size()));
        double each = remaining / unfixed.size();
        for (Affix a : unfixed) {
            cumulative += each;
            if (roll < cumulative) return a;
        }
        return unfixed.get(RANDOM.nextInt(unfixed.size()));
    }

    private static int getMaxAllowedLevel(Affix affix, MaterialContext materialCtx) {
        if (affix == null) return 6;
        if (materialCtx == null || materialCtx.isEmpty()) return affix.getMaxLevel();
        int maxLevel = affix.getMaxLevel();
        MaterialContext.AffixBonusEntry bonus = materialCtx.getBonusForAffix(affix.getId());
        if (bonus != null) maxLevel = Math.max(maxLevel, bonus.getMaxLevel());
        if (materialCtx.getMaxLevelCap() > 0) maxLevel = Math.min(maxLevel, materialCtx.getMaxLevelCap());
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
        if (existing != null && FALLBACK_RANDOM.nextFloat() < 0.6F) {
            chosen = existing;
        } else {
            List<Affix> pool = existing != null
                    ? allAffixes.stream().filter(a -> !a.getId().equals(existing.getId())).toList()
                    : allAffixes;
            if (pool.isEmpty()) pool = allAffixes;
            chosen = pool.get(FALLBACK_RANDOM.nextInt(pool.size()));
        }
        rolledLevel = Math.min(rolledLevel, chosen.getMaxLevel());

        return RollResult.builder().affix(chosen).level(rolledLevel).originalLevel(rolledLevel)
                .addDebug("Fallback roll - Level: " + rolledLevel + ", Affix: " + chosen.getId())
                .addDebug("EnchantValue: " + enchantValue).addDebug("ExistingLevel: " + existingLevel).build();
    }

    @Deprecated
    private static int fallbackRollLevel(int enchantValue, int existingLevel) {
        float bonus = existingLevel > 0 ? 0.2F : 0F;
        float b1 = Math.max(0, 50 - enchantValue * 2F);
        float b2 = Math.max(0, 25 - enchantValue * 0.5F);
        float b3 = enchantValue * 1.5F;
        float b4 = Math.max(0, enchantValue - 10F);
        float b5 = Math.max(0, enchantValue - 18F);
        float b6 = Math.max(0, enchantValue - 25F);
        if (existingLevel > 0) { b1 *= (1F - bonus); b2 *= (1F - bonus); b3 *= (1F + bonus); b4 *= (1F + bonus * 2); b5 *= (1F + bonus * 3); b6 *= (1F + bonus * 4); }
        float total = b1 + b2 + b3 + b4 + b5 + b6;
        float roll = FALLBACK_RANDOM.nextFloat() * total;
        if (roll < b1) return 1; roll -= b1;
        if (roll < b2) return 2; roll -= b2;
        if (roll < b3) return 3; roll -= b3;
        if (roll < b4) return 4; roll -= b4;
        if (roll < b5) return 5;
        return 6;
    }
}