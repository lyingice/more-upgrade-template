package net.mcreator.mut.block.entity.boss_spawner;

import net.minecraft.util.StringRepresentable;

public enum BossSpawnerState implements StringRepresentable {
    INACTIVE("inactive"),      // 未激活，等待钥匙
    CHARGING("charging"),      // 倒计时中
    ACTIVE("active"),          // Boss已生成，战斗中
    EJECTING("ejecting"),      // 击败Boss，弹出奖励
    COOLDOWN("cooldown");      // 冷却中

    private final String name;

    BossSpawnerState(String name) { this.name = name; }

    @Override
    public String getSerializedName() { return name; }
}