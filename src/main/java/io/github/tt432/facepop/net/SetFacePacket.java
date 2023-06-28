package io.github.tt432.facepop.net;

import io.github.tt432.facepop.common.capability.FaceCapability;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

/**
 * @author TT432
 */
public class SetFacePacket {
    int index;
    String packedFace;

    public SetFacePacket(FriendlyByteBuf buf) {
        index = buf.readInt();
        packedFace = buf.readUtf();
    }

    public SetFacePacket(int select, String packedFace) {
        this.index = select;
        this.packedFace = packedFace;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(index);
        buf.writeUtf(packedFace);
    }

    public static void handle(SetFacePacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer sender = context.getSender();

            sender.getCapability(FaceCapability.CAPABILITY).ifPresent(cap -> {
                cap.trySetPackedFace(msg.index, sender.level().registryAccess(), msg.packedFace);
                NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> sender), new SyncCapabilityPacket(cap.serializeNBT()));
            });

            context.setPacketHandled(true);
        });
    }
}
