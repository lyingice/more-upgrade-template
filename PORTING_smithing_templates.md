# 锻造模板移植说明（TemplateCraftRevolution → mut）

来源：`C:\Users\12501\MCreatorWorkspaces\templatecraftrevolution`（TCR，NeoForge 21.1.232 / MC 1.21.1）
目标：`C:\Users\12501\MCreatorWorkspaces\mut`（MCreator 工作区，NeoForge 21.1.190 / MC 1.21.1，命名空间 **mut**）

## 一、移植内容

| 类别 | 源（tcr） | 目标（mut） |
| --- | --- | --- |
| 箭头纹饰锻造模板 | `ArrowheadArmorTrimSmithingTemplate` | `mut:arrowhead_armor_trim_smithing_template` |
| 下界合金纹饰锻造模板 | `NetheriteTrimSmithingTemplate` | `mut:netherite_trim_smithing_template` |
| 下界合金（升级）锻造模板 | `NetheriteUpgradeSmithingTemplate` | `mut:netherite_upgrade_smithing_template` |

附带移植：

- 纹饰数据：`data/mut/trim_pattern/{arrowhead,netherite}.json`、`data/mut/trim_material/netherite.json`
- 配方：`data/mut/recipe/arrowhead_armor_trim.json`、`netherite_smithing_trim.json`
- 标签：`data/mut/tags/item/{netherite_trim_materials,netherite_trimmable_armor,no_extra_trim_template}.json`
- 原版下界合金升级配方放宽（模板同时接受 `minecraft:` 与 `mut:` 两种模板）：
  `data/minecraft/recipe/netherite_*_smithing.json` + 对应 advancement
- 物品模型 / 贴图 / 纹饰贴图 / 调色板（`tcr:` → `mut:`）
- 下界合金纹饰盔甲图标覆盖：40 个纹饰模型 + `assets/minecraft/overrides/item/netherite_*.json`
- 图集合并：`assets/minecraft/atlases/{armor_trims,blocks}.json`
  （完整保留原版条目，仅追加 mut 的 paletted_permutations）
- 语言：追加到 `assets/mutextra/lang/{en_us,zh_cn}.json`（避免被 MCreator 重写）

## 二、MCreator 兼容策略

1. **独立包**：所有新代码位于 `net.mcreator.mut.porting.smithing`（及其 `client` 子包），
   不修改 `net.mcreator.mut.init.*`、`MutModTabs` 等标注 “will be REGENERATED” 的文件。
2. **独立注册器**：`MutSmithingTemplateItems.REGISTRY` 为独立 `DeferredRegister`，
   只在 `MutMod` 的 `// Start of user code block mod init` 中注册（MCreator 保留该用户代码块）。
3. **创造栏**：通过 `BuildCreativeModeTabContentsEvent` 把物品挂到已有的
   `mut:more_upgrade_tmplates` 标签，不编辑 `MutModTabs`。
4. **语言**：写在 `assets/mutextra/lang`（项目既有补充语言包），避免 MCreator 语言文件回写覆盖。
5. **不使用 Mixin**：下界合金纹饰图标覆盖只依赖 NeoForge 的
   `ModelEvent.ModifyBakingResult` + `ModelEvent.RegisterAdditional`，无需修改 `mut.mixins.json`。

## 三、保留但未移植的部分

TCR 的纹饰台、工具纹饰（core）、发光纹饰（glow）、马铠/狼铠纹饰渲染依赖其自定义方块/实体/渲染层，
本次不在移植范围内；因此 `assets/mut/textures/trims/models/horse_armor|wolf_armor` 与对应图集条目未复制。

## 四、验证

- `gradlew compileJava` 通过（临时使用缓存中的 NeoForge 21.1.232 完成编译，随后已还原为 21.1.190）。
- 新增 JSON 全部通过 `ConvertFrom-Json` 语法校验。
- 资源引用命名空间已统一为 `mut`，无残留 `tcr` 引用。

## 五、美术色卡 → 纹饰材质（trim_color）

来源：`C:\Users\12501\Desktop\MUT贴图\美术专栏\trim_color`（10 张 8×1 调色板）。
每张色卡生成一个 `mut:trim_material`，`ingredient` 指向模组**原材料**物品（不是装备）：

