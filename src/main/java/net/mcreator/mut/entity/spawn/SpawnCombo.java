package net.mcreator.mut.entity.spawn;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;

import java.util.Set;

/**
 * 一条"主生物生成时顺带带一个骑乘者"的组合配置。
 * 例：幻翼生成时背一只红色闪电苦力怕。
 */
public record SpawnCombo(String id, ResourceLocation host, ResourceLocation rider,
                         float chance, int minCount, int maxCount, Set<MobSpawnType> spawnTypes) {

    public EntityType<?> hostType() {
        return BuiltInRegistries.ENTITY_TYPE.get(host);
    }

    public EntityType<?> riderType() {
        return BuiltInRegistries.ENTITY_TYPE.get(rider);
    }

    public boolean appliesTo(MobSpawnType type) {
        return spawnTypes.isEmpty() || spawnTypes.contains(type);
    }
}
