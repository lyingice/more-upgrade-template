package net.mcreator.mut.init.data;

import net.mcreator.mut.MutMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MutArmorTagProvider extends ItemTagsProvider {
    private final PackOutput packOutput;

    public MutArmorTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                               CompletableFuture<TagsProvider.TagLookup<Block>> blockTags,
                               ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, MutMod.MODID, existingFileHelper);
        this.packOutput = output;
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        tag("head_armor", HELMETS);
        tag("chest_armor", CHESTPLATES);
        tag("leg_armor", LEGGINGS);
        tag("foot_armor", BOOTS);
        tag("horse_armor", HORSE_ARMOR);
        tag("horse_armor2", HORSE_ARMOR2);
        tag("wolf_armor", WOLF_ARMOR);
        tag("elytra", ELYTRA);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        return CompletableFuture.allOf(
                writeTag(cache, "head_armor", HELMETS),
                writeTag(cache, "chest_armor", CHESTPLATES),
                writeTag(cache, "leg_armor", LEGGINGS),
                writeTag(cache, "foot_armor", BOOTS),
                writeTag(cache, "horse_armor", HORSE_ARMOR),
                writeTag(cache, "horse_armor2", HORSE_ARMOR2),
                writeTag(cache, "wolf_armor", WOLF_ARMOR),
                writeTag(cache, "elytra", ELYTRA)
        );
    }

    private CompletableFuture<?> writeTag(CachedOutput cache, String path, List<String> itemIds) {
        JsonObject json = new JsonObject();
        json.addProperty("replace", false);
        JsonArray values = new JsonArray();
        for (String itemId : itemIds) {
            if (itemId.startsWith("#")) {
                JsonObject tagEntry = new JsonObject();
                tagEntry.addProperty("id", itemId);
                tagEntry.addProperty("required", false);
                values.add(tagEntry);
            } else {
                values.add(itemId);
            }
        }
        json.add("values", values);
        Path outputPath = packOutput.getOutputFolder(PackOutput.Target.DATA_PACK)
                .resolve(MutMod.MODID)
                .resolve("tags/item")
                .resolve(path + ".json");
        return DataProvider.saveStable(cache, json, outputPath);
    }

    private void tag(String path, List<String> itemIds) {
        TagKey<Item> tagKey = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(MutMod.MODID, path));
        var appender = tag(tagKey).replace(false);
        for (String itemId : itemIds) {
            if (itemId.startsWith("#")) {
                appender.addOptionalTag(ResourceLocation.parse(itemId.substring(1)));
            } else {
                ResourceLocation itemLocation = ResourceLocation.parse(itemId);
                appender.add(ResourceKey.create(Registries.ITEM, itemLocation));
            }
        }
    }

    private static final List<String> HELMETS = List.of(
            "mut:advanced_steel_helmet", "mut:amethyst_helmet", "mut:blue_diamond_helmet",
            "mut:copper_helmet", "mut:crying_obsidian_helmet", "mut:diamond_chain_helmet",
            "mut:dragon_helmet", "mut:echoite_helmet", "mut:emerald_armor_helmet",
            "mut:flame_gold_helmet", "mut:gilding_helmet", "mut:golden_chain_helmet",
            "mut:lapis_lazuli_helmet", "mut:nether_star_helmet", "mut:netherite_amethyst_helmet",
            "mut:netherite_chain_helmet", "mut:netherite_copper_helmet",
            "mut:netherite_emerald_helmet", "mut:netherite_lapis_lazuli_helmet",
            "mut:netherite_obsidian_helmet", "mut:netherite_redstone_helmet",
            "mut:obsidian_helmet", "mut:position_steel_helmet", "mut:steel_helmet",
            "mut:super_netherite_helmet", "mut:thunder_copper_helmet",
            "mut:uncanny_amethyst_helmet", "mut:wither_helmet"
    );

    private static final List<String> CHESTPLATES = List.of(
            "mut:advanced_steel_chestplate", "mut:amethyst_chestplate",
            "mut:blue_diamond_chestplate", "mut:copper_chestplate",
            "mut:crying_obsidian_chestplate", "mut:diamond_chain_chestplate",
            "mut:dragon_chestplate", "mut:echoite_chestplate",
            "mut:emerald_armor_chestplate", "mut:flame_gold_chestplate",
            "mut:gilding_chestplate", "mut:golden_chain_chestplate",
            "mut:lapis_lazuli_chestplate", "mut:nether_star_chestplate",
            "mut:netherite_amethyst_chestplate", "mut:netherite_chain_chestplate",
            "mut:netherite_copper_chestplate", "mut:netherite_emerald_chestplate",
            "mut:netherite_lapis_lazuli_chestplate", "mut:netherite_obsidian_chestplate",
            "mut:netherite_redstone_chestplate", "mut:obsidian_chestplate",
            "mut:position_steel_chestplate", "mut:steel_chestplate",
            "mut:super_netherite_chestplate", "mut:thunder_copper_chestplate",
            "mut:uncanny_amethyst_chestplate", "mut:wither_chestplate"
    );

    private static final List<String> LEGGINGS = List.of(
            "mut:advanced_steel_leggings", "mut:amethyst_leggings",
            "mut:blue_diamond_leggings", "mut:copper_leggings",
            "mut:crying_obsidian_leggings", "mut:diamond_chain_leggings",
            "mut:dragon_leggings", "mut:echoite_leggings",
            "mut:emerald_armor_leggings", "mut:flame_gold_leggings",
            "mut:gilding_leggings", "mut:golden_chain_leggings",
            "mut:lapis_lazuli_leggings", "mut:nether_star_leggings",
            "mut:netherite_amethyst_leggings", "mut:netherite_chain_leggings",
            "mut:netherite_copper_leggings", "mut:netherite_emerald_leggings",
            "mut:netherite_lapis_lazuli_leggings", "mut:netherite_obsidian_leggings",
            "mut:netherite_redstone_leggings", "mut:obsidian_leggings",
            "mut:position_steel_leggings", "mut:steel_leggings",
            "mut:super_netherite_leggings", "mut:thunder_copper_leggings",
            "mut:uncanny_amethyst_leggings", "mut:wither_leggings"
    );

    private static final List<String> BOOTS = List.of(
            "mut:advanced_steel_boots", "mut:amethyst_boots",
            "mut:blue_diamond_boots", "mut:copper_boots",
            "mut:crying_obsidian_boots", "mut:diamond_chain_boots",
            "mut:dragon_boots", "mut:echoite_boots",
            "mut:emerald_armor_boots", "mut:flame_gold_boots",
            "mut:gilding_boots", "mut:golden_chain_boots",
            "mut:lapis_lazuli_boots", "mut:nether_star_boots",
            "mut:netherite_amethyst_boots", "mut:netherite_chain_boots",
            "mut:netherite_copper_boots", "mut:netherite_emerald_boots",
            "mut:netherite_lapis_lazuli_boots", "mut:netherite_obsidian_boots",
            "mut:netherite_redstone_boots", "mut:obsidian_boots",
            "mut:position_steel_boots", "mut:steel_boots",
            "mut:super_netherite_boots", "mut:thunder_copper_boots",
            "mut:uncanny_amethyst_boots", "mut:wither_boots"
    );

    private static final List<String> HORSE_ARMOR = List.of(
            "minecraft:golden_horse_armor", "minecraft:diamond_horse_armor",
            "minecraft:leather_horse_armor", "minecraft:iron_horse_armor",
            "mut:netherite_horse_armor", "mut:copper_horse_armor",
            "#mut:horse_armor2"
    );

    private static final List<String> HORSE_ARMOR2 = List.of(
            "mut:steel_horse_armor", "mut:advanced_steel_horse_armor",
            "mut:gilding_horse_armor", "mut:blue_diamond_horse_armor",
            "mut:obsidian_horse_armor", "mut:netherite_obsidian_horse_armor",
            "mut:crying_obsidian_horse_armor", "mut:nether_star_horse_armor",
            "mut:dragon_horse_armor", "mut:wither_horse_armor",
            "mut:netherite_copper_horse_armor", "mut:netherite_redstone_horse_armor",
            "mut:netherite_emerald_horse_armor", "mut:netherite_amethyst_horse_armor",
            "mut:emerald_horse_armor", "mut:amethyst_horse_armor",
            "mut:lapis_lazuli_horse_armor", "mut:netherite_lapis_lazuli_horse_armor"
    );

    private static final List<String> WOLF_ARMOR = List.of(
            "mut:iron_wolf_armor", "mut:golden_wolf_armor",
            "mut:diamond_wolf_armor", "mut:netherite_wolf_armor",
            "mut:copper_wolf_armor", "mut:steel_wolf_armor",
            "mut:advanced_steel_wolf_armor", "mut:gilding_wolf_armor",
            "mut:blue_diamond_wolf_armor", "mut:obsidian_wolf_armor",
            "mut:netherite_obsidian_wolf_armor", "mut:crying_obsidian_wolf_armor",
            "mut:nether_star_wolf_armor", "mut:dragon_wolf_armor",
            "mut:wither_wolf_armor", "mut:netherite_copper_wolf_armor",
            "mut:netherite_redstone_wolf_armor", "mut:emerald_wolf_armor",
            "mut:netherite_emerald_wolf_armor", "mut:amethyst_wolf_armor",
            "mut:netherite_amethyst_wolf_armor", "mut:lapis_lazuli_wolf_armor",
            "mut:netherite_lapis_lazuli_wolf_armor", "minecraft:wolf_armor"
    );

    private static final List<String> ELYTRA = List.of(
            "mut:copper_elytra", "mut:dragon_elytra", "mut:amethyst_elytra",
            "mut:netherite_elytra", "mut:obsidian_elytra", "mut:golden_elytra",
            "mut:nether_star_elytra", "mut:diamond_elytra", "mut:lapis_lazuli_elytra",
            "mut:poison_steel_elytra", "mut:iron_elytra", "mut:emerald_elytra"
    );
}

