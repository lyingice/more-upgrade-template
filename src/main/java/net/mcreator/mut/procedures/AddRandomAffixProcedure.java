package net.mcreator.mut.procedures;

import net.mcreator.mut.affix.Affix;
import net.mcreator.mut.affix.AffixRoller;
import net.mcreator.mut.affix.data.MaterialContext;
import net.mcreator.mut.affix.data.MaterialBonusRegistry;
import net.mcreator.mut.affix.data.RollResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;

import net.mcreator.mut.init.MutModMenus;

/**
 * 随机词缀赋予流程 - 重构版
 *
 * 现在由 AffixRoller 处理核心随机逻辑，
 * 此 Procedure 作为外观层简化调用。
 *
 * 两个重载版本：
 * - execute(Entity)      → 从超级锻造台 GUI 槽位 3 获取物品
 * - execute(ItemStack)   → 直接给 ItemStack 加词缀（无材料加成）
 * - execute(ItemStack, MaterialContext) → 带材料加成的词缀赋予
 */
public class AddRandomAffixProcedure {

    /**
     * 从容器槽位3获取物品并添加随机词缀
     */
    public static void execute(Entity entity) {
        if (entity == null) return;
        if (!(entity instanceof Player _player && _player.containerMenu instanceof MutModMenus.MenuAccessor _menu)) return;

        ItemStack stack = _menu.getSlots().get(3).getItem();
        if (stack.isEmpty()) return;

        // 尝试从 GUI 中获取材料上下文（槽位2）
        ItemStack addition = _menu.getSlots().get(2).getItem();
        MaterialContext context = MaterialBonusRegistry.getInstance().evaluate(addition);

        execute(stack, context);
    }

    /**
     * 直接给 ItemStack 添加随机词缀（无材料加成）
     */
    public static void execute(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return;
        execute(stack, MaterialContext.empty());
    }

    /**
     * 带材料上下文的词缀赋予（核心方法）
     */
    public static void execute(ItemStack stack, MaterialContext context) {
        if (stack == null || stack.isEmpty()) return;
        if (context == null) context = MaterialContext.empty();

        // 使用 AffixRoller 核心引擎进行随机
        RollResult result = AffixRoller.roll(stack, context);

        // 应用结果到物品
        result.applyToStack(stack);
    }
}
