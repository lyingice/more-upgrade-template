package net.mcreator.mut.miracle;

import net.mcreator.mut.beastiary.BeastiaryEntities;
import net.mcreator.mut.porting.mercenary.MercenaryEntities;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

/**
 * 金质号角。
 *
 * <p>右键使用：召唤 2~4 只猪灵雇佣兵，从三种雇佣兵里随机（近战 / 弩手 / 持盾）。
 * 耐久 16，每次使用 -1，用完（第 16 次）直接消失。
 * 召唤只在服务端执行。
 */
public class GoldenHornItem extends Item {

    public static final int DURABILITY = 16;

    /** 单次召唤数量范围 */
    private static final int MIN_SUMMON = 2;
    private static final int MAX_SUMMON = 4;

    /** 召唤环绕玩家的半径范围（格） */
    private static final double MIN_SPAWN_RADIUS = 1.5D;
    private static final double MAX_SPAWN_RADIUS = 3.0D;

    /** 使用冷却：10 秒 */
    public static final int COOLDOWN_TICKS = 200;

    /** 号角音效池：8 种原版山羊角 + 掠夺者号角 */
    private static final List<Holder<SoundEvent>> HORN_SOUNDS = buildHornSounds();

    private static List<Holder<SoundEvent>> buildHornSounds() {
        List<Holder<SoundEvent>> list = new ArrayList<>(SoundEvents.GOAT_HORN_SOUND_VARIANTS);
        list.add(SoundEvents.RAID_HORN);
        return List.copyOf(list);
    }

    public GoldenHornItem() {
        super(new Item.Properties()
                .durability(DURABILITY)
                .stacksTo(1)
                .rarity(Rarity.RARE));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        // 冷却双端都记，客户端才能正确显示物品冷却转圈
        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        if (!level.isClientSide()) {
            this.summonMercenaries(level, player);
            // 每次使用掉 1 点耐久，掉光即销毁
            stack.hurtAndBreak(1, player,
                    hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    /** 召唤 2~4 只随机雇佣兵 */
    private void summonMercenaries(Level level, Player player) {
        RandomSource random = level.getRandom();
        int count = MIN_SUMMON + random.nextInt(MAX_SUMMON - MIN_SUMMON + 1);
        // 每次吹响随机挑一种号角音色
        Holder<SoundEvent> horn = HORN_SOUNDS.get(random.nextInt(HORN_SOUNDS.size()));
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                horn, SoundSource.PLAYERS, 1.0F, 0.95F + random.nextFloat() * 0.1F);
        for (int i = 0; i < count; i++) {
            EntityType<? extends Mob> type = this.randomMercenaryType(random);
            Mob mercenary = type.create(level);
            if (mercenary == null) {
                continue;
            }
            double angle = random.nextDouble() * Math.PI * 2.0D;
            double radius = MIN_SPAWN_RADIUS + random.nextDouble() * (MAX_SPAWN_RADIUS - MIN_SPAWN_RADIUS);
            double x = player.getX() + Math.cos(angle) * radius;
            double z = player.getZ() + Math.sin(angle) * radius;
            mercenary.moveTo(x, player.getY(), z, player.getYRot(), 0.0F);
            mercenary.setPersistenceRequired();
            level.addFreshEntity(mercenary);
        }
    }

    /** 三选一：近战 / 弩手 / 持盾 */
    private EntityType<? extends Mob> randomMercenaryType(RandomSource random) {
        return switch (random.nextInt(3)) {
            case 0 -> MercenaryEntities.PIGLIN_MERCENARY.get();
            case 1 -> BeastiaryEntities.PIGLIN_MERCENARY_RANGED.get();
            default -> BeastiaryEntities.PIGLIN_MERCENARY_DEFENDER.get();
        };
    }
}
