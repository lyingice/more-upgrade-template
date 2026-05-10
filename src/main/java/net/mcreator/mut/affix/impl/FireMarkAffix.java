package net.mcreator.mut.affix.impl;

import net.mcreator.mut.affix.Affix;

public class FireMarkAffix implements Affix {

    public static final String AFFIX_ID = "fire_mark";

    // 印记持续时间（30秒 = 600 tick）
    public static final int MARK_DURATION_TICKS = 600;

    @Override
    public String getId() {
        return AFFIX_ID;
    }
}