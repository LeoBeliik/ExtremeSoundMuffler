package com.leobeliik.extremesoundmuffler.utils;

import com.leobeliik.extremesoundmuffler.blocks.SoundMufflerBlock;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.Sound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = "extremesoundmuffler")
public final class EventHandler {

    private static final Map<BlockPos, Set<ResourceLocation>> sounds = new HashMap<>();
    private static final byte muteIt = 0;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onSoundPlaying(PlaySoundEvent event) {
        ISound sound = event.getSound();
        Set<BlockPos> soundMufflers = SoundMufflerBlock.getPositions();
        if (soundMufflers == null) return;
        soundMufflers.forEach(pos -> {
            if (sound instanceof ITickableSound) {
                event.setResultSound(new Muffler.TickableMuffler((ITickableSound) sound, muteIt));
            } else {
                double distance = DistanceCalculator.distance(sound, pos);
                ResourceLocation soundLocat = sound.getSoundLocation();
                if (distance <= 32 && !soundLocat.toString().contains("music") && !soundLocat.toString().contains("ui.")) {
                    if (!sounds.containsKey(pos)) {
                        sounds.put(pos, new HashSet<>());
                    }
                    sounds.get(pos).add(soundLocat);
                    Set<ResourceLocation> mufflerList = SoundMufflerBlock.getMufflerOnPosition(pos);
                    if (mufflerList == null) return;
                    if (mufflerList.contains(soundLocat)) {
                        event.setResultSound(new Muffler(sound, muteIt));
                    }
                }
            }
        });
    }

    public static Map<BlockPos, Set<ResourceLocation>> getSounds() {
        return sounds;
    }
}