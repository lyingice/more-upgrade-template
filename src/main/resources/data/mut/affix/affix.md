# 词缀数据包配置文档（重构版）

## 目录

1. [配置总览](#1-配置总览)
2. [`affix_levels.json`](#2-affix_levelsjson)
3. [`material_bonuses.json`](#3-material_bonusesjson)
4. [`pity_config.json`](#4-pity_configjson)
5. [`item_affix_bindings.json`](#5-item_affix_bindingsjson)
6. [词缀 ID 列表](#6-词缀-id-列表)
7. [重构说明](#7-重构说明)

---

## 1. 配置总览

| 文件 | 路径 | Java 类 |
|------|------|---------|
| 等级定义 | `affix_levels.json` | `LevelConfig.java` |
| 材料加成 | `material_bonuses.json` | `MaterialBonusConfig.java` |
| 软保底 | `pity_config.json` | `PityConfig.java` |
| 物品绑定 | `item_affix_bindings.json` | `ItemAffixBindingConfig.java` |

---

## 2. `affix_levels.json`

```jsonc
{
  "levels": [
    { "level": 1, "formatting": "white",  "color": "#FFFFFF", "weight_curve": {...} },
    { "level": 2, "formatting": "blue",   "color": "#5555FF", "weight_curve": {...} },
    { "level": 3, "formatting": "gold",   "color": "#FFAA00", "weight_curve": {...} },
    { "level": 4, "formatting": "light_purple", "color": "#AA00FF", ... },
    { "level": 5, "formatting": "yellow", "color": "#FFFF55", ... },
    { "level": 6, "formatting": "red",    "color": "#FF5555", ... },
    { "level": 7, "formatting": "dark_red",    "color": "#AA0000", ... },
    { "level": 8, "formatting": "light_purple", "color": "#FF55FF", ... }
  ],
  "total_pool_scale_factor": 2,
  "total_pool_base": 100,
  "default_max_level_cap": 5
}
```

> **修复说明**: 等级 2 的 `formatting` 从 `"green"` 改为 `"blue"`，与 HEX 色值 `#5555FF` 一致。

### weight_curve 公式
```
score = base × (1 - decay × 0.5) + max(0, enchantValue - threshold) × growth
```

---

## 3. `material_bonuses.json`

三类材料：
- **universal_materials**: 提供附魔加成 + 保底等级 + 等级上限
- **directed_materials**: 指定词缀的固定概率
- **tag_driven_materials**: 通过标签批量匹配

---

## 4. `pity_config.json`

```jsonc
{
  "global": {
    "pity_per_attempt": 1,
    "pity_cap": 12,
    "pity_bonus_per_point": 0.12,
    "min_level_for_reset": 5   // ★ 已对齐 default_max_level_cap
  }
}
```

> **修复说明**: `min_level_for_reset` 从 6 → 5，确保不使用特殊材料时也能触发保底重置。

---

## 5. `item_affix_bindings.json`

通过 tag 或 item 精确指定每个物品可用的词缀池。

---

## 6. 词缀 ID 列表

| ID | 名称 | 类型 |
|----|------|------|
| `poison_mark` | 剧毒印记 | IMarkAffix |
| `fire_mark` | 灼烧印记 | IMarkAffix |
| `wither_mark` | 凋零印记 | IMarkAffix |
| `nirvana` | 涅槃 | Affix（属性修改器）★复活 |
| `momentum` | 势能印记 | Affix |
| `regeneration_mark` | 再生印记 | Affix |
| `piercing_spear` | 贯穿之矛 | Affix |
| `energy_conversion` | 能量转化 | Affix |
| `sharpshooter` | 神射手 | Affix |
| `strength_blessing` | 力量增效 | Affix |
| `tidal_surge` | 潮涌之力 | Affix |
| `big_stomach` | 大胃袋 | Affix |

---

## 7. 重构说明

### 7.1 Helper → Affix 内化
所有 `XxxHelper` 类的业务逻辑已合并到对应的 `impl/XxxAffix` 类中。
旧 Helper 类保留为 `@Deprecated` 委托存根，现有代码仍可编译，逐步迁移到 `AffixRegistry.XXX.method()` 调用。

### 7.2 通用装备等级计算
`Affix.getEquippedLevel(LivingEntity)` 消除了 11 个 Helper 中的重复模板代码。

### 7.3 属性修改器支持
`Affix` 接口新增 `hasAttributeModifiers()` / `getAttributeModifiers()` 方法。
`NirvanaAffix` 已复活并正式注册。

### 7.4 JEI 端去重
`SuperSmithingTableRecipe` 改为调用 `MaterialBonusRegistry`，不再手动解析 JSON。

### 7.5 保底 per-material 覆盖
`PityTracker` 现在正确读取 `per_material_overrides` 中的 `pity_bonus_per_point` 和 `min_level_for_reset`。

### 7.6 死代码清理
- `MomentumAffix.DAMAGE_PER_LEVEL`（未使用）
- `PiercingSpearHelper.STAB_MULTIPLIER` / `CHARGE_MULTIPLIER`（被 AffixConfig 替代）
- `AffixRoller.WeightedAffix` 内部类（从未实例化）
- Legacy 权重路径标记 `@Deprecated`
