package net.mcreator.mut.porting.animalarmor.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.ItemStack;

/**
 * 动物铠实体贴图解析。
 *
 * <p>原版 {@link AnimalArmorItem#getTexture()} 对 CANINE / EQUESTRIAN 返回的是固定贴图，
 * 模组动物铠因此会显示成原版外观。本模组（以及多数模组）的约定是：</p>
 *
 * <pre>
 *   &lt;namespace&gt;:textures/entity/wolf/&lt;item_path&gt;.png          （狼铠）
 *   &lt;namespace&gt;:textures/entity/horse/armor/&lt;item_path&gt;.png   （马铠）
 * </pre>
 *
 * <p>这里按该约定探测贴图文件，命中就用它，缺失（或其它模组不遵循该约定）时回退原版贴图。
 * 不做缓存：资源包切换后存在性可能变化，直接查询 ResourceManager 最稳妥。</p>
 */
public final class MutAnimalArmorTextures {

    /** 解析某个动物铠物品的实体贴图。 */
    public static ResourceLocation resolve(ItemStack armor, AnimalArmorItem item) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);

        String dir = item.getBodyType() == AnimalArmorItem.BodyType.CANINE
                ? "wolf"
                : "horse/armor";

        ResourceLocation candidate = ResourceLocation.fromNamespaceAndPath(
                id.getNamespace(),
                "textures/entity/" + dir + "/" + id.getPath() + ".png"
        );

        return textureExists(candidate) ? candidate : item.getTexture();
    }

    private static boolean textureExists(ResourceLocation texture) {
        try {
            return Minecraft.getInstance().getResourceManager().getResource(texture).isPresent();
        } catch (Exception e) {
            return false;
        }
    }

    private MutAnimalArmorTextures() {
    }
}
