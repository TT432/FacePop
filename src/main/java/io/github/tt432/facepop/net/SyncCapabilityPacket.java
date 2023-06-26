package io.github.tt432.facepop.net;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

/**
 * @author TT432
 */
public class SyncCapabilityPacket {
    CompoundTag tag;


    public SyncCapabilityPacket(FriendlyByteBuf buf) {
        tag = buf.readNbt();
    }

    public SyncCapabilityPacket(CompoundTag tag) {
        this.tag = tag;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeNbt(tag);
    }
}
