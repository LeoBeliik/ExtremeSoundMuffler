package com.leobeliik.extremesoundmuffler.utils;

import com.leobeliik.extremesoundmuffler.blocks.SoundMufflerBlock;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = "extremesoundmuffler")
public final class EventHandler {

    private static final Map<BlockPos, Set<ResourceLocation>> sounds = new HashMap<>();
    private static final byte muteIt = 0;
    private static final String[] forbidenSounds = {"music", "ui.button", "ambient.cave", "ui.toast"};

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onSoundPlaying(PlaySoundEvent event) {
        ISound sound = event.getSound();
        Set<BlockPos> soundMufflers = SoundMufflerBlock.getPositions();
        if (soundMufflers.isEmpty()) return;
        soundMufflers.forEach(pos -> {
            if (distance(sound, pos) >= 32) return;
            if (sound instanceof ITickableSound) {
                event.setResultSound(new Muffler.TickableMuffler((ITickableSound) sound, muteIt));
            } else {
                ResourceLocation soundLocat = sound.getSoundLocation();
                for (String fs : forbidenSounds) {
                    if (soundLocat.toString().contains(fs)) return;
                }
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
        });
    }

    public static Map<BlockPos, Set<ResourceLocation>> getSounds() {
        return sounds;
    }

    public static void setSounds(BlockPos pos, ResourceLocation sound) {
        if (sounds.containsKey(pos)) {
            sounds.get(pos).add(sound);
        } else {
            sounds.put(pos, new HashSet<>());
            setSounds(pos, sound);
        }

    }

    private static double distance(ISound sound, BlockPos pos) {
        return Math.sqrt( // d(P1, P2) = √(x2 - x1)² + (y2 - y1)² + (z2 - z1)²'
                Math.pow((sound.getX() - pos.getX()), 2)
                        + Math.pow((sound.getY() - pos.getY()), 2)
                        + Math.pow((sound.getZ() - pos.getZ()), 2)
        );
    }
}