package net.mcreator.mut.client.gui;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.client.gui.GuiGraphics;

import net.mcreator.mut.world.inventory.SuperSmithingTableGuiMenu;
import net.mcreator.mut.affix.Affix;
import net.mcreator.mut.affix.AffixRegistry;
import net.mcreator.mut.affix.AffixProbabilityPreview;
import net.mcreator.mut.affix.data.MaterialBonusRegistry;
import net.mcreator.mut.affix.data.MaterialContext;
import net.mcreator.mut.affix.data.AffixDataLoader;
import net.mcreator.mut.affix.data.PityTracker;

import com.mojang.blaze3d.systems.RenderSystem;

import java.util.List;

public class SuperSmithingTableGuiScreen extends ItemCombinerScreen<SuperSmithingTableGuiMenu> {

    private static final ResourceLocation BACKGROUND = ResourceLocation.parse("mut:textures/screens/super_smithing_table_gui.png");
    private static final ResourceLocation IMAGE_0 = ResourceLocation.parse("mut:textures/screens/smithing_template_netherite_upgrade.png");
    private static final ResourceLocation IMAGE_1 = ResourceLocation.parse("mut:textures/screens/nether_star_empty.png");

    // 概率预览按钮区域（在 GUI 上的书本图标位置）
    private static final int PREVIEW_BUTTON_X = 140;
    private static final int PREVIEW_BUTTON_Y = 52;
    private static final int PREVIEW_BUTTON_W = 16;
    private static final int PREVIEW_BUTTON_H = 14;

