package com.leobeliik.extremesoundmuffler.utils;

import com.leobeliik.extremesoundmuffler.interfaces.ISoundLists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;

import java.util.*;

public class Anchor {

    private BlockPos anchorPos;
    private String name;
    private Identifier dimension;
    private int radius;
    private Map<String, Double> muffledSounds = new HashMap<>();

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

    public void setRadius(int radius) {
        this.radius = radius;
    }

    private void setName(String name) {
        this.name = name;
    }

    public Map<String, Double> getMuffledSounds() {
        return this.muffledSounds;
    }

    public void setMuffledSounds(Map<String, Double> muffledSounds) {
	    this.muffledSounds.putAll(muffledSounds);
    }

    public void addSound(String sound, double volume) {
        muffledSounds.put(sound, volume);
    }

    public void replaceSound(String sound, double volume) {
        muffledSounds.replace(sound, volume);
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

    public void removeSound(Identifier sound) {
        muffledSounds.remove(sound.toString());
    }

    public void setAnchor(int radius) {
        LocalPlayer player = Objects.requireNonNull(Minecraft.getInstance().player);
        setAnchorPos(player.blockPosition());
        setDimension(player.level().dimension().identifier());
        setRadius(this.getRange() == 0 ? radius : this.getRange());
    }

    public void deleteAnchor() {
        setName("Empty Anchor");
        setAnchorPos(null);
        setDimension(null);
        setRadius(0);
        muffledSounds.clear();
    }

    public void editAnchor(String name, BlockPos anchorPos, Identifier dimension, int radius) {
        setName(name);
        setAnchorPos(anchorPos);
        setDimension(dimension);
        setRadius(radius);
    }

    public static Anchor getAnchor(SoundInstance sound) {
        BlockPos soundPos = new BlockPos((int) sound.getX(), (int) sound.getY(), (int) sound.getZ());
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        ClientLevel world = minecraft.level;

        if (player != null && sound.getIdentifier().getPath().contains("entity.minecart.inside")) {
            //give player coordinates if it's in the minecart, minecart.inside sound pos is always at 0
            soundPos = player.getOnPos();
        }
        for (Anchor anchor : ISoundLists.anchorList) {
            if (anchor.getAnchorPos() != null
                    && world != null
                    && world.dimension().identifier().equals(anchor.getDimension())
                    && soundPos.closerThan(anchor.getAnchorPos(), anchor.getRange())
                    && anchor.getMuffledSounds().containsKey(sound.getIdentifier().toString())) {
                return anchor;
            }
        }
        return null;
    }
}