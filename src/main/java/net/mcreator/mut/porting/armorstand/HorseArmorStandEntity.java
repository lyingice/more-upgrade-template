package net.mcreator.mut.porting.armorstand;

import net.mcreator.mut.porting.armorstand.MutArmorStandItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;

/**
 * 静态马铠展示架。继承 Horse 以直接复用原版 HorseModel、HorseArmorLayer
 * （纹饰渲染由 HorseArmorLayerMixin 注入在原版层上自动生效）。
 * 实体本身完全静态：无 AI、不可骑乘、不可驯服、不可繁殖。
 */
public class HorseArmorStandEntity extends Horse {

    /** 受击事件 ID（同原版盔甲架）：客户端播放敲击声并刷新摇晃计时。 */
    public static final byte EVENT_HIT = 32;

    /** 上次被击中的游戏时间（客户端由 EVENT_HIT 同步），用于受击摇晃与连击破坏判定。 */
    public long lastHit;

    public HorseArmorStandEntity(EntityType<? extends Horse> entityType, Level level) {
        super(entityType, level);
        this.setNoAi(true);
        this.setPersistenceRequired();
        this.bodyArmorDropChance = 0.0F;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbstractHorse.createBaseHorseAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.STEP_HEIGHT, 0.0D);
    }

    // ---------- 行为关闭 ----------

    @Override
    protected void registerGoals() {
        // 静态展示架：无任何 AI 目标
    }

    @Override
    public boolean isImmobile() {
        return true;
    }

    @Override
    public boolean isEffectiveAi() {
        return false;
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    @Override
    public boolean canEatGrass() {
        // 展示架不吃草：AbstractHorse.aiStep 在草方块上会随机低头吃草
        return false;
    }

    @Override
    public boolean isSaddleable() {
        return false;
    }

    @Override
    public boolean canUseSlot(EquipmentSlot slot) {
        // 只保留 BODY 马铠槽
        return slot.getType() == EquipmentSlot.Type.ANIMAL_ARMOR;
    }

    @Override
    public boolean canAddPassenger(Entity passenger) {
        return false;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canMate(Animal other) {
        return false;
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    public boolean isAffectedByPotions() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(Entity entity) {
    }

    @Override
    public void push(Entity entity) {
    }

    @Override
    protected void pushEntities() {
    }

    // ---------- 声音关闭 ----------

    @Override
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return null;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return null;
    }

    @Override
    protected SoundEvent getEatingSound() {
        return null;
    }

    @Override
    protected SoundEvent getAngrySound() {
        return null;
    }

    @Override
    protected void playStepSound(BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
    }

    // ---------- 交互：手持马铠放入 / 空手取出 ----------

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        // 不调用 super：阻断骑乘、驯服、喂食、上鞍与繁殖
        ItemStack held = player.getItemInHand(hand);
        ItemStack armor = this.getBodyArmorItem();

        if (held.getItem() instanceof AnimalArmorItem animalArmorItem
                && animalArmorItem.getBodyType() == AnimalArmorItem.BodyType.EQUESTRIAN) {
            if (this.tryEquipAnimalArmor(held, player)) {
                this.gameEvent(GameEvent.ENTITY_INTERACT, player);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        if (held.isEmpty() && !armor.isEmpty()) {
            if (!this.level().isClientSide) {
                if (!player.getInventory().add(armor)) {
                    player.drop(armor, false);
                }
                this.setBodyArmorItem(ItemStack.EMPTY);
                this.gameEvent(GameEvent.ENTITY_INTERACT, player);
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    /**
     * 把手中的动物铠装到展示架上。
     *
     * <p>不走原版 {@code equipBodyArmor}：那只在空槽时生效，连续更换铠甲会失败。
     * 这里支持替换——已有不同铠甲时先把旧的掉出来，再装上新的；
     * 同一件铠甲重复右键不做处理。</p>
     *
     * @return 是否发生了装备（客户端恒为 false，由 InteractionResult 负责同步）
     */
    public boolean tryEquipAnimalArmor(ItemStack held, Player player) {
        if (this.level().isClientSide || this.isRemoved()) {
            return false;
        }
        if (!(held.getItem() instanceof AnimalArmorItem animalArmorItem)
                || animalArmorItem.getBodyType() != AnimalArmorItem.BodyType.EQUESTRIAN) {
            return false;
        }

        ItemStack current = this.getBodyArmorItem();
        if (!current.isEmpty()) {
            if (ItemStack.isSameItem(current, held)) {
                return false;
            }
            this.setBodyArmorItem(ItemStack.EMPTY);
            Block.popResource(this.level(), this.blockPosition().above(), current);
        }

        this.setBodyArmorItem(held.copyWithCount(1));
        if (player != null) {
            held.consume(1, player);
        }
        return true;
    }

    // ---------- 保存 / 读取 ----------

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        // 兼容旧版实体数据：马铠原本存在自定义 "ArmorItem" 标签里
        if (this.getBodyArmorItem().isEmpty() && tag.contains("ArmorItem")) {
            ItemStack old = ItemStack.parse(this.registryAccess(), tag.getCompound("ArmorItem"))
                    .orElse(ItemStack.EMPTY);
            if (!old.isEmpty()) {
                this.setBodyArmorItem(old);
            }
        }
    }

    // ---------- 碰撞 / 尺寸 ----------
    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    protected AABB makeBoundingBox() {
        return makeStandAABB(this.getX(), this.getY(), this.getZ(), this.getYRot());
    }

    /**
     * 碰撞箱：宽 0.75 格、长 1.5 格（与底座 12×24px 一致）、高 1.5 格，
     * 以实体位置为中心，长边随四向朝向旋转。
     * 放置时实体位置会沿朝向前移 0.25 格（见 HorseArmorStand 物品），
     * 使后边缘与放置格后边缘对齐、只往前占格子。
     */
    public static AABB makeStandAABB(double x, double y, double z, float yRot) {
        int quarter = Math.round(yRot / 90.0F) & 3;

        if ((quarter & 1) == 1) {
            // 朝东/西：长边沿 X 轴
            return new AABB(x - 0.75D, y, z - 0.375D, x + 0.75D, y + 1.5D, z + 0.375D);
        }

        // 朝南/北：长边沿 Z 轴
        return new AABB(x - 0.375D, y, z - 0.75D, x + 0.375D, y + 1.5D, z + 0.75D);
    }

    // ---------- 固定四向旋转 ----------

    @Override
    public void setYRot(float value) {
        float snapped = Math.round(value / 90.0F) * 90.0F;

        super.setYRot(snapped);

        this.yRotO = snapped;
        this.yBodyRot = snapped;
        this.yBodyRotO = snapped;
        this.yHeadRot = snapped;
        this.yHeadRotO = snapped;
    }

    @Override
    protected float tickHeadTurn(float yaw, float pitch) {
        this.yBodyRotO = this.yRotO;
        this.yBodyRot = this.getYRot();
        return 0.0F;
    }

    // ---------- 显示关闭 ----------

    @Override
    public Component getTypeName() {
        return Component.translatable("entity.mut.horse_armor_stand");
    }

    @Override
    public boolean shouldShowName() {
        return false;
    }

    @Override
    public boolean isCustomNameVisible() {
        return false;
    }

    // ---------- 破坏与掉落 ----------

    @Override
    public boolean shouldDropLoot() {
        return false;
    }

    @Override
    public boolean shouldDropExperience() {
        return false;
    }

    @Override
    protected void dropAllDeathLoot(ServerLevel level, DamageSource source) {
        super.dropAllDeathLoot(level, source);

        if (!this.getBodyArmorItem().isEmpty()) {
            Block.popResource(this.level(), this.blockPosition().above(), this.getBodyArmorItem());
            this.setBodyArmorItem(ItemStack.EMPTY);
        }
    }

    /**
     * 盔甲架式破坏：箭/三叉戟/火球等（ALWAYS_KILLS）直接打碎；
     * 玩家攻击（CAN_BREAK_ARMOR_STAND）先摇晃并累积，5 tick 内再次击中才打碎。
     */
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.level().isClientSide || this.isRemoved()) {
            return false;
        }

        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            this.remove(RemovalReason.KILLED);
            this.gameEvent(GameEvent.ENTITY_DIE);
            return false;
        }

        if (source.is(DamageTypeTags.IS_EXPLOSION)) {
            this.brokenByPlayer();
            this.showBreakingParticles();
            this.remove(RemovalReason.KILLED);
            this.gameEvent(GameEvent.ENTITY_DIE);
            return true;
        }

        if (source.isCreativePlayer()) {
            this.playBrokenSound();
            this.showBreakingParticles();
            this.remove(RemovalReason.KILLED);
            this.gameEvent(GameEvent.ENTITY_DIE);
            return true;
        }

        boolean alwaysKills = source.is(DamageTypeTags.ALWAYS_KILLS_ARMOR_STANDS);
        if (!alwaysKills && !source.is(DamageTypeTags.CAN_BREAK_ARMOR_STAND)) {
            return false;
        }

        if (source.getEntity() instanceof Player player && !player.getAbilities().mayBuild) {
            return false;
        }

        long now = ((ServerLevel) this.level()).getGameTime();
        if (alwaysKills || now - this.lastHit <= 5L) {
            this.brokenByPlayer();
            this.showBreakingParticles();
            this.remove(RemovalReason.KILLED);
            this.gameEvent(GameEvent.ENTITY_DIE);
        } else {
            ((ServerLevel) this.level()).broadcastEntityEvent(this, EVENT_HIT);
            this.gameEvent(GameEvent.ENTITY_DAMAGE, source.getEntity());
            this.lastHit = now;
        }

        return true;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == EVENT_HIT) {
            this.level().playLocalSound(
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    SoundEvents.ARMOR_STAND_HIT,
                    this.getSoundSource(),
                    0.3F,
                    1.0F,
                    false
            );
            this.lastHit = this.level().getGameTime();
        } else {
            super.handleEntityEvent(id);
        }
    }

    private void playBrokenSound() {
        this.level().playSound(
                null,
                this.getX(),
                this.getY(),
                this.getZ(),
                SoundEvents.ARMOR_STAND_BREAK,
                this.getSoundSource(),
                1.0F,
                1.0F
        );
    }

    private void showBreakingParticles() {
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    new BlockParticleOption(
                            ParticleTypes.BLOCK,
                            Blocks.OAK_PLANKS.defaultBlockState()
                    ),
                    this.getX(),
                    this.getY(0.6666666666666666D),
                    this.getZ(),
                    10,
                    this.getBbWidth() / 4.0F,
                    this.getBbHeight() / 4.0F,
                    this.getBbWidth() / 4.0F,
                    0.05D
            );
        }
    }

    private void brokenByPlayer() {
        if (!this.getBodyArmorItem().isEmpty()) {
            Block.popResource(this.level(), this.blockPosition(), this.getBodyArmorItem());
            this.setBodyArmorItem(ItemStack.EMPTY);
        }

        Block.popResource(
                this.level(),
                this.blockPosition(),
                new ItemStack(MutArmorStandItems.HORSE_ARMOR_STAND.get())
        );
    }
}