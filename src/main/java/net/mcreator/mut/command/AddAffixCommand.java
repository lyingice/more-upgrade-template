package net.mcreator.mut.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.mcreator.mut.affix.Affix;
import net.mcreator.mut.affix.AffixRegistry;
import net.mcreator.mut.affix.AffixRoller;
import net.mcreator.mut.affix.data.*;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

/**
 * 词缀管理命令 - 与新数据驱动系统保持一致
 *
 * 子命令:
 *   /affix add <id> [level]       — 直接添加词缀（带等级校验）
 *   /affix view                   — 查看手持物品的词缀
 *   /affix remove                 — 移除词缀
 *   /affix roll [material]        — 模拟使用指定材料进行词缀随机
 *   /affix pity get               — 查看手持物品的软保底值
 *   /affix pity set <value>       — 设置软保底值
 *   /affix pity reset             — 重置软保底值
 *   /affix reload                 — 重载词缀配置（服务端管理员）
 *   /affix debug                  — 显示当前配置的 Debug 信息
 */
public class AddAffixCommand {

    private static final SuggestionProvider<CommandSourceStack> AFFIX_SUGGESTIONS =
            (context, builder) -> {
                for (Affix affix : AffixRegistry.getAll()) {
                    builder.suggest(affix.getId());
                }
                return builder.buildFuture();
            };

