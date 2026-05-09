package net.mcreator.mut.init;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import javax.annotation.Nullable;
import java.util.*;

/**
 * 额外属性配置中心
 * <p>
 * 工具额外属性 + 盔甲四部位独立额外属性。
 */
public class MutMoreAttributeMaterials {

    public record AttributeEntry(
            Holder<Attribute> attribute,
            double amount,
            AttributeModifier.Operation operation,
            EquipmentSlotGroup slotGroup,
            String suffixId
    ) {}

    public record MaterialExtraAttributes(
            String materialName,
            List<AttributeEntry> toolAttributes,
            List<AttributeEntry> helmetAttributes,
            List<AttributeEntry> chestplateAttributes,
            List<AttributeEntry> leggingsAttributes,
            List<AttributeEntry> bootsAttributes
    ) {}

    private static final List<MaterialExtraAttributes> REGISTRY = new ArrayList<>();

    // =====================================================================
    // 注册
    // =====================================================================

    public static void register(
            String materialName,
            List<AttributeEntry> toolEntries,
            List<AttributeEntry> helmetEntries,
            List<AttributeEntry> chestplateEntries,
            List<AttributeEntry> leggingsEntries,
            List<AttributeEntry> bootsEntries
    ) {
        for (MaterialExtraAttributes existing : REGISTRY) {
            if (existing.materialName().equals(materialName)) {
                throw new IllegalArgumentException("材质 '" + materialName + "' 的额外属性已经注册过了！");
            }
        }
        REGISTRY.add(new MaterialExtraAttributes(
                materialName,
                List.copyOf(toolEntries),
                List.copyOf(helmetEntries),
                List.copyOf(chestplateEntries),
                List.copyOf(leggingsEntries),
                List.copyOf(bootsEntries)
        ));
    }

    // =====================================================================
    // 工厂
    // =====================================================================

    public static AttributeEntry attr(Holder<Attribute> attribute, double amount,
                                      AttributeModifier.Operation operation, EquipmentSlotGroup slotGroup, String suffixId) {
        return new AttributeEntry(attribute, amount, operation, slotGroup, suffixId);
    }

    @SafeVarargs
    public static List<AttributeEntry> entries(AttributeEntry... entries) {
        return List.of(entries);
    }

    // =====================================================================
    // 查询
    // =====================================================================

    public static List<AttributeEntry> getToolAttributes(String name) {
        return REGISTRY.stream().filter(m -> m.materialName().equals(name)).findFirst()
                .map(MaterialExtraAttributes::toolAttributes).orElse(Collections.emptyList());
    }

    public static List<AttributeEntry> getHelmetAttributes(String name) {
        return REGISTRY.stream().filter(m -> m.materialName().equals(name)).findFirst()
                .map(MaterialExtraAttributes::helmetAttributes).orElse(Collections.emptyList());
    }

    public static List<AttributeEntry> getChestplateAttributes(String name) {
        return REGISTRY.stream().filter(m -> m.materialName().equals(name)).findFirst()
                .map(MaterialExtraAttributes::chestplateAttributes).orElse(Collections.emptyList());
    }

    public static List<AttributeEntry> getLeggingsAttributes(String name) {
        return REGISTRY.stream().filter(m -> m.materialName().equals(name)).findFirst()
                .map(MaterialExtraAttributes::leggingsAttributes).orElse(Collections.emptyList());
    }

    public static List<AttributeEntry> getBootsAttributes(String name) {
        return REGISTRY.stream().filter(m -> m.materialName().equals(name)).findFirst()
                .map(MaterialExtraAttributes::bootsAttributes).orElse(Collections.emptyList());
    }

    // =====================================================================
    // 构建
    // =====================================================================

    @Nullable
    public static ItemAttributeModifiers buildModifiers(String materialName, List<AttributeEntry> entries) {
        if (entries.isEmpty()) return null;
        var builder = ItemAttributeModifiers.builder();
        for (AttributeEntry entry : entries) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath("mut", materialName + "_" + entry.suffixId());
            builder.add(entry.attribute(), new AttributeModifier(id, entry.amount(), entry.operation()), entry.slotGroup());
        }
        return builder.build();
    }

    // =====================================================================
    // 预设示例（可删除，仅作参考）
    // =====================================================================

    static {
        register("advanced_steel",
                entries(
                        attr(Attributes.BLOCK_INTERACTION_RANGE, 1.0, AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.MAINHAND, "block_range"),
                        attr(Attributes.ENTITY_INTERACTION_RANGE, 1.0, AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.MAINHAND, "entity_range")
                ),
                entries(),
                entries(),
                entries(),
                entries());
        // ──────────────────── 紫水晶材质 ────────────────────
        register("amethyst",
                entries(),
                entries(
                        attr(Attributes.MAX_HEALTH, 3.0, AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.HEAD, "armor_health_head")
                ),
                entries(
                        attr(Attributes.MAX_HEALTH, 3.0, AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.CHEST, "armor_health_chest")
                ),
                entries(
                        attr(Attributes.MAX_HEALTH, 3.0, AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.LEGS, "armor_health_legs")
                ),
                entries(
                        attr(Attributes.MAX_HEALTH, 3.0, AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.FEET, "armor_health_feet")
                )
        );

        // ──────────────────── 下界合金紫水晶 ────────────────────
        register("netherite_amethyst",
                entries(
                        attr(Attributes.BLOCK_INTERACTION_RANGE, 1.0, AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.MAINHAND, "block_range"),
                        attr(Attributes.ENTITY_INTERACTION_RANGE, 0.5, AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.MAINHAND, "entity_range")
                ),
                entries(
                        attr(Attributes.MAX_HEALTH, 6.0, AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.HEAD, "armor_health_head")
                ),
                entries(
                        attr(Attributes.MAX_HEALTH, 6.0, AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.CHEST, "armor_health_chest")
                ),
                entries(
                        attr(Attributes.MAX_HEALTH, 6.0, AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.LEGS, "armor_health_legs")
                ),
                entries(
                        attr(Attributes.MAX_HEALTH, 6.0, AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.FEET, "armor_health_feet")
                )
        );
        /*
        // ──────────────────── 单个材质模板 ────────────────────
        register("material_name",
                entries(
                        // 工具额外属性
                        attr(Attributes.XXX, 数值, AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.MAINHAND, "唯一后缀")
                ),
                entries(
                // 头盔额外属性
                        attr(Attributes.YYY, 数值, AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.HEAD, "唯一后缀")
                ),
                entries(
                // 胸甲额外属性
                        attr(Attributes.ZZZ, 数值, AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.CHEST, "唯一后缀")
                ),
                entries(
                // 护腿额外属性
                ),
                entries(
                // 靴子额外属性
                ));
        */
    }
}