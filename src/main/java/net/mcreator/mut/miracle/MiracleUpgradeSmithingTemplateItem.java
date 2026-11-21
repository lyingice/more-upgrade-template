package net.mcreator.mut.miracle;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

/**
 * 奇迹升级锻造模板。
 * <p>
 * 在锻造台上把"任意对应部位的装备"（哪怕已经是奇迹装备本身）升级成奇迹装备，
 * 消耗一块奇迹之锭。工具提示沿用本模组其它模板的六行格式。
 */
public class MiracleUpgradeSmithingTemplateItem extends Item {

    public MiracleUpgradeSmithingTemplateItem() {
        super(new Item.Properties().fireResistant().rarity(Rarity.RARE));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        for (int i = 0; i <= 5; i++) {
            tooltip.add(Component.translatable("item.mut.miracle_upgrade_smithing_template.description_" + i));
        }
    }
}
