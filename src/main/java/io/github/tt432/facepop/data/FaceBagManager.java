package io.github.tt432.facepop.data;

import io.github.tt432.facepop.Facepop;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DataPackRegistryEvent;

/**
 * @author TT432
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class FaceBagManager {
    public static final ResourceKey<Registry<FaceBag>> FACE_BAG_KEY = ResourceKey
            .createRegistryKey(new ResourceLocation(Facepop.MOD_ID, "facebag"));

    @SubscribeEvent
    public static void onEvent(DataPackRegistryEvent.NewRegistry event){
        event.dataPackRegistry(FACE_BAG_KEY, FaceBag.CODEC, FaceBag.CODEC);
    }

    private FaceBagManager() {
        // can't instance manager class
    }
}
