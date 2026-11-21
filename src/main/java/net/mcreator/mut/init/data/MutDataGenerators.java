package net.mcreator.mut.init.data;

import net.mcreator.mut.MutMod;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = MutMod.MODID)
public class MutDataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        event.createBlockAndItemTags(
                (output, lookupProvider) -> new MutArmorBlockTagsProvider(output, lookupProvider, event.getExistingFileHelper()),
                (itemOutput, lookupProvider, blockTags) ->
                        new MutArmorTagProvider(itemOutput, lookupProvider, blockTags, event.getExistingFileHelper())
        );
        event.createProvider(output ->
                new MutArmorModelProvider(output, event.getExistingFileHelper())
        );
    }
}
