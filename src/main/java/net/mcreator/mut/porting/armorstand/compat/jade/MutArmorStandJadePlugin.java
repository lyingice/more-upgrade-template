package net.mcreator.mut.porting.armorstand.compat.jade;

import net.mcreator.mut.MutMod;
import net.mcreator.mut.porting.armorstand.HorseArmorStandEntity;
import net.mcreator.mut.porting.armorstand.WolfArmorStandEntity;

import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.JadeIds;
import snownee.jade.api.WailaPlugin;

/**
 * Jade 集成（移植自 TCR 的 TcrJadePlugin）。
 *
 * <p>仅在安装了 Jade 时由其注解扫描加载；未安装时本类不会被加载。</p>
 */
@WailaPlugin(MutMod.MODID)
public class MutArmorStandJadePlugin implements IWailaPlugin {

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerEntityComponent(
                MutHorseArmorStandProvider.INSTANCE,
                HorseArmorStandEntity.class
        );
        registration.registerEntityComponent(
                MutWolfArmorStandProvider.INSTANCE,
                WolfArmorStandEntity.class
        );
        registration.registerEntityIcon(MutHorseArmorStandProvider.INSTANCE, HorseArmorStandEntity.class);
        registration.registerEntityIcon(MutWolfArmorStandProvider.INSTANCE, WolfArmorStandEntity.class);

        // 展示架不受伤害，隐藏原版生命/护甲/马匹状态条目
        registration.addTooltipCollectedCallback((box, accessor) -> {
            if (accessor instanceof EntityAccessor entityAccessor) {
                if (entityAccessor.getEntity() instanceof HorseArmorStandEntity
                        || entityAccessor.getEntity() instanceof WolfArmorStandEntity) {
                    box.getTooltip().remove(JadeIds.MC_ENTITY_HEALTH);
                    box.getTooltip().remove(JadeIds.MC_ENTITY_ARMOR);
                    box.getTooltip().remove(JadeIds.MC_HORSE_STATS);
                }
            }
        });
    }
}
