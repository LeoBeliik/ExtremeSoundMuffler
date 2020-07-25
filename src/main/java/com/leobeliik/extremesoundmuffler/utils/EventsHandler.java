package com.leobeliik.extremesoundmuffler.utils;

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
import net.minecraftforge.registries.ForgeRegistries;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = SoundMuffler.MODID)
public class EventsHandler {

    private static final String fileName = "soundsMuffled.dat";
    private static Set<String> forbiddenSounds = new HashSet<>();
    private static Set<ResourceLocation> allSoundsList = new HashSet<>(ForgeRegistries.SOUND_EVENTS.getKeys());
    private static boolean fromPSB = false;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    @OnlyIn(Dist.CLIENT)
    public static void onSoundPlaying(PlaySoundEvent event) {
        if (fromPSB) {
            fromPSB = false;
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
    public static void loadMufledList(WorldEvent.Load event) {
        Set<ResourceLocation> list = JsonIO.loadMuffledList(new File(fileName));
        SoundMufflerScreen.getAnchors().clear();
        removeForbiddenSounds();
        if (list != null) {
            SoundMufflerScreen.setMuffledList(list);
        }
        for (int i = 0; i <= 9; i++) {
            SoundMufflerScreen.getAnchors().add(JsonIO.loadAnchor(new File("ESM/Anchor" + i + ".dat"), i));
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void saveMufledList(WorldEvent.Unload event) {
        JsonIO.saveMuffledList(new File(fileName), SoundMufflerScreen.getMuffledList());
        //TODO find world name/save folder and create intermediate folders or something
        for (int i = 0; i <= 9; i++) {
            if (Minecraft.getInstance().world == null || SoundMufflerScreen.getAnchors().get(i) == null) continue;
            JsonIO.saveAnchor(new File("ESM/Anchor" + i + ".dat"), SoundMufflerScreen.getAnchors().get(i));
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
        fromPSB = b;
    }
}