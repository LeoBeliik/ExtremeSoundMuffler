package com.leobeliik.extremesoundmuffler.utils.eventHandlers;

import com.leobeliik.extremesoundmuffler.SoundMuffler;
import com.leobeliik.extremesoundmuffler.gui.MainScreen;
import com.leobeliik.extremesoundmuffler.utils.Anchor;
import com.leobeliik.extremesoundmuffler.utils.JsonIO;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = SoundMuffler.MODID)
public class WorldEventsHandler {

    private static final String fileName = "soundsMuffled.dat";
    private static final String serverWorld = "saves/ESM/ServerWorld/";
    private static String path = serverWorld;


    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onWorldLoad(WorldEvent.Load event) {

        //TODO loads the sound muffled, should refer to SoundEventHandler not MainScreen
        //JsonIO.loadMuffledMap(new File(fileName)).forEach((R, V) -> MainScreen.setMuffledMap(new ResourceLocation(R), V));

        //Fill the sound list with all the sounds registered
        SoundEventHandler.AddAllSounds(ForgeRegistries.SOUND_EVENTS.getKeys());

        //TODO maybe change this, make anchors not gen here or smting
        for (int i = 0; i <= 9; i++) {
            MainScreen.setAnchor(new Anchor(i, "Anchor: " + i));
        }

        //Save all the anchors and only the anchors
        //Simple muffled sounds still uses json save
        if (SoundMuffler.isServer) {
            //Save
        } else {
            //Save json
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onWorldUnload(WorldEvent.Unload event) {

        //JsonIO.saveMuffledMap(new File(fileName), muffledSounds);

        //For anchors
        if (SoundMuffler.isServer) {
            //Load
        } else {
            //Load json
        }
    }

}