    public SuperSmithingTableGuiScreen(SuperSmithingTableGuiMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, Component.translatable("gui.mut.super_smithing_table_gui.label_super_smithing_upgrade"), BACKGROUND);
    }

    @Override
    protected void renderErrorIcon(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        guiGraphics.blit(BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
        guiGraphics.blit(IMAGE_0, this.leftPos + 15, this.topPos + 35, 0, 0, 16, 16, 16, 16);
        guiGraphics.blit(IMAGE_1, this.leftPos + 70, this.topPos + 35, 0, 0, 16, 16, 16, 16);
        renderErrorIcon(guiGraphics, this.leftPos, this.topPos);

        // 渲染概率预览书本图标
        guiGraphics.drawString(this.font, "§7[§b?§7]", this.leftPos + PREVIEW_BUTTON_X + 4, this.topPos + PREVIEW_BUTTON_Y + 2, 0xFFFFFF, false);

        RenderSystem.disableBlend();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);

        // 概率预览按钮的悬浮提示
        int btnX = this.leftPos + PREVIEW_BUTTON_X;
        int btnY = this.topPos + PREVIEW_BUTTON_Y;
        if (mouseX >= btnX && mouseX < btnX + PREVIEW_BUTTON_W && mouseY >= btnY && mouseY < btnY + PREVIEW_BUTTON_H) {
            guiGraphics.renderTooltip(this.font,
                    Component.translatable("gui.mut.super_smithing_table_gui.open_probability_overview"),
                    mouseX, mouseY);
        }

        // 输出槽位悬浮提示（等级概率tooltip）
        int slot3X = this.leftPos + 111;
        int slot3Y = this.topPos + 31;
        if (mouseX >= slot3X && mouseX < slot3X + 24 && mouseY >= slot3Y && mouseY < slot3Y + 24
                && !this.menu.getSlot(3).getItem().isEmpty()) {
            var base = this.menu.getSlot(1).getItem();
            var addition = this.menu.getSlot(2).getItem();
            if (!base.isEmpty() && !addition.isEmpty()) {
                var materialCtx = MaterialBonusRegistry.getInstance().evaluate(addition);
                int enchantValue = base.getItem().getEnchantmentValue(base) + materialCtx.getEnchantBonus();
                var existing = Affix.fromStack(base);
                int existingLevel = existing != null ? Affix.getLevelFromStack(base) : 0;
                int pityCount = PityTracker.getPity(base);


                var probLines = AffixProbabilityPreview.generateLevelProbabilityAsText(
                        enchantValue, existingLevel, materialCtx, pityCount);

                var tooltip = new java.util.ArrayList<Component>();
                tooltip.add(Component.translatable("gui.mut.super_smithing_table_gui.tooltip_add_random_affix_on_item"));
                tooltip.add(Component.literal(""));

                // 等级概率标题
                tooltip.add(Component.translatable("gui.mut.super_smithing_table_gui.probability_title"));
                for (String line : probLines) {
                    tooltip.add(Component.literal(line));
                }

                // 材料等级限制
                int minLevel = materialCtx.getMinGuaranteedLevel();
                int maxCap = materialCtx.getMaxLevelCap();
                if (maxCap == 0) maxCap = AffixDataLoader.getDefaultMaxLevelCap();

                if (minLevel > 0 || maxCap > 0) {
                    tooltip.add(Component.literal(""));
                    if (minLevel > 0) {
                        tooltip.add(Component.literal("§a保底等级≥Lv" + minLevel));
                    }
                    if (maxCap > 0) {
                        tooltip.add(Component.literal("§c上限等级≤Lv" + maxCap));
                    }
                }

                // 软保底行
                if (pityCount > 0) {
                    tooltip.add(Component.literal(""));
                    double pityBonusPerPoint = AffixDataLoader.getPityConfig()
                            .getGlobal().getPityBonusPerPoint();
                    int pityPercent = (int)(pityCount * pityBonusPerPoint * 100);
                    tooltip.add(Component.translatable("gui.mut.super_smithing_table_gui.pity_line",
                            pityCount, pityPercent));
                }

                // 材料加成行
                if (!materialCtx.isEmpty()) {
                    if (materialCtx.getEnchantBonus() > 0) {
                        tooltip.add(Component.literal(String.format("§7附魔加成: +%d",
                                materialCtx.getEnchantBonus())));
                    }
                    for (var bonus : materialCtx.getAffixBonuses()) {
                        int probPercent = (int)(bonus.getFixedProbability() * 100);
                        tooltip.add(Component.literal(String.format("§d%s §7概率§a%d%%  §7最大Lv%d",
                                bonus.getTargetAffix(), probPercent, bonus.getMaxLevel())));
                    }
                }

                // 最高概率词缀
                String topAffixLine = computeTopAffixLine(base, materialCtx);
                if (topAffixLine != null) {
                    tooltip.add(Component.literal(""));
                    tooltip.add(Component.literal(topAffixLine));
                }

                guiGraphics.renderTooltip(this.font, tooltip, java.util.Optional.empty(),
                        mouseX - 150, mouseY - 20);
            } else {
                guiGraphics.renderTooltip(this.font,
                        Component.translatable("gui.mut.super_smithing_table_gui.tooltip_add_random_affix_on_item"),
                        mouseX - 180, mouseY - 20);
            }
        }
    }

    /**
     * 计算受材料加成影响下，出现概率最高的词缀及其概率
     */
    private String computeTopAffixLine(ItemStack base, MaterialContext materialCtx) {
        if (materialCtx == null || materialCtx.isEmpty() || materialCtx.getAffixBonuses().isEmpty()) {
            return null;
        }

        var affixPool = AffixDataLoader.getItemAffixCache().getAffixesForItem(base);
        if (affixPool.isEmpty()) {
            affixPool = List.copyOf(AffixRegistry.getAll());
        }
        if (affixPool.isEmpty()) return null;

        String topAffixId = null;
        double topWeight = 0;
        double totalWeight = 0;

        for (Affix affix : affixPool) {
            double weight = 1.0;
            var bonus = materialCtx.getBonusForAffix(affix.getId());
            if (bonus != null) {
                weight = bonus.getFixedProbability();
            }
            totalWeight += weight;
            if (weight > topWeight) {
                topWeight = weight;
                topAffixId = affix.getId();
            }
        }

        if (topAffixId == null || totalWeight <= 0) return null;

        double topProb = topWeight / totalWeight * 100;
        return "§6⇨ §e" + topAffixId + " §7出现概率: §a" + String.format("%.1f", topProb) + "%";
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int btnX = this.leftPos + PREVIEW_BUTTON_X;
        int btnY = this.topPos + PREVIEW_BUTTON_Y;
        if (mouseX >= btnX && mouseX < btnX + PREVIEW_BUTTON_W
                && mouseY >= btnY && mouseY < btnY + PREVIEW_BUTTON_H) {
            // 打开概率预览覆盖层
            if (this.minecraft != null) {
                this.minecraft.setScreen(new AffixProbabilityOverlay(this));
            }
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

    @Override
    public boolean keyPressed(int key, int b, int c) {
        if (key == 256) {
            this.minecraft.player.closeContainer();
            return true;
        }
        return super.keyPressed(key, b, c);
    }
}

