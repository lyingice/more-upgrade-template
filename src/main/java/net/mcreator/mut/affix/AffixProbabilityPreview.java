package net.mcreator.mut.affix;

import net.mcreator.mut.affix.data.AffixDataLoader;
import net.mcreator.mut.affix.data.LevelConfig;
import net.mcreator.mut.affix.data.MaterialContext;
import net.mcreator.mut.affix.data.PityTracker;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 概率预览计算器 - 在 GUI/JEI 中显示各等级和各种词缀的出现概率
 * 纯计算，不修改物品，可安全在客户端调用
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
        double previousFinalScore = 0;
        // 计算 pity 倍率（从配置文件读取）
        double pityBonusPerPoint = net.mcreator.mut.affix.data.AffixDataLoader.getPityConfig()
                .getGlobal().getPityBonusPerPoint();
        float pityMultiplier = 1.0F + (float)(pityCount * pityBonusPerPoint);
        if (pityMultiplier < 1.0F) pityMultiplier = 1.0F;

        // 已有词缀加成（转换为加法值）
        float existingBonusAdd = existingLevel > 0 ? existingLevel * 0.1F : 0F;

        // 材料加成
        double matLevelBonus = materialCtx != null ? materialCtx.getUniversalLevelBonus() : 0.0;
        double matTierBonus = materialCtx != null ? materialCtx.getTierLevelBonus() : 0.0;

        // 软保底加成（加法值）
        float pityAdd = pityMultiplier - 1.0F;

        // 总加成
        double totalBonus = matLevelBonus + matTierBonus + pityAdd + existingBonusAdd;

        // 从 JSON 读取等级倍率递增步长（默认 0.3）
        double bonusPerLevel = net.mcreator.mut.affix.data.AffixDataLoader.getBonusMultiplierPerLevel();

        // 计算各等级得分
        // 使用等级加权加成：该等级加成 = 总加成 × (等级 × bonusPerLevel)
        // 每个等级的加成比例不同 → 不会被归一化约掉
        Map<Integer, Double> scores = new LinkedHashMap<>();
        for (LevelConfig lc : levels) {
            double score = lc.getWeightCurve().computeScore(enchantValue);

            // 核心修复：基础得分为 0 时（附魔能力不够阈值），
            // 用总加成 × threshold × 0.3 作为种子得分（等级越高种子越大），
            // 避免低附魔物品所有零分等级用相同种子导致高等级虚高
            if (score <= 0 && totalBonus > 0) {
                score = totalBonus / lc.getLevel();
            }

            if (previousFinalScore > 0) {
                double leakRatio = Math.min(0.2 * (1.0 + totalBonus), 0.9);
                double inheritedSeed = previousFinalScore * leakRatio;
                score = Math.max(score, inheritedSeed / lc.getLevel());
            }

            // 该等级获得的加成比例
            double levelRatio = lc.getLevel() * bonusPerLevel;

            // 该等级额外配置的倍率（可选，数据包留空则 = 0）
            double extraMult = lc.getExtraBonusMultiplier();

            // 加权后的加成倍率 = 1.0 + 总加成 × (等级比例 + 额外倍率)
            double multiplier = 1.0 + totalBonus * (levelRatio + extraMult);
            double finalScore = score * multiplier;

            scores.put(lc.getLevel(), Math.max(0, finalScore));
            previousFinalScore = finalScore;
        }

        // 线性归一化（取代 Softmax）
        return AffixRoller.normalizeLinear(scores);
    }

    /**
     * 生成可读的概率文本行（带进度条）
     */
    public static java.util.List<String> generateProbabilityLines(
            int enchantValue, int existingLevel, MaterialContext materialCtx, int pityCount) {

        return generateLevelProbabilityAsText(enchantValue, existingLevel, materialCtx, pityCount);
    }

    /**
     * 生成可读的概率文本行（带进度条）
     * 与 generateProbabilityLines 相同，名称更直观
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
