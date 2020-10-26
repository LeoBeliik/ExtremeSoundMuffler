package com.leobeliik.extremesoundmuffler.utils.eventHandlers;

import com.leobeliik.extremesoundmuffler.Config;
import com.leobeliik.extremesoundmuffler.gui.MainScreen;
import com.leobeliik.extremesoundmuffler.utils.Anchor;
import com.leobeliik.extremesoundmuffler.utils.ISoundLists;
import com.leobeliik.extremesoundmuffler.utils.MuffledSound;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class SoundEventHandler implements ISoundLists {

    private static boolean isFromPSB = false;

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

        recentSoundsList.add(sound.getSoundLocation());

        if (MainScreen.isMuffled()) {
            if (muffledSounds.containsKey(sound.getSoundLocation())) {
                event.setResultSound(new MuffledSound(sound, muffledSounds.get(sound.getSoundLocation()).floatValue()));
                return;
            }

            //If Anchors are disabled in config
            if (Config.getDisableAchors()) {
                return;
            }

            for (Anchor anchor : MainScreen.getAnchors()) {
                if (soundPos.withinDistance(anchor.getAnchorPos(), anchor.getRadius())) {
                    if (anchor.getMuffledSounds().containsKey(sound.getSoundLocation())) {
                        //TODO this is not ok i think
                        event.setResultSound(new MuffledSound(sound, muffledSounds.get(sound.getSoundLocation()).floatValue()));
                    }
                }
            }
        }
    }

    public static SortedSet<ResourceLocation> getSoundsList() {
        return soundsList;
    }

    private static void removeForbiddenSounds() {
        forbiddenSounds().forEach(fs -> soundsList.removeIf(sl -> sl.toString().contains(fs)));
    }

    public static Set<String> forbiddenSounds() {
        return forbiddenSounds;
    }

    public static void isFromPlaySoundButton(boolean b) {
        isFromPSB = b;
    }

    static void AddAllSounds(Set<ResourceLocation> sounds) {
        soundsList.addAll(sounds);
    }

}
