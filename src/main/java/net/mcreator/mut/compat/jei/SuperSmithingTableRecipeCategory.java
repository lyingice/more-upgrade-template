package net.mcreator.mut.compat.jei;

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

/**
 * 超级锻造台 JEI 配方类别
 * 在 JEI 中展示超级锻造台的使用方式
 *
 * 继承 AbstractRecipeCategory 而非直接实现 IRecipeCategory，
 * 避免 getBackground() 等已弃用方法的兼容问题。
 */
public class SuperSmithingTableRecipeCategory extends AbstractRecipeCategory<SuperSmithingTableRecipe> {

    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(MutMod.MODID, "super_smithing_table");
    public static final RecipeType<SuperSmithingTableRecipe> TYPE = RecipeType.create(MutMod.MODID, "super_smithing_table", SuperSmithingTableRecipe.class);

    private static final ResourceLocation BACKGROUND_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(MutMod.MODID, "textures/screens/jei_super_smithing_table.png");

    private final IDrawable arrow;
    private final IDrawable infoIcon;
    private final IDrawable slotDrawable;
    private final IDrawable background;

    private static final int WIDTH = 145;
    private static final int HEIGHT = 68;

    public SuperSmithingTableRecipeCategory(IGuiHelper guiHelper) {
        super(
                TYPE,
                Component.translatable("gui.mut.super_smithing_table_gui.label_super_smithing_upgrade"),
                guiHelper.createDrawableIngredient(
                        VanillaTypes.ITEM_STACK, new ItemStack(MutModBlocks.SUPER_SMITHING_TABLE.get())
                ),
                WIDTH,
                HEIGHT
        );

        // 创建背景（使用自定义纹理）
        this.background = guiHelper.drawableBuilder(BACKGROUND_TEXTURE, 0, 0, WIDTH, HEIGHT)
                .build();

        // 箭头图标
        this.arrow = guiHelper.createDrawable(
                ResourceLocation.parse("jei:textures/jei/atlas/gui/recipe_arrow.png"),
                0, 0, 24, 17
        );

        // 信息图标
        this.infoIcon = guiHelper.createDrawable(
                ResourceLocation.parse("jei:textures/jei/atlas/gui/recipe_arrow.png"),
                0, 17, 12, 12
        );

        this.slotDrawable = guiHelper.getSlotDrawable();
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void draw(SuperSmithingTableRecipe recipe, IRecipeSlotsView recipeSlotsView,
                     GuiGraphics guiGraphics, double mouseX, double mouseY) {

        // 绘制槽位背景
        slotDrawable.draw(guiGraphics, 16, 17);
        slotDrawable.draw(guiGraphics, 43, 17);
        slotDrawable.draw(guiGraphics, 70, 17);
        slotDrawable.draw(guiGraphics, 115, 17);

        // 箭头 (96, 20)
        arrow.draw(guiGraphics, 96, 20);

        // 信息图标 (120, 44)
        infoIcon.draw(guiGraphics, 120, 44);

        // 绘制加成描述信息
        if (Minecraft.getInstance().font != null) {
            String desc = recipe.getBonusDescription();
            if (desc != null && !desc.isEmpty()) {
                // 描绘材料加成信息（使用换行分两行显示）
                guiGraphics.drawString(
                        Minecraft.getInstance().font,
                        "§7加成: " + desc,
                        8, 48, 0xFFFFFF, false
                );
            }

            // 底部提示
            guiGraphics.drawString(
                    Minecraft.getInstance().font,
                    Component.translatable("jei.mut.super_smithing_table.tag"),
                    8, 58, 0x555555, false
            );
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SuperSmithingTableRecipe recipe,
                          IFocusGroup focuses) {

        // 槽位0: 模板（输入）
        builder.addSlot(RecipeIngredientRole.INPUT, 17, 18)
                .addItemStack(recipe.getTemplate())
                .setSlotName("template");

        // 槽位1: 基础物品（输入）
        builder.addSlot(RecipeIngredientRole.INPUT, 44, 18)
                .addItemStack(recipe.getBase())
                .setSlotName("base");

        // 槽位2: 材料（输入）
        builder.addSlot(RecipeIngredientRole.INPUT, 71, 18)
                .addItemStack(recipe.getAddition())
                .setSlotName("addition");

        // 槽位3: 输出
        builder.addSlot(RecipeIngredientRole.OUTPUT, 117, 18)
                .addItemStack(recipe.getResult())
                .setSlotName("result");
    }

    @Override
    public boolean isHandled(SuperSmithingTableRecipe recipe) {
        return true;
    }
}