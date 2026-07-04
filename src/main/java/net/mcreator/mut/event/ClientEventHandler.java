package net.mcreator.mut.event;

import net.mcreator.mut.affix.*;
import net.mcreator.mut.affix.data.AffixDataLoader;
import net.mcreator.mut.affix.data.LevelConfig;
import net.mcreator.mut.affix.impl.*;
import net.mcreator.mut.config.AffixConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
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

        Player player = event.getEntity();
        int level = Affix.getLevelFromStack(event.getItemStack());
        ChatFormatting color = affix.getColor(event.getItemStack());

        event.getToolTip().add(Component.literal(""));

        // 词缀名称 + 等级名称（从 JSON 数据包读取）
        String levelName = getLevelName(level);
        event.getToolTip().add(
                Component.translatable("affix." + affix.getId() + ".name")
                        .append(" " + levelName)
                        .withStyle(color)
        );

        // 动态描述
        Component dynamicDesc = getDynamicDescription(affix, level, player);
        event.getToolTip().add(dynamicDesc.copy().withStyle(color));
    }

    /**
     * 从 JSON 数据包读取等级名称（如果未加载则回退到旧格式）
     */
    private static String getLevelName(int level) {
        LevelConfig lc = AffixDataLoader.getLevel(level);
        if (lc != null) {
            // 使用 JSON 中定义的本地化键
            return net.minecraft.network.chat.Component.translatable(lc.getNameKey()).getString();
        }
        // 回退到旧格式
        return switch (level) {
            case 1 -> "§7I";
            case 2 -> "§9II";
            case 3 -> "§6III";
            case 4 -> "§dIV";
            case 5 -> "§eV";
            case 6 -> "§cVI";
            default -> "§f" + level;
        };
    }

    private static MutableComponent getDynamicDescription(Affix affix, int level, Player player) {
        String key = "affix." + affix.getId() + ".description";

        if (affix instanceof PoisonMarkAffix) {
            float bonus = level * (float) AffixConfig.getCoefficient("poison_mark");
            return Component.translatable(key, String.format("%.1f", bonus));
        }
        if (affix instanceof FireMarkAffix) {
            float bonus = level * (float) AffixConfig.getCoefficient("fire_mark");
            return Component.translatable(key, String.format("%.1f", bonus));
        }
        if (affix instanceof WitherMarkAffix) {
            float bonus = level * (float) AffixConfig.getCoefficient("wither_mark");
            return Component.translatable(key, String.format("%.1f", bonus));
        }
        if (affix instanceof RegenerationMarkAffix) {
            float bonus = level * (float) AffixConfig.getCoefficient("regeneration_mark");
            return Component.translatable(key, String.format("%.1f", bonus));
        }
        if (affix instanceof StrengthBlessingAffix) {
            int percent = (int)(level * (float) AffixConfig.getCoefficient("strength_blessing") * 100);
            return Component.translatable(key, percent);
        }
        if (affix instanceof SharpshooterAffix) {
            int percent = (int)(level * (float) AffixConfig.getCoefficient("sharpshooter") * 100);
            return Component.translatable(key, percent);
        }
        if (affix instanceof PiercingSpearAffix) {
            int stab = (int)(level * (float) AffixConfig.getCoefficient("piercing_spear_stab") * 100);
            int charge = (int)(level * (float) AffixConfig.getCoefficient("piercing_spear_charge") * 100);
            return Component.translatable(key, stab, charge);
        }
        if (affix instanceof MomentumAffix) {
            int percent = (int)(level * (float) AffixConfig.getCoefficient("momentum") * 100);
            return Component.translatable(key, percent);
        }
        if (affix instanceof TidalSurgeAffix) {
            int water = (int)(level * (float) AffixConfig.getCoefficient("tidal_surge_water") * 100);
            int rain = (int)(level * (float) AffixConfig.getCoefficient("tidal_surge_rain") * 100);
            int oxygen = level * 3;
            return Component.translatable(key, water, rain, oxygen);
        }
        if (affix instanceof BigStomachAffix) {
            float bonus = level * (float) AffixConfig.getCoefficient("big_stomach");
            return Component.translatable(key, String.format("%.1f", bonus));
        }
        if (affix instanceof EnergyConversionAffix) {
            int cap = Math.round(level * (float) AffixConfig.getCoefficient("energy_conversion"));
            return Component.translatable(key, cap);
        }

        return Component.translatable(key);
    }
}
