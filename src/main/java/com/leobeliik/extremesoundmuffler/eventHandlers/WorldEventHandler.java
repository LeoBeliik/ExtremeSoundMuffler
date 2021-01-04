package com.leobeliik.extremesoundmuffler.eventHandlers;

import com.leobeliik.extremesoundmuffler.SoundMuffler;
import com.leobeliik.extremesoundmuffler.interfaces.IAnchorList;
import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import com.leobeliik.extremesoundmuffler.utils.Anchor;
import com.leobeliik.extremesoundmuffler.utils.JsonIO;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Objects;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = SoundMuffler.MODID)
public class WorldEventHandler implements ISoundLists, IAnchorList {

    public static boolean isClientSide;

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onWorldLoad(WorldEvent.Load event) {
        muffledSounds.clear();
        JsonIO.loadMuffledMap().forEach((R, V) -> ISoundLists.muffledSounds.put(new ResourceLocation(R), V));
        anchorList.clear();

        if (JsonIO.loadAnchors() == null || Objects.requireNonNull(JsonIO.loadAnchors()).size() == 0) {
            for (int i = 0; i < 10; i++) {
                anchorList.add(new Anchor(i, "Anchor " + i));
            }
        } else {
            anchorList.addAll(Objects.requireNonNull(JsonIO.loadAnchors()));
        }
        isClientSide = true;
    }

    //TODO think about sending all the load / unload to when the GUI is opened instead of relying on other events
    /*@SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onWorldUnload(WorldEvent.Unload event) {
        if (isClientSide) {
            JsonIO.saveAnchors(anchorList);
        }
    }*/
}
