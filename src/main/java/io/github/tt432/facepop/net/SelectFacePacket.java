package io.github.tt432.facepop.net;

import io.github.tt432.facepop.common.capability.FaceCapability;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

/**
 * @author TT432
 */
public class SelectFacePacket {
    int select;

    public SelectFacePacket(FriendlyByteBuf buf) {
        select = buf.readInt();
    }

    public SelectFacePacket(int select) {
        this.select = select;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(select);
    }

    public static void handle(SelectFacePacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer sender = context.getSender();
            ChunkPos chunkPos = sender.chunkPosition();

            sender.getCapability(FaceCapability.CAPABILITY).ifPresent(cap ->
                    NetworkHandler.INSTANCE.send(
                            PacketDistributor.TRACKING_CHUNK.with(() -> sender.level().getChunk(chunkPos.x, chunkPos.z)),
                            new BroadcastFacePacket(cap.getFace(msg.select), sender.getId())));

            context.setPacketHandled(true);
        });
    }
}
