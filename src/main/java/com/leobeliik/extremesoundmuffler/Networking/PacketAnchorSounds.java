package com.leobeliik.extremesoundmuffler.Networking;

import com.leobeliik.extremesoundmuffler.anchors.AnchorEntity;
import com.leobeliik.extremesoundmuffler.gui.MufflerScreen;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class PacketAnchorSounds {
    private static Map<ResourceLocation, Float> anchorMuffledSounds = new HashMap<>();
    private static BlockPos anchorPos;
    private static int radius;

    public PacketAnchorSounds(Map<ResourceLocation, Float> ms, BlockPos pos, int rad) {
        anchorMuffledSounds.clear();
        anchorMuffledSounds.putAll(ms);
        anchorPos = pos;
        radius = rad;
    }

    public void encode(PacketBuffer buf) {
        buf.writeNbt(serializeMap());
        buf.writeBlockPos(anchorPos);
        buf.writeInt(radius);
    }

    public static PacketAnchorSounds decode(PacketBuffer buf) {
        return new PacketAnchorSounds(deserializeMap(Objects.requireNonNull(buf.readNbt())), buf.readBlockPos(), buf.readInt());
    }

    private CompoundNBT serializeMap() {
        CompoundNBT nbt = new CompoundNBT();
        anchorMuffledSounds.forEach((R, F) -> nbt.putFloat(R.toString(), F));
        return nbt;
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
            ctx.get().enqueueWork(() -> MufflerScreen.open(anchorMuffledSounds, anchorPos, radius));
        } else {
            TileEntity anchor = ctx.get().getSender().level.getBlockEntity(anchorPos);
            if (anchor instanceof AnchorEntity) {
                ((AnchorEntity) anchor).setCurrentMuffledSounds(anchorMuffledSounds);
                ((AnchorEntity) anchor).setRadius(radius);
                anchor.setChanged();
            }
        }
        return true;
    }

}
