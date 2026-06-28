package net.mcreator.mut.affix;

import net.mcreator.mut.affix.data.*;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 核心随机引擎 - 替换 AddRandomAffixProcedure 中的旧逻辑
 *
 * 职责：
 * 1. 从 JSON 加载等级配置（LevelConfig）
 * 2. 使用 Softmax 归一化计算等级概率
 * 3. 考虑材料加成、附魔能力、软保底对等级权重的影响
 * 4. 考虑材料定向词缀对词缀选择的影响
 *
 * 二改：支持"总分池+比率分配+分数再分配"新方案，
 *       以及羽化因子/蜕变因子、材料保底/等级上限。
 */
public class AffixRoller {

    private static final Random RANDOM = new Random();

    /**
     * 执行词缀随机 - 完整流程
     *
     * @param target      目标物品
     * @param materialCtx 材料上下文（槽位2中的材料带来的加成）
     * @return 随机结果
     */
    public static RollResult roll(ItemStack target, MaterialContext materialCtx) {
        if (target == null || target.isEmpty()) {
            return RollResult.builder().build();
        }

        if (materialCtx == null) {
            materialCtx = MaterialContext.empty();
        }

        // Step 1: 获取物品附魔能力
        int enchantValue = target.getItem().getEnchantmentValue(target);

        // Step 2: 获取已有词缀
        Affix existing = Affix.fromStack(target);
        int existingLevel = existing != null ? Affix.getLevelFromStack(target) : 0;

        // Step 3: 获取软保底
        int pityBefore = PityTracker.getPity(target);
        float pityMultiplier = PityTracker.getPityBonusMultiplier(target);

        // Step 4: 计算各级权重（含所有修正）
        LevelConfig[] levels = AffixDataLoader.getLevels();
        if (levels == null || levels.length == 0) {
            // 回退到旧逻辑
            return fallbackRoll(target, existing);
        }

        Map<Integer, Double> weightMap = computeWeights(
                levels, enchantValue, existingLevel, materialCtx, pityMultiplier
        );

        // Step 5: 线性归一化
        Map<Integer, Double> probabilityMap = normalizeLinear(weightMap);

        // Step 6: 选择等级
        int selectedLevel = selectLevel(probabilityMap);
        int originalLevel = selectedLevel;

        // Step 7: 选择词缀
        Affix chosen = selectAffix(target, existing, selectedLevel, materialCtx);

        // Step 8: 等级不能超过该词缀允许的最大等级（结合材料加成）
        int maxAllowedLevel = getMaxAllowedLevel(chosen, materialCtx);
        selectedLevel = Math.min(selectedLevel, maxAllowedLevel);

        // Step 9: 等级不能超过 JSON 中定义的最大等级
        int jsonMaxLevel = AffixDataLoader.getMaxLevel();
        selectedLevel = Math.min(selectedLevel, jsonMaxLevel);

        // Step 10: 处理软保底
        boolean pityTriggered = false;
        int pityAfter = pityBefore;
        if (PityTracker.shouldReset(selectedLevel, target)) {
            PityTracker.resetPity(target);
            pityAfter = 0;
            pityTriggered = true;
        } else {
            PityTracker.incrementPity(target);
            pityAfter = PityTracker.getPity(target);
        }

        // Step 11: 组装结果
        RollResult.Builder resultBuilder = RollResult.builder()
                .affix(chosen)
                .level(selectedLevel)
                .originalLevel(originalLevel)
                .materialContext(materialCtx)
                .pityBefore(pityBefore)
                .pityAfter(pityAfter)
                .pityTriggered(pityTriggered)
                .addDebug("EnchantValue: " + enchantValue)
                .addDebug("PityBefore: " + pityBefore + " -> After: " + pityAfter)
                .addDebug("Material: " + (materialCtx.isEmpty() ? "none" :
                        materialCtx.getAdditionStack().getItem().getDescription().getString()))
                .addDebug("SelectedLevel: " + selectedLevel + " (original: " + originalLevel + ")")
                .addDebug("Weights: " + weightMap.entrySet().stream()
                        .map(e -> "Lv" + e.getKey() + "=" + String.format("%.2f", e.getValue()))
                        .collect(Collectors.joining(", ")))
                .addDebug("Probabilities: " + probabilityMap.entrySet().stream()
                        .map(e -> "Lv" + e.getKey() + "=" + String.format("%.1f%%", e.getValue() * 100))
                        .collect(Collectors.joining(", ")));

        if (chosen != null) {
            resultBuilder.addDebug("ChosenAffix: " + chosen.getId() + " (maxLv: " + maxAllowedLevel + ")");
        }

        if (materialCtx != null && !materialCtx.isEmpty()) {
            resultBuilder.addDebug("UniversalBonus: +" + (materialCtx.getUniversalLevelBonus() * 100) + "%");
            for (MaterialContext.AffixBonusEntry bonus : materialCtx.getAffixBonuses()) {
                resultBuilder.addDebug("  -> " + bonus.getTargetAffix() +
                        ": weight_x" + bonus.getAffixWeightMultiplier() +
                        ", levelBonus: +" + (bonus.getLevelWeightBonus() * 100) + "%" +
                        ", maxLv: " + bonus.getMaxLevel());
            }
            if (materialCtx.getMinGuaranteedLevel() > 0) {
                resultBuilder.addDebug("MinGuaranteedLevel: " + materialCtx.getMinGuaranteedLevel());
            }
            if (materialCtx.getMaxLevelCap() > 0) {
                resultBuilder.addDebug("MaxLevelCap: " + materialCtx.getMaxLevelCap());
            }
        }

        LevelFactorConfig factorConfig = AffixDataLoader.getLevelFactorConfig();
        if (factorConfig != null && factorConfig.isEnabled()) {
            resultBuilder.addDebug("LevelFactors: ascFact=" + factorConfig.getAscensionFactorial()
                    + ", degFact=" + factorConfig.getDegenerationFactorial());
        }

        return resultBuilder.build();
    }

