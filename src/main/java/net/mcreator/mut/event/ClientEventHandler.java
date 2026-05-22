package net.mcreator.mut.event;

import net.mcreator.mut.affix.Affix;
import net.mcreator.mut.affix.impl.*;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = "mut")
public class ClientEventHandler {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        Affix affix = Affix.fromStack(event.getItemStack());

        if (affix != null) {
            event.getToolTip().add(Component.literal(""));

            // 词条名称（金色默认）
            event.getToolTip().add(
                    Component.translatable("affix." + affix.getId() + ".name")
                            .withStyle(affix.getNameColor())
            );

            // 词条描述（所有词条通用，颜色根据词条类型切换）
            ChatFormatting color = ChatFormatting.GRAY;
            if (affix instanceof PoisonMarkAffix) color = ChatFormatting.GOLD;
            if (affix instanceof FireMarkAffix) color = ChatFormatting.GOLD;
            if (affix instanceof WitherMarkAffix) color = ChatFormatting.GOLD;
            if (affix instanceof MomentumAffix) color = ChatFormatting.GOLD;
            if (affix instanceof RegenerationMarkAffix) color = ChatFormatting.GOLD;
            if (affix instanceof NirvanaAffix) color = ChatFormatting.RED;

            event.getToolTip().add(
                    Component.translatable("affix." + affix.getId() + ".description")
                            .withStyle(color)
            );
        }
    }
}