| 色卡文件 | trim 材质 id | ingredient（原材料） | 本地化名称 |
| --- | --- | --- | --- |
| 悲悯.png | `mut:crying_obsidian` | `mut:crying_obsidian_ingot` | 悲悯锭 |
| 赤焰金.png | `mut:flame_gold` | `mut:flame_gold_ingot` | 赤焰金锭 |
| 黑曜石.png | `mut:obsidian` | `mut:obsidian_ingot` | 黑曜石锭 |
| 回响合金.png | `mut:echoite` | `mut:echoite_ingot` | 回响合金锭 |
| 金属毒.png | `mut:poison_steel` | `mut:position_steel_ingot` | 毒金属锭 |
| 精钢.png | `mut:advanced_steel` | `mut:advanced_steel_ingot` | 精钢锭 |
| 龙鳞.png | `mut:dragon_scale` | `mut:dragon_scale_cluster` | 龙鳞簇 |
| 异色水晶(饱).png | `mut:heterochromatic_amethyst` | `mut:heterochromatic_amethyst_shard` | 异色紫水晶碎片 |
| 异色水晶(淡).png | `mut:heterochromatic_amethyst_pale` | `mut:heterochromatic_amethyst_shard` | 异色紫水晶碎片 |
| 钻石结晶.png | `mut:refined_diamond_crystals` | `mut:refined_diamond_crystals` | 精炼钻石结晶 |

- 色卡本体：`assets/mut/textures/trims/color_palettes/<asset>.png`
- 材质数据：`data/mut/trim_material/<asset>.json`（`item_model_index` 取 1.1–2.0，避开原版 0.1–1.0）
- 图集：`armor_trims.json` 的三组、`blocks.json` 的两组 `paletted_permutations` 都已补上这 10 个 permutations
- 标签：`data/minecraft/tags/item/trim_materials.json` 追加对应原材料，锻造台才能把它们当 addition 使用
- 语言：`assets/mutextra/lang/{en_us,zh_cn}.json` 的 `trim_material.mut.*`
- 原版下界合金盔甲图标：新增 4 部件 × 10 材质共 40 个 `netherite_<part>_<asset>_trim` 模型，
  并扩展了 `assets/minecraft/overrides/item/netherite_*.json`（`trim_type` 1.1–2.0）

> 说明：MCreator 自定义盔甲（钢、精钢、回响等）的物品图标纹饰模型由用户已有的
> `init/data/MutArmorModelProvider` 生成，当前只包含原版 10 种材质；若要让色卡材质也出现在这些
> 盔甲的物品图标上，在该生成器的 `TRIMS` 列表追加这 10 项后重新跑 datagen 即可（穿戴时的纹饰渲染已生效）。
## 六、马铠 / 狼铠纹饰（动物铠）

来源：TCR 的 `HorseArmorLayerMixin` / `WolfArmorLayerMixin` / `AnimalArmorTrimHelper`。

### 渲染（已去掉发光）

- mixin：`porting/mixin/HorseArmorLayerMixin`、`porting/mixin/WolfArmorLayerMixin`
  注入原版 `HorseArmorLayer#render` / `WolfArmorLayer#render` 的 RETURN，用动物铠模型再渲染一层纹饰。
- 纹饰贴图：`mut:trims/models/{horse_armor|wolf_armor}/{pattern}_{material}`
  （源码 `assets/mut/textures/trims/models/{horse_armor,wolf_armor}/arrowhead.png`）。
- **发光已移除**：TCR 原来用 `GLOW_TRIM` 数据组件把纹饰层渲染成全亮度，
  移植版删掉了这一分支（`MutAnimalArmorTrimRenderer` 始终沿用铠本体的 `packedLight`），
  也不再注册任何发光组件。

### 限制：其它模组的纹饰模板不能应用到动物铠

1. **配方层（天然成立）**：原版 `minecraft:trimmable_armor` 只含 head/chest/leg/foot armor，
   不含马铠/狼铠，因此原版 16 种模板与任何沿用该标签的模组模板都无法应用。
   本模组动物铠走独立配方 `data/mut/recipe/arrowhead_animal_armor_trim.json`，
   base = `mut:animal_armor_trimmable`（48 个马铠/狼铠），template = 箭头模板。
