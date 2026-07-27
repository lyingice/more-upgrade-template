package net.mcreator.mut.affix;

import net.mcreator.mut.affix.impl.*;
import javax.annotation.Nullable;
import java.util.*;

public class AffixRegistry {

    private static final Map<String, Affix> AFFIXES = new LinkedHashMap<>();

    // ========== 注册所有词缀 ==========

    public static final Affix POISON_MARK       = register(new PoisonMarkAffix());
    public static final Affix FIRE_MARK         = register(new FireMarkAffix());
    public static final Affix WITHER_MARK       = register(new WitherMarkAffix());
    public static final Affix NIRVANA           = register(new NirvanaAffix());
    public static final Affix MOMENTUM          = register(new MomentumAffix());
    public static final Affix REGENERATION_MARK = register(new RegenerationMarkAffix());
    public static final Affix PIERCING_SPEAR    = register(new PiercingSpearAffix());
    public static final Affix ENERGY_CONVERSION = register(new EnergyConversionAffix());
    public static final Affix SHARPSHOOTER      = register(new SharpshooterAffix());
    public static final Affix STRENGTH_BLESSING = register(new StrengthBlessingAffix());
    public static final Affix TIDAL_SURGE       = register(new TidalSurgeAffix());
    public static final Affix BIG_STOMACH       = register(new BigStomachAffix());

    private static Affix register(Affix affix) {
        AFFIXES.put(affix.getId(), affix);
        return affix;
    }

    @Nullable
    public static Affix get(String id) { return AFFIXES.get(id); }

    public static Collection<Affix> getAll() {
        return Collections.unmodifiableCollection(new ArrayList<>(AFFIXES.values()));
    }
}
