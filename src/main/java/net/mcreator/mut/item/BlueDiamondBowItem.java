package net.mcreator.mut.item;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.*;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ArrowLooseEvent;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Items;

public class BlueDiamondBowItem extends BowItem {
    public BlueDiamondBowItem() {
        super(new Item.Properties()
                .stacksTo(1)
                .durability(1400)
                .rarity(Rarity.COMMON)
                .component(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY));
    }

    @Override
    public UseAnim getUseAnimation(ItemStack itemstack) {
        return UseAnim.BOW;
    }

    @Override
    public int getEnchantmentValue() {
        return 1;
    }

    @Override
    public int getUseDuration(ItemStack itemstack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack bowStack = player.getItemInHand(hand);
        boolean hasAmmo = !player.getProjectile(bowStack).isEmpty() || player.getAbilities().instabuild;

        if (!hasAmmo) {
            return InteractionResultHolder.fail(bowStack);
        } else {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(bowStack);
        }
    }

    @Override
public void releaseUsing(ItemStack bowStack, Level world, LivingEntity shooter, int remainingUseTicks) {
    if (shooter instanceof Player player) {
        int useTime = this.getUseDuration(bowStack, shooter) - remainingUseTicks;
        //draw time
        float pullPower;
        if (useTime >= 15) {
            pullPower = 1.0F;
        } else {
            pullPower = (float)useTime / 15.0F;
        }
        
        if (pullPower > 1.0F) {
            pullPower = 1.0F;
        }

        if (pullPower < 0.1F) {
            return;
        }

            // find ammo
            ItemStack ammoStack = player.getProjectile(bowStack);
            boolean hasAmmo = !ammoStack.isEmpty() || player.getAbilities().instabuild;
            
            if (!world.isClientSide) {
                ArrowLooseEvent event = new ArrowLooseEvent(player, bowStack, world, useTime, hasAmmo);
                if (NeoForge.EVENT_BUS.post(event).isCanceled()) {
                    return;
                }
                useTime = event.getCharge();
            }

            boolean isCreative = player.getAbilities().instabuild;

            if (hasAmmo) {
                Arrow arrow = new Arrow(world, player, bowStack, ammoStack);
                arrow.setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());
                arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, pullPower * 3.15F, 1.0F);

                if (pullPower >= 1.0F) {
                    arrow.setCritArrow(true);
                }


                arrow.setBaseDamage(3.5);  
             

                if (!world.isClientSide && player instanceof ServerPlayer serverPlayer) {
                    applyEnchantmentEffects(arrow, bowStack, serverPlayer);
                }

                if (isCreative) {
                    arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                } else {
                    arrow.pickup = AbstractArrow.Pickup.ALLOWED;
                }

                world.addFreshEntity(arrow);

                world.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(),
                        SoundEvents.ARROW_SHOOT,
                        SoundSource.PLAYERS, 1.0F, 1.0F / (world.random.nextFloat() * 0.5F + 1.0F));

              
                bowStack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);

                // not creative
                if (!isCreative && !ammoStack.isEmpty()) {
                    ammoStack.shrink(1);
                }
            }
        }
    }

    private void applyEnchantmentEffects(Arrow arrow, ItemStack bowStack, ServerPlayer player) {
        Holder<Enchantment> powerEnchant = player.registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(Enchantments.POWER);
        
        Holder<Enchantment> punchEnchant = player.registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(Enchantments.PUNCH);
        
        Holder<Enchantment> flameEnchant = player.registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(Enchantments.FLAME);

        int powerLevel = EnchantmentHelper.getItemEnchantmentLevel(powerEnchant, bowStack);
        if (powerLevel > 0) {
            arrow.setBaseDamage(arrow.getBaseDamage() + (double) powerLevel * 0.5D + 0.5D);
        }

        int punchLevel = EnchantmentHelper.getItemEnchantmentLevel(punchEnchant, bowStack);
        if (punchLevel > 0) {
            arrow.hasImpulse = true; 
        }

        int flameLevel = EnchantmentHelper.getItemEnchantmentLevel(flameEnchant, bowStack);
        if (flameLevel > 0) {
            arrow.setRemainingFireTicks(100);
        }
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return (stack) -> stack.getItem() instanceof ArrowItem;
    }

    @Override
    public int getDefaultProjectileRange() {
        return 15;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack itemstack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(itemstack, context, list, flag);
        list.add(Component.translatable("item.mut.blue_diamond_bow.description_0"));
        list.add(Component.translatable("item.mut.blue_diamond_bow.description_1"));
    }

    @Override
    public boolean isValidRepairItem(ItemStack itemstack, ItemStack repairitem) {
        return Ingredient.of(new ItemStack(Items.IRON_INGOT)).test(repairitem);
    }
}