2. **代码兜底**：`SmithingMenuAnimalArmorMixin` 在 `SmithingMenu#createResult` 返回时检查，
   若 base 属于 `mut:animal_armor_trimmable` 且结果带 TRIM 组件、而模板不在
   `mut:allowed_animal_armor_templates` 白名单里，就直接清空结果槽 —— 挡住其它模组显式写 base 的配方。
3. **渲染兜底**：`MutAnimalArmorTrimRenderer` 只渲染白名单模板产生的图案，
   且该图案没有对应动物铠贴图时静默跳过（不会出现紫黑格）。

### 图标

- 960 个图标模型（48 物品 × 20 材质）：`assets/mut/models/item/<item>_<material>_trim.json`，
  layer1 使用 `minecraft:trims/items/{horse,wolf}_trim_<material>`。
- 旁挂覆盖：`assets/mut/overrides/item/*.json`（43 个）与 `assets/minecraft/overrides/item/*.json`（原版 5 个），
  谓词为 `mut:animal_armor_trim`（返回纹饰材质的 `item_model_index`，无纹饰为 0）。
- 材质共 20 种：原版 10 种 + 上一节的 10 张色卡；预烘焙由
  `MutAnimalArmorTrimModelRegistration` 完成（只注册真实存在模型文件的物品）。

### 已放开的图案（原版 18 个模板 + 箭头）

- **贴图**：`assets/mut/textures/trims/models/{horse_armor,wolf_armor}/` 各 19 个图案
  （原版 18 个：`bolt coast dune eye flow host raiser rib sentry shaper silence snout spire tide vex ward wayfinder wild`，
  加上 `arrowhead`），由 `armor_trims.json` 中马铠/狼铠两组 `paletted_permutations` 注册（每组 19 个纹理 × 24 种材质）。
- **配方**：`data/mut/recipe/<pattern>_animal_armor_trim.json` 共 19 条，
  base = `mut:animal_armor_trimmable`、addition = `minecraft:trim_materials`。
  原版模板自身的配方（base = `#minecraft:trimmable_armor`，不含动物铠）没有被动过，
  普通盔甲的纹饰行为与原版一致。
- **白名单**：`mut:allowed_animal_armor_templates` = 箭头模板 + 原版 18 个模板（共 19 项）。

> 与 TCR 的差异：TCR 是把 `minecraft:trimmable_armor` 整体替换成 `#c:horse_armors` + `#c:wolf_armors`，
> 代价是普通盔甲失去原版配方的纹饰途径；本移植改为新增独立配方，普通盔甲不受影响。

### 已知边界

- **下界合金（netherite）图案**没有动物铠贴图（TCR 未提供），因此不在白名单内：
  强行放开会出现"图标有色层、穿戴时不显示图案"的不一致。
- 其它模组自己的动物铠不在 `mut:animal_armor_trimmable` 内，不受本模组限制（也不会生成图标）。
- 其它模组的纹饰模板仍被 `SmithingMenuAnimalArmorMixin` 拦住（不在白名单 → 结果清空）。
## 七、Mixin 清单

配置：`mut.porting.mixins.json`，在 `META-INF/neoforge.mods.toml` 的 user code block 里声明
（不动 MCreator 生成的 `mut.mixins.json`）。

| mixin | 目标 | 作用 |
| --- | --- | --- |
| `ItemCombinerMenuAccessor` | `ItemCombinerMenu` | 读取 `inputSlots` / `resultSlots` |
| `SmithingMenuUpgradeTemplateMixin` | `SmithingMenu#shrinkStackInSlot` | 下界合金（升级）锻造模板**不消耗**，可反复使用 |
| `SmithingMenuAnimalArmorMixin` | `SmithingMenu#createResult` | 动物铠纹饰的模板白名单限制 |
| `HorseArmorLayerMixin` | `HorseArmorLayer#render` | 马铠纹饰层（无发光） |
| `WolfArmorLayerMixin` | `WolfArmorLayer#render` | 狼铠纹饰层（无发光） |

> 不消耗逻辑只针对本模组的 `net.mcreator.mut.porting.smithing.NetheriteUpgradeSmithingTemplate`；
> 原版 `minecraft:netherite_upgrade_smithing_template` 仍然按原版规则被消耗。
## 八、马铠 / 狼铠展示架

