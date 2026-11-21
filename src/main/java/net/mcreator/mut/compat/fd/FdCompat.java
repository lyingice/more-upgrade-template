package net.mcreator.mut.compat.fd;

import net.mcreator.mut.MutMod;
import net.mcreator.mut.compat.CompatItems;
import net.mcreator.mut.init.MutMaterials;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraft.world.item.CreativeModeTab;
import vectorwing.farmersdelight.common.item.KnifeItem;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 农夫乐事联动的刀。
 * <p>
 * 本类只在 farmersdelight 存在时才会被加载（由 CompatBootstrap 反射调用），
 * 所以直接引用 KnifeItem 是安全的：模组缺席时这段字节码永远不会被解析。
 */
public final class FdCompat {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MutMod.MODID);

    /** 材质名 -> 刀 */
    public static final Map<String, DeferredItem<Item>> KNIVES = new LinkedHashMap<>();

    private static final String[] KNIFE_MATERIALS = {
            "advanced_steel", "amethyst", "blue_diamond", "copper", "crying_obsidian", "dragon",
            "echoite", "emerald", "flame_gold", "gilding", "lapis_lazuli", "nether_star",
            "netherite_amethyst", "netherite_copper", "netherite_emerald", "netherite_lapis_lazuli", "netherite_obsidian", "netherite_redstone",
            "obsidian", "position_steel", "steel", "thunder_copper", "uncanny_amethyst", "wither",
    };

    public static void register(IEventBus bus) {
        for (String material : KNIFE_MATERIALS) {
            KNIVES.put(material, ITEMS.register(material + "_knife", () -> {
                Tier tier = knifeTier(material);
                return new KnifeItem(tier, knifeProperties(material)
                        .attributes(DiggerItem.createAttributes(tier, 0.5F, -2.0F)));
            }));
        }
        ITEMS.register(bus);
        // 创造模式标签页：挂在"工具与实用物品"页
        bus.addListener((BuildCreativeModeTabContentsEvent event) -> {
            if (event.getTabKey().equals(ResourceKey.create(Registries.CREATIVE_MODE_TAB,
                    ResourceLocation.fromNamespaceAndPath(MutMod.MODID, "more_upgrade_template_tools")))) {
                KNIVES.values().forEach(item -> event.accept(item.get()));
            }
        });
        // 模组构造阶段物品尚未实例化，注册表里还查不到同材质剑，
        // 因此覆盖统计延迟到 common setup（与 AetherCompat 一致）。
        bus.addListener((FMLCommonSetupEvent event) -> event.enqueueWork(FdCompat::logMaterialCoverage));
    }

    private static void logMaterialCoverage() {
        int borrowedFromSword = 0;
        int unresolved = 0;
        for (String material : KNIFE_MATERIALS) {
            if (find(material) != null) {
                continue;
            }
            if (findSword(material) != null) {
                borrowedFromSword++;
                MutMod.LOGGER.debug("[compat] 材质 {} 不在 MutMaterials 中，刀借用同材质剑的 Tier/稀有度/防火", material);
            } else {
                unresolved++;
                MutMod.LOGGER.warn("[compat] 未知材质（刀退化为铁级）: {}", material);
            }
        }
        MutMod.LOGGER.info("[compat] Farmers Delight knives registered: {} (借用剑 Tier {} 个，退化铁级 {} 个)",
                KNIVES.size(), borrowedFromSword, unresolved);
    }

    /**
     * 复用本模组的材质层级。
     * Tier 与同材质剑保持一致：优先查 MutMaterials；查不到（那些材质定义在各自的物品类里）
     * 就直接借用同材质"剑"的 Tier，最后才退回铁级。
     * 很多回退剑的 {@code getAttackDamageBonus()} 是 0、真实伤害写在属性修饰符里，
     * 所以这里用 {@link CompatItems#swordBaseAttackDamage(net.minecraft.world.item.Item)}
     * 读出剑的伤害并包装成 Tier 的伤害加成（FD 的刀伤害 = tier bonus + 0.5）。
     * 稀有度与防火也尽量继承同材质剑。
     */
    private static Tier knifeTier(String material) {
        MutMaterials.MutMaterial m = find(material);
        if (m != null) {
            return m.asToolTier(MutMaterials.ToolType.SWORD);
        }
        SwordItem sword = findSword(material);
        if (sword != null) {
            Tier base = sword.getTier();
            float damageBonus = CompatItems.swordBaseAttackDamage(sword);
            return damageBonus > 0.0F ? wrapTier(base, damageBonus) : base;
        }
        return net.minecraft.world.item.Tiers.IRON;
    }

    /** 包装剑的 Tier，仅把伤害加成替换成从属性修饰符读出的值，其余全部委托。 */
    private static Tier wrapTier(Tier base, float damageBonus) {
        return new Tier() {
            @Override
            public int getUses() {
                return base.getUses();
            }

            @Override
            public float getSpeed() {
                return base.getSpeed();
            }

            @Override
            public float getAttackDamageBonus() {
                return damageBonus;
            }

            @Override
            public TagKey<Block> getIncorrectBlocksForDrops() {
                return base.getIncorrectBlocksForDrops();
            }

            @Override
            public int getEnchantmentValue() {
                return base.getEnchantmentValue();
            }

            @Override
            public Ingredient getRepairIngredient() {
                return base.getRepairIngredient();
            }
        };
    }

    /** 查同材质的剑；找不到返回 null。 */
    private static SwordItem findSword(String material) {
        Item item = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(
                ResourceLocation.fromNamespaceAndPath(MutMod.MODID, material + "_sword"));
        return item instanceof SwordItem sword ? sword : null;
    }

    private static Item.Properties knifeProperties(String material) {
        MutMaterials.MutMaterial m = find(material);
        Item.Properties props = new Item.Properties();
        if (m != null) {
            props = props.rarity(m.rarity);
            if (m.fireResistant) {
                props = props.fireResistant();
            }
        } else {
            SwordItem sword = findSword(material);
            if (sword != null) {
                props = props.rarity(sword.getDefaultInstance().getRarity());
                if (sword.components().has(DataComponents.FIRE_RESISTANT)) {
                    props = props.fireResistant();
                }
            } else {
                props = props.rarity(Rarity.COMMON);
            }
        }
        return props;
    }


    /** 安全查表：MutMaterials.get() 遇到未知材质会抛异常，这里改成遍历 + 返回 null */
    private static MutMaterials.MutMaterial find(String name) {
        for (MutMaterials.MutMaterial m : MutMaterials.getAll()) {
            if (m.name.equals(name)) {
                return m;
            }
        }
        return null;
    }

    private FdCompat() {
    }
}
