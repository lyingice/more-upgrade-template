package net.mcreator.mut.affix;

import net.mcreator.mut.affix.impl.*;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import javax.annotation.Nullable;
import java.util.*;

/**
 * 词条注册中心 - 所有词条集中管理
 */
public class AffixRegistry {

    private static final Map<String, Affix> AFFIXES = new LinkedHashMap<>();

    // ========== 注册所有词条 ==========

    /** 剧毒印记 */
    public static final Affix POISON_MARK = register(new PoisonMarkAffix());
    /**灼烧印记**/
    public static final Affix FIRE_MARK = register(new FireMarkAffix());
    /**凋零印记**/
    public static final Affix WITHER_MARK = register(new WitherMarkAffix());
    /**涅槃**/
    public static final Affix NIRVANA = register(new NirvanaAffix());
    /**势能印记**/
    public static final Affix MOMENTUM = register(new MomentumAffix());
    //再生印记
    public static final Affix REGENERATION_MARK = register(new RegenerationMarkAffix());


    private static Affix register(Affix affix) {
        AFFIXES.put(affix.getId(), affix);
        return affix;
    }

    /**
     * 根据ID获取词条
     */
    @Nullable
    public static Affix get(String id) {
        return AFFIXES.get(id);
    }

    /**
     * 获取所有已注册的词条
     */
    public static Collection<Affix> getAll() {
        return Collections.unmodifiableCollection(AFFIXES.values());
    }
    /**
     * 给物品应用词条的属性加成
     */
    public static ItemAttributeModifiers applyAffixAttributes(Affix affix, ItemAttributeModifiers original, EquipmentSlot slot) {
        if (affix != null && affix.hasAttributeModifiers()) {
            return affix.getAttributeModifiers(original, slot);
        }
        return original;
    }

    // ========== 暂时注释掉未来功能（报错的方法） ==========
    // 等 Affix 接口补充了这些方法后再取消注释

    /*
    public static ItemAttributeModifiers applyToolAffix(...) { ... }
    public static ItemAttributeModifiers applyArmorAffix(...) { ... }
    public static Item.Properties modifyProperties(...) { ... }
    public static Holder<MobEffect> getStrikeEffect(...) { ... }
    public static int getStrikeEffectAmplifier(...) { ... }
    public static int getStrikeEffectDuration(...) { ... }
    public static double getTriggerChance(...) { ... }
    */
}