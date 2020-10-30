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

import java.io.File;

import static com.leobeliik.extremesoundmuffler.utils.ISoundLists.muffledSounds;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = SoundMuffler.MODID)
public class WorldEventsHandler {

    private static final String fileName = "soundsMuffled.dat";

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onWorldLoad(WorldEvent.Load event) {

        //TODO loads the sound muffled, should refer to SoundEventHandler not MainScreen
        JsonIO.loadMuffledMap(new File(fileName)).forEach((R, V) -> ISoundLists.muffledSounds.put(new ResourceLocation(R), V));

        //TODO maybe change this, make anchors not gen here or smting
        MainScreen.setAnchors();


        //Save all the anchors and only the anchors
        //Simple muffled sounds still uses json save
        if (SoundMuffler.isServer) {
            //Load
        } else {
            for (int i = 0; i < 10; i++) {
                try {
                    MainScreen.addAnchor(i, JsonIO.loadAnchors(i));
                } catch (NullPointerException ignored) {
                    new Anchor(i, "Anchor: " + i);
                }
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onWorldUnload(WorldEvent.Unload event) {

        JsonIO.saveMuffledMap(new File(fileName), muffledSounds);

        //For anchors
        if (SoundMuffler.isServer) {
            //Save
        } else {
            for (int i = 0; i < 9; i++) {
                try {
                    JsonIO.saveAnchors(MainScreen.getAnchors().get(i));
                } catch (NullPointerException ignored) {
                    JsonIO.saveAnchors(new Anchor(i, "Anchor: " + i));
                }
            }
        }
    }

}