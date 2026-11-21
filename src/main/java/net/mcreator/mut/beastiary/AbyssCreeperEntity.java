package net.mcreator.mut.beastiary;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;

/**
 * 渊咒苦力怕：50 血，爆炸范围更大（半径 5），中心伤害约 120，
 * 命中范围内生物会吃到 10 秒的坠渊诅咒 IV（削减 5 点生命上限）。
 * <p>
 * 爆炸半径通过原版 NBT 字段 ExplosionRadius 设置 —— Creeper 没有公开的 setter。
 */
public class AbyssCreeperEntity extends Creeper {

    private static final int RADIUS = 5;

    public AbyssCreeperEntity(EntityType<? extends AbyssCreeperEntity> type, Level level) {
        super(type, level);
        CompoundTag tag = new CompoundTag();
        tag.putByte("ExplosionRadius", (byte) RADIUS);
        this.readAdditionalSaveData(tag);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Creeper.createAttributes().add(Attributes.MAX_HEALTH, 50.0D);
    }

    public int getAbyssRadius() {
        return RADIUS;
    }
}