来源：TCR 的 `HorseArmorStand` / `WolfArmorStand`（实体 + 物品 + 渲染 + 模型 + 底座 + Jade 集成）。
代码包：`net.mcreator.mut.porting.armorstand`（含 `client.model`、`client.renderer`、`compat.jade`）。

### 实体与物品

| 内容 | id | 说明 |
| --- | --- | --- |
| 马铠展示架实体 | `mut:horse_armor_stand` | 继承 `Horse`，尺寸 0.75×1.5，直接复用原版 HorseModel 与 HorseArmorLayer |
| 狼铠展示架实体 | `mut:wolf_armor_stand` | 继承 `Wolf`，尺寸 0.6×0.9，狼铠用自写铠层 |
| 马铠展示架物品 | `mut:horse_armor_stand` | 放置时沿朝向前移 0.425 格并做旋转碰撞箱检测 |
| 狼铠展示架物品 | `mut:wolf_armor_stand` | 居中放置 |

- 完全静态：无 AI、不可骑乘/驯服/繁殖/喂食/上鞍、无声音、不吃草、狼不坐下、不淋雨发抖。
- 四向旋转吸附 + 旋转碰撞箱；盔甲架式破坏（弓/三叉戟/爆炸直接打碎，玩家攻击需 5 tick 内两击）。
- 破坏或死亡时掉落展示架本体与身上的铠甲；被击中有摇晃动画（EVENT_HIT 同步）。
- 交互：手持对应动物铠右键放入，空手右键取出（不调用 super，阻断原版骑乘/驯服）。

### 渲染

- 本体：原版 `HorseModel` / `StaticWolfModel`（尾巴固定 50°，不再随 ageInTicks 摆动）。
- 底座：自写 base 模型（64×64 UV，木质底座），单独一个渲染层。
- 马铠：直接复用原版 `HorseArmorLayer`，因此纹饰由 `HorseArmorLayerMixin` 自动生效。
- 狼铠：自写 `WolfArmorStandArmorLayer`（底图 + 染色 overlay + 耐久裂纹 + 纹饰），
  纹饰部分调用 `MutAnimalArmorTrimRenderer`（模板白名单、无发光、贴图缺失静默跳过）。

### 资源与集成

- 贴图：`assets/mut/textures/entity/{horse_armor_stand,wolf_armor_stand,wolf_armor_stand_base}.png`、
  `assets/mut/textures/item/misc/{horse_armor_stand,wolf_armor_stand}.png`
- 物品模型：`assets/mut/models/item/{horse_armor_stand,wolf_armor_stand}.json`
- 语言：`assets/mutextra/lang` 的 `item` / `entity` / `tooltip.mut.*`
- Jade：`compat/jade` 的插件与两个 provider（显示穿着中的铠甲、隐藏生命/护甲/马匹状态条目）；
  未安装 Jade 时这些类不会被加载。
- 注册：`MutMod` 的 user code block 中注册 `MutArmorStandItems.ITEMS` 与 `MutArmorStandEntities.ENTITIES`，
  属性在 `MutArmorStandAttributes`（EntityAttributeCreationEvent）注册。

### 模组动物铠贴图兼容（修复）

**问题**：展示架原先照搬 TCR 的写法，用 `AnimalArmorItem#getTexture()` 取铠甲贴图。
原版该方法对 CANINE / EQUESTRIAN 返回的是**固定贴图**，因此模组马铠/狼铠在展示架上会显示成原版外观 ——
狼身上之所以正常，是因为项目自己在 `WolfRenderer` / `HorseRenderer` 上追加了
`MutWolfArmorLayer` / `MutHorseArmorLayer`，而展示架有自己的渲染器，吃不到那两个层。

**修复**：

- 新增 `porting/animalarmor/client/MutAnimalArmorTextures`：按本模组约定探测
  `<ns>:textures/entity/wolf/<item_path>.png`（狼铠）与
  `<ns>:textures/entity/horse/armor/<item_path>.png`（马铠），命中即用；
  缺失（或其它模组不遵循该约定）时回退 `AnimalArmorItem#getTexture()`。不缓存，资源包切换后依然正确。
