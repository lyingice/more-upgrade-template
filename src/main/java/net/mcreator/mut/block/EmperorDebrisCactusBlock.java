package net.mcreator.mut.block;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.level.material.MapColor;

public class EmperorDebrisCactusBlock extends CactusBlock {
    public EmperorDebrisCactusBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_BROWN)
                .sound(SoundType.WET_GRASS)
                .strength(1.0f, 10.0f)
                .noOcclusion()
                .randomTicks()
                .instabreak()
        );
    }
    // ========== 自定义伤害数值 ==========
    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return level.getBlockState(pos.below()).is(BlockTags.create(ResourceLocation.fromNamespaceAndPath("minecraft", "cactus_plantable_on")));
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        // 踩踏伤害
        entity.hurt(level.damageSources().cactus(), 20.0F);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        // 持续接触伤害
        entity.hurt(level.damageSources().cactus(), 5.0F);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);  // 自动继承 AGE 属性
    }
}