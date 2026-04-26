package net.mcreator.mut.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.extensions.IItemExtension;

public class GoldenElytraItem extends Item implements IItemExtension {
    
    public GoldenElytraItem() {
        super(new Item.Properties()
            .durability(682)
            .rarity(Rarity.UNCOMMON)
        );
    }
    
    // 允许鞘翅飞行
    public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
        return true;
    }
    
    // 飞行时消耗耐久（每20 tick消耗1点）
    public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
        if (!entity.level().isClientSide && (flightTicks + 1) % 20 == 0) {
            stack.hurtAndBreak(1, entity, EquipmentSlot.CHEST);
        }
        return true;
    }
    
    // 指定穿戴槽位为胸甲
    public EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EquipmentSlot.CHEST;
    }
    
    // 自定义纹理
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return "mut:textures/entities/golden_elytra.png";
    }
    
    // 修复材料（金锭）
    @Override
    public boolean isValidRepairItem(ItemStack itemstack, ItemStack repairitem) {
        return repairitem.is(Items.GOLD_INGOT);
    }
}