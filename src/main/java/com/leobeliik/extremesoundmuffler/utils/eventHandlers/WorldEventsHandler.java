package com.leobeliik.extremesoundmuffler.utils.eventHandlers;

import com.leobeliik.extremesoundmuffler.SoundMuffler;
import com.leobeliik.extremesoundmuffler.gui.MainScreen;
import com.leobeliik.extremesoundmuffler.utils.Anchor;
import com.leobeliik.extremesoundmuffler.utils.ISoundLists;
import com.leobeliik.extremesoundmuffler.utils.JsonIO;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import static com.leobeliik.extremesoundmuffler.utils.ISoundLists.muffledSounds;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = SoundMuffler.MODID)
public class WorldEventsHandler {

    private static final Logger LOGGER = LogManager.getLogger();


    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onWorldLoad(WorldEvent.Load event) {
        JsonIO.loadMuffledMap().forEach((R, V) -> ISoundLists.muffledSounds.put(new ResourceLocation(R), V));
        MainScreen.setAnchors();


        //TODO figure out how to save The anchors on the player
        //^^ look at saving the list of anchors not the anchors themselves
        //Save all the anchors and only the anchors
        //Simple muffled sounds still uses json save

        MainScreen.addAnchors(JsonIO.loadAnchors());
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onWorldUnload(WorldEvent.Unload event) {
        JsonIO.saveMuffledMap(muffledSounds);
        //For anchors
        JsonIO.saveAnchors(MainScreen.getAnchors());
    }
}