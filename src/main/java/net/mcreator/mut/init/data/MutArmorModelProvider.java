package net.mcreator.mut.init.data;

import net.mcreator.mut.MutMod;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.List;

public class MutArmorModelProvider extends ItemModelProvider {

    private record TrimData(String name, float predicateValue) {
    }

    private static final List<TrimData> TRIMS = List.of(
            new TrimData("quartz", 0.1F),
            new TrimData("iron", 0.2F),
            new TrimData("netherite", 0.3F),
            new TrimData("redstone", 0.4F),
            new TrimData("copper", 0.5F),
            new TrimData("gold", 0.6F),
            new TrimData("emerald", 0.7F),
            new TrimData("diamond", 0.8F),
            new TrimData("lapis", 0.9F),
            new TrimData("amethyst", 1.0F)
    );

    private static final List<String> HELMETS = List.of(
            "advanced_steel_helmet", "amethyst_helmet", "blue_diamond_helmet", "copper_helmet",
            "crying_obsidian_helmet", "diamond_chain_helmet", "dragon_helmet", "echoite_helmet",
            "emerald_armor_helmet", "flame_gold_helmet", "gilding_helmet", "golden_chain_helmet",
            "lapis_lazuli_helmet", "nether_star_helmet", "netherite_amethyst_helmet",
            "netherite_chain_helmet", "netherite_copper_helmet", "netherite_emerald_helmet",
            "netherite_lapis_lazuli_helmet", "netherite_obsidian_helmet", "netherite_redstone_helmet",
            "obsidian_helmet", "position_steel_helmet", "steel_helmet", "super_netherite_helmet",
            "thunder_copper_helmet", "uncanny_amethyst_helmet", "wither_helmet"
    );

    private static final List<String> CHESTPLATES = List.of(
            "advanced_steel_chestplate", "amethyst_chestplate", "blue_diamond_chestplate",
            "copper_chestplate", "crying_obsidian_chestplate", "diamond_chain_chestplate",
            "dragon_chestplate", "echoite_chestplate", "emerald_armor_chestplate", "flame_gold_chestplate",
            "gilding_chestplate", "golden_chain_chestplate", "lapis_lazuli_chestplate",
            "nether_star_chestplate", "netherite_amethyst_chestplate", "netherite_chain_chestplate",
            "netherite_copper_chestplate", "netherite_emerald_chestplate",
            "netherite_lapis_lazuli_chestplate", "netherite_obsidian_chestplate",
            "netherite_redstone_chestplate", "obsidian_chestplate", "position_steel_chestplate",
            "steel_chestplate", "super_netherite_chestplate", "thunder_copper_chestplate",
            "uncanny_amethyst_chestplate", "wither_chestplate"
    );

    private static final List<String> LEGGINGS = List.of(
            "advanced_steel_leggings", "amethyst_leggings", "blue_diamond_leggings", "copper_leggings",
            "crying_obsidian_leggings", "diamond_chain_leggings", "dragon_leggings", "echoite_leggings",
            "emerald_armor_leggings", "flame_gold_leggings", "gilding_leggings", "golden_chain_leggings",
            "lapis_lazuli_leggings", "nether_star_leggings", "netherite_amethyst_leggings",
            "netherite_chain_leggings", "netherite_copper_leggings", "netherite_emerald_leggings",
            "netherite_lapis_lazuli_leggings", "netherite_obsidian_leggings",
            "netherite_redstone_leggings", "obsidian_leggings", "position_steel_leggings",
            "steel_leggings", "super_netherite_leggings", "thunder_copper_leggings",
            "uncanny_amethyst_leggings", "wither_leggings"
    );

    private static final List<String> BOOTS = List.of(
            "advanced_steel_boots", "amethyst_boots", "blue_diamond_boots", "copper_boots",
            "crying_obsidian_boots", "diamond_chain_boots", "dragon_boots", "echoite_boots",
            "emerald_armor_boots", "flame_gold_boots", "gilding_boots", "golden_chain_boots",
            "lapis_lazuli_boots", "nether_star_boots", "netherite_amethyst_boots",
            "netherite_chain_boots", "netherite_copper_boots", "netherite_emerald_boots",
            "netherite_lapis_lazuli_boots", "netherite_obsidian_boots", "netherite_redstone_boots",
            "obsidian_boots", "position_steel_boots", "steel_boots", "super_netherite_boots",
            "thunder_copper_boots", "uncanny_amethyst_boots", "wither_boots"
    );

    public MutArmorModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MutMod.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        generateArmorSet(HELMETS, "helmet");
        generateArmorSet(CHESTPLATES, "chestplate");
        generateArmorSet(LEGGINGS, "leggings");
        generateArmorSet(BOOTS, "boots");
    }

    private void generateArmorSet(List<String> items, String slotName) {
        for (String itemName : items) {
            generateArmorItem(itemName, slotName);
        }
    }

    private void generateArmorItem(String itemName, String slotName) {
        ResourceLocation baseTexture = ResourceLocation.fromNamespaceAndPath(MutMod.MODID, "item/" + itemName);
        ModelFile.UncheckedModelFile generatedModel = new ModelFile.UncheckedModelFile("minecraft:item/generated");

        ItemModelBuilder baseModel = getBuilder(itemName)
                .parent(generatedModel)
                .texture("layer0", baseTexture);

        for (TrimData trim : TRIMS) {
            ResourceLocation trimTexture = ResourceLocation.withDefaultNamespace("trims/items/" + slotName + "_trim_" + trim.name());
            String trimModelName = itemName + "_" + trim.name() + "_trim";

            getBuilder(trimModelName)
                    .parent(generatedModel)
                    .texture("layer0", baseTexture)
                    .texture("layer1", trimTexture);

            baseModel.override()
                    .model(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(MutMod.MODID, "item/" + trimModelName)))
                    .predicate(ResourceLocation.withDefaultNamespace("trim_type"), trim.predicateValue())
                    .end();
        }
    }
}
