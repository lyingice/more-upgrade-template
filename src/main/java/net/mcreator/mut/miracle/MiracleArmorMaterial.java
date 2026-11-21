package net.mcreator.mut.miracle;

import net.mcreator.mut.MutMod;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.EnumMap;
import java.util.List;

/**
 * 奇迹装备的护甲材质。
 * <p>
 * 当前数值：防御 36（6/13/11/6）、韧性 6、击退抗性 0.3、耐久倍率 100、附魔能力 30。
 * 它不靠堆数值取胜 —— 真正的强度在套装机制与"不被模板之神篡改"上，
 * 平衡思路见仓库根目录 MIRACLE_SET_DESIGN.md。
 */
@EventBusSubscriber(modid = MutMod.MODID)
public final class MiracleArmorMaterial {

    /** 耐久倍率 */
    public static final int DURABILITY_MULTIPLIER = 100;

    public static Holder<ArmorMaterial> MATERIAL = null;

    @SubscribeEvent
    public static void registerMaterial(RegisterEvent event) {
        event.register(Registries.ARMOR_MATERIAL, helper -> {
            EnumMap<ArmorItem.Type, Integer> defense = new EnumMap<>(ArmorItem.Type.class);
            defense.put(ArmorItem.Type.HELMET, 6);
            defense.put(ArmorItem.Type.CHESTPLATE, 13);
            defense.put(ArmorItem.Type.LEGGINGS, 11);
            defense.put(ArmorItem.Type.BOOTS, 6);
            defense.put(ArmorItem.Type.BODY, 13);

            ArmorMaterial material = new ArmorMaterial(
                    defense,
                    30,
                    DeferredHolder.create(Registries.SOUND_EVENT, ResourceLocation.parse("item.armor.equip_netherite")),
                    () -> Ingredient.of(Items.NETHERITE_INGOT),
                    List.of(new ArmorMaterial.Layer(ResourceLocation.parse("mut:miracle"))),
                    6.0F,
                    0.3F);

            helper.register(ResourceLocation.parse("mut:miracle"), material);
            MATERIAL = BuiltInRegistries.ARMOR_MATERIAL.wrapAsHolder(material);
            MutMod.LOGGER.info("Registered armor material mut:miracle");
        });
    }

    private MiracleArmorMaterial() {
    }
}
