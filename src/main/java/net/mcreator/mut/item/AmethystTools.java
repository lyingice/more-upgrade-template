package net.mcreator.mut.item;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.*;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.BlockTags;
import net.mcreator.mut.init.MutMaterials;
import net.mcreator.mut.init.MutMaterials.ToolType;

public abstract class AmethystTools extends Item {
    public AmethystTools(Properties p) { super(p); }

    // ── 剑 ──
    public static class Sword extends SwordItem {
        private static final Tier TIER = new Tier() {
            @Override public int getUses() { return 350; }
            @Override public float getSpeed() { return 7.0f; }
            @Override public float getAttackDamageBonus() { return 5.5f; }
            @Override public TagKey<Block> getIncorrectBlocksForDrops() { return BlockTags.INCORRECT_FOR_IRON_TOOL; }
            @Override public int getEnchantmentValue() { return 16; }
            @Override public Ingredient getRepairIngredient() { return Ingredient.of(new ItemStack(Items.AMETHYST_SHARD)); }
        };
        public Sword() { super(TIER, new Item.Properties().attributes(MutMaterials.createToolAttributes(MutMaterials.AMETHYST, ToolType.SWORD))); }
    }

    // ── 锹 ──
    public static class Shovel extends ShovelItem {
        private static final Tier TIER = new Tier() {
            @Override public int getUses() { return 350; }
            @Override public float getSpeed() { return 7.0f; }
            @Override public float getAttackDamageBonus() { return 4.0f; }
            @Override public TagKey<Block> getIncorrectBlocksForDrops() { return BlockTags.INCORRECT_FOR_IRON_TOOL; }
            @Override public int getEnchantmentValue() { return 16; }
            @Override public Ingredient getRepairIngredient() { return Ingredient.of(new ItemStack(Items.AMETHYST_SHARD)); }
        };
        public Shovel() { super(TIER, new Item.Properties().attributes(MutMaterials.createToolAttributes(MutMaterials.AMETHYST, ToolType.SHOVEL))); }
    }

    // ── 镐 ──
    public static class Pickaxe extends PickaxeItem {
        private static final Tier TIER = new Tier() {
            @Override public int getUses() { return 350; }
            @Override public float getSpeed() { return 7.0f; }
            @Override public float getAttackDamageBonus() { return 3.5f; }
            @Override public TagKey<Block> getIncorrectBlocksForDrops() { return BlockTags.INCORRECT_FOR_IRON_TOOL; }
            @Override public int getEnchantmentValue() { return 16; }
            @Override public Ingredient getRepairIngredient() { return Ingredient.of(new ItemStack(Items.AMETHYST_SHARD)); }
        };
        public Pickaxe() { super(TIER, new Item.Properties().attributes(MutMaterials.createToolAttributes(MutMaterials.AMETHYST, ToolType.PICKAXE))); }
    }

    // ── 斧 ──
    public static class Axe extends AxeItem {
        private static final Tier TIER = new Tier() {
            @Override public int getUses() { return 350; }
            @Override public float getSpeed() { return 7.0f; }
            @Override public float getAttackDamageBonus() { return 8.0f; }
            @Override public TagKey<Block> getIncorrectBlocksForDrops() { return BlockTags.INCORRECT_FOR_IRON_TOOL; }
            @Override public int getEnchantmentValue() { return 16; }
            @Override public Ingredient getRepairIngredient() { return Ingredient.of(new ItemStack(Items.AMETHYST_SHARD)); }
        };
        public Axe() { super(TIER, new Item.Properties().attributes(MutMaterials.createToolAttributes(MutMaterials.AMETHYST, ToolType.AXE))); }
    }

    // ── 锄 ──
    public static class Hoe extends HoeItem {
        private static final Tier TIER = new Tier() {
            @Override public int getUses() { return 350; }
            @Override public float getSpeed() { return 7.0f; }
            @Override public float getAttackDamageBonus() { return 1.8f; }
            @Override public TagKey<Block> getIncorrectBlocksForDrops() { return BlockTags.INCORRECT_FOR_IRON_TOOL; }
            @Override public int getEnchantmentValue() { return 16; }
            @Override public Ingredient getRepairIngredient() { return Ingredient.of(new ItemStack(Items.AMETHYST_SHARD)); }
        };
        public Hoe() { super(TIER, new Item.Properties().attributes(MutMaterials.createToolAttributes(MutMaterials.AMETHYST, ToolType.HOE))); }
    }
}
