package net.mcreator.mut.porting.smithing.client;

import net.mcreator.mut.MutMod;
import net.mcreator.mut.porting.smithing.MutTrimPatternKeys;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.armortrim.ArmorTrim;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.List;

/**
 * 注册 {@code mut:netherite_trim} 物品模型谓词：下界合金盔甲带下界合金纹饰时返回 1.0。
 *
 * <p>配合 {@code assets/minecraft/overrides/item/netherite_*.json} 的旁挂 override，
 * 让纹饰材质显示到物品图标上。</p>
 */
@EventBusSubscriber(modid = MutMod.MODID, value = Dist.CLIENT)
public final class MutNetheriteTrimProperty {
    public static final ResourceLocation NETHERITE_TRIM_ID =
            ResourceLocation.fromNamespaceAndPath(MutMod.MODID, "netherite_trim");

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(MutNetheriteTrimProperty::register);
    }

    private static void register() {
        List<Item> armors = List.of(
                Items.NETHERITE_HELMET,
                Items.NETHERITE_CHESTPLATE,
                Items.NETHERITE_LEGGINGS,
                Items.NETHERITE_BOOTS
        );

        for (Item item : armors) {
            ItemProperties.register(item, NETHERITE_TRIM_ID, MutNetheriteTrimProperty::computeNetheriteTrim);
        }
    }

    private static float computeNetheriteTrim(
            ItemStack stack,
            ClientLevel level,
            LivingEntity entity,
            int seed
    ) {
        ArmorTrim trim = stack.get(DataComponents.TRIM);
        if (trim == null) {
            return 0.0F;
        }
        return trim.pattern().is(MutTrimPatternKeys.NETHERITE) ? 1.0F : 0.0F;
    }

    private MutNetheriteTrimProperty() {
    }
}