    /**
     * 简化版本 - 无需材料上下文（向后兼容）
     */
    public static RollResult roll(ItemStack target) {
        return roll(target, MaterialContext.empty());
    }

    // ========== 权重计算 ==========

    /**
     * 计算每个等级的权重得分
     *
     * 二改：
     * - 新方案（total_pool_scale_factor > 0）："总分池+比率分配+分数再分配"
     * - 旧方案（默认）：原有"基分×乘法修正"逻辑
     */
    private static Map<Integer, Double> computeWeights(
            LevelConfig[] levels, int enchantValue, int existingLevel,
            MaterialContext materialCtx, float pityMultiplier) {

        if (AffixDataLoader.isNewPoolSystem()) {
            return computeWeightsNewPool(levels, enchantValue, existingLevel, materialCtx, pityMultiplier);
        } else {
            return computeWeightsLegacy(levels, enchantValue, existingLevel, materialCtx, pityMultiplier);
        }
    }

    // ====== 新：总分池+比率分配+分数再分配 ======

    /**
     * 新方案权重计算
     *
     * 阶段一：计算总分池 totalBudget
     * 阶段二：计算各等级比率系数 ratio（基分+羽化-蜕变）
     * 阶段三：按比率分配总分
     * 阶段四：材料等级限制 → 分数再分配
     * 阶段五：应用材料加成(totalBonus)修正
     */
    private static Map<Integer, Double> computeWeightsNewPool(
            LevelConfig[] levels, int enchantValue, int existingLevel,
            MaterialContext materialCtx, float pityMultiplier) {

        Map<Integer, Double> result = new LinkedHashMap<>();
        int levelCount = levels.length;
        int maxLevel = levels[levelCount - 1].getLevel();

        // ——阶段一：计算总分池——
        double scaleFactor = AffixDataLoader.getTotalPoolScaleFactor();
        double basePool = AffixDataLoader.getTotalPoolBase();
        double totalBudget = scaleFactor * enchantValue * levelCount + basePool;
        totalBudget = Math.max(totalBudget, levelCount * 10.0); // 最低保障

        // ——阶段二：计算各等级比率系数——
        double sumRatio = 0;
        Map<Integer, Double> ratioMap = new LinkedHashMap<>();

        // 获取羽化/蜕变配置
        LevelFactorConfig factorConfig = AffixDataLoader.getLevelFactorConfig();

        // 材料加成（在ratio阶段应用：影响基分，作为临时加成）
        double materialLevelBonus = materialCtx != null ? materialCtx.getUniversalLevelBonus() : 0.0;
        double materialTierBonus = materialCtx != null ? materialCtx.getTierLevelBonus() : 0.0;
        float pityAdd = pityMultiplier - 1.0F;
        float existingBonusAdd = existingLevel > 0 ? existingLevel * 0.1F : 0F;
        double totalBonus = materialLevelBonus + materialTierBonus + pityAdd + existingBonusAdd;

        for (LevelConfig lc : levels) {
            int level = lc.getLevel();

            // 2a: 基础比率（复用原有 weightCurve 计分）
            double baseRatio = lc.getWeightCurve().computeScore(enchantValue);
            baseRatio = Math.max(baseRatio, 0);

            // 2b: 材料/保底加成影响比率
            if (totalBonus > 0) {
                // 用等级差异化加成替代旧 multiplier 机制
                // 加成比例随等级递增：level / maxLevel * totalBonus
                double levelWeightedBonus = totalBonus * (double) level / maxLevel;
                baseRatio = baseRatio * (1.0 + levelWeightedBonus);
            }

            // 2c: 羽化因子（固定分数加成，阶乘调整）
            double ascension = 0;
            if (factorConfig != null && factorConfig.isEnabled()) {
                double baseAscension = factorConfig.getAscensionFactorial();
                double individualExtra = 0;
                LevelFactorConfig.PerLevelOverride override = factorConfig.getPerLevelOverride(level);
                if (override != null) {
                    individualExtra = override.getAscensionExtra();
                }
                ascension = (level - 1) * baseAscension + individualExtra;
            }

            // 2d: 蜕变因子（固定分数减成，阶乘调整）
            double degeneration = 0;
            if (factorConfig != null && factorConfig.isEnabled()) {
                double baseDegeneration = factorConfig.getDegenerationFactorial();
                double individualExtra = 0;
                LevelFactorConfig.PerLevelOverride override = factorConfig.getPerLevelOverride(level);
                if (override != null) {
                    individualExtra = override.getDegenerationExtra();
                }
                degeneration = (maxLevel - level) * baseDegeneration + individualExtra;
            }

            double ratio = baseRatio + ascension - degeneration;
            ratio = Math.max(0, ratio); // 保证非负

            ratioMap.put(level, ratio);
            sumRatio += ratio;
        }

        // ——阶段三：按比率分配总分——
        if (sumRatio <= 0) {
            // 兜底：均匀分配
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

        // ——阶段四：材料等级限制 → 分数再分配——
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
                // 没有有效等级或没有可清零的分数，保持原样
                result.putAll(distributedScores);
            }
        } else {
            result.putAll(distributedScores);
        }

