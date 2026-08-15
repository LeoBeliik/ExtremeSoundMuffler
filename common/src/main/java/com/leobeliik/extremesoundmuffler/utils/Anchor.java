package com.leobeliik.extremesoundmuffler.utils;

import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import java.util.*;
import java.util.List;
import static com.leobeliik.extremesoundmuffler.Constants.*;

public class Anchor {
	
	private static final double DEFAULT_VOLUME = 1D;

	private BlockPos anchorPos;
	private String name;
	private Identifier dimension;
	private int radius;
	private Map<String, Double> muffledSounds = new HashMap<>();
	private List<String> muffledBlocks = new ArrayList<>();

	public Anchor(String name, BlockPos anchorPos, Identifier dimension, int radius) {
		this.name = name;
		this.anchorPos = anchorPos;
		this.dimension = dimension;
		this.radius = radius;
	}

	private BlockPos getAnchorPos() {
		return anchorPos;
	}

	private void setAnchorPos(BlockPos anchorPos) {
		this.anchorPos = anchorPos;
	}

	public String getName() {
		return name;
	}

	public int getRange() {
		return radius;
	}

	void setRange(int radius) {
		this.radius = radius;
	}

	private void setName(String name) {
		this.name = name;
	}

	public Map<String, Double> getMuffledSounds() {
		return this.muffledSounds;
	}

	public List<String> getMuffledBlocks() {
		//muffledBlocks can be null when loading an old anchors.dat file
		if (this.muffledBlocks == null) this.muffledBlocks = new ArrayList<>();
		return this.muffledBlocks;
	}

	public void addSound(String sound, double volume) {
		muffledSounds.put(sound, volume);
	}

	public void replaceSound(String sound, double volume) {
		muffledSounds.replace(sound, volume);
	}

	public void addBlock(String block) {
		//muffledBlocks can be null when loading an old anchors.dat file
		if (muffledBlocks == null) muffledBlocks = new ArrayList<>();
		muffledBlocks.add(block);
	}

	public String getX() {
		return anchorPos != null ? String.valueOf(anchorPos.getX()) : "";
	}

	public String getY() {
		return anchorPos != null ? String.valueOf(anchorPos.getY()) : "";
	}

	public String getZ() {
		return anchorPos != null ? String.valueOf(anchorPos.getZ()) : "";
	}

	public Identifier getDimension() {
		return dimension;
	}

	private void setDimension(Identifier dimension) {
		this.dimension = dimension;
	}

	public void removeSound(String sound) {
		muffledSounds.remove(sound);
	}

	public void removeBlock(String sound) {
		muffledBlocks.remove(sound);
	}

	public void editAnchor(String name, BlockPos anchorPos, Identifier dimension, int radius) {
		setName(name);
		setAnchorPos(anchorPos);
		setDimension(dimension);
		setRange(radius);
	}

    private double containsSound(String sound, String mod) {
		//Check if mute everything first
	    if (muffledSounds.containsKey(EVERYTHING)) {
		    return muffledSounds.get(EVERYTHING);
	    }

	    //then find the sound if exists
	    if (muffledSounds.containsKey(sound)) {
		    return muffledSounds.get(sound);
	    }

		//Lastly check for mod or return full volume
		return muffledSounds.getOrDefault(mod, DEFAULT_VOLUME);
    }

	//TODO I really need to optimize this
	public static double getMuffling(SoundInstance sound) {
		ClientLevel world = minecraft.level;
		LocalPlayer player = minecraft.player;
		if (world == null || player == null) return DEFAULT_VOLUME;

		BlockPos soundPos = new BlockPos((int) sound.getX(), (int) sound.getY(), (int) sound.getZ());
		Identifier dimID = world.dimension().identifier();
		Identifier id = sound.getIdentifier();
		String soundID = id.toString();
		String mod = id.getNamespace().toLowerCase(Locale.ROOT);

		if (id.getPath().contains("entity.minecart.inside")) {
			//give player coordinates if it's in the minecart, minecart.inside sound pos is always at 0
			soundPos = player.getOnPos();
		}

		for (Anchor anchor : ISoundLists.anchorList) {
			if (anchor.getAnchorPos() != null && !anchor.getMuffledSounds().isEmpty()
					&& anchor.getDimension() != null && dimID.equals(anchor.getDimension())
					&& soundPos.closerThan(anchor.getAnchorPos(), anchor.getRange())) {

				double volume = anchor.containsSound(soundID, mod);
				if (volume != DEFAULT_VOLUME) {
					return volume;
				}
			}
		}
		
		return DEFAULT_VOLUME;
	}
}