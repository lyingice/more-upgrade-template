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
 * 三改：显示材料等级限制信息
 */
public class SuperSmithingTableRecipeCategory extends AbstractRecipeCategory<SuperSmithingTableRecipe> {

    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(MutMod.MODID, "super_smithing_table");
    public static final RecipeType<SuperSmithingTableRecipe> TYPE = RecipeType.create(MutMod.MODID, "super_smithing_table", SuperSmithingTableRecipe.class);

    private static final ResourceLocation BACKGROUND_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(MutMod.MODID, "textures/screens/jei_super_smithing_table.png");

    private final IDrawable arrow;
    private final IDrawable slotDrawable;
    private final IDrawable background;

    private static final int WIDTH = 145;
    private static final int HEIGHT = 72;

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

        this.background = guiHelper.drawableBuilder(BACKGROUND_TEXTURE, 0, 0, WIDTH, HEIGHT)
                .build();

        this.arrow = guiHelper.createDrawable(
                ResourceLocation.parse("jei:textures/jei/atlas/gui/recipe_arrow.png"),
                0, 0, 24, 17
        );

        this.slotDrawable = guiHelper.getSlotDrawable();
    }


    @Override
    public void draw(SuperSmithingTableRecipe recipe, IRecipeSlotsView recipeSlotsView,
                     GuiGraphics guiGraphics, double mouseX, double mouseY) {

        slotDrawable.draw(guiGraphics, 16, 17);
        slotDrawable.draw(guiGraphics, 43, 17);
        slotDrawable.draw(guiGraphics, 70, 17);
        slotDrawable.draw(guiGraphics, 115, 17);

        arrow.draw(guiGraphics, 96, 20);

        if (Minecraft.getInstance().font != null) {
            String desc = recipe.getBonusDescription();
            if (desc != null && !desc.isEmpty()) {
                guiGraphics.drawString(
                        Minecraft.getInstance().font,
                        "§7加成: " + desc,
                        8, 46, 0xFFFFFF, false
                );
            }

            // 显示等级信息
            if (recipe.getMaxLevel() > 0) {
                guiGraphics.drawString(
                        Minecraft.getInstance().font,
                        "§7最高等级: " + recipe.getMaxLevel(),
                        8, 56, 0xAAAAAA, false
                );
            }

            guiGraphics.drawString(
                    Minecraft.getInstance().font,
                    Component.translatable("jei.mut.super_smithing_table.tag"),
                    8, 64, 0x555555, false
            );
        }
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SuperSmithingTableRecipe recipe,
                          IFocusGroup focuses) {

        builder.addSlot(RecipeIngredientRole.INPUT, 17, 18)
                .addItemStack(recipe.getTemplate())
                .setSlotName("template");

        builder.addSlot(RecipeIngredientRole.INPUT, 44, 18)
                .addItemStack(recipe.getBase())
                .setSlotName("base");

        builder.addSlot(RecipeIngredientRole.INPUT, 71, 18)
                .addItemStack(recipe.getAddition())
                .setSlotName("addition");

        builder.addSlot(RecipeIngredientRole.OUTPUT, 117, 18)
                .addItemStack(recipe.getResult())
                .setSlotName("result");
    }

    @Override
    public boolean isHandled(SuperSmithingTableRecipe recipe) {
        return true;
    }
}
