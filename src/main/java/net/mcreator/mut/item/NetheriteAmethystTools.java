package net.mcreator.mut.item;

import net.mcreator.mut.init.MutModItems;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.*;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.BlockTags;
import net.mcreator.mut.init.MutMaterials;
import net.mcreator.mut.init.MutMaterials.ToolType;

public abstract class NetheriteAmethystTools extends Item {
    public NetheriteAmethystTools(Properties p) { super(p); }

    // ── 剑 ──
    public static class Sword extends SwordItem {
        private static final Tier TIER = new Tier() {
            @Override public int getUses() { return 1400; }
            @Override public float getSpeed() { return 10.0f; }
            @Override public float getAttackDamageBonus() { return 7.5f; }
            @Override public TagKey<Block> getIncorrectBlocksForDrops() { return BlockTags.INCORRECT_FOR_NETHERITE_TOOL; }
            @Override public int getEnchantmentValue() { return 16; }
            @Override public Ingredient getRepairIngredient() { return Ingredient.of(new ItemStack(MutModItems.NETHERITE_AMETHYST_INGOT.get())); }
        };
        public Sword() { super(TIER, new Item.Properties()
                                    .fireResistant().attributes(MutMaterials.createToolAttributes(MutMaterials.NETHERITE_AMETHYST, ToolType.SWORD))); }
    }

    // ── 锹 ──
    public static class Shovel extends ShovelItem {
        private static final Tier TIER = new Tier() {
            @Override public int getUses() { return 1400; }
            @Override public float getSpeed() { return 10.0f; }
            @Override public float getAttackDamageBonus() { return 6.0f; }
            @Override public TagKey<Block> getIncorrectBlocksForDrops() { return BlockTags.INCORRECT_FOR_NETHERITE_TOOL; }
            @Override public int getEnchantmentValue() { return 16; }
            @Override public Ingredient getRepairIngredient() { return Ingredient.of(new ItemStack(MutModItems.NETHERITE_AMETHYST_INGOT.get())); }
        };
        public Shovel() { super(TIER, new Item.Properties()
                                    .fireResistant().attributes(MutMaterials.createToolAttributes(MutMaterials.NETHERITE_AMETHYST, ToolType.SHOVEL))); }
    }

    // ── 镐 ──
    public static class Pickaxe extends PickaxeItem {
        private static final Tier TIER = new Tier() {
            @Override public int getUses() { return 1400; }
            @Override public float getSpeed() { return 10.0f; }
            @Override public float getAttackDamageBonus() { return 5.5f; }
            @Override public TagKey<Block> getIncorrectBlocksForDrops() { return BlockTags.INCORRECT_FOR_NETHERITE_TOOL; }
            @Override public int getEnchantmentValue() { return 16; }
            @Override public Ingredient getRepairIngredient() { return Ingredient.of(new ItemStack(MutModItems.NETHERITE_AMETHYST_INGOT.get())); }
        };
        public Pickaxe() { super(TIER, new Item.Properties()
                                    .fireResistant().attributes(MutMaterials.createToolAttributes(MutMaterials.NETHERITE_AMETHYST, ToolType.PICKAXE))); }
    }

    // ── 斧 ──
    public static class Axe extends AxeItem {
        private static final Tier TIER = new Tier() {
            @Override public int getUses() { return 1400; }
            @Override public float getSpeed() { return 10.0f; }
            @Override public float getAttackDamageBonus() { return 9.5f; }
            @Override public TagKey<Block> getIncorrectBlocksForDrops() { return BlockTags.INCORRECT_FOR_NETHERITE_TOOL; }
            @Override public int getEnchantmentValue() { return 16; }
            @Override public Ingredient getRepairIngredient() { return Ingredient.of(new ItemStack(MutModItems.NETHERITE_AMETHYST_INGOT.get())); }
        };
        public Axe() { super(TIER, new Item.Properties()
                                    .fireResistant().attributes(MutMaterials.createToolAttributes(MutMaterials.NETHERITE_AMETHYST, ToolType.AXE))); }
    }

    // ── 锄 ──
    public static class Hoe extends HoeItem {
        private static final Tier TIER = new Tier() {
            @Override public int getUses() { return 1400; }
            @Override public float getSpeed() { return 10.0f; }
            @Override public float getAttackDamageBonus() { return 2.3f; }
            @Override public TagKey<Block> getIncorrectBlocksForDrops() { return BlockTags.INCORRECT_FOR_NETHERITE_TOOL; }
            @Override public int getEnchantmentValue() { return 16; }
            @Override public Ingredient getRepairIngredient() { return Ingredient.of(new ItemStack(MutModItems.NETHERITE_AMETHYST_INGOT.get())); }
        };
        public Hoe() { super(TIER, new Item.Properties()
                                    .fireResistant().attributes(MutMaterials.createToolAttributes(MutMaterials.NETHERITE_AMETHYST, ToolType.HOE))); }
    }
}
