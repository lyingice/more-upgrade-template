package net.mcreator.mut.porting.ruingolem.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * 岩石傀儡渲染器。
 * <p>
 * 伪装状态不做任何特殊处理 —— 它就是站着不动的自己，
 * 和 {@link RockGolemBlockRenderer} 画出来的石像完全一致，玩家靠眼睛分不出来。
 */
public class RockRuinGolemRenderer extends RuinGolemRenderer {

    public RockRuinGolemRenderer(EntityRendererProvider.Context context, ResourceLocation texture) {
        super(context, texture);
    }
}
