package net.mcreator.mut.compat.jei;

import com.mojang.blaze3d.systems.RenderSystem;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.mcreator.mut.MutMod;
import net.mcreator.mut.init.MutModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class SuperSmithingTableRecipeCategory extends AbstractRecipeCategory<SuperSmithingTableRecipe> {

    public static final ResourceLocation UID =
            ResourceLocation.fromNamespaceAndPath(MutMod.MODID, "super_smithing_table");
    public static final RecipeType<SuperSmithingTableRecipe> TYPE =
            RecipeType.create(MutMod.MODID, "super_smithing_table", SuperSmithingTableRecipe.class);

    private final IDrawable slotDrawable;

    private static final int WIDTH = 176;
    private static final int HEIGHT = 100;

    public SuperSmithingTableRecipeCategory(IGuiHelper guiHelper) {
        super(
                TYPE,
                Component.translatable("gui.mut.super_smithing_table_gui.label_super_smithing_upgrade"),
                guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                        new ItemStack(MutModBlocks.SUPER_SMITHING_TABLE.get())),
                WIDTH, HEIGHT
        );
        this.slotDrawable = guiHelper.getSlotDrawable();
    }

    @Override
    public void draw(SuperSmithingTableRecipe recipe, IRecipeSlotsView slots,
                     GuiGraphics g, double mouseX, double mouseY) {

        var font = Minecraft.getInstance().font;
        if (font == null) return;

        // ── 4 个槽位 ──
        slotDrawable.draw(g, 8, 18);   // 模板
        slotDrawable.draw(g, 36, 18);  // 基底
        slotDrawable.draw(g, 64, 18);  // 材料
        slotDrawable.draw(g, 144, 18); // 输出

        // ── 箭头 → ──
        g.drawString(font, "→", 100, 22, 0xFFCCCCCC, false);

        // ── 输出标签：词缀结果 ──
        g.drawCenteredString(font, "§e✦ 词缀 ✦", 164, 8, 0xFFFFFF);

        // ═══════════════════════════════════════
        // 材料信息条（第 46px 行）
        // ═══════════════════════════════════════
        int y = 46;
        g.fill(4, y - 2, 172, y + 14, 0x20FFFFFF); // 半透明背景条

        // 材料名
        g.drawString(font, recipe.getMaterialName(), 8, y, 0xFFFFFFFF, false);

        // 附魔加成标签
        if (recipe.isUniversal()) {
            g.drawString(font, "✦ +" + recipe.getEnchantBonus(), 100, y, 0xFF55FFFF, false);
        }

        // 等级范围
        int minLv = recipe.getMinGuaranteedLevel();
        int maxLv = recipe.getMaxLevelCap();
        if (minLv > 0 || maxLv > 0) {
            String range;
            if (minLv > 0 && maxLv > 0) range = "Lv" + minLv + "~" + maxLv;
            else if (minLv > 0) range = "≥Lv" + minLv;
            else range = "≤Lv" + maxLv;
            g.drawString(font, range, 146, y, 0xFFAAFFAA, false);
        }

        // ═══════════════════════════════════════
        // 词缀概率条（第 64px 行起）
        // ═══════════════════════════════════════
        if (recipe.isDirected()) {
            int barY = 64;
            List<SuperSmithingTableRecipe.AffixProbDisplay> probs = recipe.getAffixProbs();
            int count = Math.min(probs.size(), 4); // 最多显示 4 条
            int barH = 8;
            int barGap = 3;

            for (int i = 0; i < count; i++) {
                var p = probs.get(i);
                int rowY = barY + i * (barH + barGap);

                // 标签名
                String label = p.getDisplayName();
                g.drawString(font, label, 8, rowY - 1, 0xFFFFFFFF, false);

                // 概率条背景
                int barX = 70;
                int barW = 72;
                g.fill(barX, rowY, barX + barW, rowY + barH, 0xFF333333);

                // 概率条前景
                int fillW = (int) (p.probability * barW);
                if (fillW > 0) {
                    g.fill(barX, rowY, barX + fillW, rowY + barH, 0xFF000000 | p.getColor());
                }

                // 百分比数字
                String pct = String.format("%.0f%%", p.probability * 100);
                g.drawString(font, pct, barX + barW + 4, rowY - 1, 0xFFFFFFFF, false);
            }

            // 指向性说明
            if (probs.size() > 4) {
                g.drawString(font, "§7... 共" + probs.size() + "项", 8, barY + 4 * (barH + barGap), 0xFF888888, false);
            }
        } else {
            // 无特殊加成
            g.drawString(font, "§7无特定词缀指向，随机分配", 8, 64, 0xFF888888, false);
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SuperSmithingTableRecipe recipe,
                          IFocusGroup focuses) {

        builder.addSlot(RecipeIngredientRole.INPUT, 9, 19)
                .addItemStack(recipe.getTemplate())
                .setSlotName("template");

        builder.addSlot(RecipeIngredientRole.INPUT, 37, 19)
                .addItemStack(recipe.getBase())
                .setSlotName("base");

        builder.addSlot(RecipeIngredientRole.INPUT, 65, 19)
                .addItemStack(recipe.getAddition())
                .setSlotName("addition");

        builder.addSlot(RecipeIngredientRole.OUTPUT, 145, 19)
                .addItemStack(recipe.getResult())
                .setSlotName("result");
    }

    @Override
    public boolean isHandled(SuperSmithingTableRecipe recipe) {
        return true;
    }
}