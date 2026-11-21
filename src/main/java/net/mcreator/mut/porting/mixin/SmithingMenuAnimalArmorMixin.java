package net.mcreator.mut.porting.mixin;

import net.mcreator.mut.porting.animalarmor.MutAnimalArmorTrim;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 限制：马铠 / 狼铠只能使用本模组白名单里的纹饰模板。
 *
 * <p>配方层已经把原版模板挡在外面（原版 {@code trimmable_armor} 不含动物铠，
 * 本模组动物铠配方使用 {@code mut:animal_armor_trimmable}）。
 * 这里再兜一层：任何落在动物铠上的纹饰结果，如果模板不在
 * {@code mut:allowed_animal_armor_templates} 里，就直接清空结果，
 * 从而挡住其它模组显式写 base 的配方。</p>
 */
@Mixin(SmithingMenu.class)
public abstract class SmithingMenuAnimalArmorMixin {

    @Inject(method = "createResult", at = @At("RETURN"))
    private void mut$restrictAnimalArmorTrimTemplates(CallbackInfo callbackInfo) {
        ItemCombinerMenuAccessor accessor = (ItemCombinerMenuAccessor) this;

        Container inputSlots = accessor.mut$getInputSlots();
        ItemStack base = inputSlots.getItem(SmithingMenu.BASE_SLOT);

        // 只限制本模组登记的动物铠，其它物品一切照旧
        if (!base.is(MutAnimalArmorTrim.TRIMMABLE_ANIMAL_ARMOR)) {
            return;
        }

        ItemStack template = inputSlots.getItem(SmithingMenu.TEMPLATE_SLOT);
        if (template.is(MutAnimalArmorTrim.ALLOWED_TEMPLATES)) {
            return;
        }

        ResultContainer resultSlots = accessor.mut$getResultSlots();
        ItemStack result = resultSlots.getItem(0);
        if (result.isEmpty()) {
            return;
        }

        // 只拦"纹饰结果"，不影响动物铠上的其它锻造配方
        if (result.get(DataComponents.TRIM) == null) {
            return;
        }

        resultSlots.setItem(0, ItemStack.EMPTY);
    }
}
