package io.github.tt432.facepop.net;

import net.minecraft.network.FriendlyByteBuf;

/**
 * @author TT432
 */
public class BroadcastFacePacket {
    String select;
    int entityId;

    public BroadcastFacePacket(FriendlyByteBuf buf) {
        select =buf.readUtf();
        entityId = buf.readInt();
    }

    public BroadcastFacePacket(String select, int entityId) {
        this.select = select;
        this.entityId = entityId;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(select);
        buf.writeInt(entityId);
    }
}
