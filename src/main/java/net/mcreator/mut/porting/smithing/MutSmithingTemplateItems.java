package net.mcreator.mut.porting.smithing;

import net.mcreator.mut.MutMod;

import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 移植自 TemplateCraftRevolution 的锻造模板物品注册。
 *
 * <p>刻意独立于 MCreator 自动生成的 {@code net.mcreator.mut.init.MutModItems}，
 * 这样 MCreator 重新生成代码时不会丢失移植内容。注册入口在
 * {@code MutMod} 的 user code block 中调用。</p>
 */
public final class MutSmithingTemplateItems {
    public static final DeferredRegister.Items REGISTRY = DeferredRegister.createItems(MutMod.MODID);

    public static final DeferredItem<NetheriteUpgradeSmithingTemplate> NETHERITE_UPGRADE_SMITHING_TEMPLATE =
            REGISTRY.registerItem("netherite_upgrade_smithing_template", NetheriteUpgradeSmithingTemplate::new);

    public static final DeferredItem<NetheriteTrimSmithingTemplate> NETHERITE_TRIM_SMITHING_TEMPLATE =
            REGISTRY.registerItem("netherite_trim_smithing_template", NetheriteTrimSmithingTemplate::new);

    public static final DeferredItem<ArrowheadArmorTrimSmithingTemplate> ARROWHEAD_ARMOR_TRIM_SMITHING_TEMPLATE =
            REGISTRY.registerItem("arrowhead_armor_trim_smithing_template", ArrowheadArmorTrimSmithingTemplate::new);

    private MutSmithingTemplateItems() {
    }
}
