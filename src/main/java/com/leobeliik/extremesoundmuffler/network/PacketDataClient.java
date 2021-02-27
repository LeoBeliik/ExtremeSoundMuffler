package com.leobeliik.extremesoundmuffler.network;

import com.leobeliik.extremesoundmuffler.eventHandlers.PlayerEventsHandler;
import com.leobeliik.extremesoundmuffler.interfaces.IAnchorList;
import com.leobeliik.extremesoundmuffler.utils.Anchor;
import com.leobeliik.extremesoundmuffler.utils.DataManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Supplier;

public class PacketDataClient implements IAnchorList {

    private final CompoundNBT data;

    PacketDataClient(PacketBuffer buf) {
        data = buf.readCompoundTag();
    }

    public PacketDataClient(CompoundNBT data) {
        this.data = data;
    }

    void toBytes(PacketBuffer buf) {
        buf.writeCompoundTag(data);
    }

    @SuppressWarnings("SameReturnValue")
    boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (data.contains("isClientSide")) {
                PlayerEventsHandler.setClientSide(data.getBoolean("isClientSide"));
                PlayerEventsHandler.setPlayerEntity(ctx.get().getSender());
                return;
            }

            for (int i = 0; i <= 9; i++) {
                if (!data.contains("anchor" + i)) {
                    anchorList.add(i, new Anchor(i, "Anchor: " + i));
                } else {
                    anchorList.add(i, deserializeNBT(data.getCompound("anchor" + i)));
                }
            }
            DataManager.loadData();
        });
        return true;
    }

    private static Anchor deserializeNBT(CompoundNBT nbt) {
        SortedMap<String, Double> muffledSounds = new TreeMap<>();
        CompoundNBT muffledNBT = nbt.getCompound("MUFFLED");

        for (String key : muffledNBT.keySet()) {
            muffledSounds.put(key, muffledNBT.getDouble(key));
        }

        if (!nbt.contains("POS")) {
            return new Anchor(nbt.getInt("ID"), nbt.getString("NAME"));
        } else {
            return new Anchor(nbt.getInt("ID"),
                    nbt.getString("NAME"),
                    NBTUtil.readBlockPos(nbt.getCompound("POS")),
                    new ResourceLocation(nbt.getString("DIM")),
                    nbt.getInt("RAD"),
                    muffledSounds);
        }
    }
}