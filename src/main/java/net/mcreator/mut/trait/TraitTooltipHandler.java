package net.mcreator.mut.trait;

import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = "mut")
public class TraitTooltipHandler {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        for (Trait trait : TraitRegistry.getTraitsFor(stack)) {
            event.getToolTip().add(Component.literal(""));
            MutableComponent combined = Component.empty()
                    .append(Component.translatable("tooltip.mut.trait").withStyle(ChatFormatting.BLUE))
                    .append(Component.literal(" "))
                    .append(Component.translatable(trait.getNameKey())
                            .withStyle(ChatFormatting.AQUA));
            event.getToolTip().add(combined);
            for (String descKey : trait.getDescriptionKeys()) {
                event.getToolTip().add(
                        Component.translatable(descKey)
                                .withStyle(ChatFormatting.AQUA)
                );
            }
        }
    }
}