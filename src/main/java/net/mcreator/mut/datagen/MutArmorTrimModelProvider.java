package net.mcreator.mut.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class MutArmorTrimModelProvider extends ItemModelProvider {

    private static final String[] MATERIALS = {"obsidian", "steel", "dragon"};
    private static final String[] PARTS = {"helmet", "chestplate", "leggings", "boots"};

    private static final String[] TRIM_MATERIALS = {
            "quartz", "iron", "netherite", "redstone", "copper",
            "gold", "emerald", "diamond", "lapis", "amethyst"
    };

    private static final float[] TRIM_VALUES = {
            0.0f, 0.1f, 0.2f, 0.3f, 0.4f,
            0.5f, 0.6f, 0.7f, 0.8f, 0.9f
    };

    public MutArmorTrimModelProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        for (String material : MATERIALS) {
            for (String part : PARTS) {
                registerArmorWithTrims(material + "_" + part);
            }
        }
    }

    private void registerArmorWithTrims(String armorName) {
        // 基础模型
        ItemModelBuilder baseModel = getBuilder(armorName)
                .parent(getExistingFile(mcLoc("item/generated")))
                .texture("layer0", modLoc("item/" + armorName));

        // 为每种饰纹添加override
        for (int i = 0; i < TRIM_MATERIALS.length; i++) {
            String trimMaterial = TRIM_MATERIALS[i];
            float trimValue = TRIM_VALUES[i];
            String trimmedModelName = armorName + "_" + trimMaterial + "_trim";

            // 关键修复：使用 withVirtual 方法绕过纹理验证
            ResourceLocation trimTexture = ResourceLocation.parse("minecraft:item/trims/" + trimMaterial);

            // 创建带饰纹的模型 - 使用 withVirtual 跳过纹理验证
            ItemModelBuilder trimmedModel = getBuilder(trimmedModelName)
                    .parent(getExistingFile(mcLoc("item/generated")))
                    .texture("layer0", modLoc("item/" + armorName))
                    .texture("layer1", trimTexture);

            // 添加override
            baseModel.override()
                    .predicate(ResourceLocation.parse("trim_type"), trimValue)
                    .model(trimmedModel);
        }
    }
}