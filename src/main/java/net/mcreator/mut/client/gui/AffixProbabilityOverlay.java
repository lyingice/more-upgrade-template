package net.mcreator.mut.client.gui;

import net.mcreator.mut.affix.data.AffixDataLoader;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import net.mcreator.mut.affix.Affix;
import net.mcreator.mut.affix.AffixProbabilityPreview;
import net.mcreator.mut.affix.AffixRegistry;
import net.mcreator.mut.affix.data.MaterialBonusRegistry;
import net.mcreator.mut.affix.data.MaterialContext;
import net.mcreator.mut.affix.data.PityTracker;
import net.mcreator.mut.world.inventory.SuperSmithingTableGuiMenu;

import java.util.ArrayList;
import java.util.List;

public class AffixProbabilityOverlay extends Screen {

    private static final int OVERLAY_WIDTH = 160;
    private static final int OVERLAY_HEIGHT = 200;
    private static final int BG_COLOR = 0xCC1A1A2E;
    private static final int BORDER_COLOR = 0xFF4444AA;

    private final SuperSmithingTableGuiScreen parentScreen;
    private List<String> levelProbabilityLines = new ArrayList<>();
    private List<String> affixWeightLines = new ArrayList<>();
    private String titleLine = "";
    private String enchantLine = "";
    private String pityLine = "";
    private String materialLine = "";
    private boolean hasData = false;

    protected AffixProbabilityOverlay(SuperSmithingTableGuiScreen parent) {
        super(Component.translatable("gui.mut.probability_overlay.title"));
        this.parentScreen = parent;
        calculateProbabilities();
    }

    @Override
    protected void init() {
        super.init();
    }

