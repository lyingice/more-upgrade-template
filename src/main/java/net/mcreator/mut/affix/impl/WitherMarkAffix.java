package net.mcreator.mut.affix.impl;

import net.mcreator.mut.affix.Affix;

public class WitherMarkAffix implements Affix {

    public static final String AFFIX_ID = "wither_mark";
    public static final int MARK_DURATION_TICKS = 600;

    @Override
    public String getId() {
        return AFFIX_ID;
    }
}