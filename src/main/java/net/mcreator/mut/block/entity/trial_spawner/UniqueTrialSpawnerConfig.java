package net.mcreator.mut.block.entity.trial_spawner;

import net.minecraft.world.item.Items;

public class UniqueTrialSpawnerConfig extends TrialSpawnerConfig {
    public UniqueTrialSpawnerConfig() {
        super();
        this.rewardItem = Items.OMINOUS_TRIAL_KEY.getDefaultInstance();
    }
}

