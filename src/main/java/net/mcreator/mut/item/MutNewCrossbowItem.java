package net.mcreator.mut.item;

/**
 * 具体弩的注册类
 * 所有弩自动继承 NewCrossbowItem 的全部逻辑
 */
public class MutNewCrossbowItem {

    /** 铁弩 */
    public static class IronCrossbowItem extends NewCrossbowItem {
        public IronCrossbowItem() {
            super(net.mcreator.mut.init.MutModItems.IRON_CROSSBOW.get());
        }
    }

    /** 钻石弩 */
    public static class DiamondCrossbowItem extends NewCrossbowItem {
        public DiamondCrossbowItem() {
            super(net.mcreator.mut.init.MutModItems.DIAMOND_CROSSBOW.get());
        }
    }
}