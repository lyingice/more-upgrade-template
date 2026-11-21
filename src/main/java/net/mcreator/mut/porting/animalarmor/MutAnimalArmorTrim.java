package net.mcreator.mut.porting.animalarmor;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.List;

/**
 * 马铠 / 狼铠纹饰的公共约定（移植自 TemplateCraftRevolution，去掉发光相关部分）。
 */
public final class MutAnimalArmorTrim {

    /** 动物铠纹饰图标可用的材质顺序：原版 10 种 + 美术色卡 10 种。 */
    public static final List<String> MATERIALS = List.of(
            "quartz", "iron", "netherite", "redstone", "copper",
            "gold", "emerald", "diamond", "lapis", "amethyst",
            "crying_obsidian", "flame_gold", "obsidian", "echoite", "poison_steel",
            "advanced_steel", "dragon_scale", "heterochromatic_amethyst",
            "heterochromatic_amethyst_pale", "refined_diamond_crystals"
    );

    /**
     * 允许应用到动物铠的纹饰模板白名单。
     *
     * <p>原版 {@code minecraft:trimmable_armor} 不含马铠/狼铠，所以原版模板本来就无法应用；
     * 这个标签用于拦住其它模组显式写了 base 的配方。</p>
     */
    public static final TagKey<Item> ALLOWED_TEMPLATES = TagKey.create(
            Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath("mut", "allowed_animal_armor_templates")
    );

    /** 本模组管理的可纹饰动物铠（马铠 / 狼铠）。 */
    public static final TagKey<Item> TRIMMABLE_ANIMAL_ARMOR = TagKey.create(
            Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath("mut", "animal_armor_trimmable")
    );

    private MutAnimalArmorTrim() {
    }
}
