package net.mcreator.mut.porting.armorstand;

import net.mcreator.mut.porting.armorstand.HorseArmorStandEntity;
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

public class HorseArmorStandItem extends Item {

    public HorseArmorStandItem(Properties properties) {
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

        // 实体沿朝向前移 0.5 格：模型与碰撞箱（长 1.5 格、以实体为中心）
        // 相对放置格整体靠前，只往前占格子
        Direction facing = Direction.fromYRot(yRot);
        Vec3 vec = Vec3.atBottomCenterOf(pos).add(
                facing.getStepX() * 0.425D,
                0.0D,
                facing.getStepZ() * 0.425D
        );

        // 放置检测使用与实体一致的旋转碰撞箱
        AABB aabb = HorseArmorStandEntity.makeStandAABB(
                vec.x(),
                vec.y(),
                vec.z(),
                yRot
        );
        if (!level.noCollision(null, aabb)
                || !level.getEntities(null, aabb).isEmpty()) {
            return InteractionResult.FAIL;
        }
        HorseArmorStandEntity stand =
                MutArmorStandEntities.HORSE_ARMOR_STAND.get().create(serverLevel);
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