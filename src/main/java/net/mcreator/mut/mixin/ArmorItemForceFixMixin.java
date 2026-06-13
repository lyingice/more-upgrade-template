package net.mcreator.mut.mixin;

import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArmorItem.class)
public class ArmorItemForceFixMixin {

    @Inject(method = "getDefaultAttributeModifiers", at = @At("HEAD"), cancellable = true)
    private void forceFixArmorModifiers(CallbackInfoReturnable<ItemAttributeModifiers> cir) {
        ArmorItem self = (ArmorItem) (Object) this;
        ArmorMaterial material = self.getMaterial().value();
        ArmorItem.Type type = self.getType();

        // 直接从 defense map 获取护甲值
        int defense = material.defense().getOrDefault(type, 0);

        // 如果护甲值为0，不处理（可能是非护甲物品）
        if (defense == 0) return;

        // 构建正确的属性修饰器
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();

        // 添加护甲值
        builder.add(Attributes.ARMOR,
                new AttributeModifier(
                        ResourceLocation.parse("minecraft:armor"),
                        defense,
                        AttributeModifier.Operation.ADD_VALUE
                ),
                EquipmentSlotGroup.bySlot(type.getSlot()));

        // 添加护甲韧性
        float toughness = material.toughness();
        if (toughness > 0) {
            builder.add(Attributes.ARMOR_TOUGHNESS,
                    new AttributeModifier(
                            ResourceLocation.parse("minecraft:armor_toughness"),
                            toughness,
                            AttributeModifier.Operation.ADD_VALUE
                    ),
                    EquipmentSlotGroup.bySlot(type.getSlot()));
        }

        // 添加击退抗性
        float knockbackRes = material.knockbackResistance();
        if (knockbackRes > 0) {
            builder.add(Attributes.KNOCKBACK_RESISTANCE,
                    new AttributeModifier(
                            ResourceLocation.parse("minecraft:knockback_resistance"),
                            knockbackRes,
                            AttributeModifier.Operation.ADD_VALUE
                    ),
                    EquipmentSlotGroup.bySlot(type.getSlot()));
        }

        // 替换返回值
        cir.setReturnValue(builder.build());
    }
}