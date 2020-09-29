package com.leobeliik.extremesoundmuffler.utils;

import com.leobeliik.extremesoundmuffler.Config;
import com.leobeliik.extremesoundmuffler.SoundMuffler;
import com.leobeliik.extremesoundmuffler.gui.SoundMufflerScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = SoundMuffler.MODID)
public class EventsHandler {

    private static final String fileName = "soundsMuffled.json";
    private static final Set<String> forbiddenSounds = new HashSet<>();
    private static final boolean isAnchorsDisabled = Config.getDisableAchors().get();
    private static final String serverWorld = "saves/ESM/ServerWorld/";
    private static Set<ResourceLocation> allSoundsList;
    private static boolean isFromPSB = false;
    private static boolean isFirstLoad = true;
    private static boolean isThisClient = false;
    private static int isWorldLoaded = 0;
    private static String path = serverWorld;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    @OnlyIn(Dist.CLIENT)
    public static void onSoundPlaying(PlaySoundEvent event) {
        if (Minecraft.getInstance().world == null) {
            return;
        }

        if (isFromPSB) {
            isFromPSB = false;
            return;
        }

        ISound sound = event.getSound();
        BlockPos soundPos = new BlockPos(sound.getX(), sound.getY(), sound.getZ());
        for (String fs : forbiddenSounds) {
            if (sound.getSoundLocation().toString().contains(fs)) {
                return;
            }
        }
        SoundMufflerScreen.addSound(sound.getSoundLocation());
        if (SoundMufflerScreen.isMuffled()) {
            if (SoundMufflerScreen.getMuffledMap().containsKey(sound.getSoundLocation())) {
                if (!Config.getShouldMufflePlaySub().get()) {
                    event.setResult(null);
                }
                event.setResultSound(new MuffledSound(sound, SoundMufflerScreen.getMuffledMap().get(sound.getSoundLocation()).floatValue()));
            }

            if (isAnchorsDisabled) {
                return;
            }

            for (int i = 0; i < 9; i++) {
                Anchor anchor = SoundMufflerScreen.getAnchors().get(i);
                if (!anchor.getMuffledSounds().containsKey(sound.getSoundLocation())) {
                    continue;
                }
                if (soundPos.withinDistance(anchor.getAnchorPos(), 16D)) {
                    event.setResultSound(null);
                    return;
                }
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onWorldLoad(WorldEvent.Load event) {
        for (int i = 0; i <= 9; i++) {
           SoundMufflerScreen.setAnchor(new Anchor(i, "Anchor: " + i));
        }

        if (SoundMuffler.isServer) {
            //Save
        } else {
            //Save json
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onWorldUnload(WorldEvent.Unload event) {
        if (SoundMuffler.isServer) {
            //Load
        } else {
            //Load json
        }
    }

    private static void removeForbiddenSounds() {
        forbiddenSounds().forEach(fs -> allSoundsList.removeIf(sl -> sl.toString().contains(fs)));
    }

    public static Set<String> forbiddenSounds() {
        return forbiddenSounds;
    }

    public static Set<ResourceLocation> getAllSounds() {
        return allSoundsList;
    }

    public static void isFromPlaySoundButton(boolean b) {
        isFromPSB = b;
    }

}