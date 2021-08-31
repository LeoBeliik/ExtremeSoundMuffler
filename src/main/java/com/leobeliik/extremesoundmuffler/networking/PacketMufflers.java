package com.leobeliik.extremesoundmuffler.networking;

import com.leobeliik.extremesoundmuffler.gui.MufflerScreen;
import com.leobeliik.extremesoundmuffler.mufflers.MufflerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import static com.leobeliik.extremesoundmuffler.interfaces.ISoundLists.mufflerClientList;

public class PacketMufflers {
    private static Map<ResourceLocation, Float> muffledSounds = new HashMap<>();
    private static BlockPos mufflerPos;
    private static int radius;
    private static boolean isMuffling;
    private static ITextComponent title;
    private static boolean openGUI;

    public PacketMufflers(Map<ResourceLocation, Float> ms, BlockPos pos, int rad, boolean muffling, ITextComponent name, boolean gui) {
        muffledSounds.putAll(ms);
        mufflerPos = pos;
        radius = rad;
        isMuffling = muffling;
        title = name;
        openGUI = gui;
    }

    void encode(PacketBuffer buf) {
        buf.writeNbt(serializeMap());
        buf.writeBlockPos(mufflerPos);
        buf.writeInt(radius);
        buf.writeBoolean(isMuffling);
        buf.writeComponent(title);
        buf.writeBoolean(openGUI);
    }

    static PacketMufflers decode(PacketBuffer buf) {
        return new PacketMufflers(
                deserializeMap(Objects.requireNonNull(buf.readNbt())),
                buf.readBlockPos(),
                buf.readInt(),
                buf.readBoolean(),
                buf.readComponent(),
                buf.readBoolean());
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
        if (ctx.get().getDirection().getReceptionSide().isClient()) {
            if (openGUI) {
                ctx.get().enqueueWork(() -> MufflerScreen.open(muffledSounds, mufflerPos, radius, isMuffling, title));
            } else {
                MufflerEntity muffler = new MufflerEntity(mufflerPos, radius, isMuffling, muffledSounds, title);
                mufflerClientList.add(muffler);
            }
        } else {
            TileEntity muffler = Objects.requireNonNull(ctx.get().getSender()).level.getBlockEntity(mufflerPos);
            if (muffler instanceof MufflerEntity) {
                ((MufflerEntity) muffler).clearCurrentMuffledSounds();
                ((MufflerEntity) muffler).setCurrentMuffledSounds(muffledSounds);
                ((MufflerEntity) muffler).setRadius(radius);
                ((MufflerEntity) muffler).setMuffling(isMuffling);
                muffler.setChanged();
            }
        }
        muffledSounds.clear();
        return true;
    }

}
