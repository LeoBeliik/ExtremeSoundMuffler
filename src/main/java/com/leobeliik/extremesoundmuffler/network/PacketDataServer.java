package com.leobeliik.extremesoundmuffler.network;

import com.leobeliik.extremesoundmuffler.interfaces.IAnchorList;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class PacketDataServer implements IAnchorList {

    private final CompoundNBT data;

    PacketDataServer(PacketBuffer buf) {
        data = buf.readCompoundTag();
    }

    public PacketDataServer(CompoundNBT data) {
        this.data = data;
    }

    void toBytes(PacketBuffer buf) {
        buf.writeCompoundTag(data);
    }

    @SuppressWarnings("SameReturnValue")
    boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity sender = ctx.get().getSender();
            if (sender != null) {
                IntStream.range(0, 10).forEach(i ->
                        sender.getPersistentData().put("anchor" + i, Objects.requireNonNull(data.get("anchor" + i))));
            }
        });
        return true;
    }
}