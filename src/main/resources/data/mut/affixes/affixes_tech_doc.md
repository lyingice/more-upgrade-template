# 词缀 JSON 技术文档

## 目录

1. [文件结构](#1-文件结构)
2. [顶层字段](#2-顶层字段)
3. [Effect 效果类型](#3-effect-效果类型)
4. [Effect 通用字段](#4-effect-通用字段)
5. [configurable 可配置参数](#5-configurable-可配置参数)
6. [完整示例](#6-完整示例)
7. [trigger 触发类型](#7-trigger-触发类型)
8. [damage_type 参考](#8-damage_type-参考)

---

## 1. 文件结构

data/mut/affixes/<词缀id>.json

一个文件定义一个词缀，文件名建议与 id 一致，支持 /reload 热重载。

---

## 2. 顶层字段

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | string | 是 | 唯一标识，如 "fire_mark" |
| name_key | string | 是 | 名称翻译键 |
| desc_key | string | 是 | 描述翻译键 |
| effects | array | 是 | 效果列表，至少 1 个 |
| configurable | array | 否 | 玩家可调参数，空=无 |

---

## 3. Effect 效果类型

### 3.1 mark_on_attack — 施加印记

攻击时给目标施加 mob effect（仅标记，不扣血）。

字段:
type       = "mark_on_attack"
trigger    = "attack"
mark_effect  资源位置 必填  施加的药水效果 ID，如 "mut:fire_mark"
duration_ticks  int  默认600  持续 tick（20 tick = 1秒）

### 3.2 mark_amplify — 印记放大原始伤害

实体受指定伤害类型时，按印记等级放大伤害。

字段:
type                = "mark_amplify"
mark_effect           资源位置 必填  检查的药水效果 ID
amplify_damage_types  string[] 必填  匹配的伤害类型列表
coefficient          string  默认"per_level"  对应 configurable 的 name

伤害公式: 原伤害 + 印记等级 × 系数
印记等级 = amplifier + 1

### 3.3 damage_multiplier — 伤害倍率

攻击伤害乘以倍率。

字段:
type    = "damage_multiplier"
trigger = "attack"
amount  double 默认0.5  每级倍率增量

公式: 伤害 × (1.0 + 词缀等级 × amount)

适用: momentum, strength_blessing, sharpshooter, piercing_spear

### 3.4 heal_bonus — 治疗加成

自然恢复额外回血。

字段:
type    = "heal_bonus"
trigger = "heal"
amount  double 默认0.5  每级增量

公式: 回复量 + 词缀等级 × amount

适用: regeneration_mark, big_stomach

### 3.5 conditional_multiplier — 条件倍率

满足环境条件时生效的倍率。

字段:
type      = "conditional_multiplier"
trigger   = "attack"
condition string 必填  条件: water/rain/sprinting/falling
amount    double 默认0.5  每级倍率增量

condition 值:
water     在水中
rain      天降雨且露天
sprinting 疾跑
falling   下落距离>1.5

适用: tidal_surge

### 3.6 attribute_modifier — 属性修改器

装备时永久改物品属性。

字段:
type      = "attribute_modifier"
trigger   = "always"
slot      string 必填  mainhand/offhand/head/chest/legs/feet
attribute 资源位置 必填  属性 ID
value     double 默认1.0  修改值
operation string 默认"add"  add/multiply_base/multiply_total

常用属性 ID:
minecraft:generic.attack_damage
minecraft:generic.attack_speed
minecraft:generic.armor
minecraft:generic.armor_toughness
minecraft:generic.max_health
minecraft:generic.movement_speed
minecraft:generic.knockback_resistance
minecraft:player.block_interaction_range
minecraft:player.entity_interaction_range
minecraft:generic.luck

### 3.7 durability_repair — 耐久恢复

消耗耐久时恢复耐久。

字段:
type                = "durability_repair"
trigger             = "durability_change"
per_durability      int 默认1  每消耗n耐久触发
saturation_per_point int 默认4  每点耐久消耗的饱食度

适用: energy_conversion

---

## 4. Effect 通用字段

所有 effect 支持:
type    string 必填  效果类型(§3)
trigger string 见各类型  触发时机(§7)

---

## 5. configurable 可配置参数

字段:
name       string 必填  参数名
default    double 必填  默认值
min        double 默认0  最小值
max        double 默认10 最大值
percentage bool   默认false  显示为百分比(×100+"%")

name 与 effect 对应:
per_level  → 一般 coefficient/amount
stab       → piercing_spear 戳击
charge     → piercing_spear 冲刺
water      → tidal_surge 水中
rain       → tidal_surge 雨天

---

## 6. 完整示例

### 6.1 印记类 (fire_mark)

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

### 6.2 倍率类 (momentum)

{
"id": "momentum",
"name_key": "affix.momentum.name",
"desc_key": "affix.momentum.description",
"effects": [
{
"type": "damage_multiplier",
"trigger": "attack",
"amount": 0.125
}
],
"configurable": [
{ "name": "per_level", "default": 0.125, "min": 0, "max": 1, "percentage": true }
]
}

### 6.3 条件倍率 (tidal_surge)

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

### 6.4 属性修改 (nirvana)

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

---

## 7. trigger 触发类型

attack             攻击命中时
heal               自然恢复时
always             始终生效
durability_change  耐久变化时

mark_amplify 不通过 trigger 触发，由 hurt() 中伤害类型自动匹配。

---

## 8. damage_type 参考

mark_amplify.amplify_damage_types 可用值:

minecraft:on_fire           着火
minecraft:in_fire           站在火里
minecraft:wither            凋零
minecraft:magic             魔法
minecraft:drown             溺水
minecraft:freeze            冰冻
minecraft:lightning_bolt    雷击
minecraft:fall              摔落
minecraft:explosion         爆炸
minecraft:player_explosion  玩家爆炸
minecraft:mob_attack        生物近战
minecraft:player_attack     玩家近战
minecraft:arrow             箭矢
minecraft:thrown            投掷物
minecraft:thorns            荆棘
minecraft:sonic_boom        音爆
minecraft:cactus            仙人掌
minecraft:sweet_berry_bush  甜浆果
minecraft:lava              岩浆
minecraft:hot_floor          岩浆块

中毒特别注意：NeoForge 中毒类型为 neoforge:poison，需用完整 ID:
"amplify_damage_types": ["neoforge:poison"]