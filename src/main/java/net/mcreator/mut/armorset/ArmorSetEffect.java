package net.mcreator.mut.armorset;

import net.minecraft.world.entity.player.Player;

/**
 * 套装效果。触发时机由 JSON 里的 trigger 决定，处理器只在对应时机调用。
 */
public interface ArmorSetEffect {

    /** attack 之外的套装触发：hurt（受伤）、kill（击杀）、interval（定时） */
    String getTrigger();

    void apply(Player player, ArmorSet set);

    /** 仅 interval 触发使用；单位为秒，返回 0 表示不定时触发 */
    default int getIntervalSeconds() {
        return 0;
    }
}
