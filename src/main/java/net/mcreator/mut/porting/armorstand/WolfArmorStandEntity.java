package net.mcreator.mut.porting.armorstand;

import net.mcreator.mut.porting.armorstand.MutArmorStandItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EntityType;

import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;

/**
 * 静态狼铠展示架。继承 Wolf 以直接复用原版 WolfModel 形状
 * （狼铠本体/染色/裂纹由自写铠层渲染，纹饰渲染内联在同层）。
 * 实体本身完全静态：无 AI、不可骑乘、不可驯服、不可繁殖、不坐姿、不湿身。
 */
public class WolfArmorStandEntity extends Wolf {

    /** 受击事件 ID（同原版盔甲架）：客户端播放敲击声并刷新摇晃计时。 */
    public static final byte EVENT_HIT = 32;

    /** 上次被击中的游戏时间（客户端由 EVENT_HIT 同步），用于受击摇晃与连击破坏判定。 */
    public long lastHit;

    public WolfArmorStandEntity(EntityType<? extends Wolf> entityType, Level level) {
        super(entityType, level);
        this.setNoAi(true);
        this.setPersistenceRequired();
        this.bodyArmorDropChance = 0.0F;
        // 展示架保持站姿，不坐下
        this.setInSittingPose(false);
        this.setOrderedToSit(false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Wolf.createAttributes()
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
    public boolean canUseSlot(EquipmentSlot slot) {
        // 只保留 BODY 狼铠槽
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
    public boolean isInWaterRainOrBubble() {
        // 防止淋雨触发湿身/发抖动画
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
    protected void playStepSound(BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
    }

    // ---------- 交互：手持狼铠放入 / 空手取出 ----------

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        // 不调用 super：阻断驯服、喂食、坐下切换、染项圈、剪铠
        ItemStack held = player.getItemInHand(hand);
        ItemStack armor = this.getBodyArmorItem();

        if (held.getItem() instanceof AnimalArmorItem animalArmorItem
                && animalArmorItem.getBodyType() == AnimalArmorItem.BodyType.CANINE) {
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
     * 把手中的狼铠装到展示架上。
     *
     * <p>支持替换：已有不同狼铠时先把旧的掉出来再装新的；同一件重复右键不做处理。</p>
     *
     * @return 是否发生了装备（客户端恒为 false，由 InteractionResult 负责同步）
     */
    public boolean tryEquipAnimalArmor(ItemStack held, Player player) {
        if (this.level().isClientSide || this.isRemoved()) {
            return false;
        }
        if (!(held.getItem() instanceof AnimalArmorItem animalArmorItem)
                || animalArmorItem.getBodyType() != AnimalArmorItem.BodyType.CANINE) {
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
     * 碰撞箱：宽 0.625 格、长 0.9375 格（与底座 10×15px 一致）、高 0.9 格，
     * 以实体位置为中心，长边随四向朝向旋转，居中放置。
     */
    public static AABB makeStandAABB(double x, double y, double z, float yRot) {
        int quarter = Math.round(yRot / 90.0F) & 3;

        if ((quarter & 1) == 1) {
            // 朝东/西：长边沿 X 轴
            return new AABB(x - 0.46875D, y, z - 0.3125D, x + 0.46875D, y + 0.9D, z + 0.3125D);
        }

        // 朝南/北：长边沿 Z 轴
        return new AABB(x - 0.3125D, y, z - 0.46875D, x + 0.3125D, y + 0.9D, z + 0.46875D);
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

    // ---------- 显示 ----------

    @Override
    public Component getTypeName() {
        return Component.translatable("entity.mut.wolf_armor_stand");
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
            this.remove(Entity.RemovalReason.KILLED);
            this.gameEvent(GameEvent.ENTITY_DIE);
            return false;
        }

        if (source.is(DamageTypeTags.IS_EXPLOSION)) {
            this.brokenByPlayer();
            this.showBreakingParticles();
            this.remove(Entity.RemovalReason.KILLED);
            this.gameEvent(GameEvent.ENTITY_DIE);
            return true;
        }

        if (source.isCreativePlayer()) {
            this.playBrokenSound();
            this.showBreakingParticles();
            this.remove(Entity.RemovalReason.KILLED);
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
            this.remove(Entity.RemovalReason.KILLED);
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
                new ItemStack(MutArmorStandItems.WOLF_ARMOR_STAND.get())
        );
    }
}