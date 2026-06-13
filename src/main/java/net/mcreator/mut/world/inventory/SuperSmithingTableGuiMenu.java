package net.mcreator.mut.world.inventory;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.inventory.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.ItemTags;
import net.minecraft.resources.ResourceLocation;

import net.mcreator.mut.init.MutModMenus;
import net.mcreator.mut.init.MutModItems;
import net.mcreator.mut.init.MutModBlocks;
import net.mcreator.mut.affix.data.MaterialContext;
import net.mcreator.mut.affix.data.MaterialBonusRegistry;

import javax.annotation.Nullable;

public class SuperSmithingTableGuiMenu extends ItemCombinerMenu {

    private static final ResourceLocation AFFIX_MATERIAL_TAG =
            ResourceLocation.fromNamespaceAndPath("mut", "affix_material");

    private final Level level;

    public SuperSmithingTableGuiMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        super(MutModMenus.SUPER_SMITHING_TABLE_GUI.get(), id, inv,
                extraData == null ? ContainerLevelAccess.NULL
                        : ContainerLevelAccess.create(inv.player.level(), extraData.readBlockPos()));
        this.level = inv.player.level();
    }

    public SuperSmithingTableGuiMenu(int id, Inventory inv, ContainerLevelAccess access) {
        super(MutModMenus.SUPER_SMITHING_TABLE_GUI.get(), id, inv, access);
        this.level = inv.player.level();
    }

    // ========== 槽位定义 ==========

    @Override
    protected ItemCombinerMenuSlotDefinition createInputSlotDefinitions() {
        return ItemCombinerMenuSlotDefinition.create()
                // 槽位0：只能放超级升级锻造模板
                .withSlot(0, 16, 35, stack -> stack.getItem() == MutModItems.SUPER_UPGRADE_SMITHING_TEMPLATE.get())
                // 槽位1：任何可附魔的物品
                .withSlot(1, 43, 35, stack -> isEnchantable(stack))
                // 槽位2：标签驱动 - 任何在 #mut:affix_material 标签中的物品
                .withSlot(2, 70, 35, stack -> stack.is(ItemTags.create(AFFIX_MATERIAL_TAG)))
                // 槽位3：输出
                .withResultSlot(3, 115, 35)
                .build();
    }

    /**
     * 判断物品是否属于可附魔类别
     */
    private boolean isEnchantable(ItemStack stack) {
        return stack.is(net.minecraft.tags.ItemTags.create(
                net.minecraft.resources.ResourceLocation.parse("minecraft:enchantable/weapon")))
                || stack.is(net.minecraft.tags.ItemTags.create(
                net.minecraft.resources.ResourceLocation.parse("minecraft:enchantable/mining")))
                || stack.is(net.minecraft.tags.ItemTags.create(
                net.minecraft.resources.ResourceLocation.parse("minecraft:enchantable/armor")))
                || stack.is(net.minecraft.tags.ItemTags.create(
                net.minecraft.resources.ResourceLocation.parse("minecraft:enchantable/bow")))
                || stack.is(net.minecraft.tags.ItemTags.create(
                net.minecraft.resources.ResourceLocation.parse("minecraft:enchantable/crossbow")))
                || stack.is(net.minecraft.tags.ItemTags.create(
                net.minecraft.resources.ResourceLocation.parse("minecraft:enchantable/trident")))
                || stack.is(net.minecraft.tags.ItemTags.create(
                net.minecraft.resources.ResourceLocation.parse("minecraft:enchantable/equippable")))
                || stack.is(net.minecraft.tags.ItemTags.create(
                net.minecraft.resources.ResourceLocation.parse("minecraft:enchantable/food")))
                || stack.is(net.minecraft.tags.ItemTags.create(
                net.minecraft.resources.ResourceLocation.parse("mut:affixable")));
    }

    // ========== 预览生成 ==========

    @Override
    public void createResult() {
        ItemStack template = this.inputSlots.getItem(0);
        ItemStack base = this.inputSlots.getItem(1);
        ItemStack addition = this.inputSlots.getItem(2);

        // 三样齐全 → 生成预览
        if (!template.isEmpty() && !base.isEmpty() && !addition.isEmpty()
                && template.getItem() == MutModItems.SUPER_UPGRADE_SMITHING_TEMPLATE.get()
                && addition.is(ItemTags.create(AFFIX_MATERIAL_TAG))
                && isEnchantable(base)) {

            ItemStack result = base.copyWithCount(1);
            this.resultSlots.setItem(0, result);
        } else {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
        }
    }

    // ========== 取出判定 ==========

    @Override
    protected boolean mayPickup(Player player, boolean slotHasItem) {
        ItemStack template = this.inputSlots.getItem(0);
        ItemStack base = this.inputSlots.getItem(1);
        ItemStack addition = this.inputSlots.getItem(2);

        return !template.isEmpty() && !base.isEmpty() && !addition.isEmpty()
                && template.getItem() == MutModItems.SUPER_UPGRADE_SMITHING_TEMPLATE.get()
                && addition.is(ItemTags.create(AFFIX_MATERIAL_TAG))
                && isEnchantable(base);
    }

    // ========== 取出后消耗 + 词缀 ==========

    @Override
    protected void onTake(Player player, ItemStack stack) {
        // 获取材料上下文（槽位2中的材料）
        ItemStack addition = this.inputSlots.getItem(2);
        MaterialContext materialCtx = MaterialBonusRegistry.getInstance().evaluate(addition);

        // 带材料加成赋予词缀
        net.mcreator.mut.procedures.AddRandomAffixProcedure.execute(stack, materialCtx);

        stack.onCraftedBy(player.level(), player, stack.getCount());

        // 消耗材料
        this.inputSlots.getItem(0).shrink(1);
        this.inputSlots.getItem(1).shrink(1);
        this.inputSlots.getItem(2).shrink(1);

        // 刷新预览
        createResult();
        broadcastChanges();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            // 如果是从输出槽(槽位3) shift 取出
            if (index == 3) {
                // 获取材料上下文 + 加词缀
                ItemStack addition = this.inputSlots.getItem(2);
                MaterialContext materialCtx = MaterialBonusRegistry.getInstance().evaluate(addition);
                net.mcreator.mut.procedures.AddRandomAffixProcedure.execute(itemstack1, materialCtx);

                if (!this.moveItemStackTo(itemstack1, 4, 40, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack1, itemstack);

                // 消耗材料
                this.inputSlots.getItem(0).shrink(1);
                this.inputSlots.getItem(1).shrink(1);
                this.inputSlots.getItem(2).shrink(1);

                createResult();
                broadcastChanges();
            } else if (index >= 4 && index < 40) {
                // 从玩家背包移入输入槽
                if (!this.moveItemStackTo(itemstack1, 0, 3, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 0 && index < 3) {
                // 从输入槽移出
                if (!this.moveItemStackTo(itemstack1, 4, 40, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
        }
        return itemstack;
    }

    // ========== 方块有效性 ==========

    @Override
    protected boolean isValidBlock(BlockState state) {
        return state.is(MutModBlocks.SUPER_SMITHING_TABLE.get());
    }

    // ========== 玩家距离检测 ==========

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, MutModBlocks.SUPER_SMITHING_TABLE.get());
    }
}
