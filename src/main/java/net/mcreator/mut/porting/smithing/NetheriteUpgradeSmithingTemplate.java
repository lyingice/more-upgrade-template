package net.mcreator.mut.porting.smithing;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SmithingTemplateItem;

/**
 * 下界合金锻造模板（升级模板，从 TemplateCraftRevolution 移植）。
 *
 * <p>与原版 {@code minecraft:netherite_upgrade_smithing_template} 功能相同，
 * 但只能单个堆叠。配合 data/mut/recipe 中的覆盖配方，可在锻造台替代原版模板。</p>
 */
public class NetheriteUpgradeSmithingTemplate extends SmithingTemplateItem {
    private static final Component NETHERITE_UPGRADE_APPLIES_TO = Component.translatable(
        Util.makeDescriptionId("item", ResourceLocation.withDefaultNamespace("smithing_template.netherite_upgrade.applies_to"))
    ).withStyle(ChatFormatting.BLUE);
    private static final Component NETHERITE_UPGRADE_INGREDIENTS = Component.translatable(
        Util.makeDescriptionId("item", ResourceLocation.withDefaultNamespace("smithing_template.netherite_upgrade.ingredients"))
    ).withStyle(ChatFormatting.BLUE);
    private static final Component NETHERITE_UPGRADE = Component.translatable(
        Util.makeDescriptionId("upgrade", ResourceLocation.withDefaultNamespace("netherite_upgrade"))
    ).withStyle(ChatFormatting.GRAY);
    private static final Component BASE_SLOT_DESCRIPTION = Component.translatable(
        Util.makeDescriptionId("item", ResourceLocation.withDefaultNamespace("smithing_template.netherite_upgrade.base_slot_description"))
    );
    private static final Component ADDITIONS_SLOT_DESCRIPTION = Component.translatable(
        Util.makeDescriptionId("item", ResourceLocation.withDefaultNamespace("smithing_template.netherite_upgrade.additions_slot_description"))
    );

    private static final List<ResourceLocation> BASE_SLOT_EMPTY_ICONS = List.of(
        ResourceLocation.withDefaultNamespace("item/empty_armor_slot_helmet"),
        ResourceLocation.withDefaultNamespace("item/empty_slot_sword"),
        ResourceLocation.withDefaultNamespace("item/empty_armor_slot_chestplate"),
        ResourceLocation.withDefaultNamespace("item/empty_slot_pickaxe"),
        ResourceLocation.withDefaultNamespace("item/empty_armor_slot_leggings"),
        ResourceLocation.withDefaultNamespace("item/empty_slot_axe"),
        ResourceLocation.withDefaultNamespace("item/empty_armor_slot_boots"),
        ResourceLocation.withDefaultNamespace("item/empty_slot_hoe"),
        ResourceLocation.withDefaultNamespace("item/empty_slot_shovel")
    );
    private static final List<ResourceLocation> ADDITIONAL_SLOT_EMPTY_ICONS = List.of(
        ResourceLocation.withDefaultNamespace("item/empty_slot_ingot")
    );

    public NetheriteUpgradeSmithingTemplate(Properties properties) {
        super(
            NETHERITE_UPGRADE_APPLIES_TO,
            NETHERITE_UPGRADE_INGREDIENTS,
            NETHERITE_UPGRADE,
            BASE_SLOT_DESCRIPTION,
            ADDITIONS_SLOT_DESCRIPTION,
            BASE_SLOT_EMPTY_ICONS,
            ADDITIONAL_SLOT_EMPTY_ICONS
        );
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }
}
