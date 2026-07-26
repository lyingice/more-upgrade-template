package net.mcreator.mut.mixin;

import net.mcreator.mut.util.MutKnownMovementAccessor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Deprecated
@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMotionMixin extends Player implements MutKnownMovementAccessor {

    @SuppressWarnings("deprecation")
    public ServerPlayerMotionMixin() {
        super(null, null, 0.0f, null);
    }

    @Unique
    private Vec3 mutLastKnownClientMovement = Vec3.ZERO;
    @Unique
    private Vec3 mutLastPosition = Vec3.ZERO;
    @Unique
    private boolean mutFirstTick = true;

    @Inject(method = "tick", at = @At("HEAD"))
    public void onTickHead(CallbackInfo ci) {
        ServerPlayer self = (ServerPlayer) (Object) this;
        if (this.mutFirstTick) {
            this.mutLastPosition = self.position();
            this.mutFirstTick = false;
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void onTickTail(CallbackInfo ci) {
        ServerPlayer self = (ServerPlayer) (Object) this;
        // 用位置差计算速度，不受减速影响
        Vec3 currentPos = self.position();
        this.mutLastKnownClientMovement = currentPos.subtract(this.mutLastPosition);
        this.mutLastPosition = currentPos;
    }

    @Override
    @Unique
    public Vec3 mutGetKnownMovement() {
        return this.mutLastKnownClientMovement;
    }
    @Override
    @Unique
    public void mutSetKnownMovement(Vec3 vec3) {
        this.mutLastKnownClientMovement = vec3;
    }
}