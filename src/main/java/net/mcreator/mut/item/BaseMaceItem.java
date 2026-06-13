package net.mcreator.mut.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.resources.ResourceLocation;

import net.mcreator.mut.init.MutMaceStats;
import net.mcreator.mut.init.MutMoreAttributeMaterials;
import net.mcreator.mut.MutMod;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;
import java.util.function.Supplier;

/**
 * 重锤基类 - 所有自定义重锤应继承此类
 * 风格与 NewCrossbowItem 保持一致
 */
public abstract class BaseMaceItem extends MaceItem {

    private final Supplier<Item> selfItemSupplier;

    // 原构造：没词条的调用
    protected BaseMaceItem(MutMaceStats.Stats stats) {
        super(buildProperties(stats));
        this.selfItemSupplier = () -> this;
        MutMaceStats.register(this, stats);
    }

    // 双参构造：有词条的调用
    protected BaseMaceItem(MutMaceStats.Stats stats, Properties customProps) {
        super(customProps);
        this.selfItemSupplier = () -> this;
        MutMaceStats.register(this, stats);
    }

    // ==================== 属性构建 ====================

    private static Properties buildProperties(MutMaceStats.Stats stats) {
        Properties props = new Properties()
                .stacksTo(1)
                .durability(stats.durability())
                .rarity(stats.rarity());
        if (stats.fireResistant()) {
            props.fireResistant();
        }
        return props;
    }


    /** 从 Stats 创建基础 Properties，并附带词条数据 */
    protected static Properties buildPropertiesWithAffix(MutMaceStats.Stats stats, CustomData affixData) {
        Properties props = buildProperties(stats);
        props.component(DataComponents.CUSTOM_DATA, affixData);
        return props;
    }

    private Item getSelfItem() {
        return selfItemSupplier.get();
    }

    private static ItemAttributeModifiers buildAttributeModifiers(MutMaceStats.Stats stats) {
        var builder = ItemAttributeModifiers.builder();

        // 基础攻击伤害
        builder.add(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(BASE_ATTACK_DAMAGE_ID,
                        stats.baseDamage(),
                        AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND);

        // 基础攻击速度
        builder.add(Attributes.ATTACK_SPEED,
                new AttributeModifier(BASE_ATTACK_SPEED_ID,
                        stats.getAttackSpeedModifier(),
                        AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND);

        // 从 MutMoreAttributeMaterials 获取额外工具属性
        List<MutMoreAttributeMaterials.AttributeEntry> extraAttributes =
                MutMoreAttributeMaterials.getToolAttributes(stats.materialName());

        for (MutMoreAttributeMaterials.AttributeEntry entry : extraAttributes) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(
                    MutMod.MODID,
                    stats.materialName() + "_" + entry.suffixId()
            );
            builder.add(entry.attribute(),
                    new AttributeModifier(id, entry.amount(), entry.operation()),
                    entry.slotGroup());
        }

        return builder.build();
    }
    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        return buildAttributeModifiers(MutMaceStats.get(getSelfItem()));
    }

    // ==================== 附魔能力 ====================

    @Override
    public int getEnchantmentValue() {
        return MutMaceStats.enchantmentValue(getSelfItem());
    }

