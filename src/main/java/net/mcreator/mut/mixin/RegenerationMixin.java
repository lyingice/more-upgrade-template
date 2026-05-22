package net.mcreator.mut.mixin;

import net.mcreator.mut.affix.Affix;
import net.mcreator.mut.affix.impl.RegenerationMarkAffix;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public class RegenerationMixin {

    /**
     * 拦截 heal 方法，根据自身装备的再生印记数量增加恢复量
     */
    @ModifyVariable(
            method = "heal",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private float modifyHealAmount(float amount) {
        LivingEntity entity = (LivingEntity) (Object) this;

        int count = countRegenerationMarks(entity);
        if (count > 0) {
            return amount + count; // 每件装备+1恢复量
        }

        return amount;
    }

    /**
     * 统计实体身上带再生印记词缀的装备数量（最多6件）
     */
    private static int countRegenerationMarks(LivingEntity entity) {
        int count = 0;
        if (hasRegenerationMark(entity.getMainHandItem())) count++;
        if (hasRegenerationMark(entity.getOffhandItem())) count++;
        if (hasRegenerationMark(entity.getItemBySlot(EquipmentSlot.HEAD))) count++;
        if (hasRegenerationMark(entity.getItemBySlot(EquipmentSlot.CHEST))) count++;
        if (hasRegenerationMark(entity.getItemBySlot(EquipmentSlot.LEGS))) count++;
        if (hasRegenerationMark(entity.getItemBySlot(EquipmentSlot.FEET))) count++;
        return Math.min(count, 6);
    }

    private static boolean hasRegenerationMark(ItemStack stack) {
        if (stack.isEmpty()) return false;
        Affix affix = Affix.fromStack(stack);
        return affix instanceof RegenerationMarkAffix;
    }
}