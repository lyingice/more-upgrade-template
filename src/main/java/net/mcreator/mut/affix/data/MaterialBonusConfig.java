package net.mcreator.mut.affix.data;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Nullable;
import java.util.List;

public class MaterialBonusConfig {

    @SerializedName("universal_materials")
    private List<UniversalMaterial> universalMaterials;

    @SerializedName("directed_materials")
    private List<DirectedMaterial> directedMaterials;

    @SerializedName("tag_driven_materials")
    private List<TagDrivenMaterial> tagDrivenMaterials;

    public List<UniversalMaterial> getUniversalMaterials() { return universalMaterials; }
    public List<DirectedMaterial> getDirectedMaterials() { return directedMaterials; }
    public List<TagDrivenMaterial> getTagDrivenMaterials() { return tagDrivenMaterials; }
    public void setUniversalMaterials(List<UniversalMaterial> universalMaterials) { this.universalMaterials = universalMaterials; }
    public void setDirectedMaterials(List<DirectedMaterial> directedMaterials) { this.directedMaterials = directedMaterials; }
    public void setTagDrivenMaterials(List<TagDrivenMaterial> tagDrivenMaterials) { this.tagDrivenMaterials = tagDrivenMaterials; }

    public static class UniversalMaterial {
        @Nullable private String item;
        @Nullable private String tag;
        @SerializedName("enchant_bonus") private int enchantBonus = 0;
        private String description;
        @SerializedName("min_guaranteed_level") private int minGuaranteedLevel = 0;
        @SerializedName("max_level_cap") private int maxLevelCap = 0;
        @Nullable public String getItem() { return item; }
        @Nullable public String getTag() { return tag; }
        public int getEnchantBonus() { return enchantBonus; }
        public String getDescription() { return description; }
        public int getMinGuaranteedLevel() { return minGuaranteedLevel; }
        public int getMaxLevelCap() { return maxLevelCap; }
        public void setItem(@Nullable String item) { this.item = item; }
        public void setTag(@Nullable String tag) { this.tag = tag; }
        public void setEnchantBonus(int enchantBonus) { this.enchantBonus = enchantBonus; }
        public void setDescription(String description) { this.description = description; }
        public void setMinGuaranteedLevel(int minGuaranteedLevel) { this.minGuaranteedLevel = minGuaranteedLevel; }
        public void setMaxLevelCap(int maxLevelCap) { this.maxLevelCap = maxLevelCap; }
    }

    public static class DirectedMaterial {
        @Nullable private String item;
        @Nullable private String tag;
        @SerializedName("affix_bonuses") private List<AffixBonus> affixBonuses;
        @Nullable public String getItem() { return item; }
        @Nullable public String getTag() { return tag; }
        public List<AffixBonus> getAffixBonuses() { return affixBonuses; }
        public void setItem(@Nullable String item) { this.item = item; }
        public void setTag(@Nullable String tag) { this.tag = tag; }
        public void setAffixBonuses(List<AffixBonus> affixBonuses) { this.affixBonuses = affixBonuses; }
    }

    public static class TagDrivenMaterial {
        private String tag;
        @SerializedName("affix_bonuses") private List<AffixBonus> affixBonuses;
        public String getTag() { return tag; }
        public List<AffixBonus> getAffixBonuses() { return affixBonuses; }
        public void setTag(String tag) { this.tag = tag; }
        public void setAffixBonuses(List<AffixBonus> affixBonuses) { this.affixBonuses = affixBonuses; }
    }

    public static class AffixBonus {
        @SerializedName("target_affix") private String targetAffix;
        /** 固定出现概率（如0.4=40%）。从100%中直接划走，剩余均分给其他词缀 */
        @SerializedName("fixed_probability") private double fixedProbability = 0;
        @SerializedName("min_level") private int minLevel = 1;
        @SerializedName("max_level") private int maxLevel = 8;
        public String getTargetAffix() { return targetAffix; }
        public double getFixedProbability() { return fixedProbability; }
        public int getMinLevel() { return minLevel; }
        public int getMaxLevel() { return maxLevel; }
        public void setTargetAffix(String targetAffix) { this.targetAffix = targetAffix; }
        public void setFixedProbability(double fixedProbability) { this.fixedProbability = fixedProbability; }
        public void setMinLevel(int minLevel) { this.minLevel = minLevel; }
        public void setMaxLevel(int maxLevel) { this.maxLevel = maxLevel; }
    }
}
