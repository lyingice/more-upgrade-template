package net.mcreator.mut.item;

import net.mcreator.mut.init.AnimalArmorMaterials;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

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
}