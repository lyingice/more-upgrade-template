package net.mcreator.mut.porting.armorstand;

import net.mcreator.mut.porting.armorstand.WolfArmorStandEntity;
import net.mcreator.mut.porting.armorstand.MutArmorStandEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class WolfArmorStandItem extends Item {

    public WolfArmorStandItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Direction direction = context.getClickedFace();
        if (direction == Direction.DOWN) {
            return InteractionResult.FAIL;
        }
        Level level = context.getLevel();
        BlockPlaceContext placeContext = new BlockPlaceContext(context);
        BlockPos pos = placeContext.getClickedPos();
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        float yRot = 0.0F;
        if (context.getPlayer() != null) {
            yRot = Math.round(context.getPlayer().getYRot() / 90.0F) * 90.0F;
        }

        // 狼近似对称，底座居中放置（无前移偏移）
        Vec3 vec = Vec3.atBottomCenterOf(pos);

        // 放置检测使用与实体一致的旋转碰撞箱
        AABB aabb = WolfArmorStandEntity.makeStandAABB(
                vec.x(),
                vec.y(),
                vec.z(),
                yRot
        );
        if (!level.noCollision(null, aabb)
                || !level.getEntities(null, aabb).isEmpty()) {
            return InteractionResult.FAIL;
        }
        WolfArmorStandEntity stand =
                MutArmorStandEntities.WOLF_ARMOR_STAND.get().create(serverLevel);
        if (stand == null) {
            return InteractionResult.FAIL;
        }
        stand.moveTo(
                vec.x(),
                vec.y(),
                vec.z(),
                yRot,
                0.0F
        );
        serverLevel.addFreshEntityWithPassengers(stand);
        context.getItemInHand().shrink(1);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}