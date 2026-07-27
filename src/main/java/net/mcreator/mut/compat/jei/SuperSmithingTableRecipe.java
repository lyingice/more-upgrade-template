// ============================================================
// SuperSmithingTableRecipe.java
// ============================================================
package net.mcreator.mut.compat.jei;

import net.mcreator.mut.affix.AffixRegistry;
import net.mcreator.mut.affix.data.MaterialBonusRegistry;
import net.mcreator.mut.affix.data.MaterialContext;
import net.mcreator.mut.init.MutModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
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

            if (!probs.isEmpty() && totalFixed < 1.0) {
                probs.add(new AffixProbDisplay("other", 1.0 - totalFixed, 1, 6));
            }

            probs.sort((a, b) -> Double.compare(b.probability, a.probability));

            recipes.add(new SuperSmithingTableRecipe(
                    template.copy(), diamondSword.copy(), materialStack.copy(), diamondSword.copy(),
                    materialName, ctx.getEnchantBonus(),
                    minG, maxC,
                    probs));
        }
        return recipes;
    }

    public static class AffixProbDisplay {
        public final String affixId;
        public final double probability;
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

        /** 跟随游戏内 Affix.getColorForLevel() 同一套逻辑 */
        public int getColor() {
            if ("other".equals(affixId)) return 0xFF888888;
            var affix = AffixRegistry.get(affixId);
            if (affix == null) return 0xFF888888;
            int level = Math.min(maxLevel, affix.getMaxLevel());
            ChatFormatting fmt = affix.getColorForLevel(level);
            Integer mcColor = fmt.getColor();
            return mcColor != null ? 0xFF000000 | mcColor : 0xFF888888;
        }
    }
}