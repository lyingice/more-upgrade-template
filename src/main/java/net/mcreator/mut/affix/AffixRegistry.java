package net.mcreator.mut.affix;

import net.mcreator.mut.affix.json.AffixJsonLoader;
import net.mcreator.mut.affix.json.AffixJsonConfig;
import javax.annotation.Nullable;
import java.util.*;

public class AffixRegistry {

    private static final Map<String, Affix> AFFIXES = new LinkedHashMap<>();

    // ★ 由 AffixJsonLoader.apply() 调用，不再手动 new
    public static void reloadFromJson() {
        AFFIXES.clear();
        for (AffixJsonConfig cfg : AffixJsonLoader.getAllConfigs()) {
            register(new JsonAffix(cfg));
        }
    }

    private static void register(Affix affix) {
        AFFIXES.put(affix.getId(), affix);
    }

    @Nullable
    public static Affix get(String id) { return AFFIXES.get(id); }

    public static Collection<Affix> getAll() {
        return Collections.unmodifiableCollection(new ArrayList<>(AFFIXES.values()));
    }

    // ★ 轻量实现，所有数据来自 JSON
    private static class JsonAffix implements Affix {
        private final AffixJsonConfig config;

        JsonAffix(AffixJsonConfig config) { this.config = config; }

        @Override public String getId() { return config.getId(); }

        @Override
        public String getNameTranslationKey() { return config.getNameKey(); }

        @Override
        public String getDescriptionTranslationKey() { return config.getDescKey(); }
    }
}