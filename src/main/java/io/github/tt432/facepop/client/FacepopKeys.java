package io.github.tt432.facepop.client;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.tt432.facepop.net.NetworkHandler;
import io.github.tt432.facepop.net.SelectFacePacket;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static net.minecraftforge.client.settings.KeyConflictContext.UNIVERSAL;

/**
 * @author TT432
 */
@Mod.EventBusSubscriber(Dist.CLIENT)
public class FacepopKeys {
    public static final KeyMapping faceKey =
            new KeyMapping("key.face_key", UNIVERSAL,
                    InputConstants.Type.KEYSYM.getOrCreate(InputConstants.KEY_G), "key.categories.face_pop");

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

        Minecraft mc = Minecraft.getInstance();
        if (mc.screen == null && faceKey.isDown()) {
            mc.setScreen(new FaceSelectorScreen());
        }

        boolean faceKeyDown = InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), faceKey.getKey().getValue());

        if (faceKeyDown && InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), InputConstants.KEY_LCONTROL)) {
            mc.setScreen(new FaceSettingScreen());
        }

        if (!faceKeyDown && mc.screen instanceof FaceSelectorScreen fss) {
            int select = fss.getSelect();

            if (select > 0 && select < 6) {
                NetworkHandler.INSTANCE.sendToServer(new SelectFacePacket(select));
            }

            mc.setScreen(null);
        }
    }
}
