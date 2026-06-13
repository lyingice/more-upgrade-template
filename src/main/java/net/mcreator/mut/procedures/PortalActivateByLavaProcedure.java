package net.mcreator.mut.procedures;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.player.Player;

import net.mcreator.mut.world.teleporter.SigilForgeDimensionPortalShape;

import java.util.Optional;

public class PortalActivateByLavaProcedure {

    public static boolean execute(LevelAccessor world, double x, double y, double z,
                                  Direction direction, ItemStack itemstack, Player player) {

        if (direction == null) return false;
        if (!itemstack.is(Items.FLINT_AND_STEEL)) return false;
        if (!(world instanceof Level level)) return false;

        BlockPos portalPos = BlockPos.containing(
                x + direction.getStepX(),
                y + direction.getStepY(),
                z + direction.getStepZ()
        );

        Optional<SigilForgeDimensionPortalShape> shape =
                SigilForgeDimensionPortalShape.findEmptyPortalShape(world, portalPos, Direction.Axis.X);
        if (shape.isEmpty()) {
            shape = SigilForgeDimensionPortalShape.findEmptyPortalShape(world, portalPos, Direction.Axis.Z);
        }

        if (shape.isPresent() && shape.get().isValid()) {
            shape.get().createPortalBlocks();

            // 消耗打火石耐久
            itemstack.hurtAndBreak(1, player, Player.getSlotForHand(player.getUsedItemHand()));

            level.playSound(null, portalPos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS);
            return true;
        }
        return false;
    }
}