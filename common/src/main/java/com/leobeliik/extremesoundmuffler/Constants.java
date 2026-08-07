package com.leobeliik.extremesoundmuffler;

import net.minecraft.client.KeyMapping;
import net.minecraft.world.level.block.Block;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.leobeliik.extremesoundmuffler.interfaces.ISoundLists.blocksList;

public class Constants {
    public static final String MOD_ID = "extremesoundmuffler";
    public static final Logger ESM_LOG = LoggerFactory.getLogger("Extreme Sound Muffler");
    public static final KeyMapping soundMufflerKey = SoundMufflerCommon.mufflerKey();
    public static boolean useGlobalConfig;
	public static boolean darkMode;
    public static boolean validSound(String sound) {
        return sound.matches("[a-z0-9/._\\-:]*");
    }
    public static boolean isCustomSkinLoader;

    public static List<String> loadBlockSounds(String sound) {
        List<String> sounds = new ArrayList<>(5);
	    for (Block block : blocksList) {
		    if (block.getName().getString().equalsIgnoreCase(sound)) {
			    sounds.add(block.defaultBlockState().getSoundType().getBreakSound().location().toString());
			    sounds.add(block.defaultBlockState().getSoundType().getFallSound().location().toString());
			    sounds.add(block.defaultBlockState().getSoundType().getHitSound().location().toString());
			    sounds.add(block.defaultBlockState().getSoundType().getPlaceSound().location().toString());
			    sounds.add(block.defaultBlockState().getSoundType().getStepSound().location().toString());
				break;
		    }
	    }
	    return sounds;
    }
}
