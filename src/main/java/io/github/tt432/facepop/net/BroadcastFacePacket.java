package io.github.tt432.facepop.net;

import net.minecraft.network.FriendlyByteBuf;

/**
 * @author TT432
 */
public class BroadcastFacePacket {
    String faceBag;
    String select;
    int entityId;

    public BroadcastFacePacket(FriendlyByteBuf buf) {
        faceBag =buf.readUtf();
        select =buf.readUtf();
        entityId = buf.readInt();
    }

    public BroadcastFacePacket(String faceBag, String select, int entityId) {
        this.faceBag = faceBag;
        this.select = select;
        this.entityId = entityId;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(faceBag);
        buf.writeUtf(select);
        buf.writeInt(entityId);
    }
}
