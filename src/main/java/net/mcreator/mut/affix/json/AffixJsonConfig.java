package net.mcreator.mut.affix.json;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AffixJsonConfig {
    private String id;
    @SerializedName("name_key") private String nameKey;
    @SerializedName("desc_key") private String descKey;
    private List<EffectEntry> effects;
    @SerializedName("configurable") private List<ParamDef> configurable;
    public String getId() { return id; }
    public String getNameKey() { return nameKey; }
    public String getDescKey() { return descKey; }
    public List<EffectEntry> getEffects() { return effects; }
    public List<ParamDef> getConfigurable() { return configurable; }

    public static class EffectEntry {
        @SerializedName("amplify_damage_types")
        private List<String> amplifyDamageTypes;
        @SerializedName("ranged_only")
        private boolean rangedOnly = false;
        public boolean isRangedOnly() { return rangedOnly; }
        public List<String> getAmplifyDamageTypes() { return amplifyDamageTypes; }
        private String type, trigger;
        @SerializedName("mark_effect") private String markEffect;
        @SerializedName("duration_ticks") private int durationTicks = 600;
        private String coefficient;
        private double multiplier = 1.0;
        private String condition;
        private double amount = 0.5;
        private String slot;
        @SerializedName("attribute") private String attributeId;
        private double value = 1.0;
        private String operation = "add";
        @SerializedName("per_durability") private int perDurability = 1;
        @SerializedName("saturation_per_point") private int saturationPerPoint = 4;

        public String getType() { return type; }
        public String getTrigger() { return trigger; }
        public String getMarkEffect() { return markEffect; }
        public int getDurationTicks() { return durationTicks; }
        public String getCoefficient() { return coefficient; }
        public double getMultiplier() { return multiplier; }
        public String getCondition() { return condition; }
        public double getAmount() { return amount; }
        public String getSlot() { return slot; }
        public String getAttributeId() { return attributeId; }
        public double getValue() { return value; }
        public String getOperation() { return operation; }
        public int getPerDurability() { return perDurability; }
        public int getSaturationPerPoint() { return saturationPerPoint; }
    }

    public static class ParamDef {
        private String name;
        @SerializedName("default") private double defaultValue;
        private double min = 0, max = 10;
        private boolean percentage = false;
        public String getName() { return name; }
        public double getDefaultValue() { return defaultValue; }
        public double getMin() { return min; }
        public double getMax() { return max; }
        public boolean isPercentage() { return percentage; }
    }
}