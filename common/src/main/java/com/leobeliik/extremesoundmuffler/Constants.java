package com.leobeliik.extremesoundmuffler;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {
    public static final String MOD_ID = "extremesoundmuffler";
    public static final Logger ESM_LOG = LoggerFactory.getLogger("Extreme Sound Muffler");
    public static final KeyMapping soundMufflerKey = SoundMufflerCommon.mufflerKey();
	public static final Map<String, List<String>> CACHE_BLOCK_SOUNDS = new HashMap<>();
	public static final Set<String> ALL_SOUNDS_CACHE = new HashSet<>();
	public static final String NUMBERS = "^-?[0-9]+$";
	public static String EVERYTHING;
	public static Minecraft minecraft;
	public static Font font;
	public static boolean useGlobalConfig;
	public static boolean darkMode;
    public static boolean isCustomSkinLoader;

    public static void loadBlockSounds(List<Block> blocks) {
	    blocks.forEach(block -> CACHE_BLOCK_SOUNDS.put(block.getName().getString(), List.of(
			    block.defaultBlockState().getSoundType().getBreakSound().location().toString(),
			    block.defaultBlockState().getSoundType().getFallSound().location().toString(),
			    block.defaultBlockState().getSoundType().getHitSound().location().toString(),
			    block.defaultBlockState().getSoundType().getPlaceSound().location().toString(),
			    block.defaultBlockState().getSoundType().getStepSound().location().toString()
	    )));
    }

	public static void cacheAllSounds() {
		if (ALL_SOUNDS_CACHE.isEmpty())
			BuiltInRegistries.SOUND_EVENT.forEach(k -> ALL_SOUNDS_CACHE.add(k.location().toString()));
	}

	public static void setMinecraft() {
		minecraft = Minecraft.getInstance();
		font = minecraft.font;
	}
}
