package com.leobeliik.extremesoundmuffler.eventHandlers;

import com.leobeliik.extremesoundmuffler.SoundMuffler;
import com.leobeliik.extremesoundmuffler.interfaces.IAnchorList;
import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import com.leobeliik.extremesoundmuffler.network.Network;
import com.leobeliik.extremesoundmuffler.network.PacketDataList;
import com.leobeliik.extremesoundmuffler.utils.Anchor;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SoundMuffler.MODID)
public class PlayerEventsHandler implements IAnchorList, ISoundLists {

    @SubscribeEvent
    public static void onPlayerLoggin(PlayerEvent.PlayerLoggedInEvent event) {
        saveData((ServerPlayerEntity) event.getPlayer());
    }

    @SubscribeEvent
    public static void onPlayerChangindDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        saveData((ServerPlayerEntity) event.getPlayer());
    }

    private static void saveData(ServerPlayerEntity player) {
        anchorList.clear();

        if (player == null) {
            for (int i = 0; i < 10; i++) {
                anchorList.add(i, new Anchor(i, "Anchor: " + i));
            }
        } else {
            CompoundNBT data = player.getPersistentData();
            Network.sendToClient(new PacketDataList(data), player);
        }
    }
}