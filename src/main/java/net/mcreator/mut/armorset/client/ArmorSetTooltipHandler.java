package net.mcreator.mut.armorset.client;

import net.mcreator.mut.armorset.ArmorSet;
import net.mcreator.mut.armorset.ArmorSetRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = "mut")
public class ArmorSetTooltipHandler {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        for (ArmorSet set : ArmorSetRegistry.getSetsFor(event.getItemStack())) {
            event.getToolTip().add(Component.literal(""));
            MutableComponent name = Component.empty()
                    .append(Component.translatable("tooltip.mut.set").withStyle(ChatFormatting.BLUE))
                    .append(Component.literal(" "))
                    .append(Component.translatable(set.getNameKey()).withStyle(ChatFormatting.AQUA));
            event.getToolTip().add(name);
            for (ArmorSet.Bonus bonus : set.getBonuses()) {
                if (bonus.descKey() == null || bonus.descKey().isEmpty()) continue;
                event.getToolTip().add(Component.translatable(bonus.descKey()).withStyle(ChatFormatting.GRAY));
            }
        }
    }
}
