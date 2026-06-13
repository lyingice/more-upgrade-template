package net.mcreator.mut.item;

import net.mcreator.mut.init.MutMaterials;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

public abstract class EchoiteTools extends Item {
    public EchoiteTools(Properties p) { super(p); }

    public static class Sword extends SwordItem implements ISonicBoomSword{
        public Sword() {
            super(MutMaterials.ECHOITE.asToolTier(MutMaterials.ToolType.SWORD),
                    MutMaterials.ECHOITE.createToolProperties(MutMaterials.ToolType.SWORD));
        }
        @Override
        public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
            return useSonicBoom(level, player, hand);
        }
    }
    public static class Shovel extends ShovelItem {
        public Shovel() {
            super(MutMaterials.ECHOITE.asToolTier(MutMaterials.ToolType.SHOVEL),
                    MutMaterials.ECHOITE.createToolProperties(MutMaterials.ToolType.SHOVEL));
        }
    }
    public static class Pickaxe extends PickaxeItem {
        public Pickaxe() {
            super(MutMaterials.ECHOITE.asToolTier(MutMaterials.ToolType.PICKAXE),
                    MutMaterials.ECHOITE.createToolProperties(MutMaterials.ToolType.PICKAXE));
        }
    }
    public static class Axe extends AxeItem {
        public Axe() {
            super(MutMaterials.ECHOITE.asToolTier(MutMaterials.ToolType.AXE),
                    MutMaterials.ECHOITE.createToolProperties(MutMaterials.ToolType.AXE));
        }
    }
    public static class Hoe extends HoeItem {
        public Hoe() {
            super(MutMaterials.ECHOITE.asToolTier(MutMaterials.ToolType.HOE),
                    MutMaterials.ECHOITE.createToolProperties(MutMaterials.ToolType.HOE));
        }
    }
}
