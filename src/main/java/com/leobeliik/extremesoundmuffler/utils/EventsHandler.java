package com.leobeliik.extremesoundmuffler.utils;

import com.leobeliik.extremesoundmuffler.SoundMuffler;
import com.leobeliik.extremesoundmuffler.gui.SoundMufflerScreen;
import net.minecraft.client.audio.ISound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = SoundMuffler.MODID)
public class EventsHandler {

    private static final String fileName = "soundsMuffled.dat";
    private static Set<String> forbiddenSounds = new HashSet<>();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    @OnlyIn(Dist.CLIENT)
    public static void onSoundPlaying(PlaySoundEvent event) {
        ISound sound = event.getSound();
        for (String fs : forbiddenSounds) {
            if (sound.getSoundLocation().toString().contains(fs)) {
                return;
            }
        }
        SoundMufflerScreen.addSound(sound.getSoundLocation());
        if (SoundMufflerScreen.getMuffledList().contains(sound.getSoundLocation()) && SoundMufflerScreen.isMuffled()) {
            event.setResultSound(null);
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void loadMufledList(WorldEvent.Load event) {
        Set<ResourceLocation> list = JsonIO.load(new File(fileName));
        if (list != null) {
            SoundMufflerScreen.setMuffledList(list);
        }
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void saveMufledList(WorldEvent.Unload event) {
        JsonIO.save(new File(fileName), SoundMufflerScreen.getMuffledList());
    }

    public static Set<String> ForbiddenSounds() {
        return forbiddenSounds;
    }
}