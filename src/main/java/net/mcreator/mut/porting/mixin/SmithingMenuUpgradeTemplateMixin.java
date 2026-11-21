package net.mcreator.mut.porting.mixin;

import net.mcreator.mut.porting.smithing.NetheriteUpgradeSmithingTemplate;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 下界合金（升级）锻造模板不消耗。
 *
 * <p>移植自 TCR 的 {@code SmithingMenuMixin#tcr$keepUpgradeTemplate}：
 * 原版在取出锻造结果时会把模板槽减 1，这里对
 * {@link NetheriteUpgradeSmithingTemplate} 直接取消该次 shrink，
 * 模板因此可以反复使用。</p>
 */
@Mixin(SmithingMenu.class)
public abstract class SmithingMenuUpgradeTemplateMixin {

    @Inject(
            method = "shrinkStackInSlot",
            at = @At("HEAD"),
            cancellable = true
    )
    private void mut$keepNetheriteUpgradeTemplate(
            int slot,
            CallbackInfo callbackInfo
    ) {
        if (slot != SmithingMenu.TEMPLATE_SLOT) {
            return;
        }

        Container inputSlots = ((ItemCombinerMenuAccessor) this).mut$getInputSlots();
        ItemStack template = inputSlots.getItem(slot);

        if (template.getItem() instanceof NetheriteUpgradeSmithingTemplate) {
            callbackInfo.cancel();
        }
    }
}
