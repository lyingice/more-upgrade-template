package net.mcreator.mut.porting.ruingolem;

import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;

/** 魔化的哭泣黑曜石傀儡：1000 血 / 40 护甲 / 30 韧性 / 36 攻，小 BOSS。 */
public class CryingObsidianRuinGolemEntity extends AbstractRuinGolemEntity {

    private final ServerBossEvent bossEvent =
            new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS);

    public CryingObsidianRuinGolemEntity(EntityType<? extends CryingObsidianRuinGolemEntity> type, Level level) {
        super(type, level);
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return golemBaseAttributes(1000.0D, 40.0D, 30.0D, 36.0D, 1.0D);
    }

    public ServerBossEvent getBossEvent() {
        return bossEvent;
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        if (!this.level().isClientSide()) {
            this.bossEvent.addPlayer(player);
        }
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        if (!this.level().isClientSide()) {
            this.bossEvent.removePlayer(player);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        }
    }
}
