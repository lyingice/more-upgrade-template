package net.mcreator.mut.affix.data;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 材料加成配置 POJO - 从 material_bonuses.json 加载
 */
public class MaterialBonusConfig {

    /** 通用材料（不指定词缀，只提高等级权重） */
    @SerializedName("universal_materials")
    private List<UniversalMaterial> universalMaterials;

    /** 定向材料（指定词缀，并提高该词缀权重和等级权重） */
    @SerializedName("directed_materials")
    private List<DirectedMaterial> directedMaterials;

    /** 标签驱动材料（通过物品标签匹配） */
    @SerializedName("tag_driven_materials")
    private List<TagDrivenMaterial> tagDrivenMaterials;

    /** 材料品质层级 */
    @SerializedName("material_tiers")
    private List<MaterialTier> materialTiers;

    public List<UniversalMaterial> getUniversalMaterials() { return universalMaterials; }
    public List<DirectedMaterial> getDirectedMaterials() { return directedMaterials; }
    public List<TagDrivenMaterial> getTagDrivenMaterials() { return tagDrivenMaterials; }
    public List<MaterialTier> getMaterialTiers() { return materialTiers; }

    public void setUniversalMaterials(List<UniversalMaterial> universalMaterials) { this.universalMaterials = universalMaterials; }
    public void setDirectedMaterials(List<DirectedMaterial> directedMaterials) { this.directedMaterials = directedMaterials; }
    public void setTagDrivenMaterials(List<TagDrivenMaterial> tagDrivenMaterials) { this.tagDrivenMaterials = tagDrivenMaterials; }
    public void setMaterialTiers(List<MaterialTier> materialTiers) { this.materialTiers = materialTiers; }

    // ========== 通用材料 ==========

    public static class UniversalMaterial {
        @Nullable
        private String item;
        @Nullable
        private String tag;
        @SerializedName("level_weight_bonus")
        private double levelWeightBonus;
        private String description;
        /** 材料保底等级（该等级以下分数清零，0=不启用） */
        @SerializedName("min_guaranteed_level")
        private int minGuaranteedLevel = 0;
        /** 材料最高等级限制（该等级以上分数清零，0=不限制） */
        @SerializedName("max_level_cap")
        private int maxLevelCap = 0;

        @Nullable
        public String getItem() { return item; }
        @Nullable
        public String getTag() { return tag; }
        public double getLevelWeightBonus() { return levelWeightBonus; }
        public String getDescription() { return description; }
        public int getMinGuaranteedLevel() { return minGuaranteedLevel; }
        public int getMaxLevelCap() { return maxLevelCap; }

        public void setItem(@Nullable String item) { this.item = item; }
        public void setTag(@Nullable String tag) { this.tag = tag; }
        public void setLevelWeightBonus(double levelWeightBonus) { this.levelWeightBonus = levelWeightBonus; }
        public void setDescription(String description) { this.description = description; }
        public void setMinGuaranteedLevel(int minGuaranteedLevel) { this.minGuaranteedLevel = minGuaranteedLevel; }
        public void setMaxLevelCap(int maxLevelCap) { this.maxLevelCap = maxLevelCap; }
    }

    // ========== 定向材料 ==========

    public static class DirectedMaterial {
        @Nullable
        private String item;
        @Nullable
        private String tag;
        @SerializedName("affix_bonuses")
        private List<AffixBonus> affixBonuses;

        @Nullable
        public String getItem() { return item; }
        @Nullable
        public String getTag() { return tag; }
        public List<AffixBonus> getAffixBonuses() { return affixBonuses; }

        public void setItem(@Nullable String item) { this.item = item; }
        public void setTag(@Nullable String tag) { this.tag = tag; }
        public void setAffixBonuses(List<AffixBonus> affixBonuses) { this.affixBonuses = affixBonuses; }
    }

    // ========== 标签驱动材料 ==========

    public static class TagDrivenMaterial {
        private String tag;
        @SerializedName("affix_bonuses")
        private List<AffixBonus> affixBonuses;

        public String getTag() { return tag; }
        public List<AffixBonus> getAffixBonuses() { return affixBonuses; }

        public void setTag(String tag) { this.tag = tag; }
        public void setAffixBonuses(List<AffixBonus> affixBonuses) { this.affixBonuses = affixBonuses; }
    }

    // ========== 词缀加成 ==========

    public static class AffixBonus {
        @SerializedName("target_affix")
        private String targetAffix;
        @SerializedName("affix_weight_multiplier")
        private double affixWeightMultiplier = 1.0;
        @SerializedName("level_weight_bonus")
        private double levelWeightBonus = 0.0;
        @SerializedName("min_level")
        private int minLevel = 1;
        @SerializedName("max_level")
        private int maxLevel = 8;

        public String getTargetAffix() { return targetAffix; }
        public double getAffixWeightMultiplier() { return affixWeightMultiplier; }
        public double getLevelWeightBonus() { return levelWeightBonus; }
        public int getMinLevel() { return minLevel; }
        public int getMaxLevel() { return maxLevel; }

        public void setTargetAffix(String targetAffix) { this.targetAffix = targetAffix; }
        public void setAffixWeightMultiplier(double affixWeightMultiplier) { this.affixWeightMultiplier = affixWeightMultiplier; }
        public void setLevelWeightBonus(double levelWeightBonus) { this.levelWeightBonus = levelWeightBonus; }
        public void setMinLevel(int minLevel) { this.minLevel = minLevel; }
        public void setMaxLevel(int maxLevel) { this.maxLevel = maxLevel; }
    }

    // ========== 材料品质层级 ==========

    public static class MaterialTier {
        private int tier;
        @SerializedName("level_bonus")
        private double levelBonus;
        private List<String> items;
        /** 该层级材料的最高等级限制（0=不限制） */
        @SerializedName("max_level_cap")
        private int maxLevelCap = 0;

        public int getTier() { return tier; }
        public double getLevelBonus() { return levelBonus; }
        public List<String> getItems() { return items; }
        public int getMaxLevelCap() { return maxLevelCap; }

        public void setTier(int tier) { this.tier = tier; }
        public void setLevelBonus(double levelBonus) { this.levelBonus = levelBonus; }
        public void setItems(List<String> items) { this.items = items; }
        public void setMaxLevelCap(int maxLevelCap) { this.maxLevelCap = maxLevelCap; }
    }
}