    /**
     * 覆写为空，阻止 Screen 默认渲染泥土纹理背景。
     * 原版 renderBackground 绘制的半透明 dirt 纹理与我们手动绘制的遮罩叠加会导致画面变糊。
     */
    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // no-op: 完全由本类自绘背景
    }

    private void calculateProbabilities() {
        SuperSmithingTableGuiMenu menu = parentScreen.getMenu();
        ItemStack base = menu.getSlot(1).getItem();
        ItemStack addition = menu.getSlot(2).getItem();
        ItemStack template = menu.getSlot(0).getItem();

        if (base.isEmpty() || addition.isEmpty() || template.isEmpty()) {
            titleLine = "§c§l请放入材料以预览概率";
            hasData = false;
            return;
        }

        hasData = true;
        MaterialContext materialCtx = MaterialBonusRegistry.getInstance().evaluate(addition);
        int enchantValue = base.getItem().getEnchantmentValue(base) + materialCtx.getEnchantBonus();
        Affix existing = Affix.fromStack(base);
        int existingLevel = existing != null ? Affix.getLevelFromStack(base) : 0;
        int pityCount = PityTracker.getPity(base);

        enchantLine = String.format("§7附魔能力: §b%d  %s", enchantValue,
                existing != null ? "§7已有: §" + (existingLevel >= 3 ? "a" : "e") + existing.getId() + " Lv" + existingLevel : "");

        StringBuilder matBuilder = new StringBuilder("§7材料: §f");
        matBuilder.append(addition.getHoverName().getString());
        if (materialCtx.getEnchantBonus() > 0) {
            matBuilder.append(" §b+").append(materialCtx.getEnchantBonus()).append("附魔能力");
        }
        materialLine = matBuilder.toString();

        levelProbabilityLines = AffixProbabilityPreview.generateLevelProbabilityAsText(
                enchantValue, existingLevel, materialCtx, pityCount
        );
        int minLevel = materialCtx.getMinGuaranteedLevel();
        int maxCap = materialCtx.getMaxLevelCap();
        if (maxCap == 0) maxCap = AffixDataLoader.getDefaultMaxLevelCap();

        if (minLevel > 0 || maxCap > 0) {
            if (minLevel > 0) {
                affixWeightLines.add("§a保底等级≥Lv" + minLevel);
            }
            if (maxCap > 0) {
                affixWeightLines.add("§c上限等级≤Lv" + maxCap);
            }
        }

        if (pityCount > 0) {
            double pityBonusPerPoint = AffixDataLoader.getPityConfig().getGlobal().getPityBonusPerPoint();
            pityLine = String.format("§e§l软保底: §7累计§e%d§7次 §f权重+§a%.0f%%", pityCount, pityCount * pityBonusPerPoint * 100);
        } else {
            pityLine = "§7软保底: 未触发";
        }

        if (!materialCtx.isEmpty() && !materialCtx.getAffixBonuses().isEmpty()) {
            // 使用 ArrayList 可变列表，因为后面要多次 .add()
            List<String> lines = new ArrayList<>();
            lines.add("§d§l词缀加成:");
            for (var bonus : materialCtx.getAffixBonuses()) {
                String affixName = bonus.getTargetAffix();
                Affix affix = AffixRegistry.get(bonus.getTargetAffix());
                if (affix != null) {
                    affixName = affix.getDisplayName().getString();
                }
                int probPercent = (int)(bonus.getFixedProbability() * 100);
                lines.add(String.format(" §d%s §7概率§a%d%%  §7最大Lv%d",
                        affixName,
                        probPercent,
                        bonus.getMaxLevel()));
            }
            affixWeightLines = lines;
        } else {
            affixWeightLines = List.of("§7词缀加成: 无定向加成");
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 全屏半透明遮罩 — 唯一的一层背景遮罩，不叠加任何其他背景
        guiGraphics.fill(0, 0, this.width, this.height, 0x88000000);

        int x = (this.width - OVERLAY_WIDTH) / 2;
        int y = (this.height - OVERLAY_HEIGHT) / 2;

        // 面板背景 + 边框
        guiGraphics.fill(x, y, x + OVERLAY_WIDTH, y + OVERLAY_HEIGHT, BG_COLOR);
        guiGraphics.fill(x, y, x + OVERLAY_WIDTH, y + 1, BORDER_COLOR);
        guiGraphics.fill(x, y + OVERLAY_HEIGHT - 1, x + OVERLAY_WIDTH, y + OVERLAY_HEIGHT, BORDER_COLOR);
        guiGraphics.fill(x, y, x + 1, y + OVERLAY_HEIGHT, BORDER_COLOR);
        guiGraphics.fill(x + OVERLAY_WIDTH - 1, y, x + OVERLAY_WIDTH, y + OVERLAY_HEIGHT, BORDER_COLOR);

        if (this.font != null) {
            guiGraphics.drawString(this.font, "§l§d✦ 等级概率预览 ✦", x + 10, y + 6, 0xFFFFFF, false);

            if (!hasData) {
                guiGraphics.drawString(this.font, titleLine, x + 10, y + 40, 0xFF5555, false);
                guiGraphics.drawString(this.font, "§7按 ESC 或点击外部关闭", x + 10, y + 60, 0x888888, false);
            } else {
                int lineY = y + 20;
                guiGraphics.drawString(this.font, enchantLine, x + 8, lineY, 0xFFFFFF, false);
                lineY += 11;
                guiGraphics.drawString(this.font, materialLine, x + 8, lineY, 0xFFFFFF, false);
                lineY += 11;
                guiGraphics.drawString(this.font, pityLine, x + 8, lineY, 0xFFFFFF, false);
                lineY += 13;

                guiGraphics.fill(x + 8, lineY, x + OVERLAY_WIDTH - 8, lineY + 1, 0x44FFFFFF);
                lineY += 5;

                for (String line : levelProbabilityLines) {
                    if (lineY > y + OVERLAY_HEIGHT - 30) break;
                    guiGraphics.drawString(this.font, line, x + 8, lineY, 0xFFFFFF, false);
                    lineY += 10;
                }
                lineY += 3;

                guiGraphics.fill(x + 8, lineY, x + OVERLAY_WIDTH - 8, lineY + 1, 0x44FFFFFF);
                lineY += 5;

                for (String line : affixWeightLines) {
                    if (lineY > y + OVERLAY_HEIGHT - 4) break;
                    guiGraphics.drawString(this.font, line, x + 8, lineY, 0xFFFFFF, false);
                    lineY += 10;
                }
            }

            if (hasData) {
                guiGraphics.drawString(this.font, "§7ESC 关闭  |  数据实时更新", x + 10, y + OVERLAY_HEIGHT - 12, 0x555555, false);
            }
        }

        // 不调用 super.render() — renderBackground 已被覆写为空，
        // 且 Screen.render() 会触发不必要的渲染逻辑。
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyPressed(int key, int b, int c) {
        if (key == 256) {
            closeOverlay();
            return true;
        }
        return super.keyPressed(key, b, c);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = (this.width - OVERLAY_WIDTH) / 2;
        int y = (this.height - OVERLAY_HEIGHT) / 2;
        if (mouseX < x || mouseX > x + OVERLAY_WIDTH || mouseY < y || mouseY > y + OVERLAY_HEIGHT) {
            closeOverlay();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void closeOverlay() {
        if (this.minecraft != null) {
            this.minecraft.setScreen(parentScreen);
        }
    }
}
