package com.leobeliik.extremesoundmuffler.eventHandlers;

import com.leobeliik.extremesoundmuffler.SoundMuffler;
import com.leobeliik.extremesoundmuffler.interfaces.IAnchorList;
import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import com.leobeliik.extremesoundmuffler.network.Network;
import com.leobeliik.extremesoundmuffler.network.PacketDataList;
import com.leobeliik.extremesoundmuffler.utils.Anchor;
import com.leobeliik.extremesoundmuffler.utils.JsonIO;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SoundMuffler.MODID)
public class PlayerEventsHandler implements IAnchorList, ISoundLists {

    @SubscribeEvent
    public static void onPlayerLoggin(PlayerEvent.PlayerLoggedInEvent event) {
        JsonIO.loadMuffledMap().forEach((R, V) -> muffledSounds.put(new ResourceLocation(R), V));
        anchorList.clear();

        ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();

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