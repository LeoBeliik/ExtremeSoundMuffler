package com.leobeliik.extremesoundmuffler.eventHandlers;

import com.leobeliik.extremesoundmuffler.SoundMuffler;
import com.leobeliik.extremesoundmuffler.interfaces.IAnchorList;
import com.leobeliik.extremesoundmuffler.network.Network;
import com.leobeliik.extremesoundmuffler.network.PacketDataClient;
import com.leobeliik.extremesoundmuffler.utils.DataManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod.EventBusSubscriber(modid = SoundMuffler.MODID)
public class PlayerEventsHandler implements IAnchorList {

    private static boolean isClientSide = true;
    private static ServerPlayerEntity playerEntity;

    @SubscribeEvent
    public static void onPlayerLoggin(PlayerEvent.PlayerLoggedInEvent event) {
        anchorList.clear();
        isClientSide = false;
        playerEntity = (ServerPlayerEntity) event.getPlayer();

        if (FMLEnvironment.dist.isDedicatedServer()) {
            CompoundNBT data = new CompoundNBT();
            data.putBoolean("isClientSide", isClientSide);
            Network.sendToClient(new PacketDataClient(data), playerEntity);
        }

        DataManager.loadData();
    }

    @SubscribeEvent
    public static void onPlayerLoggout(PlayerEvent.PlayerLoggedOutEvent event) {
        anchorList.clear();
        isClientSide = true;
        playerEntity = null;
    }

    public static boolean isClientSide() {
        return isClientSide;
    }

    public static ServerPlayerEntity getPlayerEntity() {
        return playerEntity;
    }

    public static void setIsClientSide(boolean clientSide) {
        isClientSide = clientSide;
    }

    public static void setPlayerEntity(ServerPlayerEntity player) {
        playerEntity = player;
    }
}