# 词缀数据包配置文档（三改版）

所有词缀相关配置文件位于 `data/mut/affix/` 目录下，支持 `/reload` 命令即时重载。

---

## 目录

1. [配置总览](#1-配置总览)
2. [`affix_levels.json` 等级与权重曲线](#2-affix_levelsjson-等级与权重曲线)
3. [`material_bonuses.json` 材料加成映射](#3-material_bonusesjson-材料加成映射)
4. [`pity_config.json` 软保底系统](#4-pity_configjson-软保底系统)
5. [`item_affix_bindings.json` 物品词缀绑定](#5-item_affix_bindingsjson-物品词缀绑定)
6. [物品标签系统](#6-物品标签系统)
7. [总分池计算模型详解](#7-总分池计算模型详解)
8. [固定概率词缀选择](#8-固定概率词缀选择)
9. [所有词缀 ID 列表](#9-所有词缀-id-列表)

---

## 1. 配置总览

| 配置文件 | 路径 | 对应 Java POJO | 作用 |
|---------|------|---------------|------|
| 等级定义 | `affix_levels.json` | `LevelConfig.java` | 定义等级数量、名称、颜色、权重曲线、总分池参数 |
| 材料加成 | `material_bonuses.json` | `MaterialBonusConfig.java` | 定义材料提供的附魔加成/定向固定概率/保底上限 |
| 软保底 | `pity_config.json` | `PityConfig.java` | 定义保底累计机制 |
| 物品绑定 | `item_affix_bindings.json` | `ItemAffixBindingConfig.java` | 定义物品←→可用词缀的映射 |

配置加载入口：`AffixDataLoader.java`

---

## 2. `affix_levels.json` 等级与权重曲线

### 结构总览

```jsonc
{
  "levels": [
    {
      "level": 1,
      "name_key": "affix.level.common",
      "color": "#FFFFFF",
      "formatting": "white",
      "weight_curve": {
        "base": 50,       // 基础分
        "decay": 0.7,     // 衰减系数
        "growth": 0,      // 附魔能力增长系数
        "threshold": 0    // 激活growth的最小附魔能力阈值
      }
    }
    // ... 更多等级
  ],
  "total_pool_scale_factor": 2,   // 总分池缩放因子
  "total_pool_base": 100,         // 总分池基数
  "default_max_level_cap": 5,     // 默认最高等级限制（材料未设置显式 max_level_cap 时自动应用此值）
  "min_probability_per_level": 0.003
}
```

### 各等级配置

| level | 格式化名 | 颜色 |
|-------|---------|------|
| 1 | white | 白色 |
| 2 | green | 绿色 |
| 3 | blue | 蓝色 |
| 4 | light_purple | 紫色 |
| 5 | gold | 金色 |
| 6 | red | 红色 |
| 7 | dark_red | 深红 |
| 8 | light_purple | 粉色 |

### weight_curve 计算公式

```
score = base × (1 - decay × 0.5) + max(0, enchantValue - threshold) × growth
```

---

## 3. `material_bonuses.json` 材料加成映射

### 3.1 通用材料 `universal_materials`

提供额外附魔能力（不固定词缀）。

```jsonc
{
  "item": "minecraft:nether_star",
  "enchant_bonus": 15,              // 额外附魔能力
  "min_guaranteed_level": 3,        // 材料保底等级
  "max_level_cap": 6,               // 材料最高等级上限（0=不设显式上限，由数据包的 default_max_level_cap 决定，默认5）
  "description": "affix.material.nether_star"
}
```

### 3.2 定向材料 `directed_materials`

指定特定词缀，分配固定出现概率。

```jsonc
{
  "item": "minecraft:blaze_rod",
  "affix_bonuses": [
    {
      "target_affix": "fire_mark",
      "fixed_probability": 0.4,      // 固定出现概率（0.4=40%）
      "min_level": 1,
      "max_level": 8
    }
  ]
}
```

**固定概率规则**：
- 材料为每个词缀设定固定概率（如0.4=40%）
- 直接从100%中划走该概率
- 剩余概率均分给其他词缀
- 如多词缀总概率超100%，自动按比率归一化到100%

### 3.3 标签驱动材料 `tag_driven_materials`

通过物品标签（ItemTag）批量匹配材料。

```jsonc
{
  "tag": "mut:affix_bonus/poison",
  "affix_bonuses": [
    { "target_affix": "poison_mark", "fixed_probability": 0.25 }
  ]
}
```

---

## 4. `pity_config.json` 软保底系统

```jsonc
{
  "enabled": true,
  "global": {
    "pity_per_attempt": 1,
    "pity_cap": 12,
    "pity_reset_on_success": true,
    "pity_bonus_per_point": 0.12,
    "min_level_for_reset": 6
  },
  "per_material_overrides": {
    "minecraft:nether_star": { "pity_per_attempt": 2, "min_level_for_reset": 4 }
  }
}
```

---

## 5. `item_affix_bindings.json` 物品词缀绑定

```jsonc
{
  "bindings": [
    {
      "tag": "minecraft:enchantable/durability",
      "affix_pool": ["regeneration_mark", "tidal_surge", ...]
    }
  ]
}
```

---

## 6. 物品标签系统

### `#mut:affix_material`
所有可以作为词缀材料的物品必须在此标签中。

### `#mut:affix_bonus/*`
标签驱动材料的匹配标签系列。

### `minecraft:enchantable/*` + `mut:affixable`
超级锻造台槽位1的物品判定标签。

---

## 7. 总分池计算模型详解

### 新模型（total_pool_scale_factor > 0 时启用）

```
阶段一：totalBudget = scaleFactor × effectiveEnchant × levelCount + basePool
阶段二：ratio = weightCurve.computeScore(effectiveEnchant)
阶段三：score(level) = totalBudget × ratio / Σratio
阶段四：材料限制→分数再分配
  ① 未设显式 max_level_cap → 使用 default_max_level_cap（默认5）
  ② 被 minLevel 或 maxCap 排除的等级 → 分数清零
  ③ 清零的分数按比例再分配给有效等级
阶段五：线性归一化

effectiveEnchant = baseEnchant + materialEnchantBonus
```

### 材料附魔加成

```
effectiveEnchant = baseEnchant + materialCtx.getEnchantBonus()
```

材料附魔加成同时影响 totalBudget 和 weightCurve.computeScore()，产生双重放大效应。

---

## 8. 固定概率词缀选择

材料提供固定概率的词缀选择机制：

```
1. 读取材料对所有词缀的 fixed_probability
2. 从100%中划走固定概率部分
3. 剩余概率均分给其他词缀
4. 如总概率超过100%，自动按比率归一化
```

---

## 9. 所有词缀 ID 列表

| 词缀ID | 说明 |
|--------|------|
| `poison_mark` | 剧毒印记 |
| `fire_mark` | 灼烧印记 |
| `wither_mark` | 凋零印记 |
| `momentum` | 势能印记 |
| `regeneration_mark` | 再生印记 |
| `piercing_spear` | 贯穿之矛 |
| `energy_conversion` | 能量转化 |
| `sharpshooter` | 神射手 |
| `strength_blessing` | 力量增效 |
| `tidal_surge` | 潮涌之力 |
| `big_stomach` | 大胃袋 |
