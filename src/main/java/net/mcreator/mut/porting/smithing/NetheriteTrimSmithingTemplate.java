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
 * 下界合金纹饰锻造模板（从 TemplateCraftRevolution 移植）。
 *
 * <p>只允许在下界合金盔甲上追加下界合金纹饰，纹饰材质取自
 * {@code mut:netherite_trim_materials} 标签。</p>
 */
public class NetheriteTrimSmithingTemplate extends SmithingTemplateItem {
    private static final Component APPLIES_TO =
            Component.translatable(
                    Util.makeDescriptionId(
                            "item",
                            ResourceLocation.withDefaultNamespace(
                                    "smithing_template.armor_trim.applies_to"
                            )
                    )
            ).withStyle(ChatFormatting.BLUE);

    private static final Component INGREDIENTS =
            Component.translatable(
                    Util.makeDescriptionId(
                            "item",
                            ResourceLocation.withDefaultNamespace(
                                    "smithing_template.armor_trim.ingredients"
                            )
                    )
            ).withStyle(ChatFormatting.BLUE);

    private static final Component DESCRIPTION =
            Component.translatable(
                    "upgrade.mut.netherite_trim"
            ).withStyle(ChatFormatting.GRAY);

    private static final Component BASE_SLOT_DESCRIPTION =
            Component.translatable(
                    Util.makeDescriptionId(
                            "item",
                            ResourceLocation.withDefaultNamespace(
                                    "smithing_template.armor_trim.base_slot_description"
                            )
                    )
            );

    private static final Component ADDITIONS_SLOT_DESCRIPTION =
            Component.translatable(
                    Util.makeDescriptionId(
                            "item",
                            ResourceLocation.withDefaultNamespace(
                                    "smithing_template.armor_trim.additions_slot_description"
                            )
                    )
            );

    private static final List<ResourceLocation> BASE_SLOT_EMPTY_ICONS =
            List.of(
                    ResourceLocation.withDefaultNamespace("item/empty_armor_slot_helmet"),
                    ResourceLocation.withDefaultNamespace("item/empty_armor_slot_chestplate"),
                    ResourceLocation.withDefaultNamespace("item/empty_armor_slot_leggings"),
                    ResourceLocation.withDefaultNamespace("item/empty_armor_slot_boots")
            );

    private static final List<ResourceLocation> ADDITIONAL_SLOT_EMPTY_ICONS =
            List.of(
                    ResourceLocation.withDefaultNamespace("item/empty_slot_ingot")
            );

    public NetheriteTrimSmithingTemplate(Item.Properties properties) {
        super(
                APPLIES_TO,
                INGREDIENTS,
                DESCRIPTION,
                BASE_SLOT_DESCRIPTION,
                ADDITIONS_SLOT_DESCRIPTION,
                BASE_SLOT_EMPTY_ICONS,
                ADDITIONAL_SLOT_EMPTY_ICONS
        );
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 64;
    }
}