    // ==================== 修复 ====================

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return MutMaceStats.repairIngredient(getSelfItem()).test(repair);
    }

    // ==================== 下落攻击 ====================

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof ServerPlayer serverplayer && canSmashAttack(serverplayer)) {
            performSmashAttack(serverplayer, target);
        }
        return true;
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
        if (canSmashAttack(attacker)) {
            attacker.resetFallDistance();
        }
    }

    @Override
    public float getAttackDamageBonus(Entity target, float damage, DamageSource source) {
        Entity directEntity = source.getDirectEntity();
        if (!(directEntity instanceof LivingEntity livingentity) || !canSmashAttack(livingentity)) {
            return 0.0F;
        }

        float fallDistance = livingentity.fallDistance;
        float baseDamage = calculateFallDamage(fallDistance);
        baseDamage *= MutMaceStats.fallDamageMultiplier(getSelfItem());

        if (livingentity.level() instanceof ServerLevel serverlevel) {
            baseDamage += EnchantmentHelper.modifyFallBasedDamage(serverlevel,
                    livingentity.getWeaponItem(), target, source, 0.0F) * fallDistance;
        }

        return baseDamage;
    }

    private float calculateFallDamage(float fallDistance) {
        if (fallDistance <= 3.0F) {
            return 4.0F * fallDistance;
        } else if (fallDistance <= 8.0F) {
            return 12.0F + 2.0F * (fallDistance - 3.0F);
        } else {
            return 22.0F + fallDistance - 8.0F;
        }
    }

    private void performSmashAttack(ServerPlayer attacker, LivingEntity target) {
        ServerLevel level = (ServerLevel) attacker.level();

        // 记录冲击位置
        if (attacker.isIgnoringFallDamageFromCurrentImpulse() && attacker.currentImpulseImpactPos != null) {
            if (attacker.currentImpulseImpactPos.y > attacker.position().y) {
                attacker.currentImpulseImpactPos = attacker.position();
            }
        } else {
            attacker.currentImpulseImpactPos = attacker.position();
        }

        attacker.setIgnoreFallDamageFromCurrentImpulse(true);
        attacker.setDeltaMovement(attacker.getDeltaMovement().with(Direction.Axis.Y, 0.1F));
        attacker.connection.send(new ClientboundSetEntityMotionPacket(attacker));

        // 播放音效
        if (target.onGround()) {
            attacker.setSpawnExtraParticlesOnFall(true);
            level.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(),
                    attacker.fallDistance > 5.0F ? SoundEvents.MACE_SMASH_GROUND_HEAVY : SoundEvents.MACE_SMASH_GROUND,
                    attacker.getSoundSource(), 1.0F, 1.0F);
        } else {
            level.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(),
                    SoundEvents.MACE_SMASH_AIR, attacker.getSoundSource(), 1.0F, 1.0F);
        }

        // 击退效果
        knockback(level, attacker, target);
    }

    private static void knockback(ServerLevel level, Player player, Entity target) {
        level.levelEvent(2013, target.getOnPos(), 750);

        double radius = 3.5;
        level.getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(radius),
                        e -> !e.isSpectator() && e != player && e != target && !player.isAlliedTo(e)
                                && target.distanceToSqr(e) <= Math.pow(radius, 2))
                .forEach(entity -> {
                    Vec3 vec3 = entity.position().subtract(target.position());
                    double distance = vec3.length();
                    if (distance < radius) {
                        double power = (radius - distance) * 0.7 * (player.fallDistance > 5.0F ? 2 : 1);
                        power *= (1.0 - entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                        Vec3 knockbackVec = vec3.normalize().scale(power);
                        entity.push(knockbackVec.x, 0.7, knockbackVec.z);
                        if (entity instanceof ServerPlayer sp) {
                            sp.connection.send(new ClientboundSetEntityMotionPacket(sp));
                        }
                    }
                });
    }

    // ==================== Tooltip（可选） ====================
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flag) {
        MutMaceStats.Stats stats = MutMaceStats.get(getSelfItem());
        list.add(Component.translatable("item.mut.mace.fall_damage",
                        String.format("%.2f", stats.fallDamageMultiplier() * 100))
                .withStyle(ChatFormatting.GRAY));
    }
    public static class ObsidianMace extends BaseMaceItem {
        public ObsidianMace() {
            super(MutMaceStats.OBSIDIAN);
        }
    }

    public static class NetheriteObsidianMace extends BaseMaceItem {
        public NetheriteObsidianMace() {
            super(MutMaceStats.NETHERITE_OBSIDIAN);
        }
    }

    public static class CryingObsidianMace extends BaseMaceItem {
        public CryingObsidianMace() {
            super(MutMaceStats.CRYING_OBSIDIAN);
        }
    }

    public static class NetherStarMace extends BaseMaceItem {
        public NetherStarMace() {
            super(MutMaceStats.NETHER_STAR);
        }
        @Override
        @OnlyIn(Dist.CLIENT)
        public boolean isFoil(ItemStack itemstack) {
            return true;
        }

    }

    public static class WitherMace extends BaseMaceItem {
        public WitherMace() {
            super(MutMaceStats.WITHER, buildPropertiesWithAffix(MutMaceStats.WITHER, createWitherMarkData()));
        }
        private static CustomData createWitherMarkData() {
            CompoundTag tag = new CompoundTag();
            tag.putString("Affix", "wither_mark");
            tag.putInt("AffixLevel", 3);
            return CustomData.of(tag);
        }
    }

    public static class DragonMace extends BaseMaceItem {
        public DragonMace() {
            super(MutMaceStats.DRAGON);
        }
    }
    public static class NetheriteCopperMace extends BaseMaceItem {
        public NetheriteCopperMace() {
            super(MutMaceStats.NETHERITE_COPPER);
        }
    }

    public static class NetheriteEmeraldMace extends BaseMaceItem {
        public NetheriteEmeraldMace() {
            super(MutMaceStats.NETHERITE_EMERALD);
        }
    }

    public static class NetheriteRedstoneMace extends BaseMaceItem {
        public NetheriteRedstoneMace() {
            super(MutMaceStats.NETHERITE_REDSTONE);
        }
    }

    public static class NetheriteAmethystMace extends BaseMaceItem {
        public NetheriteAmethystMace() {
            super(MutMaceStats.NETHERITE_AMETHYST);
        }
    }
    public static class EmeraldMace extends BaseMaceItem {
        public EmeraldMace() {
            super(MutMaceStats.EMERALD);
        }
    }

    public static class AmethystMace extends BaseMaceItem {
        public AmethystMace() {
            super(MutMaceStats.AMETHYST);
        }
    }
    public static class LapisLazuliMaceItem extends BaseMaceItem {
        public LapisLazuliMaceItem() {
            super(MutMaceStats.LAPIS_LAZULI);}}
    public static class NetheriteLapisLazuliMaceItem extends BaseMaceItem {
        public NetheriteLapisLazuliMaceItem() {
            super(MutMaceStats.NETHERITE_LAPIS_LAZULI);}}
    public static class PoisonSteelMaceItem extends BaseMaceItem {
        public PoisonSteelMaceItem() {
            super(MutMaceStats.POISON_STEEL,buildPropertiesWithAffix(MutMaceStats.POISON_STEEL,createAffixData()
            ));}
        private static CustomData createAffixData() {
            CompoundTag tag = new CompoundTag();
            tag.putString("Affix", "poison_mark"
            );
            tag.putInt("AffixLevel", 3);
            return CustomData.of(tag);}
}
    public static class FlameGoldMaceItem extends BaseMaceItem {
        public FlameGoldMaceItem() {
            super(MutMaceStats.FLAME_GOLD,buildPropertiesWithAffix(MutMaceStats.FLAME_GOLD,createAffixData()));}private static CustomData createAffixData() {
            CompoundTag tag = new CompoundTag();
            tag.putString("Affix", "fire_mark");
            tag.putInt("AffixLevel", 3);
            return CustomData.of(tag);}}
    public static class EchoiteMaceItem extends BaseMaceItem implements ISonicBoomSword{
        public EchoiteMaceItem() {
            super(MutMaceStats.ECHOITE);}
        @Override
        public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
            return useSonicBoom(level, player, hand);
        }}
    public static class ThunderCopperMaceItem extends BaseMaceItem {
        public ThunderCopperMaceItem() {
            super(MutMaceStats.THUNDER_COPPER);}}
    public static class UncannyAmethystMaceItem extends BaseMaceItem {
        public UncannyAmethystMaceItem() {
            super(MutMaceStats.UNCANNY_AMETHYST,buildPropertiesWithAffix(MutMaceStats.UNCANNY_AMETHYST,createAffixData()));}private static CustomData createAffixData() {
            CompoundTag tag = new CompoundTag();
            tag.putString("Affix", "regeneration_mark");
            tag.putInt("AffixLevel", 3);
            return CustomData.of(tag);}
    }
    /**public static class XxMace extends BaseMaceItem {
     public XxMace() {
     super(MutMaceStats.XX);}}
     ,buildPropertiesWithAffix(MutMaceStats.X,createAffixData())
     **/
}