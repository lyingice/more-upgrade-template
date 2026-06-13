package net.mcreator.mut.affix.data;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 物品→可用词缀绑定配置 POJO - 从 item_affix_bindings.json 加载
 */
public class ItemAffixBindingConfig {

    private List<Binding> bindings;

    public List<Binding> getBindings() { return bindings; }
    public void setBindings(List<Binding> bindings) { this.bindings = bindings; }

    public static class Binding {
        @Nullable
        private String item;
        @Nullable
        private String tag;
        @SerializedName("affix_pool")
        private List<String> affixPool;

        @Nullable
        public String getItem() { return item; }
        @Nullable
        public String getTag() { return tag; }
        public List<String> getAffixPool() { return affixPool; }

        public void setItem(@Nullable String item) { this.item = item; }
        public void setTag(@Nullable String tag) { this.tag = tag; }
        public void setAffixPool(List<String> affixPool) { this.affixPool = affixPool; }
    }
}
