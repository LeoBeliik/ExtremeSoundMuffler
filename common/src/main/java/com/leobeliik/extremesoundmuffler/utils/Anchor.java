package com.leobeliik.extremesoundmuffler.utils;

import com.leobeliik.extremesoundmuffler.CommonConfig;
import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import java.util.*;
import java.util.List;
import static com.leobeliik.extremesoundmuffler.Constants.EVERYTHING;

public class Anchor {

	private BlockPos anchorPos;
	private String name;
	private Identifier dimension;
	private int radius;
	private Map<String, Double> muffledSounds = new HashMap<>();
	private List<String> muffledBlocks = new ArrayList<>();

	public Anchor(String name) {
		this.name = name;
	}

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

	private void setRange(int radius) {
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

	public void setMuffledSounds(Map<String, Double> muffledSounds) {
		this.muffledSounds.putAll(muffledSounds);
	}

	public void setMuffledBlocks(List<String> muffledBlocks) {
		this.muffledBlocks.addAll(muffledBlocks);
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

	public void setAnchor(int radius) {
		LocalPlayer player = Objects.requireNonNull(Minecraft.getInstance().player);
		setAnchorPos(player.blockPosition());
		setDimension(player.level().dimension().identifier());
		setRange(this.getRange() == 0 ? radius : this.getRange());
	}

	public void deleteAnchor() {
		setName("Empty Anchor");
		setAnchorPos(null);
		setDimension(null);
		setRange(0);
		muffledSounds.clear();
		muffledBlocks.clear();
	}

	public void editAnchor(String name, BlockPos anchorPos, Identifier dimension, int radius) {
		setName(name);
		setAnchorPos(anchorPos);
		setDimension(dimension);
		setRange(radius);
	}

    private double containsSound(SoundInstance sound) {
		//Check if mute everything first
	    if (muffledSounds.containsKey(EVERYTHING)) {
		    return muffledSounds.get(EVERYTHING);
	    }

	    //then find the sound if exists
	    Identifier id = sound.getIdentifier();
	    String snd = id.toString();
	    if (muffledSounds.containsKey(snd)) {
		    return muffledSounds.get(snd);
	    }

		//Lastly check for mod or return full volume
		return muffledSounds.getOrDefault(id.getNamespace().toLowerCase(Locale.ROOT), 1D);
    }

	//TODO I really need to optimize this
	public static double getMuffling(SoundInstance sound) {
		BlockPos soundPos = new BlockPos((int) sound.getX(), (int) sound.getY(), (int) sound.getZ());
		Minecraft minecraft = Minecraft.getInstance();
		LocalPlayer player = minecraft.player;
		ClientLevel world = minecraft.level;
		int maxAnchorRange = CommonConfig.get().maxAnchorRange().get();

		if (player != null && sound.getIdentifier().getPath().contains("entity.minecart.inside")) {
			//give player coordinates if it's in the minecart, minecart.inside sound pos is always at 0
			soundPos = player.getOnPos();
		}
		for (Anchor anchor : ISoundLists.anchorList) {
			if (anchor.getRange() > maxAnchorRange) anchor.setRange(maxAnchorRange);
			if (anchor.getAnchorPos() != null
					&& world != null && anchor.getDimension() != null
					&& world.dimension().identifier().equals(anchor.getDimension())
					&& soundPos.closerThan(anchor.getAnchorPos(), anchor.getRange())) {

				double volume = anchor.containsSound(sound);
				if (volume != 1D) {
					return volume;
				}
			}
		}
		return 1D;
	}
}