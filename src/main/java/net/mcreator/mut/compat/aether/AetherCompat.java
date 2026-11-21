package net.mcreator.mut.compat.aether;

import com.aetherteam.aether.item.accessories.gloves.GlovesItem;
import io.wispforest.accessories.api.AccessoriesAPI;
import io.wispforest.accessories.api.Accessory;
import net.mcreator.mut.MutMod;
import net.mcreator.mut.compat.CompatItems;
import net.mcreator.mut.init.MutMaterials;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 天境（Aether）联动的护手。
 * <p>
 * 本类只在 aether + accessories 同时存在时才会被加载（由 CompatBootstrap 反射调用），
 * 所以直接引用 GlovesItem 是安全的：模组缺席时这段字节码永远不会被解析。
 * <p>
 * 护手要在天境槽位里真正可用，需要四件事全部到位：
 * <ol>
 *   <li>物品带 {@code #aether:accessories_gloves} 标签（槽位准入谓词唯一依据），
 *       对应 {@code data/aether/tags/item/accessories_gloves.json}；</li>
 *   <li>调用 {@link AccessoriesAPI#registerAccessory} 注册 Accessory 实例（天境自己的
 *       AccessoryItem 构造器不会自动注册），本类在 {@link FMLCommonSetupEvent} 里完成；</li>
 *   <li>客户端逐物品注册 {@code GlovesRenderer}，见 {@link AetherCompatClient}；</li>
 *   <li>贴图遵循天境约定：传给 GlovesItem 的 ResourceLocation 是裸名 {@code <material>_gloves}，
 *       其内部 setRenderTexture 会拼成 {@code mut:textures/models/accessory/gloves/<material>_gloves_accessory.png}。</li>
 * </ol>
 */
public final class AetherCompat {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MutMod.MODID);

    /** 材质名 -> 护手，材质名同时用于拼物品 id、贴图名与标签条目 */
    public static final Map<String, DeferredItem<Item>> GLOVES = new LinkedHashMap<>();

    private static final String[] GLOVE_MATERIALS = {
            "advanced_steel", "amethyst", "blue_diamond", "copper", "crying_obsidian", "diamond_chain",
            "dragon", "echoite", "emerald", "flame_gold", "gilding", "golden_chain",
            "lapis_lazuli", "nether_star", "netherite_amethyst", "netherite_chain", "netherite_copper", "netherite_emerald",
            "netherite_lapis_lazuli", "netherite_obsidian", "netherite_redstone", "obsidian", "position_steel", "steel",
            "super_netherite", "thunder_copper", "uncanny_amethyst", "wither",
    };

    public static void register(IEventBus bus) {
        for (String material : GLOVE_MATERIALS) {
            GLOVES.put(material, ITEMS.register(material + "_gloves", () -> {
                Holder<ArmorMaterial> armorMaterial = gloveMaterial(material);
                return new GlovesItem(armorMaterial, gloveDamage(material),
                        // 裸名：GlovesItem 会自行拼成 textures/models/accessory/gloves/<name>_accessory.png
                        ResourceLocation.fromNamespaceAndPath(MutMod.MODID, material + "_gloves"),
                        armorMaterial.value().equipSound(), gloveProperties(material));
            }));
        }
        ITEMS.register(bus);

        // 模组构造阶段 DeferredItem.get() 还不可用（物品尚未实例化），
        // 因此 Accessory 注册必须延迟到 common setup。
        bus.addListener((FMLCommonSetupEvent event) -> event.enqueueWork(() -> {
            for (DeferredItem<Item> glove : GLOVES.values()) {
                Item item = glove.get();
                // GlovesItem -> AccessoryItem -> io.wispforest.accessories.api.Accessory，强转安全
                AccessoriesAPI.registerAccessory(item, (Accessory) item);
            }
            logMaterialCoverage();
        }));

        // 客户端专用注册单独成类，避免服务端专用环境解析客户端类型。
        // NeoForge 1.21.1 已移除 DistExecutor，用 FMLEnvironment.dist 做同样的物理端守卫。
        if (FMLEnvironment.dist == Dist.CLIENT) {
            AetherCompatClient.register(bus);
        }

        bus.addListener((BuildCreativeModeTabContentsEvent event) -> {
            if (event.getTabKey().equals(ResourceKey.create(Registries.CREATIVE_MODE_TAB,
                    ResourceLocation.fromNamespaceAndPath(MutMod.MODID, "more_upgrade_template_combat_items")))) {
                GLOVES.values().forEach(item -> event.accept(item.get()));
            }
        });
    }

    /**
     * 护手的盔甲材质：优先用本模组 MutMaterials（裸 ArmorMaterial 需 Holder.direct 包装），
     * 否则借用同材质盔甲物品（mut:&lt;material&gt;_helmet，emerald 例外为 mut:emerald_armor_helmet）的材质，
     * 最后才退回铁。
     */
    private static Holder<ArmorMaterial> gloveMaterial(String material) {
        MutMaterials.MutMaterial m = find(material);
        if (m != null) {
            return Holder.direct(m.asArmorMaterial());
        }
        ArmorItem armor = findArmorItem(material);
        if (armor != null) {
            return armor.getMaterial();
        }
        return ArmorMaterials.IRON;
    }

    /**
     * 护手伤害 = 同材质剑的基础伤害 / 4；MutMaterials 的 swordDamage 为 0 时回退查剑，
     * 剑的伤害从其属性修饰符读取（很多回退剑的 Tier bonus 是 0，真实伤害写在修饰符里），
     * 最后才用 1 点。保留完整精度，不做四舍五入。
     */
    private static double gloveDamage(String material) {
        MutMaterials.MutMaterial m = find(material);
        if (m != null && m.swordDamage > 0.0F) {
            return m.swordDamage / 4.0D;
        }

        SwordItem sword = findSword(material);
        if (sword != null) {
            float swordDamage = CompatItems.swordBaseAttackDamage(sword);
            if (swordDamage > 0.0F) {
                return swordDamage / 4.0D;
            }
        }
        return 1.0D;
    }

    /**
     * 护手耐久：MutMaterials 优先，否则同材质剑的 Tier.getUses()，再否则同材质盔甲的最大耐久，
     * 都没有才用 480。按 1/6 折算并夹在 80~1200 之间。
     * 有耐久才会进 #minecraft:enchantable/durability，从而吃到附魔与词缀。
     */
    private static int gloveDurability(String material) {
        MutMaterials.MutMaterial m = find(material);
        int uses = m != null ? m.durability : 0;

        // <=0 一律视为未命中，继续沿回退链往下找。
        if (uses <= 0) {
            SwordItem sword = findSword(material);
            if (sword != null) {
                uses = sword.getTier().getUses();
            }
        }
        if (uses <= 0) {
            ArmorItem armor = findArmorItem(material);
            if (armor != null) {
                uses = armor.getDefaultInstance().getMaxDamage();
            }
        }
        if (uses <= 0) {
            uses = 480;
        }
        return Math.max(80, Math.min(1200, uses / 6));
    }

    private static Item.Properties gloveProperties(String material) {
        MutMaterials.MutMaterial m = find(material);
        Item.Properties props = new Item.Properties().stacksTo(1).durability(gloveDurability(material));
        if (m != null) {
            props = props.rarity(m.rarity);
            if (m.fireResistant) {
                props = props.fireResistant();
            }
        } else {
            props = props.rarity(Rarity.COMMON);
        }
        return props;
    }

    /** 同材质盔甲物品：常规是 mut:&lt;material&gt;_helmet，emerald 例外是 mut:emerald_armor_helmet。 */
    private static ArmorItem findArmorItem(String material) {
        Item item = BuiltInRegistries.ITEM.get(
                ResourceLocation.fromNamespaceAndPath(MutMod.MODID, material + "_helmet"));
        if (item instanceof ArmorItem armor) {
            return armor;
        }
        item = BuiltInRegistries.ITEM.get(
                ResourceLocation.fromNamespaceAndPath(MutMod.MODID, material + "_armor_helmet"));
        return item instanceof ArmorItem armor ? armor : null;
    }

    private static SwordItem findSword(String material) {
        Item item = BuiltInRegistries.ITEM.get(
                ResourceLocation.fromNamespaceAndPath(MutMod.MODID, material + "_sword"));
        return item instanceof SwordItem sword ? sword : null;
    }

    /** 仿照 FdCompat 的口径输出材质覆盖统计，完全未匹配的材质单独 warn。 */
    private static void logMaterialCoverage() {
        int fromMaterials = 0;
        int borrowed = 0;
        int unresolved = 0;
        for (String material : GLOVE_MATERIALS) {
            if (find(material) != null) {
                fromMaterials++;
            } else if (findSword(material) != null || findArmorItem(material) != null) {
                borrowed++;
            } else {
                unresolved++;
                MutMod.LOGGER.warn("[compat] 天境护手材质未匹配（材质/伤害/耐久退化为铁级、1.0、480）: {}", material);
            }
        }
        MutMod.LOGGER.info("[compat] Aether gloves registered: {} (MutMaterials {}, 借用剑/盔甲 {}, 未匹配 {})",
                GLOVES.size(), fromMaterials, borrowed, unresolved);
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

    private AetherCompat() {
    }
}
