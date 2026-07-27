package net.mcreator.mut.affix.json;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.mcreator.mut.MutMod;
import net.mcreator.mut.affix.AffixRegistry;
import net.mcreator.mut.affix.Affix;
import net.mcreator.mut.affix.effect.*;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import java.util.*;

@EventBusSubscriber(modid = MutMod.MODID)
public class AffixJsonLoader extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new Gson();
    private static final AffixJsonLoader INSTANCE = new AffixJsonLoader();
    private static Map<String, AffixJsonConfig> configs = new HashMap<>();
    private static Map<String, List<AffixEffect>> effects = new HashMap<>();


    private AffixJsonLoader() { super(GSON, "affixes"); }

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) { event.addListener(INSTANCE); }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> dataMap, ResourceManager rm, ProfilerFiller profiler) {
        configs.clear(); effects.clear();
        for (var entry : dataMap.entrySet()) {
            AffixJsonConfig cfg = GSON.fromJson(entry.getValue(), AffixJsonConfig.class);
            configs.put(cfg.getId(), cfg);
            effects.put(cfg.getId(), parseEffects(cfg));
        }
        AffixRegistry.reloadFromJson();
        MutMod.LOGGER.info("Loaded {} affix JSON configs", configs.size());
    }

    private static List<AffixEffect> parseEffects(AffixJsonConfig cfg) {
        List<AffixEffect> list = new ArrayList<>();
        for (var e : cfg.getEffects()) {
            switch (e.getType()) {
                case "mark_on_attack" -> list.add(new MarkOnAttackEffect(
                        nvl(e.getTrigger(), "attack"), e.getMarkEffect(), e.getDurationTicks()));
                case "mark_amplify" -> markAmplifyEntries.add(parseMarkAmplify(cfg.getId(), e));
                case "damage_multiplier" -> list.add(new DamageMultiplierEffect(
                        nvl(e.getTrigger(), "attack"), e.getAmount(), e.isRangedOnly()));
                case "heal_bonus" -> {
                    String cond = e.getCondition();
                    list.add(cond != null && !cond.isEmpty()
                            ? new HealBonusEffect(nvl(e.getTrigger(), "heal"), e.getAmount(), cond)
                            : new HealBonusEffect(nvl(e.getTrigger(), "heal"), e.getAmount()));
                }
                case "conditional_multiplier" -> list.add(new ConditionalMultiplierEffect(
                        nvl(e.getTrigger(), "attack"), e.getCondition(), e.getAmount()));
                case "attribute_modifier" -> list.add(new AttributeModifierEffect(
                        "always", e.getSlot(), e.getAttributeId(), e.getValue(), e.getOperation()));
                case "durability_repair" -> list.add(new DurabilityRepairEffect(
                        "durability_change", e.getPerDurability(), e.getSaturationPerPoint()));
            }
        }
        return list;
    }

    private static String nvl(String val, String def) { return val != null ? val : def; }

    public static AffixJsonConfig getConfig(String id) { return configs.get(id); }
    public static List<AffixEffect> getEffects(String id) { return effects.getOrDefault(id, List.of()); }
    public static Collection<AffixJsonConfig> getAllConfigs() { return configs.values(); }
    public static double getDefault(String affixId, String param) {
        var cfg = configs.get(affixId);
        if (cfg == null || cfg.getConfigurable() == null) return 0;
        return cfg.getConfigurable().stream()
                .filter(p -> p.getName().equals(param)).mapToDouble(AffixJsonConfig.ParamDef::getDefaultValue).findFirst().orElse(0);
    }
    private static List<MarkAmplifyEntry> markAmplifyEntries = new ArrayList<>();
    private static MarkAmplifyEntry parseMarkAmplify(String affixId, AffixJsonConfig.EffectEntry e) {
        Holder<net.minecraft.world.effect.MobEffect> effect = BuiltInRegistries.MOB_EFFECT
                .getHolder(ResourceLocation.parse(e.getMarkEffect())).orElse(null);
        List<String> types = new ArrayList<>();
        if (e.getAmplifyDamageTypes() != null) types.addAll(e.getAmplifyDamageTypes());
        return new MarkAmplifyEntry(effect, types, affixId, nvl(e.getCoefficient(), "per_level"));
    }

    public record MarkAmplifyEntry(Holder<MobEffect> markEffect, List<String> damageTypes,
                                   String affixId, String coefficient) {}

    public static List<MarkAmplifyEntry> getMarkAmplifyEntries() { return markAmplifyEntries; }
    @Deprecated
    public static AffixJsonConfig getAffixConfig(String id) { return getConfig(id); }
}