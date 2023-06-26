package io.github.tt432.facepop.net;

import io.github.tt432.facepop.client.FaceRenderHandler;
import io.github.tt432.facepop.data.Face;
import io.github.tt432.facepop.data.FaceBag;
import io.github.tt432.facepop.data.FaceBagManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * @author TT432
 */
public class ClientPacketHandler {
    public static void handleBroadcastFacePacket(BroadcastFacePacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            RegistryAccess registryAccess = mc.level.registryAccess();

            registryAccess.registry(FaceBagManager.FACE_BAG_KEY).ifPresent(registry -> {
                FaceBag faceBag = registry.get(new ResourceLocation(msg.faceBag));

                if (faceBag != null) {
                    Face targetFace = null;

                    for (Face face : faceBag.faces()) {
                        if (face.id().toString().equals(msg.select)) {
                            targetFace = face;
                            break;
                        }
                    }

                    if (targetFace != null) {
                        Entity entity = mc.level.getEntity(msg.entityId);

                        if (entity instanceof Player) {
                            FaceRenderHandler.putRenderData(msg.entityId, targetFace);
                        }
                    }
                }
            });

            context.setPacketHandled(true);
        });
    }

    private ClientPacketHandler() {
        // can't instance...
    }
}
