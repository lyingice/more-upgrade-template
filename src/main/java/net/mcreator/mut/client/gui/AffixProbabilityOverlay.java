package net.mcreator.mut.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;

import net.mcreator.mut.affix.Affix;
import net.mcreator.mut.affix.AffixProbabilityPreview;
import net.mcreator.mut.affix.AffixRegistry;
import net.mcreator.mut.affix.data.MaterialBonusRegistry;
import net.mcreator.mut.affix.data.MaterialContext;
import net.mcreator.mut.affix.data.PityTracker;
import net.mcreator.mut.world.inventory.SuperSmithingTableGuiMenu;

import java.util.List;
import java.util.Map;

/**
 * 概率预览覆盖层
 * 在超级锻造台 GUI 上层显示等级概率和词缀权重信息
 * 点击空白处或按 ESC 关闭
 */
public class AffixProbabilityOverlay extends Screen {

    private static final ResourceLocation BACKGROUND =
            ResourceLocation.parse("mut:textures/screens/probability_overlay.png");

    private static final int OVERLAY_WIDTH = 160;
    private static final int OVERLAY_HEIGHT = 200;
    private static final int BG_COLOR = 0xCC1A1A2E; // 深蓝紫半透明背景
    private static final int BORDER_COLOR = 0xFF4444AA;

    private final SuperSmithingTableGuiScreen parentScreen;
    private List<String> levelProbabilityLines = List.of();
    private List<String> affixWeightLines = List.of();
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
     * 从父 GUI 的菜单中获取槽位物品并计算概率
     */
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

        // 基础信息
        int enchantValue = base.getItem().getEnchantmentValue(base);
        Affix existing = Affix.fromStack(base);
        int existingLevel = existing != null ? Affix.getLevelFromStack(base) : 0;
        int pityCount = PityTracker.getPity(base);

        enchantLine = String.format("§7附魔能力: §b%d  %s", enchantValue,
                existing != null ? "§7已有: §" + (existingLevel >= 3 ? "a" : "e") + existing.getId() + " Lv" + existingLevel : "");

        // 材料上下文
        MaterialContext materialCtx = MaterialBonusRegistry.getInstance().evaluate(addition);

        // 材料说明
        StringBuilder matBuilder = new StringBuilder("§7材料: §f");
        matBuilder.append(addition.getHoverName().getString());
        if (materialCtx.getUniversalLevelBonus() > 0) {
            matBuilder.append(" §b+").append(String.format("%.0f%%", materialCtx.getUniversalLevelBonus() * 100)).append("权重");
        }
        materialLine = matBuilder.toString();

        // 等级概率行
        levelProbabilityLines = AffixProbabilityPreview.generateLevelProbabilityAsText(
                enchantValue, existingLevel, materialCtx, pityCount
        );

        // 软保底行
        if (pityCount > 0) {
            pityLine = String.format("§e§l软保底: §7累计§e%d§7次 §f权重+§a%.0f%%", pityCount, pityCount * 12);
        } else {
            pityLine = "§7软保底: 未触发";
        }

