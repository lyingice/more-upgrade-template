package net.mcreator.mut.event;

import net.mcreator.mut.affix.*;
import net.mcreator.mut.affix.data.AffixDataLoader;
import net.mcreator.mut.affix.data.LevelConfig;
import net.mcreator.mut.affix.json.AffixJsonLoader;
import net.mcreator.mut.affix.json.AffixJsonConfig;
import net.mcreator.mut.config.AffixConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = "mut")
public class ClientEventHandler {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        Affix affix = Affix.fromStack(event.getItemStack());
        if (affix == null) return;

        int level = Affix.getLevelFromStack(event.getItemStack());
        ChatFormatting color = affix.getColor(event.getItemStack());

        event.getToolTip().add(Component.literal(""));

        String levelName = getLevelName(level);
        event.getToolTip().add(
                Component.translatable("affix." + affix.getId() + ".name")
                        .append(" " + levelName)
                        .withStyle(color)
        );

        Component dynamicDesc = getDynamicDescription(affix, level);
        event.getToolTip().add(dynamicDesc.copy().withStyle(color));
    }

    private static String getLevelName(int level) {
        LevelConfig lc = AffixDataLoader.getLevel(level);
        if (lc != null) return Component.translatable(lc.getNameKey()).getString();
        return switch (level) {
            case 1 -> "§7I"; case 2 -> "§9II"; case 3 -> "§6III";
            case 4 -> "§dIV"; case 5 -> "§eV"; case 6 -> "§cVI";
            default -> "§f" + level;
        };
    }

    private static MutableComponent getDynamicDescription(Affix affix, int level) {
        AffixJsonConfig cfg = AffixJsonLoader.getConfig(affix.getId());
        String key = "affix." + affix.getId() + ".description";
        if (cfg != null && cfg.getConfigurable() != null && !cfg.getConfigurable().isEmpty()) {
            Object[] args = cfg.getConfigurable().stream()
                    .map(p -> {
                        double raw = AffixConfig.getCoefficient(affix.getId(), p.getName()) * level;
                        if (p.isPercentage()) {
                            int pct = (int) Math.round(raw * 100);
                            return String.valueOf(pct);
                        }
                        return raw == Math.floor(raw) ? String.valueOf((int) raw) : String.format("%.1f", raw);
                    }).toArray();
            return Component.translatable(key, args);
        }
        return Component.translatable(key);
    }
}