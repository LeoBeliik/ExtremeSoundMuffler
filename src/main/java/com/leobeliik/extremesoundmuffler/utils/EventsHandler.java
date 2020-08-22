package com.leobeliik.extremesoundmuffler.utils;

import com.leobeliik.extremesoundmuffler.Config;
import com.leobeliik.extremesoundmuffler.SoundMuffler;
import com.leobeliik.extremesoundmuffler.gui.SoundMufflerScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = SoundMuffler.MODID)
public class EventsHandler {

    private static final String fileName = "soundsMuffled.dat";
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
            if (SoundMufflerScreen.getMuffledList().contains(sound.getSoundLocation())) {
                event.setResultSound(null);
            }

            if (isAnchorsDisabled) {
                return;
            }

            for (int i = 0; i < 9; i++) {
                Anchor anchor = SoundMufflerScreen.getAnchors().get(i);
                if (!anchor.getMuffledSounds().contains(sound.getSoundLocation())) {
                    continue;
                }

                if (anchor.getAnchorPos() == null) {
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
    public static void onWorldSave(WorldEvent.Save event) {
        if (isFirstLoad) {
            isFirstLoad = false;
            isThisClient = true;
            if (SoundMuffler.isServer) {
                if (event.getWorld() instanceof ServerWorld) {
                    path = "saves/" + ((ServerWorld) event.getWorld()).getServer().getFolderName() + "/ESM/";
                }
            } else {
                path = serverWorld;
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onWorldLoad(WorldEvent.Load event) {
        isWorldLoaded++;
        if (SoundMuffler.isServer) {
            if (event.getWorld() instanceof ServerWorld) {
                path = "saves/" + ((ServerWorld) event.getWorld()).getServer().getFolderName() + "/ESM/";
            }
        } else {
            path = serverWorld;
        }

        allSoundsList = new HashSet<>(ForgeRegistries.SOUND_EVENTS.getKeys());

        Set<ResourceLocation> list = JsonIO.loadMuffledList(new File(fileName));
        SoundMufflerScreen.getAnchors().clear();
        removeForbiddenSounds();

        if (list != null) {
            SoundMufflerScreen.setMuffledList(list);
        }

        if (isAnchorsDisabled) {
            return;
        }

        for (int i = 0; i <= 9; i++) {
            SoundMufflerScreen.setAnchor(JsonIO.loadAnchor(new File(path + "Anchor" + i + ".dat"), i));
            try {
                //noinspection ResultOfMethodCallIgnored
                SoundMufflerScreen.getAnchors().get(i);
            } catch (IndexOutOfBoundsException  e) {
                SoundMufflerScreen.setAnchor(new Anchor(i, "Anchor " + i));
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onWorldUnload(WorldEvent.Unload event) {
        if (isWorldLoaded == 3) {
            return;
        }

        if (isThisClient) {
            isThisClient = false;
            return;
        }

        JsonIO.saveMuffledList(new File(fileName), SoundMufflerScreen.getMuffledList());
        if (!isAnchorsDisabled) {
            for (int i = 0; i <= 9; i++) {
                JsonIO.saveAnchor(new File(path), new File(path + "Anchor" + i + ".dat"), SoundMufflerScreen.getAnchors().get(i));
            }
        }
        isWorldLoaded = 0;
        isFirstLoad = true;
        path = serverWorld;
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