package net.mcreator.mut.porting.smithing.client;

import net.mcreator.mut.MutMod;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

/**
 * 预烘焙下界合金纹饰盔甲物品模型（移植自 TCR 的 TcrModelRegistration）。
 */
@EventBusSubscriber(modid = MutMod.MODID, value = Dist.CLIENT)
public class MutTrimModelRegistration {

    private static final String[] ARMOR_PIECES = {
            "helmet", "chestplate", "leggings", "boots"
    };

    private static final String[] TRIM_MATERIALS = {
            "quartz", "iron", "netherite", "redstone", "copper",
            "gold", "emerald", "diamond", "lapis", "amethyst",
            // 来自美术色卡的模组原材料材质
            "crying_obsidian", "flame_gold", "obsidian", "echoite", "poison_steel",
            "advanced_steel", "dragon_scale", "heterochromatic_amethyst",
            "heterochromatic_amethyst_pale", "refined_diamond_crystals"
    };

    @SubscribeEvent
    public static void onRegisterAdditional(ModelEvent.RegisterAdditional event) {
        for (String piece : ARMOR_PIECES) {
            for (String material : TRIM_MATERIALS) {
                ResourceLocation modelId = ResourceLocation.fromNamespaceAndPath(
                        MutMod.MODID,
                        "item/netherite_" + piece + "_" + material + "_trim"
                );
                event.register(ModelResourceLocation.standalone(modelId));
            }
        }
    }

    private MutTrimModelRegistration() {
    }
}
