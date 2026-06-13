package net.mcreator.mut.item;

import net.mcreator.mut.init.AnimalArmorMaterials;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.function.Supplier;

public abstract class BaseAnimalArmorItem extends AnimalArmorItem {

    private final Supplier<Item> selfItemSupplier;

    // 基础构造（使用 stats.durability()）
    public BaseAnimalArmorItem(
            AnimalArmorMaterials.AnimalArmorExtendedStats stats,
            BodyType bodyType,
            boolean hasChest
    ) {
        this(stats, bodyType, hasChest, stats.durability(), null);
    }

    // 基础构造 + 自定义组件
    public BaseAnimalArmorItem(
            AnimalArmorMaterials.AnimalArmorExtendedStats stats,
            BodyType bodyType,
            boolean hasChest,
            java.util.function.UnaryOperator<Properties> componentApplier
    ) {
        this(stats, bodyType, hasChest, stats.durability(), componentApplier);
    }

    // 指定耐久（马铠用 durability=0）
    public BaseAnimalArmorItem(
            AnimalArmorMaterials.AnimalArmorExtendedStats stats,
            BodyType bodyType,
            boolean hasChest,
            int durability
    ) {
        this(stats, bodyType, hasChest, durability, null);
    }

    // 核心构造：支持自定义组件
    public BaseAnimalArmorItem(
            AnimalArmorMaterials.AnimalArmorExtendedStats stats,
            BodyType bodyType,
            boolean hasChest,
            int durability,
            java.util.function.UnaryOperator<Properties> componentApplier
    ) {
        super(
                stats.material(),
                bodyType,
                hasChest,
                buildProperties(stats, durability, componentApplier)
        );
        this.selfItemSupplier = () -> this;
        AnimalArmorMaterials.register(this, stats);
    }

    private static Properties buildProperties(
            AnimalArmorMaterials.AnimalArmorExtendedStats stats,
            int durability,
            java.util.function.UnaryOperator<Properties> componentApplier
    ) {
        Properties props = new Properties()
                .stacksTo(1)
                .rarity(stats.rarity())
                .attributes(AnimalArmorMaterials.createAttributes(stats));

        if (durability > 0) {
            props.durability(durability);
        }

        if (stats.fireResistant()) {
            props.fireResistant();
        }

        // 应用自定义组件
        if (componentApplier != null) {
            props = componentApplier.apply(props);
        }

        return props;
    }

    private static Properties buildProperties(AnimalArmorMaterials.AnimalArmorExtendedStats stats, int durability) {
        Properties props = new Properties()
                .stacksTo(1)
                .attributes(AnimalArmorMaterials.createAttributes(stats));

        // 只有耐久 > 0 时才设置
        if (durability > 0) {
            props.durability(durability);
        }

        if (stats.fireResistant()) {
            props.fireResistant();
        }
        return props;
    }

