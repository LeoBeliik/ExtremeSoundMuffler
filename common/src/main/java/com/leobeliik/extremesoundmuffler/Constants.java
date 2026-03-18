package com.leobeliik.extremesoundmuffler;

import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static com.leobeliik.extremesoundmuffler.interfaces.ISoundLists.blocksList;

public class Constants {
    public static final String MOD_ID = "extremesoundmuffler";
    public static final Logger LOG = LogManager.getLogger("Extreme Sound Muffler");
    public static final KeyMapping soundMufflerKey = SoundMufflerCommon.mufflerKey();
    public static boolean useGlobalConfig;
    public static boolean validSound(String sound) {
        return sound.matches("[a-z0-9/._\\-:]*");
    }
    public static boolean isCustomSkinLoader;

    public static List<String> loadBlockSounds(String sound) {
        List<String> sounds = new ArrayList<>(5);
        blocksList.stream().filter(block -> block.getName().getString().equals(sound)).forEach(block -> {
            sounds.add(block.defaultBlockState().getSoundType().getBreakSound().location().toString());
            sounds.add(block.defaultBlockState().getSoundType().getFallSound().location().toString());
            sounds.add(block.defaultBlockState().getSoundType().getHitSound().location().toString());
            sounds.add(block.defaultBlockState().getSoundType().getPlaceSound().location().toString());
            sounds.add(block.defaultBlockState().getSoundType().getStepSound().location().toString());
        });
        return sounds;
    }
}
