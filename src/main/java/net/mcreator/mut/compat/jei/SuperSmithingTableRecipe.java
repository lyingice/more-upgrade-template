package net.mcreator.mut.compat.jei;

import net.mcreator.mut.affix.AffixRegistry;
import net.mcreator.mut.affix.data.MaterialBonusRegistry;
import net.mcreator.mut.affix.data.MaterialContext;
import net.mcreator.mut.init.MutModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SuperSmithingTableRecipe {

    private static final ResourceLocation AFFIX_MATERIAL_TAG =
            ResourceLocation.fromNamespaceAndPath("mut", "affix_material");

    private final ItemStack template, base, addition, result;
    private final String materialName;
    private final int enchantBonus;
    private final int minGuaranteedLevel, maxLevelCap;
    private final List<AffixProbDisplay> affixProbs;

    public SuperSmithingTableRecipe(ItemStack template, ItemStack base, ItemStack addition, ItemStack result,
                                    String materialName, int enchantBonus, int minGuaranteedLevel, int maxLevelCap,
                                    List<AffixProbDisplay> affixProbs) {
        this.template = template; this.base = base; this.addition = addition; this.result = result;
        this.materialName = materialName; this.enchantBonus = enchantBonus;
        this.minGuaranteedLevel = minGuaranteedLevel; this.maxLevelCap = maxLevelCap;
        this.affixProbs = affixProbs;
    }

    public ItemStack getTemplate() { return template; }
    public ItemStack getBase() { return base; }
    public ItemStack getAddition() { return addition; }
    public ItemStack getResult() { return result; }
    public String getMaterialName() { return materialName; }
    public int getEnchantBonus() { return enchantBonus; }
    public int getMinGuaranteedLevel() { return minGuaranteedLevel; }
    public int getMaxLevelCap() { return maxLevelCap; }
    public List<AffixProbDisplay> getAffixProbs() { return affixProbs; }

    public boolean isDirected() { return affixProbs != null && !affixProbs.isEmpty(); }
    public boolean isUniversal() { return enchantBonus > 0; }

    public static List<SuperSmithingTableRecipe> generateAll() {
        List<SuperSmithingTableRecipe> recipes = new ArrayList<>();
        ItemStack template = new ItemStack(MutModItems.SUPER_UPGRADE_SMITHING_TEMPLATE.get());
        ItemStack diamondSword = new ItemStack(Items.DIAMOND_SWORD);

        MaterialBonusRegistry registry = MaterialBonusRegistry.getInstance();
        var affixMaterialTag = ItemTags.create(AFFIX_MATERIAL_TAG);

        for (var itemHolder : BuiltInRegistries.ITEM.holders().toList()) {
            ItemStack materialStack = new ItemStack(itemHolder.value());
            if (!materialStack.is(affixMaterialTag)) continue;

            MaterialContext ctx = registry.evaluate(materialStack);
            String materialName = materialStack.getHoverName().getString();

            List<AffixProbDisplay> probs = new ArrayList<>();
            List<MaterialContext.AffixBonusEntry> bonuses = ctx.getAffixBonuses();
            double totalFixed = 0;
            for (MaterialContext.AffixBonusEntry b : bonuses) {
                probs.add(new AffixProbDisplay(b.getTargetAffix(), b.getFixedProbability(),
                        b.getMinLevel(), b.getMaxLevel()));
                totalFixed += b.getFixedProbability();
            }

            int minG = ctx.getMinGuaranteedLevel();
            int maxC = ctx.getMaxLevelCap();
            if (!bonuses.isEmpty()) {
                int bonusMin = bonuses.stream().mapToInt(MaterialContext.AffixBonusEntry::getMinLevel).min().orElse(1);
                int bonusMax = bonuses.stream().mapToInt(MaterialContext.AffixBonusEntry::getMaxLevel).max().orElse(8);
                minG = minG > 0 ? minG : bonusMin;
                maxC = maxC > 0 ? Math.min(maxC, bonusMax) : bonusMax;
            }
            if (maxC == 0) maxC = 5;

            // 如果有固定概率词缀，补上"其他词缀"的剩余概率
            if (!probs.isEmpty() && totalFixed < 1.0) {
                probs.add(new AffixProbDisplay("other", 1.0 - totalFixed, 1, 6));
            }

            probs.sort((a, b) -> Double.compare(b.probability, a.probability));

            recipes.add(new SuperSmithingTableRecipe(
                    template.copy(), diamondSword.copy(), materialStack.copy(), diamondSword.copy(),
                    materialName, ctx.getEnchantBonus(),
                    ctx.getMinGuaranteedLevel(), ctx.getMaxLevelCap(),
                    probs));
        }
        return recipes;
    }

    /** 词缀概率展示条目 */
    public static class AffixProbDisplay {
        public final String affixId;    // "fire_mark" 或 "other"
        public final double probability; // 0.0 ~ 1.0
        public final int minLevel;
        public final int maxLevel;

        public AffixProbDisplay(String affixId, double probability, int minLevel, int maxLevel) {
            this.affixId = affixId; this.probability = probability;
            this.minLevel = minLevel; this.maxLevel = maxLevel;
        }

        public String getDisplayName() {
            if ("other".equals(affixId)) return "其他词缀";
            var affix = AffixRegistry.get(affixId);
            return affix != null ? affix.getDisplayName().getString() : affixId;
        }

        /** 根据 affixId 返回展示色 */
        public int getColor() {
            return switch (affixId) {
                case "fire_mark"         -> 0xFFFFAA00;  // 金
                case "poison_mark"       -> 0xFF55FF55;  // 绿
                case "wither_mark"       -> 0xFF777777;  // 浅灰
                case "regeneration_mark" -> 0xFFFF55AA;  // 粉
                case "tidal_surge"       -> 0xFF55AAFF;  // 水蓝
                case "momentum"          -> 0xFFFFD700;  // 金
                case "sharpshooter"      -> 0xFF88FF88;  // 亮绿
                case "strength_blessing" -> 0xFFFF5555;  // 红
                case "piercing_spear"    -> 0xFFCCCCCC;  // 银白
                case "energy_conversion" -> 0xFFBB66FF;  // 紫
                case "big_stomach"       -> 0xFFFF8855;  // 橙
                case "nirvana"           -> 0xFFFF55FF;  // 品红
                default                  -> 0xFFAAAAAA;  // 中灰
            };
        }
    }
}