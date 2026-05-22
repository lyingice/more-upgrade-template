package net.mcreator.mut.entity;

import net.mcreator.mut.entity.ai.AdaptiveAttackGoals;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.Difficulty;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import java.util.EnumSet;
import net.mcreator.mut.init.MutModItems;
import net.mcreator.mut.init.MutModEntities;

import java.util.Arrays;

import static net.mcreator.mut.entity.ai.AdaptiveAttackGoals.hasTrident;

public class EliteAborigineZombieEntity extends Zombie implements CrossbowAttackMob {

    private static final EntityDataAccessor<Boolean> IS_CHARGING_CROSSBOW =
            SynchedEntityData.defineId(EliteAborigineZombieEntity.class, EntityDataSerializers.BOOLEAN);

    private boolean equipmentInitialized = false;

    // ========== 装备材质枚举 ==========
    private enum ArmorMaterial {
        NETHERITE(
                Items.NETHERITE_HELMET, Items.NETHERITE_CHESTPLATE,
                Items.NETHERITE_LEGGINGS, Items.NETHERITE_BOOTS,
                new Item[]{Items.NETHERITE_SWORD, Items.NETHERITE_AXE},
                new Item[]{MutModItems.NETHERITE_BOW.get(), MutModItems.NETHERITE_CROSSBOW.get()},
                MutModItems.NETHERITE_TRIDENT.get()
        ),
        ADVANCED_STEEL(
                MutModItems.ADVANCED_STEEL_HELMET.get(), MutModItems.ADVANCED_STEEL_CHESTPLATE.get(),
                MutModItems.ADVANCED_STEEL_LEGGINGS.get(), MutModItems.ADVANCED_STEEL_BOOTS.get(),
                new Item[]{MutModItems.ADVANCED_STEEL_SWORD.get(), MutModItems.ADVANCED_STEEL_AXE.get()},
                new Item[]{MutModItems.ADVANCED_STEEL_BOW.get(), MutModItems.ADVANCED_STEEL_CROSSBOW.get()},
                MutModItems.ADVANCED_STEEL_TRIDENT.get()
        ),
        STEEL(
                MutModItems.STEEL_HELMET.get(), MutModItems.STEEL_CHESTPLATE.get(),
                MutModItems.STEEL_LEGGINGS.get(), MutModItems.STEEL_BOOTS.get(),
                new Item[]{MutModItems.STEEL_SWORD.get(), MutModItems.STEEL_AXE.get()},
                new Item[]{MutModItems.STEEL_BOW.get(), MutModItems.STEEL_CROSSBOW.get()},
                MutModItems.STEEL_TRIDENT.get()
        ),
        NETHERITE_OBSIDIAN(
                MutModItems.NETHERITE_OBSIDIAN_HELMET.get(), MutModItems.NETHERITE_OBSIDIAN_CHESTPLATE.get(),
                MutModItems.NETHERITE_OBSIDIAN_LEGGINGS.get(), MutModItems.NETHERITE_OBSIDIAN_BOOTS.get(),
                new Item[]{MutModItems.NETHERITE_OBSIDIAN_SWORD.get(), MutModItems.NETHERITE_OBSIDIAN_AXE.get()},
                new Item[]{MutModItems.NETHERITE_OBSIDIAN_BOW.get(), MutModItems.NETHERITE_OBSIDIAN_CROSSBOW.get()},
                MutModItems.NETHERITE_OBSIDIAN_TRIDENT.get()
        );

        final Item helmet, chestplate, leggings, boots;
        final Item[] meleeWeapons;
        final Item[] rangedWeapons;
        final Item trident;

        ArmorMaterial(Item h, Item c, Item l, Item b, Item[] melee, Item[] ranged, Item trident) {
            this.helmet = h;
            this.chestplate = c;
            this.leggings = l;
            this.boots = b;
            this.meleeWeapons = melee;
            this.rangedWeapons = ranged;
            this.trident = trident;
        }
    }

