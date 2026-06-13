package net.mcreator.mut.compat;

import net.mcreator.mut.block.SigilForgeBossTrialSpawnerBlock;
import net.mcreator.mut.block.SigilForgeTrialSpawnerBlock;
import net.mcreator.mut.block.SigilForgeUniqueTrialSpawnerBlock;
import net.mcreator.mut.block.entity.SigilForgeBossTrialSpawnerBlockEntity;
import net.mcreator.mut.block.entity.SigilForgeTrialSpawnerBlockEntity;
import net.mcreator.mut.block.entity.SigilForgeUniqueTrialSpawnerBlockEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class MutJadePlugin implements IWailaPlugin {

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(BossSpawnerInfoProvider.INSTANCE, SigilForgeBossTrialSpawnerBlockEntity.class);
        registration.registerBlockDataProvider(TrialSpawnerInfoProvider.INSTANCE, SigilForgeTrialSpawnerBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(BossSpawnerInfoProvider.INSTANCE, SigilForgeBossTrialSpawnerBlock.class);
        registration.registerBlockComponent(TrialSpawnerInfoProvider.INSTANCE, SigilForgeTrialSpawnerBlock.class);
    }
}