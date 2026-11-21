package net.mcreator.mut.porting.mixin;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.ResultContainer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** 读取锻造台菜单的输入槽与结果槽（移植自 TCR 的同类访问器）。 */
@Mixin(ItemCombinerMenu.class)
public interface ItemCombinerMenuAccessor {
    @Accessor("inputSlots")
    Container mut$getInputSlots();

    @Accessor("resultSlots")
    ResultContainer mut$getResultSlots();
}
