package net.mcreator.mut.beastiary;

import net.mcreator.mut.MutMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * 坠渊诅咒：持续削减生命上限。
 * <p>
 * 等级与削减量：I 减 1、II 减 2、III 减 4、IV 减 5（按文档口径）。
 */
public class AbyssCurseEffect extends MobEffect {

    public AbyssCurseEffect() {
        super(MobEffectCategory.HARMFUL, 0x4B3A78);
        this.addAttributeModifier(Attributes.MAX_HEALTH,
                ResourceLocation.fromNamespaceAndPath(MutMod.MODID, "abyss_curse_max_health"),
                AttributeModifier.Operation.ADD_VALUE,
                amplifier -> switch (amplifier) {
                    case 0 -> -1.0D;
                    case 1 -> -2.0D;
                    case 2 -> -4.0D;
                    default -> -5.0D;
                });
    }
}
