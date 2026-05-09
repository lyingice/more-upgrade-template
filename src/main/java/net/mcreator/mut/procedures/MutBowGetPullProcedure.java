package net.mcreator.mut.procedures;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public class MutBowGetPullProcedure {
    public static double execute(Entity entity, ItemStack bowStack) {
        if (entity == null) return 0;
        
        int maxDrawDuration = getMaxDrawDurationFromItem(bowStack);
        
        int useTime = entity instanceof LivingEntity living ? living.getTicksUsingItem() : 0;
        return Math.min(1.0, (double) useTime / maxDrawDuration);
    }
    
    private static int getMaxDrawDurationFromItem(ItemStack stack) {
        if (stack.getItem() instanceof net.mcreator.mut.item.IronBowItem) {
            return net.mcreator.mut.item.IronBowItem.MAX_DRAW_DURATION;
        } else if (stack.getItem() instanceof net.mcreator.mut.item.DiamondBowItem) {
            return net.mcreator.mut.item.DiamondBowItem.MAX_DRAW_DURATION;
        } else if (stack.getItem() instanceof net.mcreator.mut.item.GoldenBowItem) {
            return net.mcreator.mut.item.GoldenBowItem.MAX_DRAW_DURATION;
        } else if (stack.getItem() instanceof net.mcreator.mut.item.NetheriteBowItem) {
            return net.mcreator.mut.item.NetheriteBowItem.MAX_DRAW_DURATION;
        } else if (stack.getItem() instanceof net.mcreator.mut.item.SteelBowItem) {
            return net.mcreator.mut.item.SteelBowItem.MAX_DRAW_DURATION;
        } else if (stack.getItem() instanceof net.mcreator.mut.item.GildingBowItem) {
            return net.mcreator.mut.item.GildingBowItem.MAX_DRAW_DURATION;
        } else if (stack.getItem() instanceof net.mcreator.mut.item.AdvancedSteelBowItem) {
            return net.mcreator.mut.item.AdvancedSteelBowItem.MAX_DRAW_DURATION;
        } else if (stack.getItem() instanceof net.mcreator.mut.item.BlueDiamondBowItem) {
            return net.mcreator.mut.item.BlueDiamondBowItem.MAX_DRAW_DURATION;
        } else if (stack.getItem() instanceof net.mcreator.mut.item.NetherStarBowItem) {
            return net.mcreator.mut.item.NetherStarBowItem.MAX_DRAW_DURATION;
        } else if (stack.getItem() instanceof net.mcreator.mut.item.ObsidianBowItem) {
            return net.mcreator.mut.item.ObsidianBowItem.MAX_DRAW_DURATION;
        } else if (stack.getItem() instanceof net.mcreator.mut.item.NetheriteObsidianBowItem) {
            return net.mcreator.mut.item.NetheriteObsidianBowItem.MAX_DRAW_DURATION;
        } else if (stack.getItem() instanceof net.mcreator.mut.item.CryingObsidianBowItem) {
            return net.mcreator.mut.item.CryingObsidianBowItem.MAX_DRAW_DURATION;
        } else if (stack.getItem() instanceof net.mcreator.mut.item.CopperBowItem) {
            return net.mcreator.mut.item.CopperBowItem.MAX_DRAW_DURATION;
        } else if (stack.getItem() instanceof net.mcreator.mut.item.NetheriteCopperBowItem) {
            return net.mcreator.mut.item.NetheriteCopperBowItem.MAX_DRAW_DURATION;
        }
        return 20;
    }
}