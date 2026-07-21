package net.mcreator.mut.util;import net.minecraft.world.entity.Entity;
@Deprecated
public interface MutSpearCooldownAccessor {    boolean mutWasRecentlyStabbed(Entity target, int cooldownTicks);    void mutRememberStabbedEntity(Entity target);}