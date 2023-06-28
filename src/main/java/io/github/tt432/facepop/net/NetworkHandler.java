package io.github.tt432.facepop.net;

import io.github.tt432.facepop.Facepop;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

/**
 * @author TT432
 */
public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Facepop.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int id;

    public static void registerPackets() {
        INSTANCE.registerMessage(id++, SelectFacePacket.class,
                SelectFacePacket::encode,
                SelectFacePacket::new,
                SelectFacePacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));

        INSTANCE.registerMessage(id++, BroadcastFacePacket.class,
                BroadcastFacePacket::encode,
                BroadcastFacePacket::new,
                ClientPacketHandler::handleBroadcastFacePacket,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));

        INSTANCE.registerMessage(id++, SyncCapabilityPacket.class,
                SyncCapabilityPacket::encode,
                SyncCapabilityPacket::new,
                ClientPacketHandler::handleSyncCapabilityPacket,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT));

        INSTANCE.registerMessage(id++, SetFacePacket.class,
                SetFacePacket::encode,
                SetFacePacket::new,
                SetFacePacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }

    private NetworkHandler() {
        // can't instance
    }
}
