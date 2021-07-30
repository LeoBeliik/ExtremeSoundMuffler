package com.leobeliik.extremesoundmuffler.Networking;

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
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(SoundMuffler.MODID),
                () -> "1.0",
                s -> true,
                s -> true);

        /*INSTANCE.messageBuilder(PacketDataClient.class, nextID())
                .encoder(PacketDataClient::toBytes)
                .decoder(PacketDataClient::new)
                .consumer(PacketDataClient::handle)
                .add();

        INSTANCE.messageBuilder(PacketDataServer.class, nextID())
                .encoder(PacketDataServer::toBytes)
                .decoder(PacketDataServer::new)
                .consumer(PacketDataServer::handle)
                .add();*/
    }

    public static void sendToClient(Object packet, ServerPlayerEntity player) {
        INSTANCE.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    static void sendToServer(Object packet) {
        INSTANCE.sendToServer(packet);
    }
}
