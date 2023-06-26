package io.github.tt432.facepop.net;

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
    String faceBag;
    String select;

    public SelectFacePacket(FriendlyByteBuf buf) {
        faceBag = buf.readUtf();
        select = buf.readUtf();
    }

    public SelectFacePacket(String faceBag, String select) {
        this.faceBag = faceBag;
        this.select = select;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(faceBag);
        buf.writeUtf(select);
    }

    public static void handle(SelectFacePacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            // TODO 验证玩家是否有权限使用表情

            ServerPlayer sender = context.getSender();
            ChunkPos chunkPos = sender.chunkPosition();

            NetworkHandler.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() ->
                    sender.level().getChunk(chunkPos.x, chunkPos.z)
            ), new BroadcastFacePacket(msg.faceBag, msg.select, sender.getId()));

            context.setPacketHandled(true);
        });
    }
}
