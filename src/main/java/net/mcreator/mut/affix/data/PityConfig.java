package net.mcreator.mut.affix.data;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * 软保底系统配置 POJO - 从 pity_config.json 加载
 */
public class PityConfig {

    private boolean enabled = true;
    private PityGlobal global = new PityGlobal();

    @SerializedName("per_material_overrides")
    private Map<String, PerMaterialOverride> perMaterialOverrides = Map.of();

    public boolean isEnabled() { return enabled; }
    public PityGlobal getGlobal() { return global; }
    public Map<String, PerMaterialOverride> getPerMaterialOverrides() { return perMaterialOverrides; }

    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public void setGlobal(PityGlobal global) { this.global = global; }
    public void setPerMaterialOverrides(Map<String, PerMaterialOverride> perMaterialOverrides) { this.perMaterialOverrides = perMaterialOverrides; }

    // ========== 全局配置 ==========

    public static class PityGlobal {
        @SerializedName("pity_per_attempt")
        private int pityPerAttempt = 1;
        @SerializedName("pity_cap")
        private int pityCap = 12;
        @SerializedName("pity_reset_on_success")
        private boolean pityResetOnSuccess = true;
        @SerializedName("pity_bonus_per_point")
        private double pityBonusPerPoint = 0.12;
        @SerializedName("min_level_for_reset")
        private int minLevelForReset = 3;

        public int getPityPerAttempt() { return pityPerAttempt; }
        public int getPityCap() { return pityCap; }
        public boolean isPityResetOnSuccess() { return pityResetOnSuccess; }
        public double getPityBonusPerPoint() { return pityBonusPerPoint; }
        public int getMinLevelForReset() { return minLevelForReset; }

        public void setPityPerAttempt(int pityPerAttempt) { this.pityPerAttempt = pityPerAttempt; }
        public void setPityCap(int pityCap) { this.pityCap = pityCap; }
        public void setPityResetOnSuccess(boolean pityResetOnSuccess) { this.pityResetOnSuccess = pityResetOnSuccess; }
        public void setPityBonusPerPoint(double pityBonusPerPoint) { this.pityBonusPerPoint = pityBonusPerPoint; }
        public void setMinLevelForReset(int minLevelForReset) { this.minLevelForReset = minLevelForReset; }
    }

    // ========== 特定材料覆盖 ==========

    public static class PerMaterialOverride {
        @Nullable
        @SerializedName("pity_per_attempt")
        private Integer pityPerAttempt;
        @Nullable
        @SerializedName("pity_cap")
        private Integer pityCap;
        @Nullable
        @SerializedName("pity_reset_on_success")
        private Boolean pityResetOnSuccess;
        @Nullable
        @SerializedName("pity_bonus_per_point")
        private Double pityBonusPerPoint;
        @Nullable
        @SerializedName("min_level_for_reset")
        private Integer minLevelForReset;

        @Nullable
        public Integer getPityPerAttempt() { return pityPerAttempt; }
        @Nullable
        public Integer getPityCap() { return pityCap; }
        @Nullable
        public Boolean getPityResetOnSuccess() { return pityResetOnSuccess; }
        @Nullable
        public Double getPityBonusPerPoint() { return pityBonusPerPoint; }
        @Nullable
        public Integer getMinLevelForReset() { return minLevelForReset; }

        public void setPityPerAttempt(@Nullable Integer pityPerAttempt) { this.pityPerAttempt = pityPerAttempt; }
        public void setPityCap(@Nullable Integer pityCap) { this.pityCap = pityCap; }
        public void setPityResetOnSuccess(@Nullable Boolean pityResetOnSuccess) { this.pityResetOnSuccess = pityResetOnSuccess; }
        public void setPityBonusPerPoint(@Nullable Double pityBonusPerPoint) { this.pityBonusPerPoint = pityBonusPerPoint; }
        public void setMinLevelForReset(@Nullable Integer minLevelForReset) { this.minLevelForReset = minLevelForReset; }
    }
}
