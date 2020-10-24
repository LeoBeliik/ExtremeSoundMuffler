package com.leobeliik.extremesoundmuffler.utils;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import java.util.SortedMap;
import java.util.TreeMap;

public class Anchor {

    private final int id;
    private BlockPos anchorPos;
    private String name;
    private double radius;
    private SortedMap<ResourceLocation, Double> muffledSounds = new TreeMap<>();

    public Anchor(int id, String name) {
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

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SortedMap<ResourceLocation, Double> getMuffledSounds() {
        return muffledSounds;
    }

    public void setMuffledSounds(SortedMap<ResourceLocation, Double> muffledSounds) {
        this.muffledSounds = muffledSounds;
    }

    public void addSound(ResourceLocation sound, double volume) {
        muffledSounds.put(sound, volume);
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

    public void removeSound(ResourceLocation sound) {
        muffledSounds.remove(sound);
    }
}
