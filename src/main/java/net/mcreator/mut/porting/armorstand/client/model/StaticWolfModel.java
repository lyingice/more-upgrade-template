package net.mcreator.mut.porting.armorstand.client.model;

import net.minecraft.client.model.WolfModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.animal.Wolf;

/**
 * 静态狼模型：
 *
 * <ul>
 *   <li>原版 WolfModel 的尾巴摆动由 ageInTicks 驱动（会永远摇），这里强制
 *       ageInTicks=0 使尾巴静止；</li>
 *   <li>尾巴附加一个默认固定旋转（TAIL_FIXED_ROTATION，默认 50°），
 *       在 prepareMobModel 与 setupAnim 两个覆写中都生效，
 *       因为 LivingEntityRenderer 与铠层会先后调用这两者。</li>
 * </ul>
 */
public class StaticWolfModel<T extends Wolf> extends WolfModel<T> {

    /** 尾巴固定旋转角（弧度）。50°；想换角度改这里，想换方向改轴（xRot/yRot）。 */
    public static final float TAIL_FIXED_ROTATION = (float) Math.toRadians(50.0D);

    /** WolfModel.tail 是 private，从根部件自取一份引用。 */
    private final ModelPart tailPart;

    public StaticWolfModel(ModelPart root) {
        super(root);
        this.tailPart = root.getChild("tail");
    }

    @Override
    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
        this.tailPart.xRot += TAIL_FIXED_ROTATION;
    }

    @Override
    public void setupAnim(
            T entity,
            float limbSwing,
            float limbSwingAmount,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, 0.0F, netHeadYaw, headPitch);
        this.tailPart.xRot += TAIL_FIXED_ROTATION;
    }
}