package com.leobeliik.extremesoundmuffler.network;

import com.leobeliik.extremesoundmuffler.interfaces.IAnchorList;
import com.leobeliik.extremesoundmuffler.utils.Anchor;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class PacketAnchorList implements IAnchorList {

    private final CompoundNBT data;

    PacketAnchorList(PacketBuffer buf) {
        data = buf.readCompoundTag();
    }

    private PacketAnchorList(CompoundNBT data) {
        this.data = data;
    }

    void toBytes(PacketBuffer buf) {
        buf.writeCompoundTag(data);
    }

    boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity sender = ctx.get().getSender();
            if (sender != null) {
                for (int i = 0; i < 10; i++) {
                    sender.getPersistentData().put("anchor" + i, Objects.requireNonNull(data.get("anchor" + i)));
                }
            }
        });
        return true;
    }

    public static void sendAnchorList() {
        CompoundNBT anchorNBT = new CompoundNBT();
        for (int i = 0; i < 10; i++) {
            anchorNBT.put("anchor" + i, PacketAnchorList.serializeNBT(anchorList.get(i)));
        }
        Network.sendToServer(new PacketAnchorList(anchorNBT));
    }

    private static CompoundNBT serializeNBT(Anchor anchor) {

        CompoundNBT anchorNBT = new CompoundNBT();
        CompoundNBT muffledNBT = new CompoundNBT();

        anchorNBT.putInt("ID", anchor.getAnchorId());
        anchorNBT.putString("NAME", anchor.getName());

        if (anchor.getAnchorPos() == null) {
            return anchorNBT;
        }

        anchorNBT.put("POS", NBTUtil.writeBlockPos(anchor.getAnchorPos()));
        anchorNBT.putString("DIM", anchor.getDimension().toString());
        anchorNBT.putInt("RAD", anchor.getRadius());
        anchor.getMuffledSounds().forEach((r, d) -> muffledNBT.putDouble(r.toString(), d));
        anchorNBT.put("MUFFLED", muffledNBT);

        return anchorNBT;
    }
}
