package com.leobeliik.extremesoundmuffler.events;

import com.leobeliik.extremesoundmuffler.Config;
import com.leobeliik.extremesoundmuffler.SoundMuffler;
import com.leobeliik.extremesoundmuffler.interfaces.IAnchorList;
import com.leobeliik.extremesoundmuffler.network.Network;
import com.leobeliik.extremesoundmuffler.network.PacketDataClient;
import com.leobeliik.extremesoundmuffler.utils.DataManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod.EventBusSubscriber(modid = SoundMuffler.MODID)
public class PlayerEvents implements IAnchorList {

    private static ServerPlayer player;

    @SubscribeEvent
    public static void onPlayerLoggin(PlayerEvent.PlayerLoggedInEvent event) {
        if (Config.isClientSide()) {
            return;
        }

        anchorList.clear();
        player = (ServerPlayer) event.getPlayer();

        if (FMLEnvironment.dist.isDedicatedServer()) {
            CompoundTag data = new CompoundTag();
            Network.sendToClient(new PacketDataClient(data), player);
        }

        if (player == null) {
            DataManager.setAnchors(); //this should never happen
        } else {
            CompoundTag data = player.getPersistentData();
            Network.sendToClient(new PacketDataClient(data), player);
        }

    }

    @SubscribeEvent
    public static void onPlayerLoggout(PlayerEvent.PlayerLoggedOutEvent event) {
        anchorList.clear();
        player = null;
    }
}