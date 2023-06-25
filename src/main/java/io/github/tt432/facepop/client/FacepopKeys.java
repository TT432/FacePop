package io.github.tt432.facepop.client;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.tt432.facepop.Facepop;
import io.github.tt432.facepop.data.FaceBag;
import io.github.tt432.facepop.data.FaceBagManager;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * @author TT432
 */
@Mod.EventBusSubscriber(Dist.CLIENT)
public class FacepopKeys {
    public static final KeyMapping faceKey =
            new KeyMapping("key.face_key", InputConstants.KEY_G, "key.categories.face_pop");


    @Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static final class Register {
        @SubscribeEvent
        public static void onEvent(RegisterKeyMappingsEvent event) {
            event.register(faceKey);
        }
    }

    @SubscribeEvent
    public static void onEvent(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) return;

        // TODO 选择表情以及渲染表情
        if (faceKey.isDown()) {
            LocalPlayer player = Minecraft.getInstance().player;
            RegistryAccess registryAccess = player.connection.registryAccess();

            registryAccess.registry(FaceBagManager.FACE_BAG_KEY).ifPresent(registry -> {
                FaceBag aDefault = registry.get(new ResourceLocation(Facepop.MOD_ID, "default"));

                if (aDefault != null) {
                    player.sendSystemMessage(Component.literal(aDefault.faces().get(0).id().toString()));
                }
            });
        }
    }
}
