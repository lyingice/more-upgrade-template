package net.mcreator.mut.compat.appleskin;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;

import squeek.appleskin.api.event.FoodValuesEvent;

@EventBusSubscriber(modid = "mut")
public class AppleSkinIntegration {

    private static final ResourceLocation GOURMET_FEAST_ENCHANT = ResourceLocation.parse("mut:gourmet_feast");

    @SubscribeEvent
    public static void onFoodValues(FoodValuesEvent event) {
        Player player = event.player;
        ItemStack itemStack = event.itemStack;

        if (player == null || !player.level().isClientSide()) {
            return;
        }

        int enchantLevel = getGourmetFeastLevel(itemStack, player);
        if (enchantLevel == 0) {
            return;
        }

        FoodProperties original = event.defaultFoodProperties;
        if (original == null) {
            return;
        }

        int originalNutrition = original.nutrition();
        int newNutrition = originalNutrition + enchantLevel * 2;

        // 计算等效 saturationModifier，使 AppleSkin 显示的饱和度恢复值接近实际
        // 实际总饱和度恢复 = (原saturationModifier * 2 * 原nutrition) + 平加(level * 4)
        // AppleSkin 显示 = 新saturationModifier * 2 * 新nutrition
        // 令两者相等 => 新saturationModifier = (原总恢复 + 平加值) / (2 * 新nutrition)
        float originalSaturationRestored = original.saturation() * 2 * originalNutrition;
        float actualTotalSaturation = originalSaturationRestored + enchantLevel * 4;
        float newSaturationModifier = actualTotalSaturation / (2 * newNutrition);

        FoodProperties.Builder builder = new FoodProperties.Builder()
                .nutrition(newNutrition)
                .saturationModifier(newSaturationModifier);

        if (original.canAlwaysEat()) {
            builder.alwaysEdible();
        }

        for (FoodProperties.PossibleEffect possibleEffect : original.effects()) {
            builder.effect(possibleEffect.effect(), possibleEffect.probability());
        }

        event.modifiedFoodProperties = builder.build();
    }

    private static int getGourmetFeastLevel(ItemStack itemStack, Player player) {
        if (itemStack == null || itemStack.isEmpty()) {
            return 0;
        }

        try {
            var enchantmentLookup = player.level().registryAccess()
                    .lookupOrThrow(Registries.ENCHANTMENT);
            var enchantmentHolder = enchantmentLookup
                    .getOrThrow(ResourceKey.create(Registries.ENCHANTMENT, GOURMET_FEAST_ENCHANT));
            return itemStack.getEnchantmentLevel(enchantmentHolder);
        } catch (Exception e) {
            return 0;
        }
    }
}
