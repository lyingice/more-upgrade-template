package net.mcreator.mut.porting.ruingolem;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;

/** 失控的黑曜石傀儡：500 血 / 20 护甲 / 18 攻 / 铁傀儡移速。 */
public class ObsidianRuinGolemEntity extends AbstractRuinGolemEntity {

    public ObsidianRuinGolemEntity(EntityType<? extends ObsidianRuinGolemEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return golemBaseAttributes(500.0D, 20.0D, 0.0D, 18.0D, 1.0D);
    }
}
