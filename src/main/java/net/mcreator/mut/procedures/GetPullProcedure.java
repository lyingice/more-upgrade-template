package net.mcreator.mut.procedures;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;

public class GetPullProcedure {
	public static double execute(Entity entity) {
		if (entity == null)
			return 0;
		return (entity instanceof LivingEntity _entUseTicks0 ? _entUseTicks0.getTicksUsingItem() : 0) / 20d;
	}
}