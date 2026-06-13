package net.mcreator.mut.affix.data;

import net.mcreator.mut.affix.Affix;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * 随机结果 DTO - 包含词缀、等级、保底状态等信息
 */
public class RollResult {

    private final Affix affix;
    private final int level;
    private final int originalLevel;
    private final MaterialContext materialContext;
    private final int pityBefore;
    private final int pityAfter;
    private final boolean pityTriggered;
    private final List<String> debugInfo;

    private RollResult(Builder builder) {
        this.affix = builder.affix;
        this.level = builder.level;
        this.originalLevel = builder.originalLevel;
        this.materialContext = builder.materialContext;
        this.pityBefore = builder.pityBefore;
        this.pityAfter = builder.pityAfter;
        this.pityTriggered = builder.pityTriggered;
        this.debugInfo = builder.debugInfo;
    }

    public Affix getAffix() { return affix; }
    public int getLevel() { return level; }
    public int getOriginalLevel() { return originalLevel; }
    @Nullable
    public MaterialContext getMaterialContext() { return materialContext; }
    public int getPityBefore() { return pityBefore; }
    public int getPityAfter() { return pityAfter; }
    public boolean isPityTriggered() { return pityTriggered; }
    public List<String> getDebugInfo() { return debugInfo; }

    /**
     * 将结果应用到物品上
     */
    public void applyToStack(ItemStack stack) {
        if (affix != null && !stack.isEmpty()) {
            affix.applyToStack(stack, level);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Affix affix;
        private int level;
        private int originalLevel;
        private MaterialContext materialContext;
        private int pityBefore;
        private int pityAfter;
        private boolean pityTriggered;
        private final List<String> debugInfo = new ArrayList<>();

        public Builder affix(Affix affix) { this.affix = affix; return this; }
        public Builder level(int level) { this.level = level; return this; }
        public Builder originalLevel(int originalLevel) { this.originalLevel = originalLevel; return this; }
        public Builder materialContext(MaterialContext materialContext) { this.materialContext = materialContext; return this; }
        public Builder pityBefore(int pityBefore) { this.pityBefore = pityBefore; return this; }
        public Builder pityAfter(int pityAfter) { this.pityAfter = pityAfter; return this; }
        public Builder pityTriggered(boolean pityTriggered) { this.pityTriggered = pityTriggered; return this; }
        public Builder addDebug(String line) { this.debugInfo.add(line); return this; }
        public Builder debugInfo(List<String> debugInfo) { this.debugInfo.addAll(debugInfo); return this; }

        public RollResult build() {
            return new RollResult(this);
        }
    }
}
