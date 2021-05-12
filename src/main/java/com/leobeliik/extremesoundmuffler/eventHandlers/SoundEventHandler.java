package com.leobeliik.extremesoundmuffler.eventHandlers;

import com.leobeliik.extremesoundmuffler.Config;
import com.leobeliik.extremesoundmuffler.SoundMuffler;
import com.leobeliik.extremesoundmuffler.gui.MainScreen;
import com.leobeliik.extremesoundmuffler.interfaces.IAnchorList;
import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import com.leobeliik.extremesoundmuffler.utils.Anchor;
import com.leobeliik.extremesoundmuffler.utils.MuffledSound;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = SoundMuffler.MODID)
public class SoundEventHandler implements ISoundLists, IAnchorList {

    private static boolean isFromPSB = false;

    @SubscribeEvent
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


        if (isForbidden(sound)) {
            return;
        }

        recentSoundsList.add(sound.getSoundLocation());

        if (MainScreen.isMuffled()) {
            if (muffledSounds.containsKey(sound.getSoundLocation())) {
                if (!(sound instanceof ITickableSound)) {
                    event.setResultSound(new MuffledSound(sound, muffledSounds.get(sound.getSoundLocation()).floatValue()));
                }
                return;
            }

            //If Anchors are disabled in config
            if (Config.getDisableAchors()) {
                return;
            }

            Anchor anchor = getAnchor(sound);
            if (anchor != null) {
                event.setResultSound(new MuffledSound(sound, anchor.getMuffledSounds().get(sound.getSoundLocation()).floatValue()));
            }
        }
    }

    public static boolean isForbidden(ISound sound) {
        for (String fs : forbiddenSounds) {
            if (sound.getSoundLocation().toString().contains(fs)) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    public static Anchor getAnchor(ISound sound) {
        BlockPos soundPos = new BlockPos(sound.getX(), sound.getY(), sound.getZ());
        for (Anchor anchor : anchorList) {
            if (anchor.getAnchorPos() != null) {
                boolean sameDimension = Minecraft.getInstance().world.getDimensionKey().getLocation().equals(anchor.getDimension());
                if (sameDimension && soundPos.withinDistance(anchor.getAnchorPos(), anchor.getRadius())) {
                    if (anchor.getMuffledSounds().containsKey(sound.getSoundLocation())) {
                        return anchor;
                    }
                }
            }
        }
        return null;
    }

    public static void isFromPlaySoundButton(boolean b) {
        isFromPSB = b;
    }
}
