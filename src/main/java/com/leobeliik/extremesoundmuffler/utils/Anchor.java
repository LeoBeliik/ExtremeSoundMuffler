package com.leobeliik.extremesoundmuffler.utils;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import java.util.SortedSet;
import java.util.TreeSet;

public class Anchor {

    private int id;
    private BlockPos anchorPos;
    private String name;
    private SortedSet<ResourceLocation> muffledSounds = new TreeSet<>();

    Anchor(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public BlockPos getAnchorPos() {
        return anchorPos;
    }

    public void setAnchorPos(BlockPos anchorPos) {
        this.anchorPos = anchorPos;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SortedSet<ResourceLocation> getMuffledSounds() {
        return muffledSounds;
    }

    public void setMuffledSounds(SortedSet<ResourceLocation> muffledSounds) {
        this.muffledSounds = muffledSounds;
    }

    public void addSound(ResourceLocation sound) {
        muffledSounds.add(sound);
    }

    public void removeSound(ResourceLocation sound) {
        muffledSounds.remove(sound);
    }

}
