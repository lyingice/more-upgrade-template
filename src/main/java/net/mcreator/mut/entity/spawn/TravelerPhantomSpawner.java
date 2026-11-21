package net.mcreator.mut.entity.spawn;

import net.mcreator.mut.MutMod;
import net.mcreator.mut.entity.TravelerPhantomEntity;
import net.mcreator.mut.init.MutModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * 旅行者幻翼在符锻维度的生成器。
 * <p>
 * 复刻原版 {@code PhantomSpawner}：夜晚 + 开启失眠规则 + 玩家长时间未睡觉，
 * 在玩家上方 20~35 格随机位置生成。原版幻翼不走普通刷怪循环，这里同理。
 */
@EventBusSubscriber(modid = MutMod.MODID)
public class TravelerPhantomSpawner {

    private static final ResourceKey<Level> TARGET_DIMENSION =
            ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse("mut:sigil_forge_dimension"));

    /** 原版常量：72000 tick = 3 个游戏日 */
    private static final int SLEEP_TIME_THRESHOLD = 72000;

    private static final Map<ResourceKey<Level>, Integer> NEXT_TICK = new HashMap<>();

    /** 供探针/调试使用：判断某个维度是不是符锻维度 */
    public static boolean isTargetDimension(Level level) {
        return level.dimension().equals(TARGET_DIMENSION);
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (!isTargetDimension(level)) return;
        if (!level.getGameRules().getBoolean(GameRules.RULE_DOINSOMNIA)) return;

        RandomSource random = level.random;
        int countdown = NEXT_TICK.getOrDefault(level.dimension(), 1) - 1;
        if (countdown > 0) {
            NEXT_TICK.put(level.dimension(), countdown);
            return;
        }
        NEXT_TICK.put(level.dimension(), (60 + random.nextInt(60)) * 20);

        // 夜晚才生成（维度有天空光时才判断）
        if (level.dimensionType().hasSkyLight() && level.getSkyDarken() < 5) return;

        for (ServerPlayer player : level.players()) {
            if (player.isSpectator()) continue;

            BlockPos origin = player.blockPosition();
            DifficultyInstance difficulty = level.getCurrentDifficultyAt(origin);
            if (!difficulty.isHarderThan(random.nextFloat() * 3.0F)) continue;

            int sinceRest = Mth.clamp(
                    player.getStats().getValue(Stats.CUSTOM.get(Stats.TIME_SINCE_REST)),
                    1, Integer.MAX_VALUE);
            // 原版写法：nextInt(sinceRest) >= 72000 就生成
            if (random.nextInt(sinceRest) < SLEEP_TIME_THRESHOLD) continue;

            BlockPos spawnPos = origin
                    .above(20 + random.nextInt(15))
                    .east(-10 + random.nextInt(21))
                    .south(-10 + random.nextInt(21));

            BlockState state = level.getBlockState(spawnPos);
            FluidState fluid = level.getFluidState(spawnPos);
            if (!NaturalSpawner.isValidEmptySpawnBlock(level, spawnPos, state, fluid,
                    MutModEntities.TRAVELER_PHANTOM.get())) {
                continue;
            }

            int amount = 1 + random.nextInt(2);
            SpawnGroupData groupData = null;
            for (int i = 0; i < amount; i++) {
                TravelerPhantomEntity phantom = MutModEntities.TRAVELER_PHANTOM.get().create(level);
                if (phantom == null) continue;
                phantom.moveTo(spawnPos, 0.0F, 0.0F);
                groupData = phantom.finalizeSpawn(level, difficulty, MobSpawnType.NATURAL, groupData);
                // 直接调用 finalizeSpawn 不会触发 FinalizeSpawnEvent，组合要显式应用
                SpawnComboHandler.applyCombos(phantom, level, MobSpawnType.NATURAL);
                level.addFreshEntityWithPassengers(phantom);
            }
            return;
        }
    }
}
