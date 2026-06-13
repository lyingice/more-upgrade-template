package net.mcreator.mut.block.entity.vault;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.vault.VaultState;
import net.minecraft.world.phys.Vec3;

public class VaultEffects {

    private static final int ACTIVATION_PARTICLE_COUNT = 20;
    private static final int DEACTIVATION_PARTICLE_COUNT = 20;

    private float displaySpin;
    private float displayPrevSpin;

    /**
     * 客户端tick - 粒子和旋转动画
     */
    public void clientTick(Level level, BlockPos pos, VaultState vaultState, boolean hasDisplayItem) {
        displayPrevSpin = displaySpin;
        displaySpin = Mth.wrapDegrees(displaySpin + 10f);

        if (vaultState == VaultState.INACTIVE) return;

        RandomSource random = level.getRandom();

        // 50%概率生成烟雾粒子
        if (random.nextFloat() <= 0.5f) {
            Vec3 pos_ = randomPosInside(pos, random);
            level.addParticle(ParticleTypes.SMOKE, pos_.x, pos_.y, pos_.z, 0, 0, 0);
            // 有展示物品时加火焰粒子
            if (hasDisplayItem) {
                level.addParticle(ParticleTypes.SMALL_FLAME, pos_.x, pos_.y, pos_.z, 0, 0, 0);
            }
        }

        // 2%概率环境音效
        if (random.nextFloat() <= 0.02f) {
            level.playLocalSound(pos, SoundEvents.VAULT_AMBIENT, SoundSource.BLOCKS,
                    random.nextFloat() * 0.25f + 0.75f, random.nextFloat() + 0.5f, false);
        }
    }

    /**
     * 激活粒子 - 从INACTIVE切换到ACTIVE时调用
     */
    public static void emitActivationParticles(Level level, BlockPos pos, ParticleOptions particleType) {
        RandomSource random = level.getRandom();
        for (int i = 0; i < ACTIVATION_PARTICLE_COUNT; i++) {
            Vec3 pos_ = randomPosInside(pos, random);
            level.addParticle(ParticleTypes.SMOKE, pos_.x, pos_.y, pos_.z, 0, 0, 0);
            level.addParticle(particleType, pos_.x, pos_.y, pos_.z, 0, 0, 0);
        }
    }

    /**
     * 停用粒子 - 从ACTIVE切换到INACTIVE时调用
     */
    public static void emitDeactivationParticles(Level level, BlockPos pos, ParticleOptions particleType) {
        RandomSource random = level.getRandom();
        for (int i = 0; i < DEACTIVATION_PARTICLE_COUNT; i++) {
            Vec3 pos_ = randomPosCenterOf(pos, random);
            Vec3 speed = new Vec3(
                    random.nextGaussian() * 0.02,
                    random.nextGaussian() * 0.02,
                    random.nextGaussian() * 0.02
            );
            level.addParticle(particleType, pos_.x, pos_.y, pos_.z, speed.x, speed.y, speed.z);
        }
    }

    public float getDisplaySpin(float partialTicks) {
        return Mth.lerp(partialTicks, displayPrevSpin, displaySpin);
    }

    // ===== 位置计算 =====

    /**
     * 笼子内部随机位置 (0.1~0.9)
     */
    private static Vec3 randomPosInside(BlockPos pos, RandomSource random) {
        return Vec3.atLowerCornerOf(pos)
                .add(Mth.nextDouble(random, 0.1, 0.9),
                        Mth.nextDouble(random, 0.25, 0.75),
                        Mth.nextDouble(random, 0.1, 0.9));
    }

    /**
     * 笼子中央随机位置 (0.4~0.6)
     */
    private static Vec3 randomPosCenterOf(BlockPos pos, RandomSource random) {
        return Vec3.atLowerCornerOf(pos)
                .add(Mth.nextDouble(random, 0.4, 0.6),
                        Mth.nextDouble(random, 0.4, 0.6),
                        Mth.nextDouble(random, 0.4, 0.6));
    }
}