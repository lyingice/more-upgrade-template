package net.mcreator.mut.porting.ruingolem.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.IronGolemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.IronGolem;

/**
 * 通用遗迹傀儡渲染器：模型沿用原版铁傀儡，只换一张贴图。
 * 贴图路径形如 mut:textures/entity/ruin_golem/xxx.png。
 */
public class RuinGolemRenderer extends IronGolemRenderer {

    private final ResourceLocation texture;

    public RuinGolemRenderer(EntityRendererProvider.Context context, ResourceLocation texture) {
        super(context);
        this.texture = texture;
    }

    @Override
    public ResourceLocation getTextureLocation(IronGolem entity) {
        return texture;
    }
}
