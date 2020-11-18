package com.leobeliik.extremesoundmuffler.network;

import com.leobeliik.extremesoundmuffler.SoundMuffler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class Network {

    private static SimpleChannel INSTANCE;
    private static int id = 0;

    private static int nextID() {
        return id++;
    }

    public static void registerMessages() {
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(SoundMuffler.MODID), () -> "1.0", s -> true, s -> true);

        INSTANCE.messageBuilder(PacketDataList.class, nextID())
                .encoder(PacketDataList::toBytes)
                .decoder(PacketDataList::new)
                .consumer(PacketDataList::handle)
                .add();
        INSTANCE.messageBuilder(PacketAnchorList.class, nextID())
                .encoder(PacketAnchorList::toBytes)
                .decoder(PacketAnchorList::new)
                .consumer(PacketAnchorList::handle)
                .add();
    }

    public static void sendToClient(Object packet, ServerPlayerEntity player) {
        INSTANCE.sendTo(packet, player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
    }

    static void sendToServer(Object packet) {
        INSTANCE.sendToServer(packet);
    }
}
