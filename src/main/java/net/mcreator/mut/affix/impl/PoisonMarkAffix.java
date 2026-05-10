package net.mcreator.mut.affix.impl;

import net.mcreator.mut.affix.Affix;

/**
 * 剧毒印记词条实现
 */
public class PoisonMarkAffix implements Affix {

    // 词条ID
    public static final String AFFIX_ID = "poison_mark";

    // 印记持续时间（30秒 = 600 tick）
    public static final int MARK_DURATION_TICKS = 600;

    @Override
    public String getId() {
        return AFFIX_ID;
    }
}