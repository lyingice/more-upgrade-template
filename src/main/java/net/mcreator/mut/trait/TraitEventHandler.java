package net.mcreator.mut.trait;

import net.mcreator.mut.compat.AccessoriesCompat;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jetbrains.annotations.Nullable;

public class TraitEventHandler {

    @SubscribeEvent
    public void onAttack(LivingIncomingDamageEvent event) {
        LivingEntity attacker = resolveAttacker(event);
        if (attacker == null) return;
        handleTrigger("attack", attacker, event.getEntity(), null);
    }
    private LivingEntity resolveAttacker(LivingIncomingDamageEvent event) {
        if (event.getSource().getDirectEntity() instanceof LivingEntity direct) return direct;
        if (event.getSource().getEntity() instanceof LivingEntity source) return source;
        return null;
    }

    @SubscribeEvent
    public void onKill(LivingDeathEvent event) {
        if (event.getSource().getDirectEntity() instanceof LivingEntity attacker) {
            handleTrigger("kill", attacker, event.getEntity(), null);
        }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        handleTrigger("block_break", event.getPlayer(), null, event.getState());
    }

    private void handleTrigger(String trigger, LivingEntity user, @Nullable LivingEntity target,
                               @Nullable net.minecraft.world.level.block.state.BlockState broken) {
        // 主手：保持原有行为不变
        applyTraits(trigger, user, target, user.getMainHandItem(), broken);

        // 饰品槽（例如天境手套槽）：仅当 accessories 模组存在时才执行。
        // import 只是编译期信息；这里的 ModList 守卫让 AccessoriesCompat 在 accessories 缺席时
        // 永远不会被类加载 / 链接（JVM 惰性解析常量池），服务端无 accessories 也安全。
        // 性能：每次事件最多遍历 <20 个饰品槽，且不做缓存（装备变化会使缓存失效），开销可接受。
        if (ModList.get().isLoaded("accessories")) {
            for (ItemStack stack : AccessoriesCompat.getEquippedStacks(user)) {
                applyTraits(trigger, user, target, stack, broken);
            }
        }
    }

    /**
     * 对单个物品堆应用所有匹配 trigger（或 "always"）的 trait 效果。
     * <p>
     * 主手和每件饰品都是各自独立的 ItemStack，同一件物品不会同时出现在主手和饰品槽中，
     * 因此不会重复触发；每件饰品各自结算 trait 属于预期行为。
     */
    private void applyTraits(String trigger, LivingEntity user, @Nullable LivingEntity target, ItemStack stack,
                             @Nullable net.minecraft.world.level.block.state.BlockState broken) {
        for (Trait trait : TraitRegistry.getTraitsFor(stack)) {
            for (TraitEffect effect : trait.getEffects()) {
                if (effect.getTrigger().equals(trigger) || effect.getTrigger().equals("always")) {
                    effect.apply(user, target, stack, broken);
                }
            }
        }
    }
}