package net.mcreator.mut.block.MutBlock.Blocks;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SteelDebrisApple extends DebrisAppleBlock{
    // 地面版：果实 4~12, 0~8 | 茎 7~9, 8~10
    private static final VoxelShape STANDING = Shapes.or(
            box(4, 0, 4, 12, 8, 12),
            box(7, 8, 7, 9, 10, 9)
    );

    // 悬挂版：茎 7~9, 16~18 | 果实 4~12, 8~16
    private static final VoxelShape HANGING = Shapes.or(
            box(4, 6, 4, 12, 14, 12),
            box(7, 14, 7, 9, 16, 9)
    );
    public SteelDebrisApple() {
        super(STANDING, HANGING);
    }
}
