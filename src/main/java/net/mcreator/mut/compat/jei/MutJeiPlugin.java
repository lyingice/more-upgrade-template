package net.mcreator.mut.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.mcreator.mut.MutMod;
import net.mcreator.mut.init.MutModBlocks;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * JEI 主插件入口
 * 注册超级锻造台的配方展示
 */
@JeiPlugin
public class MutJeiPlugin implements IModPlugin {

    private static final ResourceLocation PLUGIN_UID =
            ResourceLocation.fromNamespaceAndPath(MutMod.MODID, "jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        registration.addRecipeCategories(
                new SuperSmithingTableRecipeCategory(jeiHelpers.getGuiHelper())
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        // 注册超级锻造台的配方展示
        List<SuperSmithingTableRecipe> recipes = SuperSmithingTableRecipe.generateAll();
        registration.addRecipes(SuperSmithingTableRecipeCategory.TYPE, recipes);

        // 注册描述信息 - 超级锻造台的使用说明
        registration.addIngredientInfo(
                new ItemStack(MutModBlocks.SUPER_SMITHING_TABLE.get()),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.mut.super_smithing_table.description.0"),
                Component.translatable("jei.mut.super_smithing_table.description.1"),
                Component.translatable("jei.mut.super_smithing_table.description.2"),
                Component.translatable("jei.mut.super_smithing_table.description.3")
        );
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        // 将超级锻造台注册为配方催化剂（点击它可以看到配方）
        registration.addRecipeCatalyst(
                new ItemStack(MutModBlocks.SUPER_SMITHING_TABLE.get()),
                SuperSmithingTableRecipeCategory.TYPE
        );
    }
}