    private static final SuggestionProvider<CommandSourceStack> MATERIAL_SUGGESTIONS =
            (context, builder) -> {
                var config = AffixDataLoader.getMaterialBonusConfig();
                if (config != null) {
                    if (config.getDirectedMaterials() != null) {
                        for (var dm : config.getDirectedMaterials()) {
                            if (dm.getItem() != null) {
                                String itemId = dm.getItem();
                                String simpleName = itemId.contains(":") ? itemId.split(":")[1] : itemId;
                                builder.suggest(itemId, Component.literal(simpleName));
                            }
                        }
                    }
                    if (config.getUniversalMaterials() != null) {
                        for (var um : config.getUniversalMaterials()) {
                            if (um.getItem() != null) {
                                builder.suggest(um.getItem());
                            }
                        }
                    }
                }
                // 常用材料快捷提示
                builder.suggest("nether_star", Component.literal("下界之星 - 通用材料"));
                builder.suggest("blaze_rod", Component.literal("烈焰棒 - 灼烧印记定向"));
                builder.suggest("poisonous_potato", Component.literal("毒马铃薯 - 剧毒印记定向"));
                return builder.buildFuture();
            };

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("affix")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("add")
                                .then(Commands.argument("affix_id", StringArgumentType.word())
                                        .suggests(AFFIX_SUGGESTIONS)
                                        .then(Commands.argument("level", IntegerArgumentType.integer(1, 99))
                                                .executes(ctx -> addAffix(
                                                        ctx.getSource(),
                                                        StringArgumentType.getString(ctx, "affix_id"),
                                                        IntegerArgumentType.getInteger(ctx, "level")
                                                ))
                                        )
                                        .executes(ctx -> addAffix(
                                                ctx.getSource(),
                                                StringArgumentType.getString(ctx, "affix_id"),
                                                1
                                        ))
                                )
                        )
                        .then(Commands.literal("view")
                                .requires(source -> source.hasPermission(0))
                                .executes(ctx -> viewAffix(ctx.getSource()))
                        )
                        .then(Commands.literal("remove")
                                .executes(ctx -> removeAffix(ctx.getSource()))
                        )
                        .then(Commands.literal("roll")
                                .then(Commands.argument("material", StringArgumentType.word())
                                        .suggests(MATERIAL_SUGGESTIONS)
                                        .executes(ctx -> rollAffix(
                                                ctx.getSource(),
                                                StringArgumentType.getString(ctx, "material")
                                        ))
                                )
                                .executes(ctx -> rollAffix(ctx.getSource(), null))
                        )
                        .then(Commands.literal("pity")
                                .then(Commands.literal("get")
                                        .executes(ctx -> getPity(ctx.getSource()))
                                )
                                .then(Commands.literal("set")
                                        .then(Commands.argument("value", IntegerArgumentType.integer(0, 99))
                                                .executes(ctx -> setPity(
                                                        ctx.getSource(),
                                                        IntegerArgumentType.getInteger(ctx, "value")
                                                ))
                                        )
                                )
                                .then(Commands.literal("reset")
                                        .executes(ctx -> resetPity(ctx.getSource()))
                                )
                        )
                        .then(Commands.literal("reload")
                                .requires(source -> source.hasPermission(2))
                                .executes(ctx -> reloadConfig(ctx.getSource()))
                        )
                        .then(Commands.literal("debug")
                                .requires(source -> source.hasPermission(0))
                                .executes(ctx -> debugInfo(ctx.getSource()))
                        )
        );
    }

    // ========== add: 直接添加词缀 ==========

    private static int addAffix(CommandSourceStack source, String affixId, int level) {
        Player player = getPlayerOrFail(source);
        if (player == null) return 0;

        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.isEmpty()) {
            source.sendFailure(Component.literal("§c你主手里没有物品"));
            return 0;
        }

        Affix affix = AffixRegistry.get(affixId);
        if (affix == null) {
            source.sendFailure(Component.literal("§c未知词缀: " + affixId));
            source.sendFailure(Component.literal("§7可用词缀: " + String.join(", ",
                    AffixRegistry.getAll().stream().map(Affix::getId).toList())));
            return 0;
        }

        if (Affix.fromStack(heldItem) != null) {
            source.sendFailure(Component.literal("§c此物品已有词缀，请先使用 /affix remove"));
            return 0;
        }

        // 从 JSON 配置读取最大等级，不再硬编码 6
        int jsonMaxLevel = AffixDataLoader.getMaxLevel();
        level = Math.min(level, Math.min(affix.getMaxLevel(), jsonMaxLevel));

        affix.applyToStack(heldItem, level);

        int finalLevel = level;
        source.sendSuccess(
                () -> Component.literal("§a✔ 已添加词缀: §e" + affix.getId() + " §a等级: §e" + finalLevel + " §7(上限: " + jsonMaxLevel + ")"),
                true
        );
        return 1;
    }

    // ========== view: 查看词缀 ==========

    private static int viewAffix(CommandSourceStack source) {
        Player player = getPlayerOrFail(source);
        if (player == null) return 0;

        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.isEmpty()) {
            source.sendFailure(Component.literal("§c你主手里没有物品"));
            return 0;
        }

        Affix affix = Affix.fromStack(heldItem);
        if (affix == null) {
            source.sendSuccess(() -> Component.literal("§7此物品没有任何词缀"), false);
        } else {
            int level = Affix.getLevelFromStack(heldItem);
            String customRarity = Affix.getCustomRarityFromStack(heldItem);
            int pity = PityTracker.getPity(heldItem);

            source.sendSuccess(() -> Component.literal("§a词缀: §e" + affix.getId()
                    + " §a等级: §e" + level + " §7/ " + affix.getMaxLevel()
                    + (customRarity != null ? " §5稀有度: " + customRarity : "")
            ), false);
            if (pity > 0) {
                source.sendSuccess(() -> Component.literal("§e软保底: " + pity + " 次 §7(权重+" + (pity * 12) + "%)"), false);
            }
        }
        return 1;
    }

    // ========== remove: 移除词缀 ==========

    private static int removeAffix(CommandSourceStack source) {
        Player player = getPlayerOrFail(source);
        if (player == null) return 0;

        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.isEmpty()) {
            source.sendFailure(Component.literal("§c你主手里没有物品"));
            return 0;
        }

        Affix affix = Affix.fromStack(heldItem);
        if (affix == null) {
            source.sendFailure(Component.literal("§c此物品没有词缀"));
            return 0;
        }

        heldItem.remove(net.minecraft.core.component.DataComponents.CUSTOM_DATA);

        source.sendSuccess(
                () -> Component.literal("§a✔ 已移除词缀: §e" + affix.getId()),
                true
        );
        return 1;
    }

    // ========== roll: 模拟词缀随机 ==========

    private static int rollAffix(CommandSourceStack source, String materialId) {
        Player player = getPlayerOrFail(source);
        if (player == null) return 0;

        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.isEmpty()) {
            source.sendFailure(Component.literal("§c你主手里没有物品"));
            return 0;
        }

        // 如果指定了材料，尝试构建 MaterialContext
        // 由于命令只能传字符串ID，简化处理：使用空上下文
        MaterialContext ctx = MaterialContext.empty();
        if (materialId != null && !materialId.isEmpty()) {
            source.sendSuccess(() -> Component.literal("§7指定材料: §e" + materialId + " §7(需完整物品ID)"), false);
        }

        // 使用新引擎随机
        RollResult result = AffixRoller.roll(heldItem, ctx);

        if (result.getAffix() != null) {
            source.sendSuccess(() -> Component.literal("§a✔ 随机结果: §e" + result.getAffix().getId()
                    + " §a等级: §e" + result.getLevel()
                    + " §7(原roll: " + result.getOriginalLevel() + ")"
            ), false);

            // Debug 信息
            if (!result.getDebugInfo().isEmpty()) {
                source.sendSuccess(() -> Component.literal("§7Debug: " + String.join(" | ", result.getDebugInfo())), false);
            }
        } else {
            source.sendSuccess(() -> Component.literal("§7随机失败，未获得词缀"), false);
        }
        return 1;
    }

    // ========== pity: 软保底管理 ==========

    private static int getPity(CommandSourceStack source) {
        Player player = getPlayerOrFail(source);
        if (player == null) return 0;

        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.isEmpty()) {
            source.sendFailure(Component.literal("§c你主手里没有物品"));
            return 0;
        }

        int pity = PityTracker.getPity(heldItem);
        source.sendSuccess(() -> Component.literal("§e软保底值: " + pity + " §7(权重+" + (pity * 12) + "%)"), false);
        return 1;
    }

    private static int setPity(CommandSourceStack source, int value) {
        Player player = getPlayerOrFail(source);
        if (player == null) return 0;

        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.isEmpty()) {
            source.sendFailure(Component.literal("§c你主手里没有物品"));
            return 0;
        }

        // 使用 NBT 直接写入 pity
        var customData = heldItem.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
        var tag = (customData != null) ? customData.copyTag() : new net.minecraft.nbt.CompoundTag();
        tag.putInt("AffixPity", Math.max(0, value));
        heldItem.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(tag));

        source.sendSuccess(() -> Component.literal("§a✔ 已设置软保底值: " + value), true);
        return 1;
    }

    private static int resetPity(CommandSourceStack source) {
        Player player = getPlayerOrFail(source);
        if (player == null) return 0;

        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.isEmpty()) {
            source.sendFailure(Component.literal("§c你主手里没有物品"));
            return 0;
        }

        PityTracker.resetPity(heldItem);
        source.sendSuccess(() -> Component.literal("§a✔ 已重置软保底值"), true);
        return 1;
    }

    // ========== reload: 重载配置 ==========

    private static int reloadConfig(CommandSourceStack source) {
        // 通知用户使用方法
        source.sendSuccess(() -> Component.literal("§e请使用 /reload 重载数据包配置"), true);
        source.sendSuccess(() -> Component.literal("§7词缀配置目录: data/<命名空间>/affix/"), false);
        source.sendSuccess(() -> Component.literal("§7当前等级: " + AffixDataLoader.getLevels().length + " 级"
                + " | 词缀: " + AffixRegistry.getAll().size() + " 个"), false);

        // 显示每种材料的配置数量
        var matConfig = AffixDataLoader.getMaterialBonusConfig();
        if (matConfig != null) {
            source.sendSuccess(() -> Component.literal("§7通用材料: " + (matConfig.getUniversalMaterials() != null ?
                    matConfig.getUniversalMaterials().size() : 0) + " 种"), false);
            source.sendSuccess(() -> Component.literal("§7定向材料: " + (matConfig.getDirectedMaterials() != null ?
                    matConfig.getDirectedMaterials().size() : 0) + " 种"), false);
            source.sendSuccess(() -> Component.literal("§7标签驱动材料: " + (matConfig.getTagDrivenMaterials() != null ?
                    matConfig.getTagDrivenMaterials().size() : 0) + " 组"), false);
        }

        // 显示软保底状态
        var pityConfig = AffixDataLoader.getPityConfig();
        if (pityConfig != null) {
            source.sendSuccess(() -> Component.literal("§7软保底: "
                    + (pityConfig.isEnabled() ? "§a启用" : "§c禁用")
                    + " | cap=" + pityConfig.getGlobal().getPityCap()
                    + " | perPoint=" + String.format("%.0f%%", pityConfig.getGlobal().getPityBonusPerPoint() * 100)
            ), false);
        }

        return 1;
    }

    // ========== debug: Debug 信息 ==========

    private static int debugInfo(CommandSourceStack source) {
        Player player = getPlayerOrFail(source);
        if (player == null) return 0;

        ItemStack heldItem = player.getMainHandItem();

        source.sendSuccess(() -> Component.literal("§6§l=== 词缀系统 Debug 信息 ==="), false);
        source.sendSuccess(() -> Component.literal("§7配置等级数: " + AffixDataLoader.getLevels().length), false);
        source.sendSuccess(() -> Component.literal("§7已注册词缀: " + AffixRegistry.getAll().size()), false);

        // 列出所有可用词缀
        source.sendSuccess(() -> Component.literal("§6词缀列表:"), false);
        for (Affix affix : AffixRegistry.getAll()) {
            source.sendSuccess(() -> Component.literal(" §e" + affix.getId()
                    + " §7最大等级: " + affix.getMaxLevel()
                    + " §8" + affix.getNameTranslationKey()
            ), false);
        }

        if (!heldItem.isEmpty()) {
            int enchantValue = heldItem.getItem().getEnchantmentValue(heldItem);
            Affix existing = Affix.fromStack(heldItem);
            int existingLevel = existing != null ? Affix.getLevelFromStack(heldItem) : 0;
            int pity = PityTracker.getPity(heldItem);

            source.sendSuccess(() -> Component.literal("§6手持物品信息:"), false);
            source.sendSuccess(() -> Component.literal(" §7附魔能力: " + enchantValue), false);
            source.sendSuccess(() -> Component.literal(" §7已有词缀: " + (existing != null ?
                    existing.getId() + " Lv" + existingLevel : "§7无")), false);
            source.sendSuccess(() -> Component.literal(" §7软保底: " + pity), false);

            // 计算并显示各等级概率
            Map<Integer, Double> probs = net.mcreator.mut.affix.AffixProbabilityPreview.computeLevelProbabilities(
                    enchantValue, existingLevel, MaterialContext.empty(), pity
            );
            source.sendSuccess(() -> Component.literal("§6等级概率(无材料):"), false);
            for (var entry : probs.entrySet()) {
                source.sendSuccess(() -> Component.literal(String.format(" §7Lv%d: §e%.1f%%",
                        entry.getKey(), entry.getValue() * 100)), false);
            }
        }

        return 1;
    }

    // ========== 工具方法 ==========

    private static Player getPlayerOrFail(CommandSourceStack source) {
        if (!(source.getEntity() instanceof Player player)) {
            source.sendFailure(Component.literal("§c此命令只能由玩家执行"));
            return null;
        }
        return player;
    }
}
