package net.mcreator.mut.block;

import net.mcreator.mut.init.MutModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PowderSnowBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.checkerframework.checker.nullness.qual.Nullable;

public class SigilForgePowderSnowBlock extends PowderSnowBlock {
	public SigilForgePowderSnowBlock() {
		super(BlockBehaviour.Properties.of().sound(SoundType.POWDER_SNOW).strength(0.25f, 10f));
	}
    @Override
    public ItemStack pickupBlock(@Nullable Player player, LevelAccessor level, BlockPos pos, BlockState state) {
        // 1. 将细雪方块移除（替换为空气）
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);

        // 2. 播放收集粒子效果（原版细雪桶的视觉反馈）
        if (!level.isClientSide()) {
            level.levelEvent(2001, pos, Block.getId(state));
        }

        // 3. ★ 返回你的自定义细雪桶，而不是原版的 Items.POWDER_SNOW_BUCKET ★
        return new ItemStack(MutModItems.SIGIL_FORGE_POWDER_SNOW_BUCKET.get());
    }
    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        // 保留原版细雪行为（冻结、熄灭火焰、减速等）
        super.entityInside(state, level, pos, entity);

        // 服务端执行逻辑（伤害和状态效果）
        if (!level.isClientSide && entity instanceof LivingEntity living) {

            // 排除创造模式和旁观模式的玩家
            if (entity instanceof Player player) {
                if (player.isCreative() || player.isSpectator()) {
                    return;
                }
            }

            // ★ 每20 tick（1秒）执行一次 ★
            if (living.tickCount % 20 == 0) {

                // 1️⃣ 原版细雪伤害是 1.0F（半颗心），提升3倍 = 3.0F（1.5颗心）
                float originalDamage = 1.0F;
                float multipliedDamage = originalDamage * 3.0F;  // = 3.0F

                // 应用伤害（使用 freeze 伤害源，与原版细雪一致）
                living.hurt(level.damageSources().freeze(), multipliedDamage);

                // 2️⃣ 给予 1秒（20 tick）的挖掘疲劳 V（等级4 = V级）
                // 注意：MobEffectInstance 的第二个参数是持续时间（tick），第三个参数是等级（0 = I级）
                living.addEffect(new MobEffectInstance(
                        MobEffects.DIG_SLOWDOWN,  // 挖掘疲劳
                        20,                       // 持续 1 秒（20 tick）
                        4                         // 等级 4 = V 级（0= I, 1= II, 2= III, 3= IV, 4= V）
                ));
            }
        }
    }
}