        return result;
    }

    // ====== 旧：基分×乘法修正（默认，向后兼容） ======

    /**
     * 旧方案权重计算 - 保留作为向后兼容
     */
    private static Map<Integer, Double> computeWeightsLegacy(
            LevelConfig[] levels, int enchantValue, int existingLevel,
            MaterialContext materialCtx, float pityMultiplier) {

        Map<Integer, Double> result = new LinkedHashMap<>();

        // 已有词缀带来的等级加成（转换为加法值）
        float existingBonusAdd = existingLevel > 0 ? existingLevel * 0.1F : 0F;

        // 材料带来的通用等级权重加成（加法值）
        double materialLevelBonus = materialCtx != null ? materialCtx.getUniversalLevelBonus() : 0.0;
        double materialTierBonus = materialCtx != null ? materialCtx.getTierLevelBonus() : 0.0;

        // 软保底加成（加法值）
        float pityAdd = pityMultiplier - 1.0F;

        // 总加成
        double totalBonus = materialLevelBonus + materialTierBonus + pityAdd + existingBonusAdd;
        double previousFinalScore = 0;

        // 从 JSON 读取等级倍率递增步长（默认 0.3）
        double bonusPerLevel = AffixDataLoader.getBonusMultiplierPerLevel();

        // 按等级递增分配加成
        for (LevelConfig lc : levels) {
            double baseScore = lc.getWeightCurve().computeScore(enchantValue);
            if (baseScore <= 0 && totalBonus > 0) {
                baseScore = totalBonus / lc.getLevel();
            }

            // 泄漏继承
            if (previousFinalScore > 0) {
                double leakRatio = Math.min(0.2 * (1.0 + totalBonus), 0.9);
                double inheritedSeed = previousFinalScore * leakRatio;
                baseScore = Math.max(baseScore, inheritedSeed / lc.getLevel());
            }

            // 该等级获得的加成比例
            double levelRatio = lc.getLevel() * bonusPerLevel;

            // 该等级额外配置的倍率（可选，数据包留空则 = 0）
            double extraMult = lc.getExtraBonusMultiplier();

            // 加权后的加成倍率
            double multiplier = 1.0 + totalBonus * (levelRatio + extraMult);

            double finalScore = baseScore * multiplier;

            result.put(lc.getLevel(), Math.max(0, finalScore));
            previousFinalScore = finalScore;
        }

        return result;
    }

    // ========== 线性归一化（替代 Softmax） ==========

    /**
     * 线性归一化
     * P(i) = score_i / Σ(score_j)
     *
     * 使用线性归一化而非 Softmax，原因：
     * Softmax 的指数函数 e^x 会指数级放大学分差距，
     * 导致高分等级锁死 99%+ 概率。
     * 线性归一化直接按权重比例分配，分布更合理。
     *
     * 公开方法，供 AffixProbabilityPreview 调用
     */
    public static Map<Integer, Double> computeSoftmax(Map<Integer, Double> weightMap) {
        return normalizeLinear(weightMap);
    }

    /**
     * 线性归一化
     * P(i) = score_i / Σ(score_j)
     */
    public static Map<Integer, Double> normalizeLinear(Map<Integer, Double> weightMap) {
        Map<Integer, Double> result = new LinkedHashMap<>();

        double totalScore = weightMap.values().stream()
                .mapToDouble(Double::doubleValue)
                .filter(v -> v > 0)
                .sum();

        if (totalScore <= 0) {
            // 兜底：均匀分布
            double uniformProb = 1.0 / weightMap.size();
            for (var entry : weightMap.entrySet()) {
                result.put(entry.getKey(), uniformProb);
            }
            return result;
        }

        // 最低概率保证（从数据包 affix_levels.json 读取）
        double minProb = AffixDataLoader.getMinProbability();

        for (var entry : weightMap.entrySet()) {
            double prob = Math.max(entry.getValue() / totalScore, minProb);
            result.put(entry.getKey(), prob);
        }

        // 重新归一化（保证 sum = 1.0）
        double sumProbs = result.values().stream().mapToDouble(Double::doubleValue).sum();
        if (sumProbs > 0 && Math.abs(sumProbs - 1.0) > 0.001) {
            for (var entry : result.entrySet()) {
                result.put(entry.getKey(), entry.getValue() / sumProbs);
            }
        }

        return result;
    }

    // ========== 等级选择 ==========

    /**
     * 根据概率分布选择等级
     */
    private static int selectLevel(Map<Integer, Double> probabilityMap) {
        double roll = RANDOM.nextDouble();
        double cumulative = 0;
        for (var entry : probabilityMap.entrySet()) {
            cumulative += entry.getValue();
            if (roll < cumulative) {
                return entry.getKey();
            }
        }
        // 兜底：返回最高等级
        return probabilityMap.keySet().stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(1);
    }

    // ========== 词缀选择 ==========

    /**
     * 选择词缀 - 考虑材料定向加成
     */
    private static Affix selectAffix(ItemStack target, Affix existing,
                                     int selectedLevel, MaterialContext materialCtx) {
        // 获取物品可用的词缀池
        List<Affix> affixPool = AffixDataLoader.getItemAffixCache().getAffixesForItem(target);
        if (affixPool.isEmpty()) {
            affixPool = List.copyOf(AffixRegistry.getAll());
        }
        if (affixPool.isEmpty()) return null;

        // 60% 概率继承相同词缀（如果有的话）
        if (existing != null && RANDOM.nextFloat() < 0.6F) {
            if (affixPool.contains(existing)) {
                return existing;
            }
        }

        // 计算每个词缀的权重（受材料定向影响）
        List<Affix> weightedPool;
        if (!materialCtx.isEmpty() && !materialCtx.getAffixBonuses().isEmpty()) {
            weightedPool = applyMaterialWeight(affixPool, materialCtx);
        } else {
            weightedPool = affixPool;
        }

        if (weightedPool.isEmpty()) return null;

        // 如果已有词缀，排除它
        if (existing != null) {
            List<Affix> filtered = weightedPool.stream()
                    .filter(a -> !a.getId().equals(existing.getId()))
                    .toList();
            if (!filtered.isEmpty()) {
                weightedPool = filtered;
            }
        }

        return weightedPool.get(RANDOM.nextInt(weightedPool.size()));
    }

    /**
     * 根据材料定向加成对词缀池进行加权选择
     */
    private static List<Affix> applyMaterialWeight(List<Affix> pool, MaterialContext materialCtx) {
        // 构建加权列表
        List<WeightedAffix> weighted = new ArrayList<>();
        for (Affix affix : pool) {
            double weight = 1.0;
            MaterialContext.AffixBonusEntry bonus = materialCtx.getBonusForAffix(affix.getId());
            if (bonus != null) {
                weight = bonus.getAffixWeightMultiplier();
            }
            // 低倍率（<1）的词缀减少出现概率
            int copies = (int) Math.round(weight * 10);
            copies = Math.max(1, copies);
            for (int i = 0; i < copies; i++) {
                weighted.add(new WeightedAffix(affix, weight));
            }
        }

        if (weighted.isEmpty()) return pool;
        return weighted.stream()
                .map(wa -> wa.affix)
                .collect(Collectors.toList());
    }

    /**
     * 获取词缀在材料加成下的最大允许等级
     */
    private static int getMaxAllowedLevel(Affix affix, MaterialContext materialCtx) {
        if (affix == null) return 6;
        if (materialCtx == null || materialCtx.isEmpty()) {
            return affix.getMaxLevel();
        }

        int maxLevel = affix.getMaxLevel();
        MaterialContext.AffixBonusEntry bonus = materialCtx.getBonusForAffix(affix.getId());
        if (bonus != null) {
            maxLevel = Math.max(maxLevel, bonus.getMaxLevel());
        }
        return maxLevel;
    }

    // ========== 回退逻辑 ==========

    private static final Random FALLBACK_RANDOM = new Random();

    /**
     * 回退到旧的硬编码逻辑（当 JSON 未加载时）
     * 内联旧版 rollLevel 方法，避免循环调用 AddRandomAffixProcedure
     */
    private static RollResult fallbackRoll(ItemStack target, Affix existing) {
        if (target == null || target.isEmpty()) {
            return RollResult.builder()
                    .addDebug("Fallback: target is empty")
                    .build();
        }

        List<Affix> allAffixes = List.copyOf(AffixRegistry.getAll());
        if (allAffixes.isEmpty()) {
            return RollResult.builder()
                    .addDebug("Fallback: no affixes registered")
                    .build();
        }

        // 获取物品附魔能力
        int enchantValue = target.getItem().getEnchantmentValue(target);

        // 检查是否已有词缀
        int existingLevel = existing != null ? Affix.getLevelFromStack(target) : 0;

        // 根据附魔能力随机等级（1-6）
        int rolledLevel = fallbackRollLevel(enchantValue, existingLevel);

        // 选择词缀
        Affix chosen;
        if (existing != null && FALLBACK_RANDOM.nextFloat() < 0.6F) {
            // 60% 概率继承相同词缀（等级可能更高）
            chosen = existing;
        } else {
            // 随机选一个
            List<Affix> pool = allAffixes;
            if (existing != null) {
                pool = allAffixes.stream()
                        .filter(a -> !a.getId().equals(existing.getId()))
                        .toList();
            }
            if (pool.isEmpty()) pool = allAffixes;
            chosen = pool.get(FALLBACK_RANDOM.nextInt(pool.size()));
        }

        // 等级不超过词缀最大等级
        rolledLevel = Math.min(rolledLevel, chosen.getMaxLevel());

        // 应用词缀
        chosen.applyToStack(target, rolledLevel);

        return RollResult.builder()
                .affix(chosen)
                .level(rolledLevel)
                .originalLevel(rolledLevel)
                .addDebug("Fallback roll - Level: " + rolledLevel + ", Affix: " + chosen.getId())
                .addDebug("EnchantValue: " + enchantValue)
                .addDebug("ExistingLevel: " + existingLevel)
                .build();
    }

    /**
     * 内联旧版 rollLevel 方法
     * 根据附魔能力随机等级，附魔能力越高，高等级概率越大
     */
    private static int fallbackRollLevel(int enchantValue, int existingLevel) {
        float bonus = existingLevel > 0 ? 0.2F : 0F;

        // 基础概率分布（0-100）
        float baseCommon = Math.max(0, 50 - enchantValue * 2F);
        float baseUncommon = Math.max(0, 25 - enchantValue * 0.5F);
        float baseRare = enchantValue * 1.5F;
        float baseEpic = Math.max(0, enchantValue - 10F);
        float baseLegendary = Math.max(0, enchantValue - 18F);
        float baseMythic = Math.max(0, enchantValue - 25F);

        // 已有词缀加成
        if (existingLevel > 0) {
            baseCommon *= (1F - bonus);
            baseUncommon *= (1F - bonus);
            baseRare *= (1F + bonus);
            baseEpic *= (1F + bonus * 2);
            baseLegendary *= (1F + bonus * 3);
            baseMythic *= (1F + bonus * 4);
        }

        float total = baseCommon + baseUncommon + baseRare + baseEpic + baseLegendary + baseMythic;
        float roll = FALLBACK_RANDOM.nextFloat() * total;

        if (roll < baseCommon) return 1;
        roll -= baseCommon;
        if (roll < baseUncommon) return 2;
        roll -= baseUncommon;
        if (roll < baseRare) return 3;
        roll -= baseRare;
        if (roll < baseEpic) return 4;
        roll -= baseEpic;
        if (roll < baseLegendary) return 5;
        return 6;
    }

    /**
     * 带权重的词缀内部类
     */
    private static class WeightedAffix {
        final Affix affix;

        WeightedAffix(Affix affix, double weight) {
            this.affix = affix;
        }
    }
}