    private Item getSelfItem() {
        return selfItemSupplier.get();
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    protected AnimalArmorMaterials.AnimalArmorExtendedStats getStats() {
        return AnimalArmorMaterials.get(getSelfItem());
    }
    /**public class SteelWolfArmorItem extends BaseAnimalArmorItem {
        public SteelWolfArmorItem() {
            super(AnimalArmorMaterials.STEEL, BodyType.EQUESTRIAN, false);
            // 自动使用 stats.durability()
        }
    }
    public class SteelHorseArmorItem extends BaseAnimalArmorItem {
        public SteelHorseArmorItem() {
            super(AnimalArmorMaterials.STEEL, BodyType.EQUESTRIAN, false, 0);
            // 传入 0，不设置耐久属性
        }
    }**/
    // ========== 马铠物品类 ==========

    public static class SteelHorseArmorItem extends BaseAnimalArmorItem {
        public SteelHorseArmorItem() {
            super(AnimalArmorMaterials.STEEL, BodyType.EQUESTRIAN, false,0);
        }
        @Override public ResourceLocation getTexture() {return ResourceLocation.fromNamespaceAndPath("mut", "textures/entity/horse/armor/steel_horse_armor.png");}
    }

    public static class AdvancedSteelHorseArmorItem extends BaseAnimalArmorItem {
        public AdvancedSteelHorseArmorItem() {
            super(AnimalArmorMaterials.ADVANCED_STEEL, BodyType.EQUESTRIAN, false,0);
        }
        @Override public ResourceLocation getTexture() {return ResourceLocation.fromNamespaceAndPath("mut", "textures/entity/horse/armor/advanced_steel_horse_armor.png");}
    }

    public static class AmethystHorseArmorItem extends BaseAnimalArmorItem {
        public AmethystHorseArmorItem() {
            super(AnimalArmorMaterials.AMETHYST, BodyType.EQUESTRIAN, false, 0);
        }
        @Override public ResourceLocation getTexture() {return ResourceLocation.fromNamespaceAndPath("mut", "textures/entity/horse/armor/amethyst_horse_armor.png");}
    }

    public static class BlueDiamondHorseArmorItem extends BaseAnimalArmorItem {
        public BlueDiamondHorseArmorItem() {
            super(AnimalArmorMaterials.BLUE_DIAMOND, BodyType.EQUESTRIAN, false, 0);
        }
        @Override public ResourceLocation getTexture() {return ResourceLocation.fromNamespaceAndPath("mut", "textures/entity/horse/armor/blue_diamond_horse_armor.png");}
    }

    public static class CryingObsidianHorseArmorItem extends BaseAnimalArmorItem {
        public CryingObsidianHorseArmorItem() {
            super(AnimalArmorMaterials.CRYING_OBSIDIAN, BodyType.EQUESTRIAN, false, 0);
        }
        @Override public ResourceLocation getTexture() {return ResourceLocation.fromNamespaceAndPath("mut", "textures/entity/horse/armor/crying_obsidian_horse_armor.png");}
    }

    public static class DragonHorseArmorItem extends BaseAnimalArmorItem {
        public DragonHorseArmorItem() {
            super(AnimalArmorMaterials.DRAGON, BodyType.EQUESTRIAN, false, 0);
        }
        @Override public ResourceLocation getTexture() {return ResourceLocation.fromNamespaceAndPath("mut", "textures/entity/horse/armor/dragon_horse_armor.png");}
    }

    public static class EmeraldHorseArmorItem extends BaseAnimalArmorItem {
        public EmeraldHorseArmorItem() {
            super(AnimalArmorMaterials.EMERALD, BodyType.EQUESTRIAN, false, 0);
        }
        @Override public ResourceLocation getTexture() {return ResourceLocation.fromNamespaceAndPath("mut", "textures/entity/horse/armor/emerald_horse_armor.png");}
    }

    public static class GildingHorseArmorItem extends BaseAnimalArmorItem {
        public GildingHorseArmorItem() {
            super(AnimalArmorMaterials.GILDING, BodyType.EQUESTRIAN, false, 0);
        }
        @Override public ResourceLocation getTexture() {return ResourceLocation.fromNamespaceAndPath("mut", "textures/entity/horse/armor/gilding_horse_armor.png");}
    }

    public static class NetherStarHorseArmorItem extends BaseAnimalArmorItem {
        public NetherStarHorseArmorItem() {
            super(AnimalArmorMaterials.NETHER_STAR, BodyType.EQUESTRIAN, false, 0);
        }
        @Override
        @OnlyIn(Dist.CLIENT)
        public boolean isFoil(ItemStack itemstack) {
            return true;
        }
        @Override public ResourceLocation getTexture() {return ResourceLocation.fromNamespaceAndPath("mut", "textures/entity/horse/armor/nether_star_horse_armor.png");}
    }

    public static class NetheriteAmethystHorseArmorItem extends BaseAnimalArmorItem {
        public NetheriteAmethystHorseArmorItem() {
            super(AnimalArmorMaterials.NETHERITE_AMETHYST, BodyType.EQUESTRIAN, false, 0);
        }@Override public ResourceLocation getTexture() {return ResourceLocation.fromNamespaceAndPath("mut", "textures/entity/horse/armor/netherite_amethyst_horse_armor.png");}
    }

    public static class NetheriteCopperHorseArmorItem extends BaseAnimalArmorItem {
        public NetheriteCopperHorseArmorItem() {
            super(AnimalArmorMaterials.NETHERITE_COPPER, BodyType.EQUESTRIAN, false, 0);
        }@Override public ResourceLocation getTexture() {return ResourceLocation.fromNamespaceAndPath("mut", "textures/entity/horse/armor/netherite_copper_horse_armor.png");}
    }

    public static class NetheriteEmeraldHorseArmorItem extends BaseAnimalArmorItem {
        public NetheriteEmeraldHorseArmorItem() {
            super(AnimalArmorMaterials.NETHERITE_EMERALD_ANIMAL, BodyType.EQUESTRIAN, false, 0);
        }@Override public ResourceLocation getTexture() {return ResourceLocation.fromNamespaceAndPath("mut", "textures/entity/horse/armor/netherite_emerald_horse_armor.png");}
    }

    public static class NetheriteObsidianHorseArmorItem extends BaseAnimalArmorItem {
        public NetheriteObsidianHorseArmorItem() {
            super(AnimalArmorMaterials.NETHERITE_OBSIDIAN, BodyType.EQUESTRIAN, false, 0);
        }@Override public ResourceLocation getTexture() {return ResourceLocation.fromNamespaceAndPath("mut", "textures/entity/horse/armor/netherite_obsidian_horse_armor.png");}
    }

    public static class NetheriteRedstoneHorseArmorItem extends BaseAnimalArmorItem {
        public NetheriteRedstoneHorseArmorItem() {
            super(AnimalArmorMaterials.NETHERITE_REDSTONE, BodyType.EQUESTRIAN, false, 0);
        }
        @Override public ResourceLocation getTexture() {return ResourceLocation.fromNamespaceAndPath("mut", "textures/entity/horse/armor/netherite_redstone_horse_armor.png");}
    }

    public static class ObsidianHorseArmorItem extends BaseAnimalArmorItem {
        public ObsidianHorseArmorItem() {
            super(AnimalArmorMaterials.OBSIDIAN, BodyType.EQUESTRIAN, false, 0);
        }
        @Override public ResourceLocation getTexture() {return ResourceLocation.fromNamespaceAndPath("mut", "textures/entity/horse/armor/obsidian_horse_armor.png");}
    }

    public static class WitherHorseArmorItem extends BaseAnimalArmorItem {
        public WitherHorseArmorItem() {
            super(AnimalArmorMaterials.WITHER, BodyType.EQUESTRIAN, false, 0,props -> props.component(DataComponents.CUSTOM_DATA, createWitherMarkData()));
        }
        private static CustomData createWitherMarkData() {
            CompoundTag tag = new CompoundTag();
            tag.putString("Affix", "wither_mark");
            tag.putInt("AffixLevel", 3);
            return CustomData.of(tag);
        }
        @Override public ResourceLocation getTexture() {return ResourceLocation.fromNamespaceAndPath("mut", "textures/entity/horse/armor/wither_horse_armor.png");}
    }
    public static class LapisLazuliHorseArmorItem extends BaseAnimalArmorItem {
        public LapisLazuliHorseArmorItem() {
            super(AnimalArmorMaterials.LAPIS_LAZULI, BodyType.EQUESTRIAN, false, 0);
        }@Override public ResourceLocation getTexture() {return ResourceLocation.fromNamespaceAndPath("mut", "textures/entity/horse/armor/lapis_lazuli_horse_armor.png");}
    }
    public static class NetheriteLapisLazuliHorseArmorItem extends BaseAnimalArmorItem {
        public NetheriteLapisLazuliHorseArmorItem() {
            super(AnimalArmorMaterials.NETHERITE_LAPIS_LAZULI, BodyType.EQUESTRIAN, false, 0);
        }@Override public ResourceLocation getTexture() {return ResourceLocation.fromNamespaceAndPath("mut", "textures/entity/horse/armor/netherite_lapis_lazuli_horse_armor.png");}
    }
}