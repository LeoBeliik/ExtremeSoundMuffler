package com.leobeliik.extremesoundmuffler.networking;

import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import com.leobeliik.extremesoundmuffler.mufflers.MufflerEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class PacketClientMuffler implements ISoundLists {
    private static Map<ResourceLocation, Float> muffledSounds = new HashMap<>();
    private static BlockPos mufflerPos;
    private static int radius;
    private static boolean isMuffling;
    private static ITextComponent title;

    public PacketClientMuffler(Map<ResourceLocation, Float> ms, BlockPos pos, int rad, boolean muffling, ITextComponent name) {
        muffledSounds.clear();
        mufflerClientList.clear();
        muffledSounds.putAll(ms);
        mufflerPos = pos;
        radius = rad;
        isMuffling = muffling;
        title = name;
    }

    void encode(PacketBuffer buf) {
        buf.writeNbt(serializeMap());
        buf.writeBlockPos(mufflerPos);
        buf.writeInt(radius);
        buf.writeBoolean(isMuffling);
        buf.writeComponent(title);
    }

    static PacketClientMuffler decode(PacketBuffer buf) {
        return new PacketClientMuffler(
                deserializeMap(Objects.requireNonNull(buf.readNbt())),
                buf.readBlockPos(),
                buf.readInt(),
                buf.readBoolean(),
                buf.readComponent());
    }

    private CompoundNBT serializeMap() {
        if (muffledSounds.isEmpty()) {
            return new CompoundNBT();
        } else {
            CompoundNBT nbt = new CompoundNBT();
            muffledSounds.forEach((R, F) -> nbt.putFloat(R.toString(), F));
            return nbt;
        }
    }

    private static Map<ResourceLocation, Float> deserializeMap(CompoundNBT nbt) {
        Map<ResourceLocation, Float> tempMap = new HashMap<>();
        for (String key : nbt.getAllKeys()) {
            tempMap.put(new ResourceLocation(key), nbt.getFloat(key));
        }
        return tempMap;
    }

    boolean handle(Supplier<NetworkEvent.Context> ctx) {
        if (ctx.get().getDirection().getReceptionSide().isClient() && Minecraft.getInstance().level != null) {
            MufflerEntity muffler = new MufflerEntity(mufflerPos, radius, isMuffling, muffledSounds, title);
            mufflerClientList.add(muffler);
        }
        return true;
    }
}
