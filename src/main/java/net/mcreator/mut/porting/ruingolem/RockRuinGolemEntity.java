package net.mcreator.mut.porting.ruingolem;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * 失控的岩石傀儡：100 血 / 10 护甲 / 10 攻。
 * <p>
 * 未索敌时它会站着不动装成石像 —— 外观和 {@link RockGolemBlock} 那块石像方块完全一致，
 * 玩家靠眼睛分不出哪块是石头、哪只活着。一旦锁定目标就"活过来"开始追击。
 */
public class RockRuinGolemEntity extends AbstractRuinGolemEntity {

    private static final EntityDataAccessor<Boolean> DATA_DISGUISED =
            SynchedEntityData.defineId(RockRuinGolemEntity.class, EntityDataSerializers.BOOLEAN);

    public RockRuinGolemEntity(EntityType<? extends RockRuinGolemEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_DISGUISED, true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return golemBaseAttributes(100.0D, 10.0D, 0.0D, 10.0D, 0.4D);
    }

    /** 当前是否处于装成石像的状态 */
    public boolean isDisguised() {
        return this.entityData.get(DATA_DISGUISED);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) return;

        boolean idle = this.getTarget() == null;
        if (this.entityData.get(DATA_DISGUISED) != idle) {
            this.entityData.set(DATA_DISGUISED, idle);
        }
        if (idle) {
            // 装成石像时不许挪窝
            this.getNavigation().stop();
            Vec3 motion = this.getDeltaMovement();
            this.setDeltaMovement(0.0D, motion.y, 0.0D);
        }
    }
}
