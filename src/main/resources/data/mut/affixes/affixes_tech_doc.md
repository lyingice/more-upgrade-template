# 词缀 JSON 技术文档（v2 — 数据驱动架构）

## 目录

1. [文件结构](#1-文件结构)
2. [顶层字段](#2-顶层字段)
3. [Effect 效果类型](#3-effect-效果类型)
    - 3.1 [mark_on_attack — 施加印记](#31-mark_on_attack--施加印记)
    - 3.2 [mark_amplify — 印记放大伤害](#32-mark_amplify--印记放大伤害)
    - 3.3 [damage_multiplier — 伤害倍率](#33-damage_multiplier--伤害倍率)
    - 3.4 [heal_bonus — 治疗加成](#34-heal_bonus--治疗加成)
    - 3.5 [conditional_multiplier — 条件倍率](#35-conditional_multiplier--条件倍率)
    - 3.6 [attribute_modifier — 属性修改器](#36-attribute_modifier--属性修改器)
    - 3.7 [durability_repair — 耐久恢复](#37-durability_repair--耐久恢复)
4. [Effect 通用字段](#4-effect-通用字段)
5. [configurable 可配置参数](#5-configurable-可配置参数)
6. [完整示例](#6-完整示例)
7. [trigger 触发类型](#7-trigger-触发类型)
8. [condition 条件类型](#8-condition-条件类型)
9. [damage_type 参考](#9-damage_type-参考)
10. [等级计算规则](#10-等级计算规则)

---

## 1. 文件结构
data/mut/affixes/<词缀id>.json

一个文件定义一个词缀，文件名建议与 `id` 一致。支持 `/reload` 热重载。

---

## 2. 顶层字段

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `id` | string | **是** | 唯一标识，如 `"fire_mark"` |
| `name_key` | string | 否 | 名称翻译键，默认 `"affix.{id}.name"` |
| `desc_key` | string | 否 | 描述翻译键，默认 `"affix.{id}.description"` |
| `effects` | array | **是** | 效果列表，至少 1 个 |
| `configurable` | array | 否 | 玩家可调参数（游戏内配置面板） |

---

## 3. Effect 效果类型

### 3.1 `mark_on_attack` — 施加印记

攻击时给目标施加药水效果（仅标记，不直接扣血）。

| 字段 | 类型 | 默认 | 说明 |
|------|------|------|------|
| `type` | string | — | `"mark_on_attack"` |
| `trigger` | string | `"attack"` | 触发时机 |
| `mark_effect` | ResourceLocation | **必填** | 药水效果 ID，如 `"mut:poison_mark"` |
| `duration_ticks` | int | `600` | 持续时间（20tick = 1秒） |

> 全身装备同印记的等级会**求和**后统一施加，而非逐件覆盖。

---

### 3.2 `mark_amplify` — 印记放大伤害

目标身上有印记时，受到指定伤害类型的伤害会按印记等级放大。

| 字段 | 类型 | 默认 | 说明 |
|------|------|------|------|
| `type` | string | — | `"mark_amplify"` |
| `mark_effect` | ResourceLocation | **必填** | 要检查的药水效果 ID |
| `amplify_damage_types` | string[] | **必填** | 匹配的伤害类型列表 |
| `coefficient` | string | `"per_level"` | 对应 `configurable` 的 name |

**公式**: `额外伤害 = 印记等级 × 系数`

> `mark_amplify` 不走 trigger 系统，在 `LivingEntity.hurt()` 中按伤害类型自动匹配。

---

### 3.3 `damage_multiplier` — 伤害倍率

攻击伤害乘以倍率。**全身同名效果等级求和后一次计算。**

| 字段 | 类型 | 默认 | 说明 |
|------|------|------|------|
| `type` | string | — | `"damage_multiplier"` |
| `trigger` | string | `"attack"` | 触发时机 |
| `amount` | double | `0.5` | 每级倍率增量 |
| `ranged_only` | bool | `false` | 仅弹射物伤害生效 |

**公式**: `伤害 × (1.0 + 全身等级总和 × amount)`

> `ranged_only: true` 时近战攻击跳过此效果。

---

### 3.4 `heal_bonus` — 治疗加成

自然恢复 / 治疗时额外回血。**全身同名效果等级求和后一次计算。**

| 字段 | 类型 | 默认 | 说明 |
|------|------|------|------|
| `type` | string | — | `"heal_bonus"` |
| `trigger` | string | `"heal"` | 触发时机 |
| `amount` | double | `0.5` | 每级增量 |
| `condition` | string | 无 | 条件，`null`=无条件 |

**公式**: `回复量 + 全身等级总和 × amount`

| condition | 说明 |
|-----------|------|
| （省略） | 无条件生效 |
| `"satiated"` | 玩家饱食度 ≥ 18 且饱和度 > 0 |

---

### 3.5 `conditional_multiplier` — 条件倍率

满足环境条件时生效的伤害倍率。多个条件互斥（只取满足中 perLevel 最高的）。**全身同名效果等级求和后一次计算。**

| 字段 | 类型 | 默认 | 说明 |
|------|------|------|------|
| `type` | string | — | `"conditional_multiplier"` |
| `trigger` | string | `"attack"` | 触发时机 |
| `condition` | string | **必填** | 条件（见 §8） |
| `amount` | double | `0.5` | 每级倍率增量 |

**公式**: `伤害 × (1.0 + 全身等级总和 × amount)` （仅条件满足时）

---

### 3.6 `attribute_modifier` — 属性修改器

装备时永久修改物品属性。

| 字段 | 类型 | 默认 | 说明 |
|------|------|------|------|
| `type` | string | — | `"attribute_modifier"` |
| `trigger` | string | `"always"` | 始终生效 |
| `slot` | string | **必填** | `mainhand`/`offhand`/`head`/`chest`/`legs`/`feet` |
| `attribute` | ResourceLocation | **必填** | 属性 ID |
| `value` | double | `1.0` | 修改值 |
| `operation` | string | `"add"` | `add`/`multiply_base`/`multiply_total` |

**常用属性 ID**:
minecraft:generic.attack_damage       攻击力
minecraft:generic.attack_speed        攻击速度
minecraft:generic.armor               护甲
minecraft:generic.armor_toughness     护甲韧性
minecraft:generic.max_health          最大生命
minecraft:generic.movement_speed      移动速度
minecraft:generic.knockback_resistance  击退抗性
minecraft:player.block_interaction_range   方块触及距离
minecraft:player.entity_interaction_range  实体触及距离
minecraft:generic.luck                幸运

---

### 3.7 `durability_repair` — 耐久恢复

吃食物溢出饱食度时恢复装备耐久。

| 字段 | 类型 | 默认 | 说明 |
|------|------|------|------|
| `type` | string | — | `"durability_repair"` |
| `trigger` | string | `"durability_change"` | 触发时机 |
| `per_durability` | int | `1` | 每消耗 n 耐久触发 |
| `saturation_per_point` | int | `4` | 每点耐久消耗的饱食度 |

---

## 4. Effect 通用字段

所有 effect 都支持：

| 字段 | 类型 | 说明 |
|------|------|------|
| `type` | string | **必填**，效果类型（§3） |
| `trigger` | string | 触发时机（§7），部分类型有默认值 |

---

## 5. configurable 可配置参数

在游戏内 Cloth Config 面板中可调。每个参数对应一个滑动条。

| 字段 | 类型 | 默认 | 说明 |
|------|------|------|------|
| `name` | string | **必填** | 参数名 |
| `default` | double | **必填** | 默认值 |
| `min` | double | `0` | 最小值 |
| `max` | double | `10` | 最大值 |
| `percentage` | bool | `false` | 显示为百分号（×100 + "%"） |

参数与 effect 的对应方式：`name` 作为系数标识符，由 `AffixConfig.getCoefficient(affixId, name)` 读取。

---

## 6. 完整示例

### 6.1 印记类 — `fire_mark.json`

```json
{
  "id": "fire_mark",
  "name_key": "affix.fire_mark.name",
  "desc_key": "affix.fire_mark.description",
  "effects": [
    {
      "type": "mark_on_attack",
      "trigger": "attack",
      "mark_effect": "mut:fire_mark",
      "duration_ticks": 600
    },
    {
      "type": "mark_amplify",
      "mark_effect": "mut:fire_mark",
      "amplify_damage_types": ["minecraft:on_fire", "minecraft:in_fire"],
      "coefficient": "per_level"
    }
  ],
  "configurable": [
    { "name": "per_level", "default": 0.5, "min": 0, "max": 10 }
  ]
}
```
### 6.2 条件倍率 — momentum.json（凌空攻击）
````
{
"id": "momentum",
"name_key": "affix.momentum.name",
"desc_key": "affix.momentum.description",
"effects": [
{ "type": "conditional_multiplier", "trigger": "attack", "condition": "midair", "amount": 0.125 }
],
"configurable": [
{ "name": "per_level", "default": 0.125, "min": 0, "max": 1, "percentage": true }
]
}
````
### 6.3 多条件倍率 — tidal_surge.json（水/雨中）
````
{
  "id": "tidal_surge",
  "name_key": "affix.tidal_surge.name",
  "desc_key": "affix.tidal_surge.description",
  "effects": [
    { "type": "conditional_multiplier", "trigger": "attack", "condition": "water", "amount": 0.10 },
    { "type": "conditional_multiplier", "trigger": "attack", "condition": "rain", "amount": 0.05 }
  ],
  "configurable": [
    { "name": "water", "default": 0.10, "min": 0, "max": 1, "percentage": true },
    { "name": "rain", "default": 0.05, "min": 0, "max": 1, "percentage": true }
  ]
}
````
### 6.4 远程专属倍率 — sharpshooter.json
````
{
  "id": "sharpshooter",
  "name_key": "affix.sharpshooter.name",
  "desc_key": "affix.sharpshooter.description",
  "effects": [
    { "type": "damage_multiplier", "trigger": "attack", "amount": 0.075, "ranged_only": true }
  ],
  "configurable": [
    { "name": "per_level", "default": 0.075, "min": 0, "max": 1, "percentage": true }
  ]
}
````
### 6.5 条件治疗 — big_stomach.json（饱食度高时）
````
{
  "id": "big_stomach",
  "name_key": "affix.big_stomach.name",
  "desc_key": "affix.big_stomach.description",
  "effects": [
    { "type": "heal_bonus", "trigger": "heal", "condition": "satiated", "amount": 0.5 }
  ],
  "configurable": [
    { "name": "per_level", "default": 0.5, "min": 0, "max": 5, "percentage": false }
  ]
}
````
### 6.6 属性修改 — nirvana.json
````
{
"id": "nirvana",
"name_key": "affix.nirvana.name",
"desc_key": "affix.nirvana.description",
"effects": [
{ "type": "attribute_modifier", "trigger": "always", "slot": "mainhand",
"attribute": "minecraft:generic.attack_damage", "value": 1.0, "operation": "add" },
{ "type": "attribute_modifier", "trigger": "always", "slot": "mainhand",
"attribute": "minecraft:generic.attack_speed", "value": 0.1, "operation": "add" }
],
"configurable": []
}
````
## 7. trigger 触发类型
| trigger | 说明 |
|---------|------|
| `"attack"` | 攻击命中时 |
| `"heal"` | 治疗 / 自然恢复时 |
| `"always"` | 始终生效 |
| `"durability_change"` | 耐久变化时 |
## 8. condition 条件类型
用于 conditional_multiplier 和 heal_bonus。
| condition | 适用类型 | 说明 |
|-----------|----------|------|
| `"water"` | conditional_multiplier | 攻击者在水中 |
| `"rain"` | conditional_multiplier | 天降雨且攻击者露天 |
| `"sprinting"` | conditional_multiplier | 攻击者疾跑中 |
| `"falling"` | conditional_multiplier | 攻击者下落距离 > 1.5 |
| `"midair"` | conditional_multiplier | 攻击者离地且有下落速度 |
| `"not_sprinting"` | conditional_multiplier | 攻击者未疾跑 |
| `"satiated"` | heal_bonus | 玩家饱食度 ≥ 18 且饱和度 > 0 |
## 9. damage_type 参考
用于 mark_amplify.amplify_damage_types：
minecraft:on_fire              着火伤害
minecraft:in_fire              站在火中
minecraft:wither               凋零
minecraft:magic                魔法
minecraft:drown                溺水
minecraft:freeze               冰冻
minecraft:lightning_bolt       雷击
minecraft:fall                 摔落
minecraft:explosion            爆炸
minecraft:player_explosion     玩家引爆
minecraft:mob_attack           生物近战
minecraft:player_attack        玩家近战
minecraft:arrow                箭矢
minecraft:thrown               投掷物
minecraft:thorns               荆棘
minecraft:sonic_boom           音爆
minecraft:cactus               仙人掌
minecraft:sweet_berry_bush     甜浆果
minecraft:lava                 岩浆
minecraft:hot_floor            岩浆块
## 10. 等级计算规则
印记
全身同名印记的等级 求和 后统一施加到一个印记上。
例：主手 Lv3 + 头盔 Lv2 + 胸甲 Lv1 = 目标挂上 Lv6 毒印记
伤害倍率
全身同名效果的等级 求和，然后 一次计算 倍率。
例：主手 Lv3(momentum) + 腿 Lv2(momentum)
= 伤害 × (1.0 + (3+2) × 0.125)
= 伤害 × 1.625
条件倍率互斥
同一词缀的多个 conditional_multiplier 只取当前活跃的，不同条件不叠加。