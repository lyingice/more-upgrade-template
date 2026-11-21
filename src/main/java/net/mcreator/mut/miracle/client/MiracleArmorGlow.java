package net.mcreator.mut.miracle.client;

import com.mojang.blaze3d.platform.NativeImage;
import net.mcreator.mut.MutMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 * 奇迹护甲的"生长中"贴图。
 * <p>
 * 盔甲层贴图（64×32）走的是独立贴图路径，.mcmeta 动画对它无效。所以这里不等贴图系统，
 * 改为每 tick 现场算一张 64×32 的图丢给 {@link DynamicTexture}，再由
 * {@code Item#getArmorTexture} 交给游戏渲染 —— 效果和像素工坊里那套
 * "彩色渐变叠加 + 滚动 + 着色（PS Color）"完全一致：
 * <pre>
 *   hue = 沿 Y 轴滚动的色带（默认 360° 一圈，每 tick 走 0.5 像素）
 *   sat = 0.85
 *   lum = 原贴图该像素的 HSL 亮度
 *   结果 = hsl2rgb(hue, sat, lum)
 * </pre>
 */
@EventBusSubscriber(modid = MutMod.MODID, value = Dist.CLIENT)
public final class MiracleArmorGlow {

    private static final int W = 64;
    private static final int H = 32;

    /** 饱和度（与工坊默认一致） */
    private static final float SATURATION = 0.85F;
    /** 每个 client tick 沿 Y 滚动的像素数（工坊里 frametime=2、每帧 1 格） */
    private static final float SCROLL_PER_TICK = 0.5F;

    private static final ResourceLocation OUTER_ID =
            ResourceLocation.fromNamespaceAndPath(MutMod.MODID, "dynamic/miracle_armor_outer");
    private static final ResourceLocation INNER_ID =
            ResourceLocation.fromNamespaceAndPath(MutMod.MODID, "dynamic/miracle_armor_inner");

    private static NativeImage outerBase;
    private static NativeImage innerBase;
    private static DynamicTexture outerLive;
    private static DynamicTexture innerLive;

    private static float phase = 0.0F;
    private static boolean initialised = false;

    /**
     * @param innerModel true = 护腿用的 layer_2，false = 头盔/胸甲/靴子用的 layer_1
     * @return 动态贴图 id；资源还没就绪时返回 null，让游戏回落到默认贴图
     */
    @Nullable
    public static ResourceLocation texture(boolean innerModel) {
        ensure();
        if (!initialised) return null;
        return innerModel ? INNER_ID : OUTER_ID;
    }

    private static void ensure() {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.getResourceManager() == null) return;

        if (!initialised) {
            outerBase = load(mc, "textures/models/armor/miracle_layer_1.png");
            innerBase = load(mc, "textures/models/armor/miracle_layer_2.png");
            if (outerBase == null || innerBase == null) return;

            outerLive = register(mc, OUTER_ID, outerBase);
            innerLive = register(mc, INNER_ID, innerBase);
            initialised = true;
            refresh();
            return;
        }

        // 资源重载（F3+T）后动态贴图会掉，掉一次补一次
        if (mc.getTextureManager().getTexture(OUTER_ID, null) == null
                || mc.getTextureManager().getTexture(INNER_ID, null) == null) {
            outerLive = register(mc, OUTER_ID, outerBase);
            innerLive = register(mc, INNER_ID, innerBase);
            refresh();
        }
    }

    @Nullable
    private static NativeImage load(Minecraft mc, String path) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(MutMod.MODID, path);
        Optional<Resource> resource = mc.getResourceManager().getResource(id);
        if (resource.isEmpty()) {
            MutMod.LOGGER.warn("Missing armor layer texture {}", id);
            return null;
        }
        try (InputStream in = resource.get().open()) {
            return NativeImage.read(in);
        } catch (IOException e) {
            MutMod.LOGGER.warn("Failed to read armor layer texture {}", id, e);
            return null;
        }
    }

    private static DynamicTexture register(Minecraft mc, ResourceLocation id, NativeImage base) {
        NativeImage work = new NativeImage(base.getWidth(), base.getHeight(), false);
        DynamicTexture texture = new DynamicTexture(work);
        mc.getTextureManager().register(id, texture);
        return texture;
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        ensure();
        if (!initialised) return;
        phase = (phase + SCROLL_PER_TICK) % H;
        refresh();
    }

    private static void refresh() {
        if (outerLive == null || innerLive == null) return;
        paint(outerBase, outerLive);
        paint(innerBase, innerLive);
    }

    /** 逐像素把彩色渐变按 PS Color 的方式叠到原贴图亮度上 */
    private static void paint(NativeImage base, DynamicTexture live) {
        NativeImage out = live.getPixels();
        for (int y = 0; y < H; y++) {
            // 滚动：色带沿 Y 轴下移，H 像素一循环
            float t = mod(y - phase, H) / (float) H;
            float hue = t;
            for (int x = 0; x < W; x++) {
                // NativeImage 的像素是 ABGR 打包
                int abgr = base.getPixelRGBA(x, y);
                int a = (abgr >>> 24) & 0xFF;
                if (a == 0) {
                    out.setPixelRGBA(x, y, 0);
                    continue;
                }
                float r = ((abgr >>> 16) & 0xFF) / 255.0F;
                float g = ((abgr >>> 8) & 0xFF) / 255.0F;
                float b = (abgr & 0xFF) / 255.0F;

                // PS Color：取渐变的色相/饱和度，保留底图的亮度
                float lum = ((Math.max(r, Math.max(g, b))) + (Math.min(r, Math.min(g, b)))) * 0.5F;
                int rgb = hslToRgbPacked(hue, SATURATION, lum);

                out.setPixelRGBA(x, y, (a << 24) | rgb);
            }
        }
        live.upload();
    }

    private static float mod(float v, float m) {
        float r = v % m;
        return r < 0 ? r + m : r;
    }

    /** 返回 ABGR 里低 24 位（0x00BBGGRR）的打包色 */
    private static int hslToRgbPacked(float h, float s, float l) {
        if (s <= 0.0F) {
            int v = Math.round(l * 255.0F);
            return (v << 16) | (v << 8) | v;
        }
        float q = l < 0.5F ? l * (1.0F + s) : l + s - l * s;
        float p = 2.0F * l - q;
        int r = Math.round(f(h + 1.0F / 3.0F, p, q) * 255.0F);
        int g = Math.round(f(h, p, q) * 255.0F);
        int b = Math.round(f(h - 1.0F / 3.0F, p, q) * 255.0F);
        return (b << 16) | (g << 8) | r;
    }

    private static float f(float t, float p, float q) {
        float tt = t % 1.0F;
        if (tt < 0) tt += 1.0F;
        if (tt < 1.0F / 6.0F) return p + (q - p) * 6.0F * tt;
        if (tt < 1.0F / 2.0F) return q;
        if (tt < 2.0F / 3.0F) return p + (q - p) * (2.0F / 3.0F - tt) * 6.0F;
        return p;
    }

    private MiracleArmorGlow() {
    }
}
