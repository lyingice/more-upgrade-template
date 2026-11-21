package net.mcreator.mut.porting.animalarmor.client;

import net.mcreator.mut.MutMod;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * 物品模型谓词 {@code mut:animal_armor_trim}：返回纹饰材质的 item_model_index
 * （无纹饰为 0），供马铠/狼铠的图标覆盖选择对应材质层。
 */
@EventBusSubscriber(modid = MutMod.MODID, value = Dist.CLIENT)
public final class MutAnimalArmorTrimProperty {
    public static final ResourceLocation TRIM_ID =
            ResourceLocation.fromNamespaceAndPath(MutMod.MODID, "animal_armor_trim");

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(MutAnimalArmorTrimProperty::register);
    }

    private static void register() {
        for (Item item : BuiltInRegistries.ITEM) {
            if (item instanceof AnimalArmorItem) {
                ItemProperties.register(item, TRIM_ID, MutAnimalArmorTrimProperty::computeTrimIndex);
            }
        }
    }

    private static float computeTrimIndex(
            ItemStack stack,
            ClientLevel level,
            LivingEntity entity,
            int seed
    ) {
        ArmorTrim trim = stack.get(DataComponents.TRIM);
        return trim == null ? 0.0F : trim.material().value().itemModelIndex();
    }

    private MutAnimalArmorTrimProperty() {
    }
}
