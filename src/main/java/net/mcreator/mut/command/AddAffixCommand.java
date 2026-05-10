package net.mcreator.mut.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.mcreator.mut.affix.Affix;
import net.mcreator.mut.affix.AffixRegistry;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * 词条命令 - 给物品添加/移除词条
 */
public class AddAffixCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("addaffix")
                        .requires(source -> source.hasPermission(2)) // OP权限
                        .then(Commands.argument("affix_id", StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    // 提示所有已注册的词条ID
                                    for (Affix affix : AffixRegistry.getAll()) {
                                        builder.suggest(affix.getId());
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(context -> {
                                    String affixId = StringArgumentType.getString(context, "affix_id");
                                    return addAffixToHeldItem(context.getSource(), affixId);
                                })
                        )
        );

        // 查看词条命令
        dispatcher.register(
                Commands.literal("viewaffix")
                        .requires(source -> source.hasPermission(0)) // 所有玩家可用
                        .executes(context -> viewAffixOnHeldItem(context.getSource()))
        );

        // 移除词条命令
        dispatcher.register(
                Commands.literal("removeaffix")
                        .requires(source -> source.hasPermission(2))
                        .executes(context -> removeAffixFromHeldItem(context.getSource()))
        );
    }

    /**
     * 给手持物品添加词条
     */
    private static int addAffixToHeldItem(CommandSourceStack source, String affixId) {
        if (!(source.getEntity() instanceof Player player)) {
            source.sendFailure(Component.literal("§c此命令只能由玩家执行"));
            return 0;
        }

        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.isEmpty()) {
            source.sendFailure(Component.literal("§c你主手里没有物品"));
            return 0;
        }

        Affix affix = AffixRegistry.get(affixId);
        if (affix == null) {
            source.sendFailure(Component.literal("§c未知词条: " + affixId));
            return 0;
        }

        // 检查是否已有词条
        Affix existingAffix = Affix.fromStack(heldItem);
        if (existingAffix != null) {
            source.sendFailure(
                    Component.literal("§c此物品已有词条: " + existingAffix.getId() +
                            "\n§c请先使用 /removeaffix 移除现有词条")
            );
            return 0;
        }

        // 应用词条
        affix.applyToStack(heldItem);

        source.sendSuccess(
                () -> Component.literal("§a✔ 已为物品添加词条: §e" + affixId),
                true
        );

        return 1;
    }

    /**
     * 查看手持物品的词条
     */
    private static int viewAffixOnHeldItem(CommandSourceStack source) {
        if (!(source.getEntity() instanceof Player player)) {
            source.sendFailure(Component.literal("§c此命令只能由玩家执行"));
            return 0;
        }

        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.isEmpty()) {
            source.sendFailure(Component.literal("§c你主手里没有物品"));
            return 0;
        }

        Affix affix = Affix.fromStack(heldItem);
        if (affix == null) {
            source.sendSuccess(
                    () -> Component.literal("§7此物品没有任何词条"),
                    false
            );
        } else {
            source.sendSuccess(
                    () -> Component.literal("§a当前词条: §e" + affix.getId()),
                    false
            );
        }

        return 1;
    }

    /**
     * 移除手持物品的词条
     */
    private static int removeAffixFromHeldItem(CommandSourceStack source) {
        if (!(source.getEntity() instanceof Player player)) {
            source.sendFailure(Component.literal("§c此命令只能由玩家执行"));
            return 0;
        }

        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.isEmpty()) {
            source.sendFailure(Component.literal("§c你主手里没有物品"));
            return 0;
        }

        Affix affix = Affix.fromStack(heldItem);
        if (affix == null) {
            source.sendFailure(Component.literal("§c此物品没有词条"));
            return 0;
        }

        // 移除词条（清除 CustomData）
        heldItem.remove(net.minecraft.core.component.DataComponents.CUSTOM_DATA);

        source.sendSuccess(
                () -> Component.literal("§a✔ 已移除词条: §e" + affix.getId()),
                true
        );

        return 1;
    }
}