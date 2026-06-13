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
        register("netherite_redstone",
                entries(
                        attr(Attributes.MINING_EFFICIENCY, 1.0, AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.MAINHAND, "block_range"),
                        attr(Attributes.ATTACK_SPEED, 0.1, AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.MAINHAND, "entity_range")
                ),
                entries(),
                entries(),
                entries(),
                entries());
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
        register("wither",
                entries(
                        attr(Attributes.BLOCK_INTERACTION_RANGE, 0.5, AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.MAINHAND, "block_range"),
                        attr(Attributes.ENTITY_INTERACTION_RANGE, 0.5, AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.MAINHAND, "entity_range")
                ),
                entries(
                        // 头盔额外属性
                        attr(Attributes.MAX_HEALTH, 5.0, AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.HEAD, "armor_health_head")
                ),
                entries(
                        // 胸甲额外属性
                        attr(Attributes.MAX_HEALTH, 5.0, AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.CHEST, "armor_health_chest")
                ),
                entries(
                        // 护腿额外属性
                        attr(Attributes.MAX_HEALTH, 5.0, AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.LEGS, "armor_health_legs")
                ),
                entries(
                        // 靴子额外属性
                        attr(Attributes.MAX_HEALTH, 5.0, AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.FEET, "armor_health_feet")
                ));
        register("super_netherite",
                entries(),
                entries(attr(Attributes.MAX_HEALTH, 2.0, AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.HEAD, "armor_health_head")
                ),
                entries(attr(Attributes.MAX_HEALTH, 2.0, AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.CHEST, "armor_health_chest")
                ),
                entries(attr(Attributes.MAX_HEALTH, 2.0, AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.LEGS, "armor_health_feet")
                ),
                entries(attr(Attributes.MAX_HEALTH, 2.0, AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.FEET, "armor_health_feet")
                ));
        register("dragon",
                entries(),  // 工具属性（如果有）
                entries(
                        attr(Attributes.MAX_HEALTH, 5.0, AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.HEAD, "health"),
                        attr(Attributes.ATTACK_DAMAGE, 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, EquipmentSlotGroup.HEAD, "attack_damage")
                ),
                entries(
                        attr(Attributes.MAX_HEALTH, 5.0, AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.CHEST, "health"),
                        attr(Attributes.ATTACK_DAMAGE, 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, EquipmentSlotGroup.CHEST, "attack_damage")
                ),
                entries(
                        attr(Attributes.MAX_HEALTH, 5.0, AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.LEGS, "health"),
                        attr(Attributes.ATTACK_DAMAGE, 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, EquipmentSlotGroup.LEGS, "attack_damage")
                ),
                entries(
                        attr(Attributes.MAX_HEALTH, 5.0, AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.FEET, "health"),
                        attr(Attributes.ATTACK_DAMAGE, 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, EquipmentSlotGroup.FEET, "attack_damage")
                )
        );
        register("nether_star",
                entries(),
                entries(
                        attr(Attributes.MAX_HEALTH, 5.0, AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.HEAD, "health")
                ),
                entries(
                        attr(Attributes.MAX_HEALTH, 5.0, AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.CHEST, "health")
                ),
                entries(
                        attr(Attributes.MAX_HEALTH, 5.0, AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.LEGS, "health")
                ),
                entries(
                        attr(Attributes.MAX_HEALTH, 5.0, AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.FEET, "health")
                )
        );
        register("crying_obsidian",
                entries(),
                entries(
                        attr(Attributes.MAX_HEALTH, 0.075, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, EquipmentSlotGroup.HEAD, "health"),
                        attr(Attributes.MOVEMENT_SPEED, -0.025, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, EquipmentSlotGroup.HEAD, "speed")
                ),
                entries(
                        attr(Attributes.MAX_HEALTH, 0.075, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, EquipmentSlotGroup.CHEST, "health"),
                        attr(Attributes.MOVEMENT_SPEED, -0.025, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, EquipmentSlotGroup.CHEST, "speed")
                ),
                entries(
                        attr(Attributes.MAX_HEALTH, 0.075, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, EquipmentSlotGroup.LEGS, "health"),
                        attr(Attributes.MOVEMENT_SPEED, -0.025, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, EquipmentSlotGroup.LEGS, "speed")
                ),
                entries(
                        attr(Attributes.MAX_HEALTH, 0.075, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, EquipmentSlotGroup.FEET, "health"),
                        attr(Attributes.MOVEMENT_SPEED, -0.025, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, EquipmentSlotGroup.FEET, "speed")
                )
        );
        // ──────────────────── 奇异紫水晶 ────────────────────
        register("uncanny_amethyst",
                entries(
                        attr(Attributes.BLOCK_INTERACTION_RANGE, 1.5, AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.MAINHAND, "block_range"),
                        attr(Attributes.ENTITY_INTERACTION_RANGE, 1.5, AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.MAINHAND, "entity_range")
                ),
                entries(
                        attr(Attributes.MAX_HEALTH, 9.0, AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.HEAD, "armor_health_head")
                ),
                entries(
                        attr(Attributes.MAX_HEALTH, 9.0, AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.CHEST, "armor_health_chest")
                ),
                entries(
                        attr(Attributes.MAX_HEALTH, 9.0, AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.LEGS, "armor_health_legs")
                ),
                entries(
                        attr(Attributes.MAX_HEALTH, 9.0, AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.FEET, "armor_health_feet")
                )
        );
        register("thunder_copper",
                entries(
                        attr(MutModAttributes.THUNDER_POWER,20,AttributeModifier.Operation.ADD_VALUE,EquipmentSlotGroup.MAINHAND,"hand")
                ),entries(
                        attr(MutModAttributes.THUNDER_POWER,20,AttributeModifier.Operation.ADD_VALUE,EquipmentSlotGroup.HEAD,"head"),
                        attr(Attributes.MAX_HEALTH,4,AttributeModifier.Operation.ADD_VALUE,EquipmentSlotGroup.HEAD,"armor_health_head")
                ),entries(
                        attr(MutModAttributes.THUNDER_POWER,20,AttributeModifier.Operation.ADD_VALUE,EquipmentSlotGroup.CHEST,"chest"),
                        attr(Attributes.MAX_HEALTH,4,AttributeModifier.Operation.ADD_VALUE,EquipmentSlotGroup.CHEST,"armor_health_chest")
                ),entries(
                        attr(MutModAttributes.THUNDER_POWER,20,AttributeModifier.Operation.ADD_VALUE,EquipmentSlotGroup.LEGS,"legs"),
                        attr(Attributes.MAX_HEALTH,4,AttributeModifier.Operation.ADD_VALUE,EquipmentSlotGroup.LEGS,"armor_health_legs")
                ),entries(
                        attr(MutModAttributes.THUNDER_POWER,20,AttributeModifier.Operation.ADD_VALUE,EquipmentSlotGroup.FEET,"feet"),
                        attr(Attributes.MAX_HEALTH,4,AttributeModifier.Operation.ADD_VALUE,EquipmentSlotGroup.FEET,"armor_health_feet")
                ));
        register("echoite",
                entries(
                        attr(Attributes.ENTITY_INTERACTION_RANGE, 1.0, AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.MAINHAND, "entity_range")
                ),entries(
                        attr(Attributes.MAX_HEALTH,5,AttributeModifier.Operation.ADD_VALUE,EquipmentSlotGroup.HEAD,"head")
                ),entries(
                        attr(Attributes.MAX_HEALTH,5,AttributeModifier.Operation.ADD_VALUE,EquipmentSlotGroup.CHEST,"chest")
                ),entries(
                        attr(Attributes.MAX_HEALTH,5,AttributeModifier.Operation.ADD_VALUE,EquipmentSlotGroup.LEGS,"legs")
                ),entries(
                        attr(Attributes.MAX_HEALTH,5,AttributeModifier.Operation.ADD_VALUE,EquipmentSlotGroup.FEET,"feet")
                ));
        register("poison_steel",
                entries(
                        attr(Attributes.ENTITY_INTERACTION_RANGE, 1.0, AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.MAINHAND, "entity_range")
                ),entries(
                        attr(Attributes.MAX_HEALTH,4,AttributeModifier.Operation.ADD_VALUE,EquipmentSlotGroup.HEAD,"head")
                ),entries(
                        attr(Attributes.MAX_HEALTH,4,AttributeModifier.Operation.ADD_VALUE,EquipmentSlotGroup.CHEST,"chest")
                ),entries(
                        attr(Attributes.MAX_HEALTH,4,AttributeModifier.Operation.ADD_VALUE,EquipmentSlotGroup.LEGS,"legs")
                ),entries(
                        attr(Attributes.MAX_HEALTH,4,AttributeModifier.Operation.ADD_VALUE,EquipmentSlotGroup.FEET,"feet")
                ));
        register("position_steel",
                entries(
                        attr(Attributes.ENTITY_INTERACTION_RANGE, 1.0, AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.MAINHAND, "entity_range")
                ),entries(
                        attr(Attributes.MAX_HEALTH,4,AttributeModifier.Operation.ADD_VALUE,EquipmentSlotGroup.HEAD,"head")
                ),entries(
                        attr(Attributes.MAX_HEALTH,4,AttributeModifier.Operation.ADD_VALUE,EquipmentSlotGroup.CHEST,"chest")
                ),entries(
                        attr(Attributes.MAX_HEALTH,4,AttributeModifier.Operation.ADD_VALUE,EquipmentSlotGroup.LEGS,"legs")
                ),entries(
                        attr(Attributes.MAX_HEALTH,4,AttributeModifier.Operation.ADD_VALUE,EquipmentSlotGroup.FEET,"feet")
                ));
        register("flame_gold",
                entries(
                        attr(Attributes.ENTITY_INTERACTION_RANGE, 1.0, AttributeModifier.Operation.ADD_VALUE, EquipmentSlotGroup.MAINHAND, "entity_range")
                ),entries(
                        attr(Attributes.MAX_HEALTH,4,AttributeModifier.Operation.ADD_VALUE,EquipmentSlotGroup.HEAD,"head")
                ),entries(
                        attr(Attributes.MAX_HEALTH,4,AttributeModifier.Operation.ADD_VALUE,EquipmentSlotGroup.CHEST,"chest")
                ),entries(
                        attr(Attributes.MAX_HEALTH,4,AttributeModifier.Operation.ADD_VALUE,EquipmentSlotGroup.LEGS,"legs")
                ),entries(
                        attr(Attributes.MAX_HEALTH,4,AttributeModifier.Operation.ADD_VALUE,EquipmentSlotGroup.FEET,"feet")
                ));
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