        // 词缀权重信息
        if (!materialCtx.isEmpty() && !materialCtx.getAffixBonuses().isEmpty()) {
            affixWeightLines = List.of("§d§l词缀加成:");
            for (var bonus : materialCtx.getAffixBonuses()) {
                String affixName = bonus.getTargetAffix();
                Affix affix = AffixRegistry.get(bonus.getTargetAffix());
                if (affix != null) {
                    affixName = affix.getDisplayName().getString();
                }
                affixWeightLines.add(String.format(" §d%s §7×%.1f  §7等级+§a%.0f%%  §7最大Lv%d",
                        affixName,
                        bonus.getAffixWeightMultiplier(),
                        bonus.getLevelWeightBonus() * 100,
                        bonus.getMaxLevel()));
            }
        } else {
            affixWeightLines = List.of("§7词缀加成: 无定向加成");
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 先渲染半透明背景（遮罩）
        guiGraphics.fill(0, 0, this.width, this.height, 0x88000000);

        // 计算覆盖层居中位置
        int x = (this.width - OVERLAY_WIDTH) / 2;
        int y = (this.height - OVERLAY_HEIGHT) / 2;

        // 绘制背景框
        guiGraphics.fill(x, y, x + OVERLAY_WIDTH, y + OVERLAY_HEIGHT, BG_COLOR);
        // 边框
        guiGraphics.fill(x, y, x + OVERLAY_WIDTH, y + 1, BORDER_COLOR);
        guiGraphics.fill(x, y + OVERLAY_HEIGHT - 1, x + OVERLAY_WIDTH, y + OVERLAY_HEIGHT, BORDER_COLOR);
        guiGraphics.fill(x, y, x + 1, y + OVERLAY_HEIGHT, BORDER_COLOR);
        guiGraphics.fill(x + OVERLAY_WIDTH - 1, y, x + OVERLAY_WIDTH, y + OVERLAY_HEIGHT, BORDER_COLOR);

        // 标题
        if (this.font != null) {
            guiGraphics.drawString(this.font, "§l§d✦ 等级概率预览 ✦", x + 10, y + 6, 0xFFFFFF, false);

            if (!hasData) {
                // 无数据提示
                guiGraphics.drawString(this.font, titleLine, x + 10, y + 40, 0xFF5555, false);
                guiGraphics.drawString(this.font, "§7按 ESC 或点击外部关闭", x + 10, y + 60, 0x888888, false);
            } else {
                // 基本信息行
                int lineY = y + 20;
                guiGraphics.drawString(this.font, enchantLine, x + 8, lineY, 0xFFFFFF, false);
                lineY += 11;
                guiGraphics.drawString(this.font, materialLine, x + 8, lineY, 0xFFFFFF, false);
                lineY += 11;
                guiGraphics.drawString(this.font, pityLine, x + 8, lineY, 0xFFFFFF, false);
                lineY += 13;

                // 分隔线
                guiGraphics.fill(x + 8, lineY, x + OVERLAY_WIDTH - 8, lineY + 1, 0x44FFFFFF);
                lineY += 5;

                // 等级概率行
                for (String line : levelProbabilityLines) {
                    if (lineY > y + OVERLAY_HEIGHT - 30) break;
                    guiGraphics.drawString(this.font, line, x + 8, lineY, 0xFFFFFF, false);
                    lineY += 10;
                }
                lineY += 3;

                // 分隔线
                guiGraphics.fill(x + 8, lineY, x + OVERLAY_WIDTH - 8, lineY + 1, 0x44FFFFFF);
                lineY += 5;

                // 词缀权重信息
                for (String line : affixWeightLines) {
                    if (lineY > y + OVERLAY_HEIGHT - 4) break;
                    guiGraphics.drawString(this.font, line, x + 8, lineY, 0xFFFFFF, false);
                    lineY += 10;
                }
            }

            // 底部提示
            if (hasData) {
                guiGraphics.drawString(this.font, "§7ESC 关闭  |  数据实时更新", x + 10, y + OVERLAY_HEIGHT - 12, 0x555555, false);
            }
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false; // 不暂停游戏
    }

    @Override
    public boolean keyPressed(int key, int b, int c) {
        if (key == 256) { // ESC
            closeOverlay();
            return true;
        }
        return super.keyPressed(key, b, c);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 点击覆盖层外部区域关闭
        int x = (this.width - OVERLAY_WIDTH) / 2;
        int y = (this.height - OVERLAY_HEIGHT) / 2;
        if (mouseX < x || mouseX > x + OVERLAY_WIDTH || mouseY < y || mouseY > y + OVERLAY_HEIGHT) {
            closeOverlay();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    /**
     * 关闭覆盖层，返回上级 GUI
     */
    private void closeOverlay() {
        if (this.minecraft != null) {
            this.minecraft.setScreen(parentScreen);
        }
    }
}
