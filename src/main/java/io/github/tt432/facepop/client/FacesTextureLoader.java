package io.github.tt432.facepop.client;

import io.github.tt432.facepop.Facepop;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * @author DustW
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FacesTextureLoader extends TextureAtlasHolder {
    private static FacesTextureLoader instance;

    private FacesTextureLoader(TextureManager pTextureManager) {
        super(pTextureManager, new ResourceLocation(Facepop.MOD_ID, "textures/atlas/facepop.png"),
                new ResourceLocation(Facepop.MOD_ID,"faces"));
    }

    @SubscribeEvent
    public static void onEvent(RegisterClientReloadListenersEvent event) {
        if (instance == null) {
            instance = new FacesTextureLoader(Minecraft.getInstance().getTextureManager());
        }

        event.registerReloadListener(instance);
    }

    public static FacesTextureLoader getInstance() {
        return instance;
    }

    public TextureAtlasSprite get(ResourceLocation id) {
        return getSprite(id);
    }
}