    // ========== 构造函数 ==========
    public EliteAborigineZombieEntity(EntityType<EliteAborigineZombieEntity> type, Level world) {
        super(type, world);
        xpReward = 50;
        setNoAi(false);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_CHARGING_CROSSBOW, false);
    }

    // ========== CrossbowAttackMob 接口实现 ==========
    @Override
    public boolean canFireProjectileWeapon(ProjectileWeaponItem item) {
        return item instanceof BowItem || item instanceof CrossbowItem;
    }

    @Override
    public void setChargingCrossbow(boolean charging) {
        this.entityData.set(IS_CHARGING_CROSSBOW, charging);

    }
    public boolean isChargingCrossbow() {
        return this.entityData.get(IS_CHARGING_CROSSBOW);
    }

    @Override
    public void onCrossbowAttackPerformed() {
        this.noActionTime = 0;
    }


    @Override
    public void performRangedAttack(LivingEntity target, float power) {
        ItemStack offhand = this.getItemBySlot(EquipmentSlot.OFFHAND);
        ItemStack mainhand = this.getMainHandItem();

        // 三叉戟
        if (mainhand.getItem() instanceof TridentItem || offhand.getItem() instanceof TridentItem) {
            performTridentAttack(target, power, mainhand.getItem() instanceof TridentItem ? mainhand : offhand);
            return;
        }

        // 弩
        if (offhand.getItem() instanceof CrossbowItem || mainhand.getItem() instanceof CrossbowItem) {
            this.performCrossbowAttack(this, 1.6F);
            return;
        }

        // 弓
        if (offhand.getItem() instanceof BowItem || mainhand.getItem() instanceof BowItem) {
            performBowAttack(target, power, offhand.getItem() instanceof BowItem ? offhand : mainhand);
        }
    }
    // ========== 射击方法 ==========
    private void performBowAttack(LivingEntity target, float power, ItemStack bowStack) {
        Arrow arrow = new Arrow(this.level(), this, new ItemStack(Items.ARROW), bowStack);
        double dy = target.getEyeY() - this.getEyeY();
        double dx = target.getX() - this.getX();
        double dz = target.getZ() - this.getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);
        arrow.shoot(dx, dy + dist * 0.2, dz, 1.6F, (float)(14 - this.level().getDifficulty().getId() * 4));
        this.playSound(SoundEvents.ARROW_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level().addFreshEntity(arrow);
    }

    private void performTridentAttack(LivingEntity target, float power, ItemStack tridentStack) {
        MutThrownTrident trident = new MutThrownTrident(this.level(), this, tridentStack.copy());
        double dy = target.getEyeY() - this.getEyeY();
        double dx = target.getX() - this.getX();
        double dz = target.getZ() - this.getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);
        trident.shoot(dx, dy + dist * 0.2, dz, 1.6F, (float)(14 - this.level().getDifficulty().getId() * 4));
        this.playSound(SoundEvents.TRIDENT_THROW.value(), 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level().addFreshEntity(trident);
    }

    // ========== AI ==========
    private static final float RANGED_RANGE = 30.0F;
    private static final float MELEE_RANGE = 3.0F;


    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));

        // 1. 三叉戟（远近兼备，随机决定投掷还是近战）
        this.goalSelector.addGoal(1, AdaptiveAttackGoals.createTridentGoal(this, 1.0, RANGED_RANGE, MELEE_RANGE));
        // 2. 弩（默认远程）
        this.goalSelector.addGoal(2, AdaptiveAttackGoals.createCrossbowGoal(this, 1.0, RANGED_RANGE, MELEE_RANGE));

        // 3. 弓（有弩时不用弓）
        this.goalSelector.addGoal(3, AdaptiveAttackGoals.createBowGoal(this, 1.0, 20, RANGED_RANGE, MELEE_RANGE));

        // 4. 近战
        float meleeRangeSqr = MELEE_RANGE * MELEE_RANGE;
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.2, false) {
            @Override
            protected boolean canPerformAttack(LivingEntity entity) {
                return this.isTimeToAttack()
                        && this.mob.distanceToSqr(entity) < (this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth() * entity.getBbWidth())
                        && this.mob.getSensing().hasLineOfSight(entity);
            }
            @Override
            public boolean canUse() {
                if (!AdaptiveAttackGoals.isMeleeWeapon(EliteAborigineZombieEntity.this.getMainHandItem())) return false;
                // 三叉戟时由上面那个Goal管理，近战不插手
                if (hasTrident(EliteAborigineZombieEntity.this)) return false;
                LivingEntity target = EliteAborigineZombieEntity.this.getTarget();
                if (target == null) return false;
                return EliteAborigineZombieEntity.this.distanceToSqr(target) <= meleeRangeSqr;
            }
        });

        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 1));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Villager.class, false, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, IronGolem.class, false, true));
    }
    // ========== tick + 装备初始化 ==========
    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && !equipmentInitialized && this.isAlive()) {
            equipmentInitialized = true;
            initEquipment();
        }
    }

    private void initEquipment() {
        ArmorMaterial[] materials = ArmorMaterial.values();
        ArmorMaterial chosen = materials[this.random.nextInt(materials.length)];

        this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(chosen.helmet));
        this.setItemSlot(EquipmentSlot.CHEST, new ItemStack(chosen.chestplate));
        this.setItemSlot(EquipmentSlot.LEGS, new ItemStack(chosen.leggings));
        this.setItemSlot(EquipmentSlot.FEET, new ItemStack(chosen.boots));

        if (this.random.nextFloat() < 0.2F) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(chosen.trident));
            this.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
        } else {
            this.setItemSlot(EquipmentSlot.MAINHAND,
                    new ItemStack(chosen.meleeWeapons[this.random.nextInt(chosen.meleeWeapons.length)]));
            this.setItemSlot(EquipmentSlot.OFFHAND,
                    new ItemStack(chosen.rangedWeapons[this.random.nextInt(chosen.rangedWeapons.length)]));
        }
    }

    // ========== 音效 ==========
    @Override
    public SoundEvent getAmbientSound() {
        return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.zombie.ambient"));
    }

    @Override
    public SoundEvent getHurtSound(DamageSource ds) {
        return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.zombie.hurt"));
    }

    @Override
    public SoundEvent getDeathSound() {
        return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.zombie.death"));
    }
    @Override
    public void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.zombie.step")), 0.15f, 1);
    }

    @Override
    public Vec3 getPassengerRidingPosition(Entity entity) {
        return super.getPassengerRidingPosition(entity).add(0, -0.35F, 0);
    }

    // ========== 伤害免疫 ==========
    @Override
    public boolean hurt(DamageSource damagesource, float amount) {
        if (damagesource.is(net.minecraft.world.damagesource.DamageTypes.FALL)) return false;
        if (damagesource.is(net.minecraft.world.damagesource.DamageTypes.CACTUS)) return false;
        if (damagesource.is(net.minecraft.world.damagesource.DamageTypes.DROWN)) return false;
        if (damagesource.is(net.minecraft.world.damagesource.DamageTypes.FALLING_ANVIL)) return false;
        return super.hurt(damagesource, amount);
    }

    // ========== 生成注册 ==========
    public static void init(RegisterSpawnPlacementsEvent event) {
        event.register(MutModEntities.ELITE_ABORIGINE_ZOMBIE.get(), SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (entityType, world, reason, pos, random) -> (world.getDifficulty() != Difficulty.PEACEFUL
                        && Monster.isDarkEnoughToSpawn(world, pos, random)
                        && Mob.checkMobSpawnRules(entityType, world, reason, pos, random)),
                RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = Mob.createMobAttributes();
        builder = builder.add(Attributes.MOVEMENT_SPEED, 0.3);
        builder = builder.add(Attributes.MAX_HEALTH, 200);
        builder = builder.add(Attributes.ARMOR, 15);
        builder = builder.add(Attributes.ATTACK_DAMAGE, 5);
        builder = builder.add(Attributes.FOLLOW_RANGE, 32);
        builder = builder.add(Attributes.STEP_HEIGHT, 0.6);
        builder = builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.2);
        builder = builder.add(Attributes.SPAWN_REINFORCEMENTS_CHANCE);
        return builder;
    }
}