package com.leobeliik.extremesoundmuffler.utils;

import com.leobeliik.extremesoundmuffler.Config;
import com.leobeliik.extremesoundmuffler.SoundMuffler;
import com.leobeliik.extremesoundmuffler.gui.SoundMufflerScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
    private static Set<String> forbiddenSounds = new HashSet<>();
    private static Set<ResourceLocation> allSoundsList;
    private static boolean isFromPSB = false;
    private static boolean isFirstLoad = true;
    private static boolean isAnchorsDisabled = Config.getDisableAchors().get();
    private static String path = "saves/ESM/ServerWorld/";

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
            ClientWorld clientWorld = Minecraft.getInstance().world;
            String worldName = event.getWorld().toString();

            if (clientWorld != null && clientWorld.isRemote && worldName.contains("ServerLevel")) {
                path = "saves/" + event.getWorld().toString().substring(12, worldName.length() - 1).replaceAll("\\.", "_") + "/ESM/";
                loadList(path);
            }
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onWorldLoad(WorldEvent.Load event) {
        path = "saves/ESM/ServerWorld/";
        allSoundsList = new HashSet<>(ForgeRegistries.SOUND_EVENTS.getKeys());
        loadList(path);
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onWorldUnload(WorldEvent.Unload event) {
        World clientWorld = Minecraft.getInstance().world;
        String worldName = event.getWorld().toString();
        if (clientWorld != null && clientWorld.isRemote) {
            if (worldName.contains("ServerLevel")) {
                path = "saves/" + event.getWorld().toString().substring(12, worldName.length() - 1).replaceAll("\\.", "_") + "/ESM/";
            }
        }

        JsonIO.saveMuffledList(new File(fileName), SoundMufflerScreen.getMuffledList());

        if (!isAnchorsDisabled) {
            for (int i = 0; i <= 9; i++) {
                JsonIO.saveAnchor(new File(path), new File(path + "Anchor" + i + ".dat"), SoundMufflerScreen.getAnchors().get(i));
            }
        }

        isFirstLoad = true;
        path = "saves/ESM/ServerWorld/";
    }

    private static void loadList(String path) {
        if (isAnchorsDisabled) {
            return;
        }
        Set<ResourceLocation> list = JsonIO.loadMuffledList(new File(fileName));
        SoundMufflerScreen.getAnchors().clear();
        removeForbiddenSounds();

        if (list != null) {
            SoundMufflerScreen.setMuffledList(list);
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