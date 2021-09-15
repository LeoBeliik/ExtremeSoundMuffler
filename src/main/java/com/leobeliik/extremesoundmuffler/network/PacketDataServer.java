package com.leobeliik.extremesoundmuffler.network;

import com.leobeliik.extremesoundmuffler.interfaces.IAnchorList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.network.NetworkEvent;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class PacketDataServer implements IAnchorList {

    private final CompoundTag data;

    PacketDataServer(FriendlyByteBuf buf) {
        data = buf.readNbt();
    }

    public PacketDataServer(CompoundTag data) {
        this.data = data;
    }

    void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(data);
    }

    @SuppressWarnings("SameReturnValue")
    boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer sender = ctx.get().getSender();
            if (sender != null) {
                IntStream.rangeClosed(0, 9).forEach(i ->
                        sender.getPersistentData().put("anchor" + i, Objects.requireNonNull(data.get("anchor" + i))));
            }
        });
        return true;
    }
}