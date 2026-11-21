package net.mcreator.mut.porting.animalarmor.client;

import net.mcreator.mut.MutMod;
import net.mcreator.mut.porting.animalarmor.MutAnimalArmorTrim;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.Item;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * 预烘焙动物铠纹饰图标模型（standalone）。
 *
 * <p>只会注册真实存在模型文件的物品，其它模组的动物铠不会导致烘焙失败。</p>
 */
@EventBusSubscriber(modid = MutMod.MODID, value = Dist.CLIENT)
public final class MutAnimalArmorTrimModelRegistration {

    @SubscribeEvent
    public static void onRegisterAdditional(ModelEvent.RegisterAdditional event) {
        for (ResourceLocation modelId : collectModelIds()) {
            if (modelFileExists(modelId)) {
                event.register(ModelResourceLocation.standalone(modelId));
            }
        }
    }

    /** 当前应存在的动物铠纹饰模型 id（物品取自动物铠注册表，材质为固定列表）。 */
    public static List<ResourceLocation> collectModelIds() {
        List<ResourceLocation> ids = new ArrayList<>();

        for (var entry : BuiltInRegistries.ITEM.entrySet()) {
            Item item = entry.getValue();
            if (!(item instanceof AnimalArmorItem)) {
                continue;
            }

            String path = entry.getKey().location().getPath();

            for (String material : MutAnimalArmorTrim.MATERIALS) {
                ids.add(
                        ResourceLocation.fromNamespaceAndPath(
                                MutMod.MODID,
                                "item/" + path + "_" + material + "_trim"
                        )
                );
            }
        }

        return ids;
    }

    private static boolean modelFileExists(ResourceLocation modelId) {
        try {
            ResourceLocation file = ResourceLocation.fromNamespaceAndPath(
                    modelId.getNamespace(),
                    "models/" + modelId.getPath() + ".json"
            );
            return Minecraft.getInstance().getResourceManager().getResource(file).isPresent();
        } catch (Exception e) {
            return false;
        }
    }

    private MutAnimalArmorTrimModelRegistration() {
    }
}