- 狼铠展示架铠层改用该解析，并补上**附魔光效**（`entityGlintDirect`），与狼身上的渲染保持一致。
- 马铠展示架不再复用原版 `HorseArmorLayer`（同样只会用固定贴图），改用自己的
  `HorseArmorStandArmorLayer`：模组贴图 + 染色 tint + 附魔光效 + 纹饰层。
- 覆盖度核对：本模组 43 件动物铠**全部**解析到专属贴图，原版 5 件按预期回退原版贴图。


### 装备替换（修复：模组马铠"装不上去"）

**问题**：展示架最初照搬 TCR 的交互——只有**空槽**时才装备
（`armor.isEmpty()` + 原版 `equipBodyArmor`）。架子上已经挂着一件铠甲时，
再拿另一件右键不会发生任何事，于是连续更换铠甲时只有第一件能上去，
表现为"精钢、鎏金等大量马铠无法装备上去"。

**定位**：临时写了一段服务端自检并跑了一次 dedicated server，结论是
`horse stand isBodyArmorItem 42 / 42` —— 所有 EQUESTRIAN 动物铠都能通过原版装备判定，
说明问题不在"识别不了模组马铠"，而在槽位已占用时没有替换路径。

**修复**：两端展示架都新增 `tryEquipAnimalArmor(held, player)`：

| 情形 | 行为 |
| --- | --- |
| 槽位为空 | 直接 `setBodyArmorItem` 并消耗 1 个（不再走原版 `equipBodyArmor`） |
| 已挂不同铠甲 | 先把旧的弹到架子上方，再装新的 |
| 已挂同一件铠甲 | 不做处理（避免无意义消耗） |

自检结果：空槽装备 ✓ / 替换装备 ✓ / 同件重复不消耗 ✓ / 狼铠替换 ✓。


### 狼铠体类型修正（修复：钢狼铠等右键空架没反应）

**问题**：18 个"狼铠"物品类把 `BodyType` 传成了 `EQUESTRIAN`（马），
而狼铠架与狼的判定都要求 `CANINE`，所以这些狼铠对着空展示架右键毫无反应。
恰好能用的铜 / 铁 / 金 / 钻石 / 下界合金狼铠，传的正是 `CANINE`。

**修正**：把下面 18 个类的 `BodyType.EQUESTRIAN` 改为 `BodyType.CANINE`：
`AdvancedSteel`、`Amethyst`、`BlueDiamond`、`CryingObsidian`、`Dragon`、`Emerald`、`Gilding`、
`LapisLazuli`、`NetheriteAmethyst`、`NetheriteCopper`、`NetheriteEmerald`、`NetheriteLapisLazuli`、
`NetheriteObsidian`、`NetheriteRedstone`、`NetherStar`、`Obsidian`、`Steel`、`Wither` 狼铠。
马铠类（含 `BaseAnimalArmorItem` 里的 `*HorseArmorItem` 嵌套类）保持 `EQUESTRIAN` 不变。

**附带效果**：

- 这些狼铠现在也能通过**原版判定**装到真狼身上（此前只能靠 `WolfArmorMixin` 的标签路径绕过）。
- 反向地，它们不再能（错误地）装到马身上。
- 纹饰/贴图不受影响：狼铠架走 `MutAnimalArmorTextures`（按 `textures/entity/wolf/<id>.png` 解析），
  真狼走 `MutWolfArmorLayer` 的 id→贴图映射。

**服务端自检**（临时跑 dedicated server 后已清理）：`wolf armor equippable 23/23`、`horse armor equippable 20/20`。


### 未移植 / 注意

- TCR 用展示架实体做**锻造台 GUI 预览**的 `SmithingScreenMixin` 没有移植（纹饰台也未移植）。
- 展示架**没有合成配方**（TCR 同样没有数据包配方），目前通过创造模式标签
  `mut:more_upgrade_template_extra_items` 获取。需要配方可以另外补。

> **重要修复**：1.21.1 的 `@EventBusSubscriber` 默认总线是 `Bus.GAME`，
> 而此前几轮照抄 TCR 写法的 6 个类订阅的都是 mod 总线事件（`FMLClientSetupEvent`、
> `ModelEvent.*`、`BuildCreativeModeTabContentsEvent`、`EntityAttributeCreationEvent`），
> 现已全部显式补上 `bus = EventBusSubscriber.Bus.MOD`。