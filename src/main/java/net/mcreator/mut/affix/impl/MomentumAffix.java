package net.mcreator.mut.affix.impl;

import net.mcreator.mut.affix.Affix;

public class MomentumAffix implements Affix {

    public static final String AFFIX_ID = "momentum";
    public static final float DAMAGE_PER_LEVEL = 0.25F; // 每层+25%

    @Override
    public String getId() {
        return AFFIX_ID;
    }
}