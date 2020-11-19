package com.leobeliik.extremesoundmuffler.network;

import com.leobeliik.extremesoundmuffler.eventHandlers.WorldEventHandler;
import com.leobeliik.extremesoundmuffler.interfaces.IAnchorList;
import com.leobeliik.extremesoundmuffler.utils.Anchor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Supplier;

public class PacketDataList implements IAnchorList {

    private final CompoundNBT data;

    PacketDataList(PacketBuffer buf) {
        data = buf.readCompoundTag();
    }

    public PacketDataList(CompoundNBT data) {
        this.data = data;
    }

    void toBytes(PacketBuffer buf) {
        buf.writeCompoundTag(data);
    }

    boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            WorldEventHandler.isClientSide = false;
            for (int i = 0; i < 10; i++) {
                if (!data.contains("anchor" + i)) {
                    anchorList.add(i, new Anchor(i, "Anchor: " + i));
                } else {
                    anchorList.add(i, deserializeNBT(data.getCompound("anchor" + i)));
                }
            }
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