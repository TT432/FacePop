package io.github.tt432.facepop.net;

import io.github.tt432.facepop.client.FaceRenderHandler;
import io.github.tt432.facepop.common.capability.FaceCapability;
import io.github.tt432.facepop.data.Face;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
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

            Face face = FaceCapability.unpackFace(registryAccess, msg.select);

            if (face != null) {
                Entity entity = mc.level.getEntity(msg.entityId);

                if (entity instanceof Player) {
                    FaceRenderHandler.putRenderData(msg.entityId, face);
                }
            }

            context.setPacketHandled(true);
        });
    }

    public static void handleSyncCapabilityPacket(SyncCapabilityPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            Minecraft.getInstance().player.getCapability(FaceCapability.CAPABILITY).ifPresent(cap -> cap.deserializeNBT(msg.tag));

            context.setPacketHandled(true);
        });
    }

    private ClientPacketHandler() {
        // can't instance...
    }
}
