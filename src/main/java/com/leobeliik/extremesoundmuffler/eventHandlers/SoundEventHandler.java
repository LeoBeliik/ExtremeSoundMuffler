package com.leobeliik.extremesoundmuffler.eventHandlers;

import com.leobeliik.extremesoundmuffler.Config;
import com.leobeliik.extremesoundmuffler.SoundMuffler;
import com.leobeliik.extremesoundmuffler.gui.MainScreen;
import com.leobeliik.extremesoundmuffler.interfaces.IAnchorList;
import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import com.leobeliik.extremesoundmuffler.utils.Anchor;
import com.leobeliik.extremesoundmuffler.utils.MuffledSound;
import com.leobeliik.extremesoundmuffler.utils.MuffledSound.MuffledTickableSound;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = SoundMuffler.MODID)
public class SoundEventHandler implements ISoundLists, IAnchorList {

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
                if (sound instanceof ITickableSound) {
                    event.setResultSound(new MuffledTickableSound((ITickableSound) event.getSound(), muffledSounds.get(sound.getSoundLocation()).floatValue()));
                } else {
                    event.setResultSound(new MuffledSound(sound, muffledSounds.get(sound.getSoundLocation()).floatValue()));
                }
                return;
            }

            //If Anchors are disabled in config
            if (Config.getDisableAchors()) {
                return;
            }

            for (Anchor anchor : anchorList) {
                if (anchor.getAnchorPos() != null) {
                    boolean sameDimension = Minecraft.getInstance().world.getDimensionKey().getLocation().equals(anchor.getDimension());
                    if (sameDimension && soundPos.withinDistance(anchor.getAnchorPos(), anchor.getRadius())) {
                        if (anchor.getMuffledSounds().containsKey(sound.getSoundLocation())) {
                            event.setResultSound(new MuffledSound(sound, anchor.getMuffledSounds().get(sound.getSoundLocation()).floatValue()));
                        }
                    }
                }
            }
        }
    }

    public static void isFromPlaySoundButton(boolean b) {
        isFromPSB = b;
    }
}
