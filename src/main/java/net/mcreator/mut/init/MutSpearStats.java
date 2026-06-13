package net.mcreator.mut.init;

import net.mcreator.mut.util.SpearCondition;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Items;
import net.minecraft.sounds.SoundEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MutSpearStats {

    public record Stats(
            int durability,
            float attackDuration,
            float damageMultiplier,
            float attackDamageBonus,
            int enchantmentValue,
            Rarity rarity,
            Ingredient repairIngredient,
            boolean fireResistant,
            String materialName,
            SoundEvent useSound,
            SoundEvent hitSound,
            SoundEvent attackSound,
            float swingTimes,
            float hitboxMargin,
            int contactCooldownTicks,
            int delayTicks,
            Optional<SpearCondition> dismountConditions,
            Optional<SpearCondition> knockbackConditions,
            Optional<SpearCondition> damageConditions,
            float forwardMovement,
            float minRange,
            float maxRange,
            float minCreativeRange,
            float maxCreativeRange,
            float hitboxMargin2,
            float mobFactor,
            boolean dealsKnockback,
            boolean dismounts
    ) {
        public float getAttackSpeedModifier() {
            return 1.0F / attackDuration - 4.0F;
        }

        // ========== 完全工厂方法 ==========
        public static Stats of(
                int durability, float attackDuration, float damageMultiplier,
                float attackDamageBonus, int enchantmentValue,
                Rarity rarity, Ingredient repairIngredient, boolean fireResistant,
                String materialName,
                SoundEvent useSound, SoundEvent hitSound, SoundEvent attackSound,
                float swingTimes, float hitboxMargin, int contactCooldownTicks,
                int delayTicks, Optional<SpearCondition> dismountConditions,
                Optional<SpearCondition> knockbackConditions, Optional<SpearCondition> damageConditions,
                float forwardMovement, float minRange, float maxRange,
                float minCreativeRange, float maxCreativeRange,
                float hitboxMargin2, float mobFactor,
                boolean dealsKnockback, boolean dismounts
        ) {
            return new Stats(durability, attackDuration, damageMultiplier, attackDamageBonus,
                    enchantmentValue, rarity, repairIngredient, fireResistant, materialName,
                    useSound, hitSound, attackSound,
                    swingTimes, hitboxMargin, contactCooldownTicks,
                    delayTicks, dismountConditions, knockbackConditions, damageConditions,
                    forwardMovement, minRange, maxRange,
                    minCreativeRange, maxCreativeRange,
                    hitboxMargin2, mobFactor,
                    dealsKnockback, dismounts);
        }
    }

    // ========== Jerotes 原版风格的简化工厂 ==========
    // 根据 attackDuration 自动计算所有阶段参数
    public static Stats spearOf(
            int durability, float attackDuration, float damageMultiplier,
            float attackDamageBonus, int enchantmentValue,
            Rarity rarity, Ingredient repairIngredient, boolean fireResistant,
            String materialName,
            SoundEvent useSound, SoundEvent hitSound, SoundEvent attackSound,
            float delaySec, float dismountSec, float knockbackSec, float damageSec
    ) {
        return Stats.of(
                durability, attackDuration, damageMultiplier, attackDamageBonus,
                enchantmentValue, rarity, repairIngredient, fireResistant,
                materialName, useSound, hitSound, attackSound,
                attackDuration,
                0.25F, 10,
                (int)(delaySec * 20.0F),
                SpearCondition.ofAttackerSpeed((int)(dismountSec * 20.0F), 0.3F),
                SpearCondition.ofAttackerSpeed((int)(knockbackSec * 20.0F), 5.1F),
                SpearCondition.ofRelativeSpeed((int)(damageSec * 20.0F), 4.6F),
                0.38F,
                2.0F, 4.5F, 2.0F, 6.5F,
                0.125F, 0.5F,
                true, false
        );
    }

    // 无防火
    public static Stats spearOf(
            int durability, float attackDuration, float damageMultiplier,
            float attackDamageBonus, int enchantmentValue,
            Rarity rarity, Ingredient repairIngredient, String materialName,
            SoundEvent useSound, SoundEvent hitSound, SoundEvent attackSound,
            float delaySec, float dismountSec, float knockbackSec, float damageSec
    ) {
        return spearOf(durability, attackDuration, damageMultiplier, attackDamageBonus,
                enchantmentValue, rarity, repairIngredient, false, materialName,
                useSound, hitSound, attackSound,
                delaySec, dismountSec, knockbackSec, damageSec);
    }

    // ========== 音效 ==========

    private static final SoundEvent WOOD_USE = MutModSounds.ITEM_SPEAR_WOOD_USE.get();
    private static final SoundEvent WOOD_HIT = MutModSounds.ITEM_SPEAR_WOOD_HIT.get();
    private static final SoundEvent WOOD_ATTACK = MutModSounds.ITEM_SPEAR_WOOD_ATTACK.get();

    private static final SoundEvent SPEAR_USE = MutModSounds.ITEM_SPEAR_USE.get();
    private static final SoundEvent SPEAR_HIT = MutModSounds.ITEM_SPEAR_HIT.get();
    private static final SoundEvent SPEAR_ATTACK = MutModSounds.ITEM_SPEAR_ATTACK.get();

    // ========== 预设常量 ==========


    public static final Stats WOOD = spearOf(
            59, 0.65f, 0.7f, 0.0f, 15,
            Rarity.COMMON, Ingredient.EMPTY, "wood",
            WOOD_USE, WOOD_HIT, WOOD_ATTACK,
            0.75f, 5.0f, 10.0f, 15.0f
    );

    public static final Stats STONE = spearOf(
            131, 0.75f, 0.82f, 1.0f, 5,
            Rarity.COMMON, Ingredient.of(Items.COBBLESTONE), "stone",
            SPEAR_USE, SPEAR_HIT, SPEAR_ATTACK,
            0.7f, 4.5f, 9.0f, 13.75f
    );

    public static final Stats COPPER = spearOf(
            195, 0.85f, 0.82f, 1.0f, 13,
            Rarity.COMMON, Ingredient.of(Items.COPPER_INGOT), "copper",
            SPEAR_USE, SPEAR_HIT, SPEAR_ATTACK,
            0.65f, 4.0f, 8.25f, 12.5f
    );

    public static final Stats IRON = spearOf(
            250, 0.95f, 0.95f, 2.0f, 14,
            Rarity.COMMON, Ingredient.of(Items.IRON_INGOT), "iron",
            SPEAR_USE, SPEAR_HIT, SPEAR_ATTACK,
            0.6f, 2.5f, 6.75f, 11.25f
    );

    public static final Stats GOLD = spearOf(
            59, 0.95f, 0.7f, 0.0f, 22,
            Rarity.COMMON, Ingredient.of(Items.GOLD_INGOT), "gold",
            SPEAR_USE, SPEAR_HIT, SPEAR_ATTACK,
            0.7f, 3.5f, 8.5f, 13.75f
    );

    public static final Stats DIAMOND = spearOf(
            1561, 1.05f, 1.075f, 3.0f, 10,
            Rarity.COMMON, Ingredient.of(Items.DIAMOND), "diamond",
            SPEAR_USE, SPEAR_HIT, SPEAR_ATTACK,
            0.5f, 3.0f, 6.5f, 10.0f
    );

    public static final Stats NETHERITE = spearOf(
            2031, 1.15f, 1.2f, 4.0f, 15,
            Rarity.COMMON, Ingredient.of(Items.NETHERITE_INGOT), true, "netherite",
            SPEAR_USE, SPEAR_HIT, SPEAR_ATTACK,
            0.4f, 2.5f, 5.5f, 8.75f
    );
    // ========== 新增材质预设（已修正攻速和伤害） ==========

    // 钢矛 — 攻速1.15 → attackDuration=0.87, 伤害4.5
    public static final Stats STEEL = spearOf(
            750, 0.87f, 1.125f, 3.5f, 15,
            Rarity.COMMON, Ingredient.of(MutModItems.STEEL_INGOT), true, "steel",
            SPEAR_USE, SPEAR_HIT, SPEAR_ATTACK,
            0.6f, 2.5f, 6.75f, 11.25f
    );

    // 精钢矛 — 攻速1.15 → attackDuration=0.87, 伤害6
    public static final Stats ADVANCED_STEEL = spearOf(
            2250, 0.87f, 1.3f, 5.0f, 15,
            Rarity.COMMON, Ingredient.of(MutModItems.ADVANCED_STEEL_INGOT), true, "advanced_steel",
            SPEAR_USE, SPEAR_HIT, SPEAR_ATTACK,
            0.6f, 2.5f, 6.75f, 11.25f
    );

    // 鎏金矛 — 攻速1.05 → attackDuration=0.95, 伤害4
    public static final Stats GILDING = spearOf(
            1561, 0.95f, 1.075f, 3.0f, 22,
            Rarity.COMMON, Ingredient.of(MutModItems.GILDING_INGOT), true, "gilding",
            SPEAR_USE, SPEAR_HIT, SPEAR_ATTACK,
            0.7f, 3.5f, 8.5f, 13.75f
    );

    // 蓝钻合金矛 — 攻速1.15 → attackDuration=0.87, 伤害6
    public static final Stats BLUE_DIAMOND = spearOf(
            2031, 0.87f, 1.3f, 5.0f, 18,
            Rarity.COMMON, Ingredient.of(MutModItems.BLUE_DIAMOND_INGOT), true, "blue_diamond",
            SPEAR_USE, SPEAR_HIT, SPEAR_ATTACK,
            0.5f, 3.0f, 6.5f, 10.0f
    );

    // 黑曜石矛 — 攻速0.87 → attackDuration=1.15, 伤害4.5
    public static final Stats OBSIDIAN = spearOf(
            2650, 1.15f, 1.15f, 3.5f, 1,
            Rarity.COMMON, Ingredient.of(Items.OBSIDIAN), true, "obsidian",
            SPEAR_USE, SPEAR_HIT, SPEAR_ATTACK,
            0.4f, 2.5f, 5.5f, 8.75f
    );

    // 下界合金黑曜石矛 — 攻速0.77 → attackDuration=1.30, 伤害6
    public static final Stats NETHERITE_OBSIDIAN = spearOf(
            3650, 1.30f, 1.3f, 5.0f, 1,
            Rarity.COMMON, Ingredient.of(MutModItems.OBSIDIAN_INGOT), true, "netherite_obsidian",
            SPEAR_USE, SPEAR_HIT, SPEAR_ATTACK,
            0.4f, 2.5f, 5.5f, 8.75f
    );

    // 悲悯矛 — 攻速0.67 → attackDuration=1.49, 伤害9
    public static final Stats CRYING_OBSIDIAN = spearOf(
            5650, 1.49f, 1.5f, 8.0f, 1,
            Rarity.EPIC, Ingredient.of(MutModItems.CRYING_OBSIDIAN_INGOT), true, "crying_obsidian",
            SPEAR_USE, SPEAR_HIT, SPEAR_ATTACK,
            0.4f, 2.5f, 5.5f, 8.75f
    );

    // 龙矛 — 攻速0.95 → attackDuration=1.05, 伤害11
    public static final Stats DRAGON = spearOf(
            5888, 1.05f, 1.4f, 10.0f, 30,
            Rarity.EPIC, Ingredient.of(Items.NETHER_STAR), true, "dragon",
            SPEAR_USE, SPEAR_HIT, SPEAR_ATTACK,
            0.4f, 2.5f, 5.5f, 8.75f
    );

    // 下界之星矛 — 攻速0.87 → attackDuration=1.15, 伤害8
    public static final Stats NETHER_STAR = spearOf(
            3000, 1.15f, 1.4f, 7.0f, 22,
            Rarity.EPIC, Ingredient.of(Items.NETHER_STAR), true, "nether_star",
            SPEAR_USE, SPEAR_HIT, SPEAR_ATTACK,
            0.4f, 2.5f, 5.5f, 8.75f
    );

    // 凋零矛 — 攻速0.87 → attackDuration=1.15, 伤害8
    public static final Stats WITHER = spearOf(
            3000, 1.15f, 1.4f, 7.0f, 22,
            Rarity.EPIC, Ingredient.of(Items.NETHER_STAR), true, "wither",
            SPEAR_USE, SPEAR_HIT, SPEAR_ATTACK,
            0.4f, 2.5f, 5.5f, 8.75f
    );

    // 下界合金铜矛 — 攻速0.87 → attackDuration=1.15, 伤害4
    public static final Stats NETHERITE_COPPER = spearOf(
            1231, 1.15f, 1.02f, 3.0f, 13,
            Rarity.COMMON, Ingredient.of(Items.COPPER_BLOCK), true, "netherite_copper",
            SPEAR_USE, SPEAR_HIT, SPEAR_ATTACK,
            0.4f, 2.5f, 5.5f, 8.75f
    );

    // 下界合金红石矛 — 攻速1.07 → attackDuration=0.93, 伤害5
    public static final Stats NETHERITE_REDSTONE = spearOf(
            2031, 0.93f, 1.2f, 4.0f, 15,
            Rarity.COMMON, Ingredient.of(MutModItems.NETHERITE_REDSTONE_INGOT), true, "netherite_redstone",
            SPEAR_USE, SPEAR_HIT, SPEAR_ATTACK,
            0.4f, 2.5f, 5.5f, 8.75f
    );

    // 绿宝石矛 — 攻速0.9 → attackDuration=1.11, 伤害4
    public static final Stats EMERALD = spearOf(
            888, 1.11f, 1.05f, 3.0f, 20,
            Rarity.COMMON, Ingredient.of(Items.EMERALD), "emerald",
            SPEAR_USE, SPEAR_HIT, SPEAR_ATTACK,
            0.5f, 3.0f, 6.5f, 10.0f
    );

    // 下界合金绿宝石矛 — 攻速0.87 → attackDuration=1.15, 伤害6
    public static final Stats NETHERITE_EMERALD = spearOf(
            1888, 1.15f, 1.175f, 5.0f, 20,
            Rarity.COMMON, Ingredient.of(MutModItems.NETHERITE_EMERALD_INGOT), true, "netherite_emerald",
            SPEAR_USE, SPEAR_HIT, SPEAR_ATTACK,
            0.4f, 2.5f, 5.5f, 8.75f
    );

    // 紫水晶矛 — 攻速1.0 → attackDuration=1.0, 伤害3.5
    public static final Stats AMETHYST = spearOf(
            350, 1.0f, 1.0f, 2.5f, 16,
            Rarity.COMMON, Ingredient.of(Items.AMETHYST_SHARD), "amethyst",
            SPEAR_USE, SPEAR_HIT, SPEAR_ATTACK,
            0.6f, 2.5f, 6.75f, 11.25f
    );

    // 下界合金紫水晶矛 — 攻速0.87 → attackDuration=1.15, 伤害5.5
    public static final Stats NETHERITE_AMETHYST = spearOf(
            1400, 1.15f, 1.15f, 4.5f, 16,
            Rarity.COMMON, Ingredient.of(MutModItems.NETHERITE_AMETHYST_INGOT), true, "netherite_amethyst",
            SPEAR_USE, SPEAR_HIT, SPEAR_ATTACK,
            0.4f, 2.5f, 5.5f, 8.75f
    );
    public static final Stats LAPIS_LAZULI = spearOf(200,0.85F,0.82F,2.5F,30,Rarity.COMMON,Ingredient.of(Items.LAPIS_LAZULI),"lapis_lazuli",SPEAR_USE,SPEAR_HIT,SPEAR_ATTACK,0.7f, 4.5f, 9.0f, 13.75f);
    public static final Stats NETHERITE_LAPIS_LAZULI = spearOf(1000,1.15F,1.02F,4.5F,60,Rarity.COMMON,Ingredient.of(MutModItems.NETHERITE_LAPIS_LAZULI_INGOT),"netherite_lapis_lazuli",SPEAR_USE,SPEAR_HIT,SPEAR_ATTACK,0.4f, 2.5f, 5.5f, 8.75f);
    public static final Stats POISON_STEEL = spearOf(2031,0.96F,1.25F,5F,15,Rarity.UNCOMMON,Ingredient.of(MutModItems.POSITION_STEEL_INGOT),"position_steel",SPEAR_USE,SPEAR_HIT,SPEAR_ATTACK,0.6f, 2.5f, 6.75f, 11.25f);
    public static final Stats FLAME_GOLD = spearOf(2031,0.96F,1.25F,5F,25,Rarity.UNCOMMON,Ingredient.of(MutModItems.FLAME_GOLD_INGOT),"flame_gold",SPEAR_USE,SPEAR_HIT,SPEAR_ATTACK,0.7f, 3.5f, 8.5f, 13.75f);
    public static final Stats ECHOITE = spearOf(4062,1.15F,1.325F,7F,30,Rarity.EPIC,Ingredient.of(MutModItems.ECHOITE_INGOT),"echoite",SPEAR_USE,SPEAR_HIT,SPEAR_ATTACK,0.4f, 2.5f, 5.5f, 8.75f);
    public static final Stats THUNDER_COPPER = spearOf(2031,0.85F,1.25F,6F,1,Rarity.UNCOMMON,Ingredient.of(MutModItems.THUNDER_COPPER_STAR),"thunder_copper",SPEAR_USE,SPEAR_HIT,SPEAR_ATTACK,0.65f, 4.0f, 8.25f, 12.5f);
    public static final Stats UNCANNY_AMETHYST = spearOf(2800,1.15F,1.25F,7.5F,16,Rarity.UNCOMMON,Ingredient.of(MutModItems.UNCANNY_AMETHYST_STAR),"uncanny_amethyst",SPEAR_USE,SPEAR_HIT,SPEAR_ATTACK,0.4f, 2.5f, 5.5f, 8.75f);
    public static final Stats DEFAULT = IRON;

    // ========== Map ==========
    private static final Map<Item, Stats> STATS_MAP = new HashMap<>();

    public static void register(Item spear, Stats stats) {
        STATS_MAP.put(spear, stats);
    }

    public static Stats get(Item spear) {
        return STATS_MAP.getOrDefault(spear, DEFAULT);
    }

    // ========== 便捷方法 ==========
    public static int durability(Item spear) { return get(spear).durability(); }
    public static float attackDuration(Item spear) { return get(spear).attackDuration(); }
    public static float damageMultiplier(Item spear) { return get(spear).damageMultiplier(); }
    public static float attackDamageBonus(Item spear) { return get(spear).attackDamageBonus(); }
    public static float getAttackSpeedModifier(Item spear) { return get(spear).getAttackSpeedModifier(); }
    public static int enchantmentValue(Item spear) { return get(spear).enchantmentValue(); }
    public static Rarity rarity(Item spear) { return get(spear).rarity(); }
    public static Ingredient repairIngredient(Item spear) { return get(spear).repairIngredient(); }
    public static boolean fireResistant(Item spear) { return get(spear).fireResistant(); }
    public static String materialName(Item spear) { return get(spear).materialName(); }
    public static SoundEvent useSound(Item spear) { return get(spear).useSound(); }
    public static SoundEvent hitSound(Item spear) { return get(spear).hitSound(); }
    public static SoundEvent attackSound(Item spear) { return get(spear).attackSound(); }
    public static float swingTimes(Item spear) { return get(spear).swingTimes(); }
    public static float hitboxMargin(Item spear) { return get(spear).hitboxMargin(); }
    public static int contactCooldownTicks(Item spear) { return get(spear).contactCooldownTicks(); }
    public static int delayTicks(Item spear) { return get(spear).delayTicks(); }
    public static Optional<SpearCondition> dismountConditions(Item spear) { return get(spear).dismountConditions(); }
    public static Optional<SpearCondition> knockbackConditions(Item spear) { return get(spear).knockbackConditions(); }
    public static Optional<SpearCondition> damageConditions(Item spear) { return get(spear).damageConditions(); }
    public static float forwardMovement(Item spear) { return get(spear).forwardMovement(); }
    public static float minRange(Item spear) { return get(spear).minRange(); }
    public static float maxRange(Item spear) { return get(spear).maxRange(); }
    public static float minCreativeRange(Item spear) { return get(spear).minCreativeRange(); }
    public static float maxCreativeRange(Item spear) { return get(spear).maxCreativeRange(); }
    public static float hitboxMargin2(Item spear) { return get(spear).hitboxMargin2(); }
    public static float mobFactor(Item spear) { return get(spear).mobFactor(); }
    public static boolean dealsKnockback(Item spear) { return get(spear).dealsKnockback(); }
    public static boolean dismounts(Item spear) { return get(spear).dismounts(); }
}