package io.github.tt432.facepop.common.capability;

import io.github.tt432.facepop.net.NetworkHandler;
import io.github.tt432.facepop.net.SyncCapabilityPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

/**
 * @author TT432
 */
@Mod.EventBusSubscriber
public class CapabilityDataHandler {
    @SubscribeEvent
    public static void onEvent(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayer p) {
            p.getCapability(FaceCapability.CAPABILITY).ifPresent(cap ->
                    NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> p), new SyncCapabilityPacket(cap.serializeNBT())));
        }
    }

    @SubscribeEvent
    public static void onEvent(PlayerEvent.Clone event) {
        event.getEntity().getCapability(FaceCapability.CAPABILITY)
                .ifPresent(cap -> event.getOriginal().getCapability(FaceCapability.CAPABILITY)
                        .ifPresent(capOriginal -> cap.deserializeNBT(capOriginal.serializeNBT())));
    }
}
